package com.tamalou.servidor.modelo.entidad.entidadesPartida;

import java.util.List;
import java.util.Stack;

public class Round {
    private List<Player> players;
    private Deck deck;
    private Stack<Card> maze;
    private boolean endRound;
    private int actualTurn;

    /**
     * When an object is instantiated, a new round is started.
     * @param players
     */
    public Round(List<Player> players) {
        this.players = players;
        this.deck = new Deck();
        this.maze = new Stack<>();
        this.actualTurn = 0;
        this.endRound = false;
    }

    /**
     * This method starts the round.
     * It first shuffles the deck.
     * It gives four cards to each player.
     * Players take turns until the end of the round.
     */
    public void playRound() {
        deck.shuffleDeck();
        for (Player player : players) {
            for (int i = 0; i < 4; i++) {
                player.takeCard(deck.takeCard());
            }
        }

        while (!endRound) {
            // If someone has discarded all their cards, the round ends.
            for (Player player : players) {
                // The player's turn begins
                boolean stand = player.playTurn(actualTurn);

                if(stand){
                    endRound();
                }

                // If any player runs out of cards, the round ends.
                if (player.getCards().size() == 0) {
                    endRound();
                }

            }
            actualTurn++;
        }

    }

    public List<Card> getMaze() {
        return maze;
    }

    /**
     * Adds the card passed as a parameter to the discard pile
     *
     * @param card The card to discard
     */
    public void addCardToMaze(Card card) {
        maze.add(card);
    }

    /**
     * Checks if the card received as a parameter is equal to the last card
     * in the discard pile (of type Stack), and discards the card if it is.
     * Otherwise, the player is penalized with 5 points.
     *
     * @param player The player who will discard the card
     * @param card   The card to discard
     */
    public void discardPlayerCard(Player player, Card card) {
        if (card.getValue() == maze.lastElement().getValue()) {
            player.getCards().remove(card);
            maze.add(card);
        } else {
            player.setPoints(player.getPoints() + 5);
        }
    }


    public void endRound() {
        endRound = true;
    }


    public Deck getDeck() {
        return deck;
    }


}
