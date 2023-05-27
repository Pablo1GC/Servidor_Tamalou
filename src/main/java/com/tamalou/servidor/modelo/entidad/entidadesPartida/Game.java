package com.tamalou.servidor.modelo.entidad.entidadesPartida;


import com.tamalou.servidor.socket.Signal;

import java.util.ArrayList;
import java.util.List;

/**
 * Es la clase encargada de gestionar una partida
 */
public class Game extends Thread {
    private Thread gameThread;

    public final int MAX_PLAYERS = 4;
    private boolean privateGame;
    private String gameName;

    private int maxRounds;
    private List<Player> playerList;
    private int actualRound;
    private boolean gameEnded;
    private Player winner;

    /**
     * Cuando se crea el objeto crea una partida basado en los parámetros que recibe
     */
    public Game(boolean isPrivate, String gameName, int maxRounds) {
        gameThread = new Thread(this);
        this.maxRounds = maxRounds;
        this.actualRound = 0;
        this.gameEnded = false;
        this.privateGame = isPrivate;
        this.gameName = gameName;
        this.playerList = new ArrayList<>();
    }

    @Override
    public void run() {
        startGame();
    }

    /**
     * This method starts the game.
     * First, sends a signal to every player (Client).
     */
    public void startGame() {
        for (Player p : playerList) {
            p.writter.packAndWrite(Signal.START_GAME, playerList);
        }

        while (!gameEnded) {
            Round round = new Round(playerList);
            round.playRound();
            actualRound++;

            if (actualRound >= maxRounds) {
                gameEnded = true;
            }
        }

        winner = returnWinner();
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
    private Player returnWinner() {
        Player winner = null;
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

    public List<Player> getPlayersList() {
        return playerList;
    }

    public void setPlayersList(List<Player> playersList) {
        this.playerList = playersList;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
