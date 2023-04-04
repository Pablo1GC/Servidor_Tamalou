/**
 * Repository class for User entity.
 *
 * This class is responsible for performing CRUD (Create, Read, Update, Delete)
 * operations on User entities in the database.
 */
package com.tamalou.servidor.modelo.persistencia;

import com.tamalou.servidor.modelo.entidad.entidadesUsuario.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Saves a User entity to the database.
     *
     * @param user The User entity to be saved.
     */
    public void save(User user) {
        entityManager.persist(user);
    }

    /**
     * Retrieves a User entity from the database by its unique identifier (uid).
     *
     * @param uid The unique identifier (uid) of the User entity to be retrieved.
     * @return The User entity with the specified uid, or null if not found.
     */
    public User findById(String uid) {
        return entityManager.find(User.class, uid);
    }

    /**
     * Retrieves all User entities from the database.
     *
     * @return A list of all User entities in the database.
     */
    public List<User> findAll() {
        return entityManager.createQuery("SELECT u FROM User u", User.class)
                .getResultList();
    }

    /**
     * Updates a User entity in the database.
     *
     * @param user The User entity to be updated.
     */
    public void update(User user) {
        entityManager.merge(user);
    }

    /**
     * Deletes a User entity from the database.
     *
     * @param user The User entity to be deleted.
     */
    public void delete(User user) {
        entityManager.remove(entityManager.contains(user) ? user : entityManager.merge(user));
    }

    /**
     * Searches for User entities in the database by username, ignoring case.
     *
     * @param username The username to search for.
     * @return A list of User entities whose username contains the specified string, ignoring case.
     */
    public List<User> findByUsernameContainingIgnoreCase(String username) {
        return entityManager.createQuery("SELECT u FROM User u WHERE lower(u.username) LIKE lower(concat('%', :username, '%'))", User.class)
                .setParameter("username", username)
                .getResultList();
    }

}
