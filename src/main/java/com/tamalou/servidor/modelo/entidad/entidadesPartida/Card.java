package com.tamalou.servidor.modelo.entidad.entidadesPartida;

public class Card {
    private int value;
    private String suit;


    public Card(int value, String suit) {
        this.value = value;
        this.suit = suit;

    }

    public int getValue() {
        return value;
    }

    public String getSuit() {
        return suit;
    }


}
