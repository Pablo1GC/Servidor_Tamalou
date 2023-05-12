package com.tamalou.servidor.modelo.entidad.entidadesPartida;

import com.tamalou.servidor.socket.Communicator;
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
            for (Player p : playersList) {
                Communicator.sendCommunication(p, Signal.START_TURN);
                // The player's turn begins
                if (deck.checkEmptyDeck()) {
                    returnDiscartedCardsToDeck();
                }

                //The last card in the Maze is always shown
                showLastCardInDiscartedDeck();

                // If round is above 5, player can stand and end the round.
                if (actualTurn > 5) {
                    boolean stand = standRound(p);
                    if (stand) {
                        endRound = true;
                        for (Player p2 : playersList) {
                            Communicator.sendCommunication(p2, Signal.PLAYER_STANDS);
                        }
                        break;
                    }
                }

                // Player can choose what to do
                chooseOptionToPlay(p);

                System.out.println(p.getUid() + " your turn has ended.");
                // If any player runs out of cards, the round ends.
                if (p.getCards().size() == 0) {
                    endRound = true;
                    for (Player p2 : playersList) {
                        Communicator.sendCommunication(p2, Signal.PLAYER_CARDS_EMPTY, p.getUid());
                        Communicator.sendCommunication(p2, Signal.END_ROUND);
                    }
                } else {
                    for (Player p2 : playersList) {
                        Communicator.sendCommunication(p2, Signal.PLAYER_TURN_ENDED, p.getUid());
                    }
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
            Communicator.sendCommunication(p, Signal.SHOW_LAST_CARD_DISCARTED, discardedCardsDeck.lastElement());
        }
    }

    /**
     * Player can choose what to do in his turn.
     *
     * @param p Player choosing the option
     */
    public void chooseOptionToPlay(Player p) {
        int option;
        if (discardedCardsDeck.isEmpty()) {
            option = 1;
        } else {
            Communicator.sendCommunication(p, Signal.ASK_PLAYER_SELECT_PLAY);
            option = Communicator.receiveCommunication();
        }
        switch (option) {
            case 1 -> {
                Card card = deck.takeCard();
                for (Player p2 : playersList) {
                    Communicator.sendCommunication(p2, Signal.SHOW_LAST_CARD_DECK, card);
                }
                System.out.println(card.toString());

                Communicator.sendCommunication(p, Signal.ASK_PLAYER_SELECT_PLAY_2);

                int option2 = Communicator.receiveCommunication();
                // Execute option2
                if (option2 == Signal.PLAYER_DISCARDS_CARD) {
                    discardedCardsDeck.add(card);
                    for (Player p2 : playersList) {
                        if (!p2.equals(p))
                            Communicator.sendCommunication(p2, Signal.PLAYER_DISCARDS_CARD);
                    }
                } else if (option2 == Signal.PLAYER_SWITCH_CARD_DECK) {
                    Card cardOfPlayer = swapCards(p, card);
                    discardedCardsDeck.add(cardOfPlayer);
                    for (Player p2 : playersList) {
                        if (!p2.equals(p))
                            Communicator.sendCommunication(p2, Signal.PLAYER_SWITCH_CARD_DECK);
                    }
                } else if (option2 == Signal.PLAYER_USE_CARD_POWER) {
                    if (card.getValue() == 11) {
                        int index = PlayerselectCard(p) - -1;
                        Card cardAux = p.getCards().get(index);
                        seeCard(p, cardAux);
                        for (Player p2 : playersList) {
                            if (!p2.equals(p))
                                Communicator.sendCommunication(p2, Signal.PLAYER_SEES_CARD, index);
                        }
                    } else if (card.getValue() == 12) {
                        int ownIndexCard = PlayerselectCard(p);
                        // Select an oponent
                        Player oponent = selectOpponent(p);
                        // Now we select the oponent index card
                        int oponentIndexCard = PlayerselectCard(p);
                        switchCardWithOponent(p, oponent, ownIndexCard, oponentIndexCard);

                    } else if (card.getValue() == 13) {
                        int ownIndexCard = PlayerselectCard(p);
                        Card cardAux = p.getCards().get(ownIndexCard);
                        seeCard(p, cardAux);
                        // Select an oponent
                        Player oponent = selectOpponent(p);
                        // Now we select the oponent index card
                        int oponentIndexCard = PlayerselectCard(p);
                        cardAux = oponent.getCards().get(oponentIndexCard);
                        seeCard(p, cardAux);

                        // Ask player if he wants to switch the cards
                        Communicator.sendCommunication(p, Signal.ASK_PLAYER_SWITCH_CARD);
                        if (Communicator.receiveCommunication() == 1) {
                            switchCardWithOponent(p, oponent, ownIndexCard, oponentIndexCard);
                        }
                    }
                }
            }
            case 2 -> discardPlayerCard(p);
            case 3 -> {
                // Implement how to communicate to other players card index of card swapped
                Card cardOfPlayer = swapCards(p, discardedCardsDeck.pop());
                discardedCardsDeck.add(cardOfPlayer);
            }
        }

    }

    /**
     * Selects the card with which the player will interact
     *
     * @return Returns the index of the card selected by the player
     */
    public int PlayerselectCard(Player p) {
        Communicator.sendCommunication(p, Signal.ASK_PLAYER_SELECT_CARD);
        int indexSelected = Communicator.receiveCommunication();
        return indexSelected;
    }


    /**
     * Allows a player to select an opponent to interact with
     *
     * @param p who is playing the turn
     * @return The player chosen as an opponent
     */
    public Player selectOpponent(Player p) {
        Communicator.sendCommunication(p, Signal.ASK_PLAYER_SELECT_OPONENT);
        /**
         *  Signal to opopnent object
         */
        Player oponent = null;
        Communicator.receiveCommunication();
        return oponent;
    }


    /**
     * Checks if the card received as a parameter is equal to the last card
     * in the discard pile (of type Stack), and discards the card if it is.
     * Otherwise, the player is penalized with 5 points.
     *
     * @param p The player who will discard the card
     */
    public void discardPlayerCard(Player p) {
        Communicator.sendCommunication(p, Signal.ASK_PLAYER_SELECT_CARD);
        int index = Communicator.receiveCommunication();
        Card card = p.getCards().get(index);
        if (card.getValue() == discardedCardsDeck.lastElement().getValue()) {
            p.getCards().remove(card);
            discardedCardsDeck.add(card);
            for (Player p2 : playersList) {
                Communicator.sendCommunication(p2, Signal.PLAYER_ONE_CARD_LESS, p.getUid());
            }
        } else {
            System.out.println("The card does not have the same value, you are penalized with 5 points.");
            p.setPoints(p.getPoints() + 5);
            for (Player p2 : playersList) {
                Communicator.sendCommunication(p2, Signal.PLAYER_POINTS_PENALTY, p.getUid());
            }
        }
    }


    /**
     * Gives the option to the player to stand and end the round.
     *
     * @return Returns true if the option is "Yes", false if is "No"
     */
    public boolean standRound(Player p) {
        Communicator.sendCommunication(p, Signal.ASK_PLAYER_TO_STAND);
        System.out.println("Do you want to stand? [Yes/No]");
        // CHECK WITH @BRIAN
        int endTurn = Communicator.receiveCommunication();
        return endTurn == Signal.PLAYER_STANDS ? true : false;
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
    public void seeCard(Player p, Card card) {
        Communicator.sendCommunication(p, Signal.PLAYER_SEES_CARD, card);
    }

    /**
     * Allows the player to swap one of his cards with another card of the game.
     *
     * @param card
     */
    public Card swapCards(Player player, Card card) {
        int cardIndex = Communicator.receiveCommunication();
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
