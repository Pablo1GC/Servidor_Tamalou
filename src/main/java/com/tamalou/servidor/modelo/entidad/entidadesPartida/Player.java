package com.tamalou.servidor.modelo.entidad.entidadesPartida;

import com.tamalou.servidor.modelo.entidad.entidadesExtra.Utilidades;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the class that refers to a player in a game
 */
public class Player {
    private String name;
    private int points;
    private List<Card> cards;
    private boolean endTurn;


    /**
     * Initializes the player with their name, 0 points, and initializes the array of cards they would have in their hand
     * @param name
     */
    public Player(String name) {
        this.name = name;
        this.points = 0;
        this.cards = new ArrayList<>();
        this.endTurn = false;

    }

    /**
     * This is the method that starts the player's turn.
     * @param actualTurn
     * @return
     */
    public Boolean playTurn(int actualTurn) {
        // Aquí el jugador elige qué hacer en su turno
        while (!endTurn) {
            if (actualTurn > 5) {
                System.out.println("Do you want to end your turn? [Yes/No]");
                String endTurn = Utilidades.leerCadena();
                if (endTurn.equals("Yes")) {
                    return true;
                }

            }

        }
        return false;
    }


    /**
     * Selects the card with which the player will interact
     * @return Returns the index of the card selected by the player
     */
    public int selectCard() {
        int cardIndex = Utilidades.leerEntero("¿Qué carta desea seleccionar?");
        return cardIndex;
    }

    /**
     *  Allows the player to see a card
     *  @param cardIndex is the index of the card we want to see
     */
    public void seeCard(int cardIndex) {
        // Implementar return
        Card card = cards.get(cardIndex);
        System.out.println("La carta es " + card);
    }

    /**
     *  It is responsible for exchanging two cards with another player
     *  @param oponent is the player with whom you want to exchange the cards
     *  @param ownIndexCard is the index of the card that will be exchanged with the opponent passed by parameter
     *  @param exchangedIndexCard is the index of the opponent's card that the player will receive
     */
    public void switchCard(Player oponent, int ownIndexCard, int exchangedIndexCard) {
        cards.set(ownIndexCard, oponent.cards.get(exchangedIndexCard));
        oponent.cards.set(exchangedIndexCard, cards.get(ownIndexCard));
    }

    /**
     *  Is responsible for executing the power of a card depending on its value
     *  @param card that must be checked for its value to execute the associated power
     */
    public void usePower(Card card, Player oponent) {
        // QUITAR EL SEGUNDO PARAMETRO Y HACER UN METODO PARA SELECCIONAR EL OPONENTE
        switch (card.getValue()) {
            case 11: //  (J)
                seeCard(selectCard());
                break;
            case 12: //  (Q)
                int ownIndexCardQ = selectCard();
                int exchangedIndexCardQ = oponent.selectCard();
                switchCard(oponent, ownIndexCardQ, exchangedIndexCardQ);
                break;
            case 13: //  (K)
                int ownIndexCardK = selectCard();
                int exchangedIndexCardK = oponent.selectCard();
                seeCard(ownIndexCardK);
                oponent.seeCard(exchangedIndexCardK);
                int opcion = Utilidades.leerEntero("¿Deseas intercambiar la carta? (1: Sí, 2: No)");
                if (opcion == 1) {
                    switchCard(oponent, ownIndexCardK, exchangedIndexCardK);
                }
                break;
        }
    }

    /**
     * @param value
     * @return
     */
    public boolean discardCard(int value) {
        for (Card card : cards) {
            if (card.getValue() == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return
     */
    public boolean endTurn() {
        return true;
    }


    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public void takeCard(Card card) {
        cards.add(card);
    }
}