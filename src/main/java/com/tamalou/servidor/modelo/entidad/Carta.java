package com.tamalou.servidor.modelo.entidad;

public class Carta {
    private int valor;
    private String palo;

    public Carta(int valor, String palo) {
        this.valor = valor;
        this.palo = palo;
    }

    public int getValor() {
        return valor;
    }

    public String getPalo() {
        return palo;
    }

    @Override
    public String toString() {
        String nombreValor;
        switch (valor) {
            case 1:
                nombreValor = "As";
                break;
            case 11:
                nombreValor = "J";
                break;
            case 12:
                nombreValor = "Q";
                break;
            case 13:
                nombreValor = "K";
                break;
            default:
                nombreValor = Integer.toString(valor);
                break;
        }
        return nombreValor + " de " + palo;
    }
}
