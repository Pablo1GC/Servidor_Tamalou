package com.tamalou.servidor.modelo.entidad.entidadesUsuario;

import java.io.Serializable;

/**
 * Enum representing the status of a friendship between two users in the app.
 */
public enum FriendshipStatus implements Serializable {

    /**
     * Status indicating that the friend request is pending and has not been responded to yet.
     */
    PENDING,

    /**
     * Status indicating that the friend request has been accepted by the recipient.
     */
    ACCEPTED,

    /**
     * Status indicating that the friend request has been rejected by the recipient.
     */
    REJECTED;
}

