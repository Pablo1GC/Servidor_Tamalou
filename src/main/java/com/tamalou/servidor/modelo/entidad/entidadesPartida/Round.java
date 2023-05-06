package com.tamalou.servidor.modelo.entidad.entidadesPartida;

import com.google.gson.Gson;
import com.tamalou.servidor.modelo.entidad.entidadesExtra.Utilidades;
import com.tamalou.servidor.socket.Signal;

import java.util.List;
import java.util.Stack;

public class Round {
    private Thread roundThread;
    private final List<Player> playersList;
    private final Deck deck;
    private final Stack<Card> discardedCardsDeck;
    private boolean endRound;
    private int actualTurn;

    /**
     * When an object is instantiated, a new round is started.
     *
     * @param playersList List of playerList in the round
     */
    public Round(List<Player> playersList) {
        this.playersList = playersList;
        this.deck = new Deck();
        this.discardedCardsDeck = new Stack<>();
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
        for (Player player : playersList) {
            for (int i = 0; i < 4; i++) {
                player.takeCard(deck.takeCard());
            }
        }

        while (!endRound) {

            // If someone has discarded all their cards, the round ends.
            for (Player player : playersList) {
                player.writter.packAndWrite(Signal.START_TURN);
                // The player's turn begins
                if (deck.checkEmptyDeck()) {
                    returnDiscartedCardsToDeck();
                }

                //The last card in the Maze is always shown
                showLastCardInDiscartedDeck();

                // If round is above 5, player can stand and end the round.
                if (actualTurn > 5) {
                    boolean stand = standRound(player);
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


    private void returnDiscartedCardsToDeck() {
        deck.setCardsDeck(discardedCardsDeck);
        deck.shuffleDeck();
        discardedCardsDeck.clear();
        discardedCardsDeck.add(deck.takeCard());
    }


    /**
     * Shows the last card in the discarded deck.
     */
    public void showLastCardInDiscartedDeck() {
        System.out.println(discardedCardsDeck.lastElement());
        for (Player p : playersList) {
            p.writter.packAndWrite(Signal.SHOW_LAST_CARD_DISCARTED_DECK, discardedCardsDeck.lastElement());
        }
    }

    /**
     * Player can choose what to do in his turn.
     *
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
                for (Player p : playersList) {
                    p.writter.packAndWrite(Signal.SHOW_LAST_CARD_DECK, card);
                }
                System.out.println(card.toString());
                int option2;
                int aux = 2;
                //Select an option2

                    /* THIS SHOULD GO IN THE CLIENT
                    System.out.println("What do you want to do with the card?");
                    System.out.println("[1] Discard the card.");
                    System.out.println("[2] Change it for one of your cards.");
                    if (card.getValue() > 10) {
                        System.out.println("[3] Use the power of the card.");
                        aux = 3;
                    }
                     */

                option2 = Integer.parseInt(player.reader.nextLine());

                // Execute option2
                if (option2 == Signal.PLAYER_DISCARDS_CARD) {
                    discardedCardsDeck.add(card);
                    for (Player p : playersList) {
                        if (!p.equals(player))
                            p.writter.packAndWrite(Signal.PLAYER_DISCARDS_CARD);
                    }
                } else if (option2 == Signal.PLAYER_SWITCH_CARD_DECK) {
                    Card cardOfPlayer = swapCards(player, card);
                    discardedCardsDeck.add(cardOfPlayer);
                } else if (option2 == Signal.PLAYER_USE_CARD_POWER) {
                    if (card.getValue() == 11) {
                        Card cardAux = player.getCards().get(player.PlayerselectCard());
                        seeCard(player, cardAux);
                    } else if (card.getValue() == 12) {
                        Player oponent = selectOpponent(player);
                        int ownIndexCard = player.PlayerselectCard();
                        int exchangedIndexCard = oponent.PlayerselectCard();
                        switchCardWithOponent(player, oponent, ownIndexCard, exchangedIndexCard);

                    } else if (card.getValue() == 13) {
                        Player oponent = selectOpponent(player);
                        int ownIndexCard = player.PlayerselectCard();
                        int exchangedIndexCard = oponent.PlayerselectCard();
                        Card cardAux = player.getCards().get(player.PlayerselectCard());
                        seeCard(player, cardAux);
                        // TO DO: SABER CÓMO INDICAR QUÉ JUGADOR ES EL OPONENTE
                        cardAux = oponent.getCards().get(oponent.PlayerselectCard());
                        seeCard(player, cardAux);

                        System.out.println("Do you want to switch the cards? [Yes/No]");
                        if (Utilidades.leerCadena().equals("Yes")) {
                            switchCardWithOponent(player, oponent, ownIndexCard, exchangedIndexCard);
                        }
                    }
                }
            }
            case 2 -> discardPlayerCard(player, player.getCard());
            case 3 -> {
                Card cardOfPlayer = swapCards(player, discardedCardsDeck.pop());
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
                oponent = playersList.get(Utilidades.leerEntero(""));
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


    /**
     * Gives the option to the player to stand and end the round.
     *
     * @return Returns true if the option is "Yes", false if is "No"
     */
    public boolean standRound(Player player) {
        player.writter.packAndWrite(Signal.ASK_PLAYER_TO_STAND);
        System.out.println("Do you want to stand? [Yes/No]");
        // CHECK WITH @BRIAN
        String endTurn = player.reader.nextLine();
        return endTurn.equals(Signal.PLAYER_STANDS) ? true : false;
    }

    /**
     * It is responsible for exchanging two cards with another player
     *
     * @param opponent           is the player with whom you want to exchange the cards
     * @param ownIndexCard       is the index of the card that will be exchanged with the opponent passed by parameter
     * @param exchangedIndexCard is the index of the opponent's card that the player will receive
     */
    public void switchCardWithOponent(Player player, Player opponent, int ownIndexCard, int exchangedIndexCard) {
        player.getCards().set(ownIndexCard, opponent.getCards().get(exchangedIndexCard));
        opponent.getCards().set(exchangedIndexCard, player.getCards().get(ownIndexCard));
    }

    /**
     * Allows the player to see a card
     */
    public void seeCard(Player player, Card card) {
        Gson codifier = new Gson();
        player.writter.packAndWrite(Signal.PLAYER_SEES_CARD, card);
    }

    /**
     * Allows the player to swap one of his cards with another card of the game.
     *
     * @param card
     */
    public Card swapCards(Player player, Card card) {
        int cardIndex = Integer.parseInt(player.reader.nextLine());
        Card myCard = player.getCards().get(cardIndex);
        player.getCards().set(cardIndex, card);
        return myCard;
    }

    // Getters and Setters
    public Deck getDeck() {
        return deck;
    }

    public List<Card> getDiscardedCardsDeck() {
        return discardedCardsDeck;
    }


}
