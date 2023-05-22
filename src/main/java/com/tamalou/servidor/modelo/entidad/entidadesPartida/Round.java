package com.tamalou.servidor.modelo.entidad.entidadesPartida;

import com.tamalou.servidor.modelo.entidad.socketEntities.JsonField;
import com.tamalou.servidor.socket.Signal;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Round {
    private Thread roundThread;
    private final List<Player> playersList;
    private final Deck deck;
    private final Stack<Card> discardedCardsDeck;
    private boolean endRound;
    private int actualTurn;

    private enum PlayOption {
        GRAB_CARD,
        DISCARD_CARD,
        SWAP_CARD,
        CARD_POWER
    }

    private enum CardValue {
        J(11),
        Q(12),
        K(13);



        private final int value;

        CardValue(final int value){
            this.value = value;
        }

    }

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
                p.writter.packAndWrite(Signal.START_TURN);

                // send all players except the one with the turn, that it's "p's" turn
                playersList.stream().filter((player -> player != p)).forEach((player -> {
                    player.writter.packAndWrite(Signal.OTHER_PLAYER_TURN, p.getUid());
                }));
                // The player's turn begins
                if (deck.checkEmptyDeck()) {
                    returnDiscardedCardsToDeck();
                }


                //The last card in the discarded deck is always shown
                if (!discardedCardsDeck.isEmpty())
                    showLastCardInDiscardedDeck();

                // If round is above 5, player can stand and end the round.
                if (actualTurn > 5) {
                    boolean stand = askPlayerToStand(p);
                    if (stand) {
                        endRound = true;
                        // send all players except the one with the turn, that "p" has stood.
                        playersList.stream().filter((player -> player != p)).forEach((player ->
                                player.writter.packAndWrite(Signal.OTHER_PLAYER_STANDS, p.getUid())));

                        p.writter.packAndWrite(Signal.END_ROUND);
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
                        p2.writter.packAndWrite(Signal.PLAYER_CARDS_EMPTY, p.getUid());
                        p2.writter.packAndWrite(Signal.END_ROUND);
                    }
                } else {
                    for (Player p2 : playersList) {
                        p2.writter.packAndWrite(Signal.PLAYER_TURN_ENDED, p.getUid());
                    }
                }


                // Creating a list with players that contain only uid and points
                List<Player> tmpPlayersWithScore = new ArrayList<>(this.playersList.size());

                playersList.forEach((player -> {
                    Player addingPlayer = new Player();

                    addingPlayer.setUid(player.getUid());
                    addingPlayer.setPoints(player.getPoints());

                    tmpPlayersWithScore.add(addingPlayer);
                }));

                for (Player player : playersList){
                    player.writter.packAndWrite(Signal.PLAYERS_POINTS,
                            new JsonField("players", tmpPlayersWithScore));
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
    public void showLastCardInDiscardedDeck() {
        System.out.println(discardedCardsDeck.lastElement());
        for (Player p : playersList) {
            p.writter.packAndWrite(Signal.SHOW_LAST_CARD_DISCARDED, discardedCardsDeck.lastElement());
        }
    }

    /**
     * Player can choose what to do in his turn.
     *
     * @param p Player choosing the option
     */
    public void chooseOptionToPlay(Player p) {
        PlayOption option;
        if (discardedCardsDeck.isEmpty()) {
            option = PlayOption.GRAB_CARD;
            p.writter.packAndWrite(Signal.DISCARDED_DECK_IS_EMPTY);

        } else {
            p.writter.packAndWrite(Signal.ASK_PLAYER_SELECT_PLAY);

            option = PlayOption.values()[p.reader.readPackage().data.getAsInt()];
        }
        switch (option) {
            case GRAB_CARD -> {
                Card card = deck.takeCard();
                for (Player p2 : playersList)
                    p2.writter.packAndWrite(Signal.SHOW_CARD_GRABBED, card);

                System.out.println(card.toString());

                p.writter.packAndWrite(Signal.ASK_PLAYER_CARD_OPTION, card);

                PlayOption optionForSelectedCard = PlayOption.values()[p.reader.readPackage().data.getAsInt()];
                // Execute optionForSelectedCard

                switch (optionForSelectedCard){
                    case DISCARD_CARD -> {
                        discardedCardsDeck.add(card);

                        for (Player p2 : playersList)
                            p2.writter.packAndWrite(Signal.PLAYER_DISCARDS_CARD, new JsonField("player_uid", p.getUid()));
                    }
                    case SWAP_CARD -> swapCards(p, card);
                    case CARD_POWER -> {
                        if (card.getValue() == CardValue.J.value) {
                            int index = playerSelectCard(p) - -1;
                            seeCard(p, index);
                        } else if (card.getValue() == CardValue.Q.value) {
                            int ownIndexCard = playerSelectCard(p);
                            // Select an oponent
                            Player oponent = selectOpponent(p);
                            // Now we select the oponent index card
                            int oponentIndexCard = playerSelectCardOponent(p, oponent);
                            switchCardWithOponent(p, oponent, ownIndexCard, oponentIndexCard);
                            for (Player p2 : playersList) {
                                p2.writter.packAndWrite(Signal.PLAYER_SWITCH_CARD_PLAYER,
                                        new JsonField("player_uid", p.getUid()), new JsonField("p_card_index", ownIndexCard),
                                        new JsonField("oponent_uid", oponent.getUid()), new JsonField("o_card_index", oponentIndexCard));
                            }
                        } else if (card.getValue() == CardValue.K.value) {
                            int ownIndexCard = playerSelectCard(p);
                            seeCard(p, ownIndexCard);
                            // Select an oponent
                            Player oponent = selectOpponent(p);
                            // Now we select the oponent index card
                            int oponentIndexCard = playerSelectCardOponent(p, oponent);
                            seeOponentCard(p, oponent, oponentIndexCard);

                            // Ask player if he wants to switch the cards
                            p.writter.packAndWrite(Signal.ASK_PLAYER_SWITCH_CARD);

                            if (p.reader.readPackage().data.getAsBoolean()) {
                                switchCardWithOponent(p, oponent, ownIndexCard, oponentIndexCard);
                                // send signal swap card
                            }

                        }
                        discardedCardsDeck.add(card);
                        // send signal discard card

                    }

                }
            }
            case DISCARD_CARD -> discardPlayerCard(p);
            case SWAP_CARD -> swapCards(p, discardedCardsDeck.pop());
        }
    }

    private int playerSelectCardOponent(Player p, Player oponent) {
        p.writter.packAndWrite(Signal.ASK_PLAYER_SELECT_OPONENT_CARD, oponent.getUid());
        int indexSelected = p.reader.readPackage().data.getAsInt();
        return indexSelected;
    }

    /**
     * Selects the card with which the player will interact
     *
     * @return Returns the index of the card selected by the player
     */
    public int playerSelectCard(Player p) {
        p.writter.packAndWrite(Signal.ASK_PLAYER_SELECT_CARD);
        int indexSelected = p.reader.readPackage().data.getAsInt();
        return indexSelected;
    }


    /**
     * Allows a player to select an opponent to interact with
     *
     * @param p who is playing the turn
     * @return The player chosen as an opponent
     */
    public Player selectOpponent(Player p) {
        p.writter.packAndWrite(Signal.ASK_PLAYER_SELECT_OPONENT);
        String uidOponent = p.reader.readPackage().data.getAsString();
        Player oponent = playersList.stream().filter(player -> player.getUid().equals(uidOponent)).toList().get(0);
        return oponent;
    }


    /**
     * Checks if the card received as a parameter is equal to the last card
     * in the discard pile, and discards the card if it is.
     * Otherwise, the player is penalized with 5 points.
     *
     * @param p The player who will discard the card
     */
    public void discardPlayerCard(Player p) {
        p.writter.packAndWrite(Signal.ASK_PLAYER_SELECT_CARD);
        int index = p.reader.readPackage().data.getAsInt();
        Card card = p.getCards().get(index);
        if (card.getValue() == discardedCardsDeck.lastElement().getValue()) {
            p.getCards().remove(card);
            discardedCardsDeck.add(card);
            for (Player p2 : playersList) {
                p2.writter.packAndWrite(Signal.PLAYER_ONE_CARD_LESS,
                        new JsonField("player_uid", p.getUid()), new JsonField("card_index", index),
                        new JsonField("card", card));
            }
        } else {
            p.writter.packAndWrite(Signal.PLAYER_SEES_OWN_CARD, new JsonField("card_index", index),
                    new JsonField("card", card));

            System.out.println("The card does not have the same value, you are penalized with 5 points.");
            p.setPoints(p.getPoints() + 5);
            for (Player p2 : playersList) {
                p2.writter.packAndWrite(Signal.PLAYER_POINTS_PENALTY, p.getUid());
            }
        }
    }


    /**
     * Gives the option to the player to stand and end the round.
     *
     * @return Returns true if the option is "Yes", false if is "No"
     */
    public boolean askPlayerToStand(Player p) {
        p.writter.packAndWrite(Signal.ASK_PLAYER_TO_STAND);
        System.out.println("Do you want to stand? [Yes/No]");

        return p.reader.readPackage().data.getAsBoolean();
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
    public void seeCard(Player p, int index) {
        Card card = p.getCards().get(index);
        p.writter.packAndWrite(Signal.PLAYER_SEES_OWN_CARD, new JsonField("card", card.toString()), new JsonField("card_index", index));

        // send all players except the one with the turn, that "p" has seen his own card.
        playersList.stream().filter((player -> player != p)).forEach((player -> {
            player.writter.packAndWrite(Signal.OTHER_PLAYER_SEES_CARD,
                    new JsonField("player_uid", p.getUid()), new JsonField("card_index", index));
        }));

    }

    public void seeOponentCard(Player p, Player oponent, int index) {
        Card card = oponent.getCards().get(index);
        p.writter.packAndWrite(Signal.PLAYER_SEES_OPONENT_CARD,
                new JsonField("player_uid", p.getUid()), new JsonField("oponent_uid", oponent.getUid()),
                new JsonField("card", card.toString()), new JsonField("index", index));

        // send all players except the one with the turn, that "p" has seen an oponent card.
        playersList.stream().filter((player -> player != p)).forEach((player -> {
            player.writter.packAndWrite(Signal.OTHER_PLAYER_SEES_OPONENT_CARD,
                    new JsonField("player_uid", p.getUid()), new JsonField("oponent_uid", oponent.getUid()), new JsonField("card_index", index));
        }));
    }

    /**
     * Allows the player to swap one of his cards with another card of the discarded deck.
     *
     * @param card The card to swap with the one the player chose
     */
    public void swapCards(Player p, Card card) {
        p.writter.packAndWrite(Signal.ASK_PLAYER_SELECT_CARD);
        int cardIndex = p.reader.readPackage().data.getAsInt();
        Card myCard = p.getCards().get(cardIndex);
        p.getCards().set(cardIndex, card);

        // for each player that's not "p"
        for (Player p2 : playersList.stream().filter(player -> player != p).toList())
            p2.writter.packAndWrite(Signal.PLAYER_SWITCH_CARD_DECK, new JsonField("player_uid", p.getUid()), new JsonField("card_index", cardIndex));

        discardedCardsDeck.add(myCard);
    }

    // Getters and Setters
    public Deck getDeck() {
        return deck;
    }

    public List<Card> getDiscardedCardsDeck() {
        return discardedCardsDeck;
    }


}
