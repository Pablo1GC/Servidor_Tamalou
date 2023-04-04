package com.tamalou.servidor.modelo.persistencia;

import com.tamalou.servidor.modelo.entidad.entidadesUsuario.Friendship;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class FriendshipRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(Friendship friendship) {
        entityManager.persist(friendship);
    }

    public Friendship findById(Long id) {
        return entityManager.find(Friendship.class, id);
    }

    public List<Friendship> findAll() {
        return entityManager.createQuery("SELECT f FROM Friendship f", Friendship.class)
                .getResultList();
    }

    public void update(Friendship friendship) {
        entityManager.merge(friendship);
    }

    public void delete(Friendship friendship) {
        entityManager.remove(entityManager.contains(friendship) ? friendship : entityManager.merge(friendship));
    }

    public List<Friendship> findByUserId(Long userId) {
        return entityManager.createQuery("SELECT f FROM Friendship f WHERE f.sender = :userId", Friendship.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<Friendship> findBySenderUid(String senderUid) {
        return entityManager.createQuery("SELECT f FROM Friendship f WHERE f.sender.uid = :senderUid", Friendship.class)
                .setParameter("senderUid", senderUid)
                .getResultList();
    }

    public List<Friendship> findByReceiverUid(String receiverUid) {
        return entityManager.createQuery("SELECT f FROM Friendship f WHERE f.receiver.uid = :receiverUid", Friendship.class)
                .setParameter("receiverUid", receiverUid)
                .getResultList();
    }
}