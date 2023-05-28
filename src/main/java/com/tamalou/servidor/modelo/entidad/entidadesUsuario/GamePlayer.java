package com.tamalou.servidor.modelo.entidad.entidadesUsuario;

import com.tamalou.servidor.modelo.entidad.entidadesPartida.Player;
import jakarta.persistence.*;

/**
 * Represents a player in a game with a score and winner status.
 * This class is mapped to the "game_player" table in the database.
 */
@Entity
@Table(name = "game_player")
public class GamePlayer {

    @EmbeddedId
    private GamePlayerId id; // Embedded composite primary key for the GamePlayer entity

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("gameId") // Maps the "gameId" field of the composite primary key to the "id_game" column in the database
    @JoinColumn(name = "id_game") // Specifies the foreign key column name in the "game_player" table
    private Game game; // The game associated with the GamePlayer

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId") // Maps the "userId" field of the composite primary key to the "id_user" column in the database
    @JoinColumn(name = "id_user") // Specifies the foreign key column name in the "game_player" table
    private Player user; // The user associated with the GamePlayer

    @Column(name = "score")
    private Integer score; // The score of the player in the game

    @Column(name = "winner")
    private Boolean winner; // The winner status of the player in the game

    // Getters and Setters

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

    public Player getUser() {
        return user;
    }

    public void setUser(Player user) {
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
