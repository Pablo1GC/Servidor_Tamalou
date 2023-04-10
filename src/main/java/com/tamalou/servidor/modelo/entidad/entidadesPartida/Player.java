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
     *
     * @param name
     */
    public Player(String name) {
        this.name = name;
        this.points = 0;
        this.cards = new ArrayList<>();
        this.endTurn = false;
    }

    /**
     * Allows the player to swap one of his cards with another card of the game.
     *
     * @param card
     * @return
     */
    public Card swapCards(Card card) {
        int cardIndex = selectCard();
        Card myCard = cards.get(cardIndex);
        cards.set(cardIndex, card);
        return myCard;
    }

    /**
     * Selects the card with which the player will interact
     *
     * @return Returns the index of the card selected by the player
     */
    public int selectCard() {
        System.out.println("Which card you want to choose?");
        return Utilidades.leerEntero("");
    }

    /**
     * Allows the player to see a card
     *
     * @param cardIndex is the index of the card we want to see
     */
    public void seeCard(int cardIndex) {
        System.out.println(cards.get(cardIndex));
    }

    /**
     * It is responsible for exchanging two cards with another player
     *
     * @param oponent            is the player with whom you want to exchange the cards
     * @param ownIndexCard       is the index of the card that will be exchanged with the opponent passed by parameter
     * @param exchangedIndexCard is the index of the opponent's card that the player will receive
     */
    public void switchCardWithOponent(Player oponent, int ownIndexCard, int exchangedIndexCard) {
        cards.set(ownIndexCard, oponent.cards.get(exchangedIndexCard));
        oponent.cards.set(exchangedIndexCard, cards.get(ownIndexCard));
    }

    /**
     * Gives the option to the player to stand and end the round.
     *
     * @return Returns true if the option is "Yes", false if is "No"
     */
    public boolean standRound() {
        System.out.println("Do you want to stand? [Yes/No]");
        String endTurn = Utilidades.leerCadena();
        if (endTurn.equals("Yes")) {
            return true;
        }
        return false;
    }

    /**
     * Gets a player card
     *
     * @return the selected card
     */
    public Card getCard() {
        return cards.get(selectCard());
    }

    // Getters and Setters
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