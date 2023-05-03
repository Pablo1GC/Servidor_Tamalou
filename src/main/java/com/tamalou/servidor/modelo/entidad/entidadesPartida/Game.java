package com.tamalou.servidor.modelo.entidad.entidadesPartida;


import com.tamalou.servidor.socket.Signal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Es la clase encargada de gestionar una partida
 */
public class Game extends Thread{
    private Thread gameThread;

    public final int MAX_PLAYERS = 4;
    private boolean privateGame;
    private String gameName;

    private int maxRounds;
    private List<Player> playersList;
    private int actualRound;
    private boolean gameEnded;
    private Player winner;

    /**
     * Cuando se crea el objeto crea una partida basado en los parámetros que recibe
     */
    public Game(boolean isPrivate, String gameName) {
        gameThread = new Thread(this);
        this.maxRounds = maxRounds;
        this.actualRound = 0;
        this.gameEnded = false;
        this.privateGame = isPrivate;
        this.gameName = gameName;
        this.playersList = new ArrayList<>();
    }

    @Override
    public void run(){
        startGame();
    }
    /**
     * This method starts the game.
     * First, sends a signal toevery player (Client).
     *
     *
     *
     */
    public Player startGame() {
        for (Player p : playersList) {
            p.writter.println(Signal.START_GAME);
        }

        while (!gameEnded) {
            Round round = new Round(playersList);
            round.playRound();
            actualRound++;

            if (actualRound >= maxRounds) {
                gameEnded = true;
            }
        }

        winner = returnWinner();
        System.out.println("¡La partida ha terminado! El ganador es: " + winner.getName());

        return winner;
    }

    /**
     * (Próxima versión) Comprueba si hay algun ganador entre la lista de jugadores.
     * @return True en caso de que haya un jugador ganador, sobrepasando los puntos maximos
     * @return False en caso de que no haya ningun jugador que haya alcanzado los puntos maximos

    private boolean hayGanador() {
        for (Player player : jugadores) {
            if (player.getPoints() >= puntosMaximos) {
                return true;
            }
        }
        return false;
    }
     */

    /**
     * Determina que jugador de la partida ha sido ganador basandose en el que ha tenido menos
     * puntaje en la partida
     * @return devuelve el jugador ganador
     */
    private Player returnWinner() {
        Player ganador = null;
        for (int i = 1; i < playersList.size(); i++) {
            Player player = playersList.get(i);
            if (player.getPoints() < ganador.getPoints()) {
                ganador = player;
            }
        }
        return ganador;
    }

    public boolean isPrivate() {
        return privateGame;
    }

    public List<Player> getPlayersList() {
        return playersList;
    }

    public void setPlayersList(List<Player> playersList) {
        this.playersList = playersList;
    }

    /**
     * Adds player to the game and send a signal to the other players (Client).
     * If the player´s list is full, the game starts.
     * @param player
     */
    public void addPlayer(Player player) {
        playersList.add(player);
        for (Player p : playersList) {
            p.writter.println(Signal.PLAYER_JOINED_GAME);
            p.writter.println(playersList.size());
        }

        if (playersList.size() == MAX_PLAYERS){
            startGame();
        }
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
