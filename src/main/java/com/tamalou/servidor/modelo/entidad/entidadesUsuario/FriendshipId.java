package com.tamalou.servidor.modelo.entidad.entidadesUsuario;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * Represents the composite primary key for the Friendship entity.
 * 
 * This class is embedded in the Friendship entity and contains the sender and
 * receiver IDs.
 */
@Embeddable
public class FriendshipId implements Serializable {

    @Column(name = "sender")
    private String senderId;

    @Column(name = "receiver")
    private String receiverId;

    // Constructors, getters, setters, and overridden methods like equals and hashCode

    public FriendshipId() {
    }

    public FriendshipId(String senderId, String receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    // Override equals and hashCode methods to properly compare FriendshipId objects

}
