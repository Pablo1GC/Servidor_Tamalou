/**
 * Repository class for User entity.*
 * This class is responsible for performing CRUD (Create, Read, Update, Delete)
 * operations on User entities in the database.
 */
package com.tamalou.servidor.modelo.persistencia;

import com.tamalou.servidor.modelo.entidad.entidadesUsuario.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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

    /**
     * Retrieves the total number of games played by a player from the database.
     *
     * @param playerUid The unique identifier (uid) of the player to retrieve the total games played for.
     * @return The total number of games played by the player.
     */
    @Procedure(name = "get_total_games_played")
    public long getTotalGamesPlayed(@Param("player_uid") String playerUid) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("get_total_games_played");
        query.registerStoredProcedureParameter("player_uid", String.class, ParameterMode.IN);
        query.setParameter("player_uid", playerUid);
        return (long) query.getSingleResult();
    }

    /**
     * Retrieves the average score of a player from the database.
     *
     * @param playerUid The unique identifier (uid) of the player to retrieve the average score for.
     * @return The average score of the player.
     */
    @Procedure(name = "get_average_score")

    public BigDecimal getAverageScore(@Param("player_uid") String playerUid) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("get_average_score");
        query.registerStoredProcedureParameter("player_uid", String.class, ParameterMode.IN);
        query.setParameter("player_uid", playerUid);
        return (BigDecimal) query.getSingleResult();
    }

    /**
     * Retrieves the total number of games won by a player from the database.
     *
     * @param playerUid The unique identifier (uid) of the player to retrieve the total games won for.
     * @return The total number of games won by the player.
     */
    @Procedure(name = "get_games_won")
    public long getGamesWon(@Param("player_uid") String playerUid) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("get_games_won");
        query.registerStoredProcedureParameter("player_uid", String.class, ParameterMode.IN);
        query.setParameter("player_uid", playerUid);
        return (long) query.getSingleResult();
    }

    /**
     * Retrieves the total number of games lost by a player from the database.
     *
     * @param playerUid The unique identifier (uid) of the player to retrieve the total games lost for.
     * @return The total number of games lost by the player.
     */
    @Procedure(name = "get_games_lost")
    public long getGamesLost(@Param("player_uid") String playerUid) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("get_games_lost");
        query.registerStoredProcedureParameter("player_uid", String.class, ParameterMode.IN);
        query.setParameter("player_uid", playerUid);
        return (long) query.getSingleResult();
    }

    /**
     * Retrieves the average play time of a player from the database.
     *
     * @param playerUid The unique identifier (uid) of the player to retrieve the average play time for.
     * @return The average play time of the player.
     */
    @Procedure(name = "get_average_play_time")
    public BigDecimal getAveragePlayTime(@Param("player_uid") String playerUid) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("get_average_play_time");
        query.registerStoredProcedureParameter("player_uid", String.class, ParameterMode.IN);
        query.setParameter("player_uid", playerUid);
        return (BigDecimal) query.getSingleResult();
    }

    @Procedure(name = "get_total_games_played")
    public ArrayList<Integer> getGamesPlayed (@Param("player_uid") String playerUid) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("get_total_games_played");
        query.registerStoredProcedureParameter("player_uid", String.class, ParameterMode.IN);
        query.setParameter("player_uid", playerUid);
        return (ArrayList<Integer>) query.getResultList();
    }

    @Procedure(name = "get_players_and_scores_in_game")
    public List<Object> getPlayersAndScoresInGame (@Param("game_uid") String playerUid) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("get_players_and_scores_in_game");
        query.registerStoredProcedureParameter("game_uid", String.class, ParameterMode.IN);
        query.setParameter("game_uid", playerUid);
        return query.getResultList();
    }

    @Procedure(name = "get_player_game_info")
    public List<Object> getPlayerGamesInfo(String uid) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("get_player_game_info");
        query.registerStoredProcedureParameter("game_uid", String.class, ParameterMode.IN);
        query.setParameter("player_uid", uid);
        return query.getResultList();
    }

}