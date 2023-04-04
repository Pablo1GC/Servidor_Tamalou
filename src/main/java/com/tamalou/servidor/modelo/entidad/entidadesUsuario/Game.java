package com.tamalou.servidor.modelo.entidad.entidadesUsuario;

import jakarta.persistence.*;

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

