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
        int numberOfCards = getCards().size();
        int indexSelected;
        do {
            System.out.println(name + " has " + numberOfCards + ", Which card you want to choose?");
            indexSelected = Utilidades.leerEntero("") - 1;
        } while (indexSelected < 0 || indexSelected > numberOfCards - 1);
        return indexSelected;
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
     * @param opponent            is the player with whom you want to exchange the cards
     * @param ownIndexCard       is the index of the card that will be exchanged with the opponent passed by parameter
     * @param exchangedIndexCard is the index of the opponent's card that the player will receive
     */
    public void switchCardWithOponent(Player opponent, int ownIndexCard, int exchangedIndexCard) {
        cards.set(ownIndexCard, opponent.cards.get(exchangedIndexCard));
        opponent.cards.set(exchangedIndexCard, cards.get(ownIndexCard));
    }

    /**
     * Gives the option to the player to stand and end the round.
     *
     * @return Returns true if the option is "Yes", false if is "No"
     */
    public boolean standRound() {
        String endTurn;
        do {
            System.out.println("Do you want to stand? [Yes/No]");
            endTurn = Utilidades.leerCadena();
        } while (!endTurn.equals("Yes") && !endTurn.equals("No"));
        return endTurn.equals("Yes");
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