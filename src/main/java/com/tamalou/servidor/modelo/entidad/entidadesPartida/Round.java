package com.tamalou.servidor.modelo.entidad.entidadesPartida;

import com.tamalou.servidor.modelo.entidad.entidadesExtra.Utilidades;

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
     *
     * @param players
     */
    public Round(List<Player> players) {
        this.players = players;
        this.deck = new Deck();
        this.maze = new Stack<>();
        this.actualTurn = 0;
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

                //The last card in the Maze is always show
                showLastCardInMaze();

                // If round is above 5, player can stand and end the round.
                if (actualTurn > 5) {
                    boolean stand = player.standRound();
                    if (stand) {
                        endRound = true;
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

    /**
     * Shows the last card in the maze.
     */
    public void showLastCardInMaze() {
        if (maze.size() == 0) {
            System.out.println("The maze is empty.");
        } else {
            System.out.println(maze.lastElement());
        }
    }

    /**
     * Player can choose what to do in his turn.
     *
     * @param player
     */
    public void chooseOptionToPlay(Player player) {
        System.out.println("What do you want to do?");
        System.out.println("[1] Take a card of the deck.");
        System.out.println("[2] Discard one of your cards.");
        System.out.println("[3] Switch the card of the maze with one of your cards.");

        int option;

        switch (option = Utilidades.leerEntero("")) {
            case 1:
                Card card = deck.takeCard();
                card.toString();

                //Select an option2
                System.out.println("What do you want to do with the card?");
                System.out.println("[1] Discard the card.");
                System.out.println("[2] Change it for one of your cards.");
                if (card.getValue() > 10) {
                    System.out.println("[3] Use the power of the card.");
                }
                int option2 = Utilidades.leerEntero("");

                // Execute option2
                if (option2 == 1) {
                    maze.add(card);
                } else if (option2 == 2) {
                    Card cardOfPlayer = player.swapCards(card);
                    maze.add(cardOfPlayer);
                } else if (option == 3) {
                    if (card.getValue() == 11) {
                        player.seeCard(player.selectCard());
                    } else if (card.getValue() == 12) {
                        Player oponent = selectOponent();
                        int ownIndexCard = player.selectCard();
                        int exchangedIndexCard = oponent.selectCard();
                        player.switchCardWithOponent(oponent, ownIndexCard, exchangedIndexCard);

                    } else if (card.getValue() == 13) {
                        Player oponent = selectOponent();
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
                break;

            case 2:
                discardPlayerCard(player, player.getCard());
                break;

            case 3:
                Card cardOfPlayer = player.swapCards(maze.pop());
                maze.add(cardOfPlayer);

        }

    }

    /**
     * Allows a player to select an oponent to interact with
     *
     * @return The player choosen as an oponent
     */
    public Player selectOponent() {
        return players.get(Utilidades.leerEntero("Which player you want to choose for switching cards?"));
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
            System.out.println("The card does not have the same value, you are penalized with 5 points.");
            player.setPoints(player.getPoints() + 5);
        }
    }


    // Getters and Setters
    public Deck getDeck() {
        return deck;
    }

    public List<Card> getMaze() {
        return maze;
    }


}
