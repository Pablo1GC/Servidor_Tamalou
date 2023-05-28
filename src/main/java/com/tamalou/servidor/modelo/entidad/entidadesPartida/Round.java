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
        for (Player p : playersList) {
            for (int i = 0; i < 4; i++) {
                p.takeCard(deck.takeCard());
            }
        }

        // This loop won't end until one player stands or has discarted all his cards
        while (!endRound) {

            /*
            // Check if any player has disconnected from the game
            for (Player p : playersList){
                if (p.getSocket().isClosed()){
                    playersList.remove(p);
                    playersList.stream().filter((player -> player != p)).forEach((player -> {
                        player.writter.packAndWrite(Signal.PLAYER_DISCONNECTED, p.getUid());
                    }));
                }
            }
             */

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


                // Creating a list with players that contain only uid and points
                List<Player> tmpPlayersWithScore = new ArrayList<>(this.playersList.size());

                playersList.forEach((player -> {
                    Player addingPlayer = new Player();

                    addingPlayer.setUid(player.getUid());
                    addingPlayer.setPoints(player.getPoints());

                    tmpPlayersWithScore.add(addingPlayer);
                }));

                for (Player player : playersList){
                    player.writter.packAndWrite(Signal.PLAYERS_POINTS, tmpPlayersWithScore);
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
     * This method send´s all the cards from the discarded deck to the deck.
     * Shuffle´s the deck
     * Clear´s the discarded deck and take the last card of the deck to the discarded deck.
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
     * @param player Player choosing the option
     */
    public void chooseOptionToPlay(Player player) {
        PlayOption option;
        if (discardedCardsDeck.isEmpty()) {
            option = PlayOption.GRAB_CARD;
            player.writter.packAndWrite(Signal.DISCARDED_DECK_IS_EMPTY);

        } else {
            player.writter.packAndWrite(Signal.ASK_PLAYER_SELECT_PLAY);

            option = PlayOption.values()[player.reader.readPackage().data.getAsInt()];
        }
        switch (option) {
            case GRAB_CARD -> {
                Card card = deck.takeCard();
                for (Player p2 : playersList)
                    p2.writter.packAndWrite(Signal.SHOW_CARD_GRABBED, card);

                System.out.println(card.toString());

                player.writter.packAndWrite(Signal.ASK_PLAYER_CARD_OPTION, card);

                PlayOption optionForSelectedCard = PlayOption.values()[player.reader.readPackage().data.getAsInt()];
                // Execute optionForSelectedCard

                switch (optionForSelectedCard){
                    case DISCARD_CARD -> {
                        discardedCardsDeck.add(card);

                        for (Player p2 : playersList)
                            p2.writter.packAndWrite(Signal.PLAYER_DISCARDS_CARD, card);
                    }
                    case SWAP_CARD -> swapCardsAndMovePlayersCardToDiscardedDeck(player, card);
                    case CARD_POWER -> {
                        // select own card to see
                        if (card.getValue() == CardValue.J.value) {
                            int ownIndex = playerSelectCard(player);
                            seeCard(player, ownIndex);
                        } else if (card.getValue() == CardValue.Q.value) { // Swap card with opponent
                            int indexCard = playerSelectCard(player);
                            // Select an opponent
                            Player opponent = selectOpponent(player);
                            // Now we select the opponent index card
                            int opponentIndexCard = playerSelectCardOponent(player, opponent);
                            switchCardWithOpponent(player, opponent, indexCard, opponentIndexCard);

                            // tell all players they exchanged cards
                            for (Player p2 : playersList) {
                                p2.writter.packAndWrite(Signal.PLAYERS_SWITCHED_CARDS,
                                        new JsonField("player_uid", player.getUid()), new JsonField("p_card_index", indexCard),
                                        new JsonField("opponent_uid", opponent.getUid()), new JsonField("o_card_index", opponentIndexCard));
                            }
                        } else if (card.getValue() == CardValue.K.value) {
                            // Player can see both their selected card and the selected opponent card
                            int ownIndexCard = playerSelectCard(player);
                            seeCard(player, ownIndexCard);
                            // Select an opponent
                            Player opponent = selectOpponent(player);
                            // Now we select the opponent index card
                            int opponentIndexCard = playerSelectCardOponent(player, opponent);
                            seeOpponentCard(player, opponent, opponentIndexCard);

                            // Ask player if he wants to switch the cards
                            player.writter.packAndWrite(Signal.ASK_PLAYER_SWITCH_CARD);

                            if (player.reader.readPackage().data.getAsBoolean()) {
                                switchCardWithOpponent(player, opponent, ownIndexCard, opponentIndexCard);
                                playersList.stream().filter(p -> p != player).forEach(p -> {
                                    p.writter.packAndWrite(Signal.PLAYERS_SWITCHED_CARDS,
                                            new JsonField("player_uid", player.getUid()), new JsonField("p_card_index", ownIndexCard),
                                            new JsonField("opponent_uid", opponent.getUid()), new JsonField("o_card_index", opponentIndexCard));
                                });
                            }

                        }
                        discardedCardsDeck.add(card);
                        playersList.forEach((p) -> p.writter.packAndWrite(Signal.SHOW_LAST_CARD_DISCARDED, card));
                    }

                }
            }
            case DISCARD_CARD -> discardPlayerCard(player);
            case SWAP_CARD -> swapCardsAndMovePlayersCardToDiscardedDeck(player, discardedCardsDeck.pop());
        }
    }

    /**
     * Allows the player to select a card from an opponent
     *
     * @param player       who is playing the turn
     * @param opponent of the player
     * @return Returns the index of the card selected by the player
     */
    private int playerSelectCardOponent(Player player, Player opponent) {
        player.writter.packAndWrite(Signal.ASK_PLAYER_SELECT_OPONENT_CARD, opponent.getUid());
        int indexSelected = player.reader.readPackage().data.getAsInt();
        return indexSelected;
    }

    /**
     * Selects the card with which the player will interact
     *
     * @param player who is playing the turn
     * @return Returns the index of the card selected by the player
     */
    public int playerSelectCard(Player player) {
        player.writter.packAndWrite(Signal.ASK_PLAYER_SELECT_CARD);
        int indexSelected = player.reader.readPackage().data.getAsInt();
        return indexSelected;
    }


    /**
     * Allows a player to select an opponent to interact with
     *
     * @param player who is playing the turn
     * @return The player chosen as an opponent
     */
    public Player selectOpponent(Player player) {
        player.writter.packAndWrite(Signal.ASK_PLAYER_SELECT_OPONENT);
        String uidOponent = player.reader.readPackage().data.getAsString();
        Player opponent = playersList.stream().filter(p -> p.getUid().equals(uidOponent)).toList().get(0);
        return opponent;
    }


    /**
     * Checks if the card received as a parameter is equal to the last card
     * in the discard pile, and discards the card if it is.
     * Otherwise, the player is penalized with 5 points.
     *
     * @param player The player who will discard the card
     */
    public void discardPlayerCard(Player player) {
        player.writter.packAndWrite(Signal.ASK_PLAYER_SELECT_CARD);
        int index = player.reader.readPackage().data.getAsInt();
        Card card = player.getCards().get(index);
        if (card.getValue() == discardedCardsDeck.lastElement().getValue()) {
            player.getCards().remove(card);
            discardedCardsDeck.add(card);
            for (Player p2 : playersList) {
                p2.writter.packAndWrite(Signal.PLAYER_ONE_CARD_LESS,
                        new JsonField("player_uid", player.getUid()), new JsonField("card_index", index),
                        new JsonField("card", card));
            }
        } else {
            playersList.forEach(p -> p.writter.packAndWrite(Signal.SHOW_CARD_GRABBED,
                    new JsonField("card_index", index),
                    new JsonField("card", card)));

            System.out.println("The card does not have the same value, you are penalized with 5 points.");
            player.setPoints(player.getPoints() + 5);
            for (Player p2 : playersList) {
                p2.writter.packAndWrite(Signal.PLAYER_POINTS_PENALTY, player.getUid());
            }
        }
    }


    /**
     * Gives the option to the player to stand and end the round.
     *
     * @param player                  who is playing the turn
     * @return Returns true if the option is "Yes", false if is "No"
     */
    public boolean askPlayerToStand(Player player) {
        player.writter.packAndWrite(Signal.ASK_PLAYER_TO_STAND);
        System.out.println("Do you want to stand? [Yes/No]");

        return player.reader.readPackage().data.getAsBoolean();
    }

    /**
     * It is responsible for exchanging two cards with another player
     *
     * @param player                  who is playing the turn
     * @param opponent           is the player with whom you want to exchange the cards
     * @param ownIndexCard       is the index of the card that will be exchanged with the opponent passed by parameter
     * @param exchangedIndexCard is the index of the opponent's card that the player will receive
     */
    public void switchCardWithOpponent(Player player, Player opponent, int ownIndexCard, int exchangedIndexCard) {
        player.getCards().set(ownIndexCard, opponent.getCards().get(exchangedIndexCard));
        opponent.getCards().set(exchangedIndexCard, player.getCards().get(ownIndexCard));

    }

    /**
     * Allows the player to see a card
     *
     * @param player     who is playing the turn
     * @param index is the index of the card
     */
    public void seeCard(Player player, int index) {
        Card card = player.getCards().get(index);
        player.writter.packAndWrite(Signal.PLAYER_SEES_OWN_CARD, new JsonField("card", card), new JsonField("card_index", index));

        // send all players except the one with the turn, that "player" has seen his own card.
        playersList.stream().filter((p -> p != player)).forEach((p -> {
            p.writter.packAndWrite(Signal.OTHER_PLAYER_SEES_CARD,
                    new JsonField("player_uid", player.getUid()), new JsonField("card_index", index));
        }));

    }

    /**
     * Allows the player to see the card of an opponent
     *
     * @param player        who is playing the turn
     * @param opponent is the opponent choosen by the player
     * @param index    is the index of the opponent's card that the player wants to see
     */
    public void seeOpponentCard(Player player, Player opponent, int index) {
        Card card = opponent.getCards().get(index);
        player.writter.packAndWrite(Signal.PLAYER_SEES_OPONENT_CARD,
                new JsonField("player_uid", player.getUid()), new JsonField("opponent_uid", opponent.getUid()),
                new JsonField("card", card), new JsonField("index", index));

        // send all players except the one with the turn, that "player" has seen an opponent card.
        playersList.stream().filter((p -> p != player)).forEach((p -> {
            p.writter.packAndWrite(Signal.OTHER_PLAYER_SEES_OPONENT_CARD,
                    new JsonField("player_uid", player.getUid()),
                    new JsonField("opponent_uid", opponent.getUid()),
                    new JsonField("card_index", index));
        }));
    }

    /**
     * Allows the player to swap one of his cards with another card of the discarded deck.
     *
     * @param player    who is playing the turn
     * @param card is the card that the player wants to swap
     */
    public void swapCardsAndMovePlayersCardToDiscardedDeck(Player player, Card card) {
        player.writter.packAndWrite(Signal.ASK_PLAYER_SELECT_CARD);
        int cardIndex = player.reader.readPackage().data.getAsInt();
        Card myCard = player.getCards().get(cardIndex);
        player.getCards().set(cardIndex, card);

        // for each player that's not "player"
        for (Player p2 : playersList.stream().filter(p -> p != player).toList())
            p2.writter.packAndWrite(Signal.PLAYER_SWITCH_CARD_DECK, new JsonField("player_uid", player.getUid()), new JsonField("card_index", cardIndex));

        discardedCardsDeck.add(myCard);

        for (Player p2 : playersList)
            p2.writter.packAndWrite(Signal.PLAYER_DISCARDS_CARD, myCard);

    }

}
