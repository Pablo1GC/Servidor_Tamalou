package com.tamalou.servidor.modelo.entidad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Ronda {
    private List<Jugador> jugadores;
    private Mazo mazo;
    private Stack<Carta> monton;
    private int numTurnos;
    private int turnoActual;

    /**
     * Cuando se instancia un objeto se inicia una nueva ronda
     * @param jugadores
     * @param mazo
     */
    public Ronda(List<Jugador> jugadores, Mazo mazo) {
        this.jugadores = jugadores;
        this.mazo = mazo;
        this.monton = new Stack<>();
        this.numTurnos = 5;
        this.turnoActual = 0;
    }



    public List<Carta> getMonton() {
        return monton;
    }

    /**
     * Agrega la carta pasada por parametro monton de cartas descartadas
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
     * @param jugador Es el jugador que descartará la acrta
     * @param carta La carta a descartar
     */
    public void descartarCartaJugador(Jugador jugador, Carta carta) {
        if (carta.getValor() == monton.lastElement().getValor()) {
            jugador.getCartas().remove(carta);
            monton.add(carta);
        } else {
            jugador.setPuntos(jugador.getPuntos() + 5);
        }
    }

    /**
     *
     */
    public void jugar() {


        // Si alguien se ha descartado de todas las cartas, termina la ronda
        for (Jugador jugador : jugadores) {
            if (jugador.getCartas().size() == 0) {
                terminarRonda();
                return;
            }
        }

        // Si nadie se ha descartado de todas las cartas, se permite a cualquier jugador plantarse
        System.out.println("Cualquier jugador puede plantarse durante su turno...");
        for (Jugador jugador : jugadores) {
            jugador.sePlanta();
        }

    }

    public void terminarRonda() {

    }



    public Mazo getMazo() {
        return mazo;
    }


}
