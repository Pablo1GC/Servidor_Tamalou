package com.tamalou.servidor.modelo.entidad.entidadesPartida;


import com.tamalou.servidor.modelo.entidad.socketEntities.JsonField;
import com.tamalou.servidor.socket.GameManager;
import com.tamalou.servidor.socket.Signal;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Es la clase encargada de gestionar una partida
 */
public class Game extends Thread {
    private Thread disconnectedPlayersThread;
    private Thread gameThread;

    public final int MAX_PLAYERS = 4;
    private final boolean privateGame;
    private String gameName;

    private final int maxRounds;
    private final CopyOnWriteArrayList<Player> playerList;
    private int actualRound;
    private boolean gameEnded;
    private Player winner;
    private GameManager gameManager;
    private String key;
    private Player owner;

    public Game(boolean isPrivate, String gameName, int maxRounds, GameManager gameManager, Player owner) {
        this.maxRounds = maxRounds;
        this.actualRound = 0;
        this.gameEnded = false;
        this.privateGame = isPrivate;
        this.gameName = gameName;
        this.playerList = new CopyOnWriteArrayList<>();
        this.gameManager = gameManager;
        this.owner = owner;
        this.playerList.add(owner);
        //this.checkForDisconnectedPlayers();
    }

    @Override
    public void run() {
        try{
            this.gameThread = Thread.currentThread();
            startGame();
        } catch (InterruptedException e){
            gameManager.removeGame(this.key);
        }
    }

    public void checkForDisconnectedPlayers() {
        disconnectedPlayersThread = new Thread(() -> {
            synchronized (this) {
                List<Player> playersToDelete = new LinkedList<>();

                try {
                    if (this.playerList.size() == 0){
                        this.gameEnded = true;
                        gameThread.interrupt();

                    }

                    while (!gameEnded) {

                        for (Player p : playerList) {

                            if (!p.isConnected()) {
                                playersToDelete.add(p);
                            }
                        }

                        playersToDelete.forEach((p) -> {
                            playerList.remove(p);
                            playerList.forEach((player -> {
                                player.writter.packAndWrite(Signal.PLAYER_DISCONNECTED,
                                        new JsonField("player_uid", p.getUid()),
                                        new JsonField("n_of_players", playerList.size()));
                            }));
                            gameManager.getSignalManager().manage(p);
                        });
                        playersToDelete.clear();
                        wait(1_000);
                    }
                } catch (InterruptedException ignored){} // Game gets deleted

            }
        });
        disconnectedPlayersThread.start();
    }

    /**
     * This method starts the game.
     * First, sends a signal to every player (Client).
     */
    public void startGame() throws InterruptedException{

        for (Player p : playerList) {
            p.writter.packAndWrite(Signal.START_GAME, playerList);
            p.setPoints(0);
        }

        while (!gameEnded) {
            Round round = new Round(playerList);
            round.playRound();
            actualRound++;

            if (actualRound >= maxRounds) {
                gameEnded = true;
            }
        }

        winner = determineWinner();
        System.out.println("¡Game has ended! The winner is: " + winner.getUid());
        for (Player p2 : playerList) {
            p2.writter.packAndWrite(Signal.END_GAME, winner.getUid());
        }
    }

    /**
     * Determines which player in the game has been the winner based on the one with the lowest score in the game
     *
     * @return the winner of the game
     */
    private Player determineWinner() {
        Player winner = playerList.get(0);
        for (int i = 1; i < playerList.size(); i++) {
            Player player = playerList.get(i);
            if (player.getPoints() < winner.getPoints()) {
                winner = player;
            }
        }
        return winner;
    }

    /**
     *
     * @return true if the game is private
     */
    public boolean isPrivate() {
        return privateGame;
    }

    /**
     * Adds player to the game and send a signal to the other players (Client).
     * If the player´s list is full, the game starts.
     *
     * @param player
     */
    public void addPlayer(Player player) {
        playerList.add(player);
        for (Player p : playerList) {
            p.writter.packAndWrite(Signal.PLAYER_JOINED_GAME, playerList.size());
        }

        if (playerList.size() == MAX_PLAYERS) {
            this.start();
        }
    }


    // GETTERS AND SETTERS

    public CopyOnWriteArrayList<Player> getPlayersList() {
        return playerList;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
