package com.tamalou.servidor.modelo.entidad.entidadesUsuario;

import jakarta.persistence.*;

@Entity
@Table(name = "game_player")
public class GamePlayer {

    @EmbeddedId
    private GamePlayerId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("gameId")
    @JoinColumn(name = "id_game")
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name = "score")
    private Integer score;

    @Column(name = "winner")
    private Boolean winner;

    // Getters and setters, constructors, etc.


    public GamePlayerId getId() {
        return id;
    }

    public void setId(GamePlayerId id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Boolean getWinner() {
        return winner;
    }

    public void setWinner(Boolean winner) {
        this.winner = winner;
    }
}

