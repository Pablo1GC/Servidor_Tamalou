package com.tamalou.servidor.modelo.entidad.entidadesUsuario;

import jakarta.persistence.*;
/**
 * Represents a friendship entity in the application.
 *
 * This class is an entity mapped to the "friendship" table in the database.
 * It represents the friendship between two users of the app, where one user
 * sends a friend request and the other user can accept, reject, or the request
 * can be pending. It contains information about the unique identifier of the
 * friendship, the sender and receiver users, and the status of the friendship.
 */
@Entity
@Table(name = "friendship")
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender", referencedColumnName = "uid")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver", referencedColumnName = "uid")
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FriendshipStatus status;


    // Getters and setters, constructors, etc.

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public FriendshipStatus getStatus() {
        return status;
    }

    public void setStatus(FriendshipStatus status) {
        this.status = status;
    }
}

