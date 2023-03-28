package com.tamalou.servidor.modelo.entidad.entidadesPartida;

import com.tamalou.servidor.modelo.entidad.entidadesExtra.Utilidades;

import java.util.ArrayList;
import java.util.List;

/**
 * Es la clase que hace referencia a un jugador de una partida
 */
public class Jugador {
    private String nombre;
    private int puntos;
    private List<Carta> cartas;
    private boolean terminarTurno;


    /**
     * Inicializa al jugador con su nombre, puntos a 0 y
     * e inicializa el array de las cartas que tendria en la mano
     *
     * @param nombre
     */
    public Jugador(String nombre) {
        this.nombre = nombre;
        this.puntos = 0;
        this.cartas = new ArrayList<>();
        this.terminarTurno = false;

    }

    /**
     * Es el método que comienza el turno del jugador.
     * @param turnoActual
     * @return
     */
    public Boolean jugarTurno(int turnoActual) {
        // Aquí el jugador elige qué hacer en su turno
        while (!terminarTurno) {
            if (turnoActual > 5) {
                System.out.println("¿Quieres plantarte? [Si/No]");
                String plantarse = Utilidades.leerCadena();
                if (plantarse.equals("Si")) {
                    return true;
                }

            }

        }
        return false;
    }


    /**
     * Selecciona la carta con la que el jugador interactuará
     *
     * @return Devuelve el indice de la carta seleccionada por el jugador
     */
    public int seleccionarCarta() {
        int indiceCarta = Utilidades.leerEntero("¿Qué carta desea seleccionar?");
        return indiceCarta;
    }

    /**
     * Permite al jugador ver una carta
     *
     * @param indiceCarta es el indice de la carta que queremos ver
     */
    public void verCarta(int indiceCarta) {
        // Implementar return
        Carta carta = cartas.get(indiceCarta);
        System.out.println("La carta es " + carta);
    }

    /**
     * Se encarga de intercambiar dos cartas con otro jugador
     *
     * @param oponente                 es el jugador con el que se desea intercambiar las cartas
     * @param indiceCartaPropia        es el índice de la carta que se intercambiara con el oponente recibo por parámetro
     * @param indiceCartaIntercambiada es el índice de la carta del oponente que recibirá el jugador
     */
    public void intercambiarCarta(Jugador oponente, int indiceCartaPropia, int indiceCartaIntercambiada) {
        cartas.set(indiceCartaPropia, oponente.cartas.get(indiceCartaIntercambiada));
        oponente.cartas.set(indiceCartaIntercambiada, cartas.get(indiceCartaPropia));
    }

    /**
     * Se encarga de ejecutar el poder de una carta dependiendo el valor de la misma
     *
     * @param carta que se debe comprobar el valor para para ejecutar el poder asociado
     */
    public void utilizarPoder(Carta carta, Jugador oponente) {
        // QUITAR EL SEGUNDO PARAMETRO Y HACER UN METODO PARA SELECCIONAR EL OPONENTE
        switch (carta.getValor()) {
            case 11: // Sota (J)
                verCarta(seleccionarCarta());
                break;
            case 12: // Caballo (Q)
                int indiceCartaPropiaQ = seleccionarCarta();
                int indiceCartaIntercambiadaQ = oponente.seleccionarCarta();
                intercambiarCarta(oponente, indiceCartaPropiaQ, indiceCartaIntercambiadaQ);
                break;
            case 13: // Rey (K)
                int indiceCartaPropiaK = seleccionarCarta();
                int indiceCartaIntercambiadaK = oponente.seleccionarCarta();
                verCarta(indiceCartaPropiaK);
                oponente.verCarta(indiceCartaIntercambiadaK);
                int opcion = Utilidades.leerEntero("¿Deseas intercambiar la carta? (1: Sí, 2: No)");
                if (opcion == 1) {
                    intercambiarCarta(oponente, indiceCartaPropiaK, indiceCartaIntercambiadaK);
                }
                break;
        }
    }

    /**
     * @param valor
     * @return
     */
    public boolean puedeDescartarCartaValor(int valor) {
        for (Carta carta : cartas) {
            if (carta.getValor() == valor) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return
     */
    public boolean sePlanta() {
        return true;
    }


    public String getNombre() {
        return nombre;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public List<Carta> getCartas() {
        return cartas;
    }

    public void setCartas(List<Carta> cartas) {
        this.cartas = cartas;
    }

    public void recibirCarta(Carta carta) {
        cartas.add(carta);
    }
}