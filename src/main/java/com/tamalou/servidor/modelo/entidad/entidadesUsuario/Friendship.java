package com.tamalou.servidor.modelo.entidad.entidadesUsuario;

import jakarta.persistence.*;

@Entity
@Table(name = "friendship")
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender", referencedColumnName = "uid")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver", referencedColumnName = "uid")
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FriendshipStatus status;

    // Getters and setters, constructors, etc.
}

