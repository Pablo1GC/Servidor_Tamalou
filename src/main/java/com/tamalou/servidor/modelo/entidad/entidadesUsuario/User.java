package com.tamalou.servidor.modelo.entidad.entidadesUsuario;

import jakarta.persistence.*;

@Entity
@Table(name = "user")
public class User {

    @Id
    @Column(name = "uid")
    private String id;

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String username;

    @Lob
    @Column(name = "image")
    private byte[] image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
