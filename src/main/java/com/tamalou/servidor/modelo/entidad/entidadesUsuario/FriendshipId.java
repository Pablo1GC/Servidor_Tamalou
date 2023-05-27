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
    private Long senderId;

    @Column(name = "receiver")
    private Long receiverId;

    // Constructors, getters, setters, and overridden methods like equals and hashCode

    public FriendshipId() {
    }

    public FriendshipId(Long senderId, Long receiverId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    // Override equals and hashCode methods to properly compare FriendshipId objects

}
