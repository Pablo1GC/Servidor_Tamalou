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

    @Override
    public String toString() {
        String valueName;
        switch (value) {
            case 1:
                valueName = "As";
                break;
            case 11:
                valueName = "J";
                break;
            case 12:
                valueName = "Q";
                break;
            case 13:
                valueName = "K";
                break;
            default:
                valueName = Integer.toString(value);
                break;
        }
        return valueName + " of " + suit;
    }
}
