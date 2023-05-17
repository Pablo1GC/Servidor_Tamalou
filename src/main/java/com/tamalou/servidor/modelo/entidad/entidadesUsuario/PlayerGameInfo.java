package com.tamalou.servidor.modelo.entidad.entidadesUsuario;

public class PlayerGameInfo {

    private int id;
    private String name;
    private  Integer score;
    private String played_on;

    public PlayerGameInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getPlayed_on() {
        return played_on;
    }

    public void setPlayed_on( String played_on) {
        this.played_on = played_on;
    }
}
