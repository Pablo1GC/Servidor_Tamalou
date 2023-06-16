package com.tamalou.servidor.modelo.entidad.entidadesPartida;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.JsonAdapter;
import com.tamalou.servidor.modelo.entidad.socketEntities.Package;
import com.tamalou.servidor.modelo.entidad.socketEntities.PackageReader;
import com.tamalou.servidor.modelo.entidad.socketEntities.PackageWriter;
import com.tamalou.servidor.socket.ImageToBase64AdapterFactory;
import com.tamalou.servidor.socket.Signal;
import jakarta.persistence.*;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
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
    @JsonAdapter(ImageToBase64AdapterFactory.class)
    private byte[] image;

    @Transient
    private int points;
    @Transient
    @JsonIgnore
    transient private List<Card> cards;
    @Transient
    @JsonIgnore
    transient public Socket socket;
    @Transient
    @JsonIgnore
    transient public PackageReader reader;
    @Transient
    @JsonIgnore
    transient public PackageWriter writter;

    @Transient
    transient private boolean isConnected;


    public Player() {
    }

    /**
     * Initializes the player with their name, 0 points, and initializes the array of cards they would have in their hand
     *
     * @param
     */
    public Player(Socket socket) throws IOException {
        this.socket = socket;
        this.reader = new PackageReader(new Scanner(socket.getInputStream()));
        this.writter = new PackageWriter(socket.getOutputStream());
        this.points = 0;
        this.cards = new ArrayList<>();
        this.isConnected = false;
    }



    // Getters and Setters


    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
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

    @JsonIgnore
    public boolean isConnected() {
        try {

            int previousTimeout = this.socket.getSoTimeout();
            this.socket.setSoTimeout(10_000);

            Package pack = this.sendPackageAndWaitForResponse(new Package(Signal.ASK_CONNECTED));

            if (pack == null)
                return false;

            this.socket.setSoTimeout(previousTimeout);

            if (pack.signal != Signal.YES)
                System.out.println();
            return isConnected = pack.signal == Signal.YES;
        } catch (SocketException e){
            throw new RuntimeException(e);
        }
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public synchronized Package sendPackageAndWaitForResponse(Package aPackage){
        this.writter.write(aPackage);
        return this.reader.readPackage();
    }
}