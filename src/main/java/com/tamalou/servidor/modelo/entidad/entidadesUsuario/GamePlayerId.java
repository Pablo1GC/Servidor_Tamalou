package com.tamalou.servidor.modelo.entidad.entidadesUsuario;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

/**
 * Represents the composite primary key for the GamePlayer entity in the application.
 *
 * This class is used as an embedded key in the GamePlayer entity to uniquely identify
 * a game player in the database. It consists of two fields, gameId and userId, which
 * together form the composite primary key for the GamePlayer entity.
 */
@Embeddable
public class GamePlayerId implements Serializable {

    /**
     * Default constructor for the GamePlayerId class.
     */
    public GamePlayerId() {
    }

    /**
     * Constructor for the GamePlayerId class.
     *
     * @param gameId The game ID.
     * @param userId The user ID.
     */
    public GamePlayerId(long gameId, String userId) {
        this.gameId = gameId;
        this.userId = userId;
    }

    @Column(name = "id_game")
    private long gameId;

    @Column(name = "id_user")
    private String userId;

    // Getters and setters, constructors, etc.

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}