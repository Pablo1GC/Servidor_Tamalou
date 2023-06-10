package com.tamalou.servidor.modelo.entidad.entidadesPartida;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tamalou.servidor.modelo.persistencia.GameRepository;
import com.tamalou.servidor.socket.Signal;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents a finished game entity in the application.
 * <p>
 * This class is an entity mapped to the "game" table in the database.
 * It contains information about a game, such as its unique identifier,
 * number of rounds, and associated players.
 */
@Entity
@Table(name = "game")
public class Game extends Thread {
    //private Thread disconnectedPlayersThread;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_game")
    private long id;

    @Column(name = "name")
    private String gameName;

    @Column(name = "num_rounds")
    private int maxRounds;

    @Transient
    public final int MAX_PLAYERS = 4;
    @Transient
    private boolean privateGame;
    @Transient
    private CopyOnWriteArrayList<Player> playerList;
    @Transient
    private int actualRound;
    @Transient
    @JsonIgnore
    private boolean gameEnded;
    @Transient
    private Player winner;

    public Game(boolean isPrivate, String gameName, int maxRounds) {
        this.maxRounds = maxRounds;
        this.actualRound = 0;
        this.gameEnded = false;
        this.privateGame = isPrivate;
        this.gameName = gameName;
        this.playerList = new CopyOnWriteArrayList<>();
        this.checkForDisconnectedPlayers();
    }

    public Game() {

    }


    @Override
    public void run() {
        startGame();
    }

    public void checkForDisconnectedPlayers() {
//        disconnectedPlayersThread = new Thread(() -> {
//            synchronized (this) {
//                try {
//                    List<Player> playersToDelete = new LinkedList<>();
//                    while (!gameEnded) {
//
//                        for (Player p : playerList) {
//                            if (!p.isConnected()) {
//                                playersToDelete.add(p);
//                            }
//                        }
//
//                        playersToDelete.forEach((p) -> {
//                            playerList.remove(p);
//                            playerList.forEach((player -> {
//                                player.writter.packAndWrite(Signal.PLAYER_DISCONNECTED,
//                                        new JsonField("player_uid", p.getUid()),
//                                        new JsonField("n_of_players", playerList.size()));
//                            }));
//                        });
//                        playersToDelete.clear();
//                        wait(1_000);
//                    }
//                } catch (InterruptedException ignored){} // Game gets deleted
//            }
//        });
//        disconnectedPlayersThread.start();
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

        GameRepository.getInstance().save(this);

    }

    /**
     * Determines which player in the game has been the winner based on the one with the lowest score in the game
     *
     * @return the winner of the game
     */
    private Player returnWinner() {
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
    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CopyOnWriteArrayList<Player> getPlayersList() {
        return playerList;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
