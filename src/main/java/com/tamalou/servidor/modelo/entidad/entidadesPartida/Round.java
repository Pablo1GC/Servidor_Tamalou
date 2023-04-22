package com.tamalou.servidor.modelo.entidad.entidadesPartida;

import com.tamalou.servidor.modelo.entidad.entidadesExtra.Utilidades;

import java.util.List;
import java.util.Stack;

public class Round {
    private final List<Player> players;
    private final Deck deck;
    private final Stack<Card> discardedCardsDeck;
    private boolean endRound;
    private int actualTurn;

    /**
     * When an object is instantiated, a new round is started.
     *
     * @param players List of players in the round
     */
    public Round(List<Player> players) {
        this.players = players;
        this.deck = new Deck();
        this.discardedCardsDeck = new Stack<>();
        this.actualTurn = 6;
        this.endRound = false;

        playRound();
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
                if (deck.checkEmptyDeck()) {
                    returnDiscardedCardsToDeck();
                }

                //The last card in the Maze is always shown
                showLastCardInMaze();

                // If round is above 5, player can stand and end the round.
                if (actualTurn > 5) {
                    boolean stand = player.standRound();
                    if (stand) {
                        endRound = true;
                        break;
                    }
                }

                // Player can choose what to do
                chooseOptionToPlay(player);

                System.out.println(player.getName() + " your turn has ended.");

                // If any player runs out of cards, the round ends.
                if (player.getCards().size() == 0) {
                    endRound = true;
                }

            }
            actualTurn++;
        }

    }


    private void returnDiscardedCardsToDeck() {
        deck.setCardsDeck(discardedCardsDeck);
        deck.shuffleDeck();
        discardedCardsDeck.clear();
        discardedCardsDeck.add(deck.takeCard());
    }


    /**
     * Shows the last card in the discarded deck.
     */
    public void showLastCardInMaze() {
        if (discardedCardsDeck.size() == 0) {
            System.out.println("The maze is empty.");
        } else {
            System.out.println(discardedCardsDeck.lastElement());
        }
    }

    /**
     * Player can choose what to do in his turn.
     * @param player Player choosing the option
     */
    public void chooseOptionToPlay(Player player) {
        int option;
        if (discardedCardsDeck.isEmpty()) {
            option = 1;
        } else {
            do {
                System.out.println("What do you want to do?");
                System.out.println("[1] Take a card of the deck.");
                System.out.println("[2] Discard one of your cards.");
                System.out.println("[3] Switch the card of the maze with one of your cards.");
                option = Utilidades.leerEntero("");
            } while (option < 1 || option > 3);
        }
        switch (option) {
            case 1 -> {
                Card card = deck.takeCard();
                System.out.println(card.toString());
                int option2;
                int aux = 2;
                do {
                    //Select an option2
                    System.out.println("What do you want to do with the card?");
                    System.out.println("[1] Discard the card.");
                    System.out.println("[2] Change it for one of your cards.");
                    if (card.getValue() > 10) {
                        System.out.println("[3] Use the power of the card.");
                        aux = 3;
                    }
                    option2 = Utilidades.leerEntero("");
                } while (option2 < 1 || option2 > aux);

                // Execute option2
                if (option2 == 1) {
                    discardedCardsDeck.add(card);
                } else if (option2 == 2) {
                    Card cardOfPlayer = player.swapCards(card);
                    discardedCardsDeck.add(cardOfPlayer);
                } else {
                    if (card.getValue() == 11) {
                        player.seeCard(player.selectCard());
                    } else if (card.getValue() == 12) {
                        Player oponent = selectOpponent(player);
                        int ownIndexCard = player.selectCard();
                        int exchangedIndexCard = oponent.selectCard();
                        player.switchCardWithOponent(oponent, ownIndexCard, exchangedIndexCard);

                    } else if (card.getValue() == 13) {
                        Player oponent = selectOpponent(player);
                        int ownIndexCard = player.selectCard();
                        int exchangedIndexCard = oponent.selectCard();
                        player.seeCard(ownIndexCard);
                        oponent.seeCard(exchangedIndexCard);

                        System.out.println("Do you want to switch the cards? [Yes/No]");
                        if (Utilidades.leerCadena().equals("Yes")) {
                            player.switchCardWithOponent(oponent, ownIndexCard, exchangedIndexCard);
                        }
                    }
                }
            }
            case 2 -> discardPlayerCard(player, player.getCard());
            case 3 -> {
                Card cardOfPlayer = player.swapCards(discardedCardsDeck.pop());
                discardedCardsDeck.add(cardOfPlayer);
            }
        }

    }

    /**
     * Allows a player to select an opponent to interact with
     *
     * @param player who is playing the turn
     * @return The player chosen as an opponent
     */
    public Player selectOpponent(Player player) {
        boolean continueGame = false;
        Player oponent = null;
        do {
            System.out.println("Which player you want to choose for switching cards?");
            try {
                oponent = players.get(Utilidades.leerEntero(""));
            } catch (Exception e) {
                System.out.println("You can´t choose that player!");
            }
            if (oponent == player) {
                System.out.println("You can´t choose yourself!");
            }
            if (oponent != null && oponent != player) {
                continueGame = true;
            }
        } while (!continueGame);
        return oponent;
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
        if (card.getValue() == discardedCardsDeck.lastElement().getValue()) {
            player.getCards().remove(card);
            discardedCardsDeck.add(card);
        } else {
            System.out.println("The card does not have the same value, you are penalized with 5 points.");
            player.setPoints(player.getPoints() + 5);
        }
    }


    // Getters and Setters
    public Deck getDeck() {
        return deck;
    }

    public List<Card> getDiscardedCardsDeck() {
        return discardedCardsDeck;
    }


}
