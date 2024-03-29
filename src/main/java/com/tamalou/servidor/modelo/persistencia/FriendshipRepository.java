/**
 * Repository class for managing Friendship entities in the database.
 *
 * This class is responsible for performing CRUD (Create, Read, Update, Delete)
 * operations on Friendship entities, such as saving, retrieving, updating, and deleting
 * friendships in the database.
 */
package com.tamalou.servidor.modelo.persistencia;

import com.tamalou.servidor.modelo.entidad.entidadesPartida.Player;
import com.tamalou.servidor.modelo.entidad.entidadesUsuario.Friendship;
import com.tamalou.servidor.modelo.entidad.entidadesUsuario.FriendshipId;
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
        entityManager.merge(friendship);
    }

    /**
     * Retrieves a Friendship entity from the database by its unique identifier.
     *
     * @param id The unique identifier of the Friendship entity.
     * @return The retrieved Friendship entity, or null if not found.
     */
    public Friendship findById(FriendshipId id) {
        return entityManager.find(Friendship.class, id);
    }

    /**
     *
     * Retrieves all pending friendship requests where the user is the receiver.
     * @param userUid the UID of the user who is the receiver of the friendship requests
     * @return a list of Friendship objects representing pending friendship requests
     */
    public List<Friendship> findPendingFriendshipRequests(Player userUid) {
        String query = "SELECT f FROM Friendship f WHERE f.receiver = :userUid AND f.status = 'PENDING'";
        return entityManager.createQuery(query, Friendship.class)
                .setParameter("userUid", userUid)
                .getResultList();
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
     * Retrieves all Friendship entities from the database for a given sender or receiver UID.
     *
     * @param uid The UID (sender or receiver) for which to retrieve the Friendship entities.
     * @return A list of Friendship entities associated with the given UID.
     */
    public List<Friendship> findByUserId(String uid) {
        return entityManager.createQuery("SELECT f FROM Friendship f WHERE " +
                                        "f.sender.uid = :uid OR f.receiver.uid = :uid", Friendship.class)
                .setParameter("uid", uid)
                .getResultList();
    }

    /**
     * Retrieves a friendship for a given pair of Player IDs.
     *
     * @param userId1 The first Player ID.
     * @param userId2 The second Player ID.
     * @return A friendship associated with the given pair of Player IDs.
     */
    public Friendship findByUsersId(String userId1, String userId2) {
        List<Friendship> friendships =  entityManager.createQuery("SELECT f FROM Friendship f WHERE " +
                        "(f.sender.uid = :userId1 AND f.receiver.uid = :userId2) OR " +
                        "(f.sender.uid = :userId2 AND f.receiver.uid = :userId1)", Friendship.class)
                .setParameter("userId1", userId1)
                .setParameter("userId2", userId2)
                .getResultList();

        if(friendships.isEmpty())
            return null;

        return friendships.get(0);
    }

    /**
     * Retrieves all Friendship entities from the database for a given user UID.
     *
     * @param userUid The sender UID for which to retrieve the Friendship entities.
     * @return A list of Friendship entities associated with the given user UID.
     */
    public List<Friendship> findByUserUid(String userUid) {
        return entityManager.createQuery("SELECT f FROM Friendship f WHERE f.sender.uid = :user_uid or " +
                        "f.receiver.uid = :user_uid", Friendship.class)
                .setParameter("user_uid", userUid)
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
