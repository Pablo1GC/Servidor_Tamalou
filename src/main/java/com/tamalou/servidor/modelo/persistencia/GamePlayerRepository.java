package com.tamalou.servidor.modelo.persistencia;

import com.tamalou.servidor.modelo.entidad.entidadesUsuario.GamePlayer;
import com.tamalou.servidor.modelo.entidad.entidadesUsuario.GamePlayerId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class GamePlayerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Saves a GamePlayer entity to the database.
     *
     * @param gamePlayer The GamePlayer entity to be saved.
     */
    public void save(GamePlayer gamePlayer) {
        entityManager.merge(gamePlayer);
    }

    /**
     * Retrieves a GamePlayer entity from the database by its composite identifier.
     *
     * @param gamePlayerId The composite identifier of the GamePlayer entity.
     * @return The retrieved GamePlayer entity, or null if not found.
     */
    public GamePlayer findById(GamePlayerId gamePlayerId) {
        String query = "SELECT gp FROM GamePlayer gp WHERE gp.id.gameId = :gameId AND gp.id.userId = :userId";
        TypedQuery<GamePlayer> queryResult = entityManager.createQuery(query, GamePlayer.class);
        queryResult.setParameter("gameId", gamePlayerId.getGameId());
        queryResult.setParameter("userId", gamePlayerId.getUserId());
        return queryResult.getSingleResult();
    }

    /**
     * Retrieves all GamePlayer entities from the database.
     *
     * @return A list of all GamePlayer entities in the database.
     */
    public List<GamePlayer> findAll() {
        return entityManager.createQuery("SELECT gp FROM GamePlayer gp", GamePlayer.class)
                .getResultList();
    }

    /**
     * Updates a GamePlayer entity in the database.
     *
     * @param gamePlayer The GamePlayer entity to be updated.
     */
    public void update(GamePlayer gamePlayer) {
        entityManager.merge(gamePlayer);
    }

    /**
     * Deletes a GamePlayer entity from the database.
     *
     * @param gamePlayer The GamePlayer entity to be deleted.
     */
    public void delete(GamePlayer gamePlayer) {
        entityManager.remove(entityManager.contains(gamePlayer) ? gamePlayer : entityManager.merge(gamePlayer));
    }

    /**
     * Deletes a GamePlayer entity from the database by its composite identifier.
     *
     * @param gamePlayerId The composite identifier of the GamePlayer entity to be deleted.
     */
    public void deleteById(GamePlayerId gamePlayerId) {
        GamePlayer gamePlayer = findById(gamePlayerId);
        if (gamePlayer != null) {
            entityManager.remove(gamePlayer);
        }
    }

    /**
     * Retrieves a list of GamePlayer objects based on the given game ID.
     *
     * @param gameId The ID of the game.
     * @return A List of GamePlayer objects that match the given game ID.
     */
    /**
     * Retrieves a list of GamePlayer entities from the database by the given gameId.
     *
     * @param gameId The gameId to be used for retrieving GamePlayer entities.
     * @return A list of GamePlayer entities with the given gameId.
     */
    public List<GamePlayer> findByGameId(long gameId) {
        String query = "SELECT gp FROM GamePlayer gp WHERE gp.id.gameId = :gameId";
        TypedQuery<GamePlayer> queryResult = entityManager.createQuery(query, GamePlayer.class);
        queryResult.setParameter("gameId", gameId);
        return queryResult.getResultList();
    }


}

