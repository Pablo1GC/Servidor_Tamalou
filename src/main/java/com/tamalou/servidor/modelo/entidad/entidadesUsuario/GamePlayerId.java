package com.tamalou.servidor.modelo.entidad.entidadesUsuario;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class GamePlayerId implements Serializable {

    public GamePlayerId() {
    }

    public GamePlayerId(Long gameId, String userId) {
        this.gameId = gameId;
        this.userId = userId;
    }

    @Column(name = "id_game")
    private Long gameId;

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