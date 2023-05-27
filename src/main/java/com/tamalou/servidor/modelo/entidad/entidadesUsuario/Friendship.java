package com.tamalou.servidor.modelo.entidad.entidadesUsuario;

import com.tamalou.servidor.modelo.entidad.entidadesPartida.Player;
import jakarta.persistence.*;
/**
 * Represents a friendship entity in the application.
 *
 * This class is an entity mapped to the "friendship" table in the database.
 * It represents the friendship between two users of the app, where one Player
 * sends a friend request and the other Player can accept, reject, or the request
 * can be pending. It contains information about the unique identifier of the
 * friendship, the sender and receiver users, and the status of the friendship.
 */
@Entity
@Table(name = "friendship")
public class Friendship {

    @EmbeddedId
    private FriendshipId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("sender")
    @JoinColumn(name = "sender", referencedColumnName = "uid")
    private Player sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("receiver")
    @JoinColumn(name = "receiver", referencedColumnName = "uid")
    private Player receiver;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FriendshipStatus status;

    // Getters and setters, constructors, etc.

    public FriendshipId getId() {
        return id;
    }

    public void setId(FriendshipId id) {
        this.id = id;
    }

    public Player getSender() {
        return sender;
    }

    public void setSender(Player sender) {
        this.sender = sender;
    }

    public Player getReceiver() {
        return receiver;
    }

    public void setReceiver(Player receiver) {
        this.receiver = receiver;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }
}
