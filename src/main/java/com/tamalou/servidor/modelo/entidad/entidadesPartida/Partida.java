package com.tamalou.servidor.modelo.entidad.entidadesPartida;


import java.util.List;

/**
 * Es la clase encargada de gestionar una partida
 */
public class Partida extends Thread{

    public final int MAX_PLAYERS = 4;
    private boolean privateGame;
    private String gameName;

    private int numeroMaximoRondas;
    private List<Player> playersList;
    private int rondaActual;
    private boolean terminada;
    private Player ganador;

    /**
     * Cuando se crea el objeto crea una partida basado en los parámetros que recibe
     */
    public Partida(boolean isPrivate, String gameName) {
        this.numeroMaximoRondas = numeroMaximoRondas;
        this.rondaActual = 0;
        this.terminada = false;
        this.privateGame = isPrivate;
    }

    /**
     * Es el método que comienza la partida.
     * Comprueba si se debe jugar una ronda mas en la partida y en caso de que
     * se deba jugar, agregaria una nueva ronda a la partida
     * @return Devuelve el jugador que haya ganado la partida
     */
    public Player jugarPartida() {

        while (!terminada) {
            Round round = new Round(playersList);
            round.playRound();
            rondaActual++;

            if (rondaActual >= numeroMaximoRondas) {
                terminada = true;
            }
        }

        ganador = determinarGanador();
        System.out.println("¡La partida ha terminado! El ganador es: " + ganador.getName());
        return ganador;
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
    private Player determinarGanador() {
        Player ganador = playersList.get(0);
        for (int i = 1; i < playersList.size(); i++) {
            Player player = playersList.get(i);
            if (player.getPoints() < ganador.getPoints()) {
                ganador = player;
            }
        }
        return ganador;
    }

    public boolean isPrivado() {
        return privateGame;
    }

    public List<Player> getPlayersList() {
        return playersList;
    }

    public void setPlayersList(List<Player> playersList) {
        this.playersList = playersList;
    }

    public void addPlayer(Player player) {
        playersList.add(player);
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
