package com.tamalou.servidor.modelo.entidad.entidadesPartida;

import java.util.List;
import java.util.Stack;

public class Ronda {
    private List<Jugador> jugadores;
    private Mazo mazo;
    private Stack<Carta> monton;
    private boolean terminarRonda;
    private int turnoActual;

    /**
     * Cuando se instancia un objeto se inicia una nueva ronda
     * @param jugadores
     */
    public Ronda(List<Jugador> jugadores) {
        this.jugadores = jugadores;
        this.mazo = new Mazo();
        this.monton = new Stack<>();
        this.turnoActual = 0;
        this.terminarRonda = false;
    }

    /**
     * Es el método que comienza la ronda.
     * Primero baraja el mazo.
     * Entrega cuatro cartas a cada jugador.
     * Los jugadores realizan cada turno hasta terminar la ronda.
     */
    public void jugarRonda() {
        mazo.barajar();
        for (Jugador jugador : jugadores) {
            for (int i = 0; i < 4; i++) {
                jugador.recibirCarta(mazo.tomarCarta());
            }
        }

        while (!terminarRonda) {
            // Si alguien se ha descartado de todas las cartas, termina la ronda
            for (Jugador jugador : jugadores) {
                // Empieza el turno del jugador
                Boolean plantarse = jugador.jugarTurno(turnoActual);

                if(plantarse = true){
                    terminarRonda();
                }

                // Si algún jugador se queda sin cartas se acaba la ronda.
                if (jugador.getCartas().size() == 0) {
                    terminarRonda();
                }

            }
            turnoActual++;
        }

    }

    public List<Carta> getMonton() {
        return monton;
    }

    /**
     * Agrega la carta pasada por parametro monton de cartas descartadas
     *
     * @param carta Es la carta a descartar
     */
    public void addCartaMonton(Carta carta) {
        monton.add(carta);
    }

    /**
     * Comprueba si la carta recibida por parametro es igual a la ultima carta
     * de la variable monton de tipo Stack que hace alusión a las cartas descartadas,
     * en caso de que sea igual la carta se descartará y en el caso contrario
     * el jugador sera penalizado con 5 puntos
     *
     * @param jugador Es el jugador que descartará la acrta
     * @param carta   La carta a descartar
     */
    public void descartarCartaJugador(Jugador jugador, Carta carta) {
        if (carta.getValor() == monton.lastElement().getValor()) {
            jugador.getCartas().remove(carta);
            monton.add(carta);
        } else {
            jugador.setPuntos(jugador.getPuntos() + 5);
        }
    }


    public void terminarRonda() {
        terminarRonda = true;
    }


    public Mazo getMazo() {
        return mazo;
    }


}
