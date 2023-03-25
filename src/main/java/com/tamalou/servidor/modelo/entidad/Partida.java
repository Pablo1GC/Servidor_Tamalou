package com.tamalou.servidor.modelo.entidad;


import java.util.List;

/**
 * Es la clase encargada de gestionar una partida
 */
public class Partida {
    private static final int NUMERO_MAXIMO_RONDAS = 10;
    private static final int PUNTOS_MAXIMOS = 100;

    private final int numeroMaximoRondas;
    private final int puntosMaximos;
    private final List<Jugador> jugadores;
    private int rondaActual;
    private boolean terminada;
    private Jugador ganador;
    private Mazo mazo;

    /**
     * Cuando se crea el objeto crea una partida basado en los parámetros que recibe
     * @param numeroMaximoRondas Hace referencia al numero maximo de rondas que tendrá la partida
     * @param puntosMaximos      Hace referencia al numero maximo de rondas que tendrá la partida para definir el ganador
     * @param jugadores          Son los jugadores que jugarán la partida
     */
    public Partida(int numeroMaximoRondas, int puntosMaximos, List<Jugador> jugadores) {
        this.numeroMaximoRondas = numeroMaximoRondas;
        this.puntosMaximos = puntosMaximos;
        this.jugadores = jugadores;
        this.rondaActual = 0;
        this.terminada = false;
    }

    /**
     * Comprueba si se debe jugar una ronda mas en la partida y en caso de que
     * se deba jugar, agregaria una nueva ronda a la partida
     * @return Devuelve el jugador que haya ganado la partida
     */
    public Jugador jugar() {
        if (terminada) {
            System.out.println("La partida ya ha terminado.");
            return ganador;
        }

        while (!terminada) {
            Ronda ronda = new Ronda(jugadores, mazo);
            ronda.jugar();
            rondaActual++;

            if (rondaActual >= numeroMaximoRondas || hayGanador()) {
                terminada = true;
            }
        }

        ganador = determinarGanador();
        System.out.println("¡La partida ha terminado! El ganador es: " + ganador.getNombre());
        return ganador;
    }

    /**
     * Comprueba si hay algun ganador en el stack de jugadores
     * @return True en caso de que haya un jugador ganador, sobrepasando los puntos maximos
     * @return False en caso de que no haya ningun jugador que haya alcanzado los puntos maximos
     */
    private boolean hayGanador() {
        for (Jugador jugador : jugadores) {
            if (jugador.getPuntos() >= puntosMaximos) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determina que jugador de la partida ha sido ganador basandose en el que ha tenido menos
     * puntaje en la partida
     * @return devuelve el jugador ganador
     */
    private Jugador determinarGanador() {
        Jugador ganador = jugadores.get(0);
        for (int i = 1; i < jugadores.size(); i++) {
            Jugador jugador = jugadores.get(i);
            if (jugador.getPuntos() < ganador.getPuntos()) {
                ganador = jugador;
            }
        }
        return ganador;
    }
}
