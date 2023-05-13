package com.tamalou.servidor.modelo.entidad.entidadesUsuario;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Represents a user entity in the application.
 *
 * This class is an entity mapped to the "user" table in the database.
 * It contains information about a user, such as their unique identifier,
 * email, username, and profile image.
 */
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Entity
@Table(name = "user")
public class User {

    @Id
    @Column(name = "uid")
    private String uid;

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String username;

    @Lob
    @Column(name = "image")
    private byte[] image;

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", image=" + Arrays.toString(image) +
                '}';
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String id) {
        this.uid = id;
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
