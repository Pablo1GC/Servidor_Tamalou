package com.tamalou.servidor.modelo.entidad.entidadesPartida;

import java.util.Collections;
import java.util.Stack;

/**
 * Es el mazo de cartas con el que se jugará en las partidas
 */
public class Mazo {
    private Stack<Carta> mazoDeCartas;

    /**
     * Cuando se crea el mazo crea un array de cartas y
     * les asigna un valor y un palo
     */
    public Mazo() {
        mazoDeCartas = new Stack<>();
        String[] palos = {"Corazones", "Picas", "Diamantes", "Tréboles"};
        int carta = 0;
        for (String palo : palos) {
            for (int valor = 1; valor <= 13; valor++) {
                mazoDeCartas.add(new Carta(valor, palo));
                carta++;
            }
        }
    }


    /**
     * Se ocupa de mezclar de manera aleatoria las cartas del mazo
     */
    public void barajar() {
        Collections.shuffle(mazoDeCartas);
    }

    /**
     * Se encarga de dar la carta de encima del mazo
     * @return Devuelve la carta de encima del mazo
     */
    public Carta tomarCarta() {
        return mazoDeCartas.pop();
    }
}
