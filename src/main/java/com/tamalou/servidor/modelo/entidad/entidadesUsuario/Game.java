package com.tamalou.servidor.modelo.entidad.entidadesUsuario;

import jakarta.persistence.*;
/**
 * Represents a finished game entity in the application.
 *
 * This class is an entity mapped to the "game" table in the database.
 * It contains information about a game, such as its unique identifier,
 * number of rounds, and associated players.
 */
@Entity
@Table(name = "game")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_game")
    private Long id;

    @Column(name = "num_rounds")
    private Integer numRounds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumRounds() {
        return numRounds;
    }

    public void setNumRounds(Integer numRounds) {
        this.numRounds = numRounds;
    }
}

