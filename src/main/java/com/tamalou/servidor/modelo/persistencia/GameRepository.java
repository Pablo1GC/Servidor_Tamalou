/**
 * Repository class for managing Game entities in the database.
 *
 * This class is responsible for performing CRUD (Create, Read, Update, Delete)
 * operations on Game entities, such as saving, retrieving, updating, and deleting
 * games in the database.
 */
package com.tamalou.servidor.modelo.persistencia;

import com.tamalou.servidor.modelo.entidad.entidadesUsuario.Game;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class GameRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Saves a Game entity to the database.
     *
     * @param game The Game entity to be saved.
     */
    public void save(Game game) {
        entityManager.persist(game);
    }

    /**
     * Retrieves a Game entity from the database by its unique identifier.
     *
     * @param id The unique identifier of the Game entity.
     * @return The retrieved Game entity, or null if not found.
     */
    public Game findById(long id) {
        return entityManager.find(Game.class, id);
    }

    /**
     * Retrieves all Game entities from the database.
     *
     * @return A list of all Game entities in the database.
     */
    public List<Game> findAll() {
        return entityManager.createQuery("SELECT g FROM Game g", Game.class)
                .getResultList();
    }

    /**
     * Updates a Game entity in the database.
     *
     * @param game The Game entity to be updated.
     */
    public void update(Game game) {
        entityManager.merge(game);
    }

    /**
     * Deletes a Game entity from the database.
     *
     * @param game The Game entity to be deleted.
     */
    public void delete(Game game) {
        entityManager.remove(entityManager.contains(game) ? game : entityManager.merge(game));
    }
}
