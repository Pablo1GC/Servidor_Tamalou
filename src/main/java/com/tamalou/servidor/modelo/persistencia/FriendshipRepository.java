/**
 * Repository class for managing Friendship entities in the database.
 *
 * This class is responsible for performing CRUD (Create, Read, Update, Delete)
 * operations on Friendship entities, such as saving, retrieving, updating, and deleting
 * friendships in the database.
 */
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

    /**
     * Saves a Friendship entity to the database.
     *
     * @param friendship The Friendship entity to be saved.
     */
    public void save(Friendship friendship) {
        entityManager.persist(friendship);
    }

    /**
     * Retrieves a Friendship entity from the database by its unique identifier.
     *
     * @param id The unique identifier of the Friendship entity.
     * @return The retrieved Friendship entity, or null if not found.
     */
    public Friendship findById(Long id) {
        return entityManager.find(Friendship.class, id);
    }

    /**
     * Retrieves all Friendship entities from the database.
     *
     * @return A list of all Friendship entities in the database.
     */
    public List<Friendship> findAll() {
        return entityManager.createQuery("SELECT f FROM Friendship f", Friendship.class)
                .getResultList();
    }

    /**
     * Updates a Friendship entity in the database.
     *
     * @param friendship The Friendship entity to be updated.
     */
    public void update(Friendship friendship) {
        entityManager.merge(friendship);
    }

    /**
     * Deletes a Friendship entity from the database.
     *
     * @param friendship The Friendship entity to be deleted.
     */
    public void delete(Friendship friendship) {
        entityManager.remove(entityManager.contains(friendship) ? friendship : entityManager.merge(friendship));
    }

    /**
     * Retrieves all Friendship entities from the database for a given user ID.
     *
     * @param userId The user ID for which to retrieve the Friendship entities.
     * @return A list of Friendship entities associated with the given user ID.
     */
    public List<Friendship> findByUserId(Long userId) {
        return entityManager.createQuery("SELECT f FROM Friendship f WHERE f.sender = :userId", Friendship.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    /**
     * Retrieves a friendship for a given pair of user IDs.
     *
     * @param userId1 The first user ID.
     * @param userId2 The second user ID.
     * @return A friendship associated with the given pair of user IDs.
     */
    public Friendship findByUsersId(Long userId1, Long userId2) {
        return entityManager.createQuery("SELECT f FROM Friendship f WHERE " +
                        "f.sender.uid = :userId1 AND f.receiver.uid = :userId2 OR" +
                        "f.sender.uid = :userId2 AND f.receiver.uid = :userId1", Friendship.class)
                .setParameter("userId1", userId1)
                .setParameter("userId2", userId2)
                .getSingleResult();
    }

    /**
     * Retrieves all Friendship entities from the database for a given sender UID.
     *
     * @param senderUid The sender UID for which to retrieve the Friendship entities.
     * @return A list of Friendship entities associated with the given sender UID.
     */
    public List<Friendship> findBySenderUid(String senderUid) {
        return entityManager.createQuery("SELECT f FROM Friendship f WHERE f.sender.uid = :senderUid", Friendship.class)
                .setParameter("senderUid", senderUid)
                .getResultList();
    }

    /**
     * Retrieves all Friendship entities from the database for a given receiver UID.
     *
     * @param receiverUid The receiver UID for which to retrieve the Friendship entities.
     * @return A list of Friendship entities associated with the given receiver UID.
     */
    public List<Friendship> findByReceiverUid(String receiverUid) {
        return entityManager.createQuery("SELECT f FROM Friendship f WHERE f.receiver.uid = :receiverUid", Friendship.class)
                .setParameter("receiverUid", receiverUid)
                .getResultList();
    }
}
