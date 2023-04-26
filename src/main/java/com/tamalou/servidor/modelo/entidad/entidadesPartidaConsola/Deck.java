package com.tamalou.servidor.modelo.entidad.entidadesPartidaConsola;

import java.util.Collections;
import java.util.Stack;

/**
 * This is the deck of cards used for playing in games
 */
public class Deck {
    private Stack<Card> cardsDeck;

    /**
     * When the deck is created, an array of cards is created and
     * assigned a value and a suit
     */
    public Deck() {
        cardsDeck = new Stack<>();
        String[] suits = {"Hearts", "Spades", "Diamonds", "Clubs"};
        int card = 0;
        for (String suit : suits) {
            for (int value = 1; value <= 13; value++) {
                cardsDeck.add(new Card(value, suit));
                card++;
            }
        }
    }

    /**
     * Takes care of randomly shuffling the cards in the deck
     */
    public void shuffleDeck() {
        Collections.shuffle(cardsDeck);
    }

    /**
     * Takes care of dealing the top card from the deck
     *
     * @return Returns the top card from the deck
     */
    public Card takeCard() {
        return cardsDeck.pop();
    }

    public boolean checkEmptyDeck() {
        if (cardsDeck.isEmpty()) {
            return true;
        }
        return false;
    }

    public Stack<Card> getCardsDeck() {
        return cardsDeck;
    }

    public void setCardsDeck(Stack<Card> cardsDeck) {
        this.cardsDeck = cardsDeck;
    }
}