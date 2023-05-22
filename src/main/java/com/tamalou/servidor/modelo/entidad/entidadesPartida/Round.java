package com.tamalou.servidor.modelo.entidad.entidadesPartida;

import com.tamalou.servidor.modelo.entidad.socketEntities.JsonField;
import com.tamalou.servidor.socket.Signal;

import java.util.List;
import java.util.Stack;

public class Round {
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

        CardValue(final int value) {
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
     */
    public void playRound() {
        deck.shuffleDeck();
        for (Player player : playersList) {
            for (int i = 0; i < 4; i++) {
                player.takeCard(deck.takeCard());
            }
        }

        // This loop won't end until one player stands or has discarted all his cards
        while (!endRound) {

            // Check if any player has disconnected from the game
            for (Player p : playersList){
                if (p.getSocket().isClosed()){
                    playersList.remove(p);
                    playersList.stream().filter((player -> player != p)).forEach((player -> {
                        player.writter.packAndWrite(Signal.PLAYER_DISCONNECTED, p.getUid());
                    }));
                }
            }

            // The player's turn begins
            for (Player p : playersList) {
                p.writter.packAndWrite(Signal.START_TURN);

                // send all players except the one with the turn, that it's "p's" turn
                playersList.stream().filter((player -> player != p)).forEach((player -> {
                    player.writter.packAndWrite(Signal.OTHER_PLAYER_TURN, p.getUid());
                }));

                if (deck.checkEmptyDeck()) {
                    for (Player p2 : playersList) {
                        p2.writter.packAndWrite(Signal.DECK_IS_EMPTY);
                    }
                    returnDiscardedCardsToDeck();
                }


                //The last card in the discarded deck is always shown
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

                // Call method that allows the player to interact with the game
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

            }
            actualTurn++;
        }
        for(Player p : playersList){
            for (Card card: p.getCards()){
                int points = 0;
                points += card.getValue();
                p.setPoints(points + p.getPoints());
            }
        }

    }

    /**
     * This method send´s all the cards from the discarted deck to the deck.
     * Shuffle´s the deck
     * Clear´s the discarted deck and take the last card of the deck to the discarted deck.
     */
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
        } else {
            p.writter.packAndWrite(Signal.ASK_PLAYER_SELECT_PLAY);

            option = PlayOption.values()[p.reader.readPackage().data.getAsInt()];
        }
        switch (option) {
            case GRAB_CARD -> {
                Card card = deck.takeCard();
                for (Player p2 : playersList)
                    p2.writter.packAndWrite(Signal.SHOW_LAST_CARD_DECK, card.toString());

                System.out.println(card.toString());

                p.writter.packAndWrite(Signal.ASK_PLAYER_CARD_OPTION, card.getValue());

                PlayOption optionForSelectedCard = PlayOption.values()[p.reader.readPackage().data.getAsInt()];
                // Execute optionForSelectedCard
                if (optionForSelectedCard == PlayOption.DISCARD_CARD) { // discard
                    discardedCardsDeck.add(card);
                    for (Player p2 : playersList) {
                        p2.writter.packAndWrite(Signal.PLAYER_DISCARDS_CARD, card.toString());
                    }
                } else if (optionForSelectedCard == PlayOption.SWAP_CARD) { // swap with one of my cards
                    swapCards(p, card);
                } else if (optionForSelectedCard == PlayOption.CARD_POWER) { // use card power
                    if (card.getValue() == CardValue.J.value) {
                        int index = playerSelectCard(p);
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
                            for (Player p2 : playersList) {
                                p2.writter.packAndWrite(Signal.PLAYER_SWITCH_CARD_PLAYER,
                                        new JsonField("player_uid", p.getUid()), new JsonField("p_card_index", ownIndexCard),
                                        new JsonField("oponent_uid", oponent.getUid()), new JsonField("o_card_index", oponentIndexCard));

                            }
                        }

                    }
                    discardedCardsDeck.add(card);
                }
            }
            case DISCARD_CARD -> discardPlayerCard(p);
            case SWAP_CARD -> swapCards(p, discardedCardsDeck.pop());
        }
    }

    /**
     * Allows the player to select a card from an oponent
     *
     * @param p       who is playing the turn
     * @param oponent of the player
     * @return Returns the index of the card selected by the player
     */
    private int playerSelectCardOponent(Player p, Player oponent) {
        p.writter.packAndWrite(Signal.ASK_PLAYER_SELECT_OPONENT_CARD, oponent.getUid());
        int indexSelected = p.reader.readPackage().data.getAsInt();
        return indexSelected;
    }

    /**
     * Selects the card with which the player will interact
     *
     * @param p who is playing the turn
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
                        new JsonField("card", card.toString()));
            }
        } else {
            p.writter.packAndWrite(Signal.PLAYER_SEES_OWN_CARD, new JsonField("card_index", index),
                    new JsonField("card", card.toString()));

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
     * @param p                  who is playing the turn
     * @param opponent           is the player with whom you want to exchange the cards
     * @param ownIndexCard       is the index of the card that will be exchanged with the opponent passed by parameter
     * @param exchangedIndexCard is the index of the opponent's card that the player will receive
     */
    public void switchCardWithOponent(Player p, Player opponent, int ownIndexCard, int exchangedIndexCard) {
        p.getCards().set(ownIndexCard, opponent.getCards().get(exchangedIndexCard));
        opponent.getCards().set(exchangedIndexCard, p.getCards().get(ownIndexCard));

    }

    /**
     * Allows the player to see a card
     *
     * @param p     who is playing the turn
     * @param index is the index of the card
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

    /**
     * Allows the player to see the card of an oponent
     *
     * @param p        who is playing the turn
     * @param opponent is the oponent choosen by the player
     * @param index    is the index of the opponent's card that the player wants to see
     */
    public void seeOponentCard(Player p, Player opponent, int index) {
        Card card = opponent.getCards().get(index);
        p.writter.packAndWrite(Signal.PLAYER_SEES_OPONENT_CARD,
                new JsonField("player_uid", p.getUid()), new JsonField("oponent_uid", opponent.getUid()),
                new JsonField("card", card.toString()), new JsonField("index", index));

        // send all players except the one with the turn, that "p" has seen an oponent card.
        playersList.stream().filter((player -> player != p)).forEach((player -> {
            player.writter.packAndWrite(Signal.OTHER_PLAYER_SEES_OPONENT_CARD,
                    new JsonField("player_uid", p.getUid()), new JsonField("oponent_uid", opponent.getUid()), new JsonField("card_index", index));
        }));
    }

    /**
     * Allows the player to swap one of his cards with another card of the game.
     *
     * @param p    who is playing the turn
     * @param card is the card that the player wants to swap
     */
    public void swapCards(Player p, Card card) {
        p.writter.packAndWrite(Signal.ASK_PLAYER_SELECT_CARD);
        int cardIndex = p.reader.readPackage().data.getAsInt();
        Card myCard = p.getCards().get(cardIndex);
        p.getCards().set(cardIndex, card);
        for (Player p2 : playersList)
            p2.writter.packAndWrite(Signal.PLAYER_SWITCH_CARD_DECK, new JsonField("player_uid", p.getUid()), new JsonField("card_index", cardIndex));

        discardedCardsDeck.add(myCard);
    }

}
