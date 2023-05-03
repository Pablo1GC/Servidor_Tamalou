package com.tamalou.servidor.modelo.entidad.entidadesPartida;

import com.tamalou.servidor.modelo.entidad.entidadesExtra.Utilidades;
import jakarta.persistence.*;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Represents a user entity in the application.
 *
 * This class is an entity mapped to the "user" table in the database.
 * It contains information about a user, such as their unique identifier,
 * email, username, and profile image.
 * Also is used to manage the player in the game.
 */
@Entity
@Table(name = "user")
public class Player {
    @Id
    @Column(name = "uid")
    private String uid;
    @Column(name = "email")
    private String email;
    @Column(name = "username")
    private String username;
    @Lob
    @Column(name = "image")
    private byte[] image;

    @Transient
    public String name;
    @Transient
    private int points;
    @Transient
    private List<Card> cards;
    @Transient
    private boolean endTurn;
    @Transient
    public Socket socket;
    @Transient
    public Scanner reader;
    @Transient
    public PrintStream writter;


    public Player() {
    }

    /**
     * Initializes the player with their name, 0 points, and initializes the array of cards they would have in their hand
     *
     * @param
     */
    public Player(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new Scanner(socket.getInputStream());
        this.writter = new PrintStream(socket.getOutputStream());
        this.name = name;
        this.points = 0;
        this.cards = new ArrayList<>();
        this.endTurn = false;
    }




    /**
     * Selects the card with which the player will interact
     *
     * @return Returns the index of the card selected by the player
     */
    public int PlayerselectCard() {
        int numberOfCards = getCards().size();
        int indexSelected;
        do {
            System.out.println(name + " has " + numberOfCards + ", Which card you want to choose?");
            indexSelected = Utilidades.leerEntero("") - 1;
        } while (indexSelected < 0 || indexSelected > numberOfCards - 1);
        return indexSelected;
    }

    // Getters and Setters
    public Card getCard() {
        return cards.get(PlayerselectCard());
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String id) {
        this.uid = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

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