/**
 * Repository class for Player entity.*
 * This class is responsible for performing CRUD (Create, Read, Update, Delete)
 * operations on Player entities in the database.
 */
package com.tamalou.servidor.modelo.persistencia;

import com.tamalou.servidor.modelo.entidad.entidadesPartida.Player;
import com.tamalou.servidor.modelo.entidad.entidadesUsuario.PlayerGameInfo;
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
import java.util.List;

@Repository
@Transactional
public class PlayerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Saves a Player entity to the database.
     *
     * @param Player The Player entity to be saved.
     */
    public void save(Player Player) {
        entityManager.persist(Player);
    }

    /**
     * Retrieves a Player entity from the database by its unique identifier (uid).
     *
     * @param uid The unique identifier (uid) of the Player entity to be retrieved.
     * @return The Player entity with the specified uid, or null if not found.
     */
    public Player findById(String uid) {
        return entityManager.find(Player.class, uid);
    }

    /**
     * Retrieves all Player entities from the database.
     *
     * @return A list of all Player entities in the database.
     */
    public List<Player> findAll() {
        return entityManager.createQuery("SELECT u FROM Player u", Player.class)
                .getResultList();
    }

    /**
     * Updates a Player entity in the database.
     *
     * @param Player The Player entity to be updated.
     */
    public void update(Player Player) {
        entityManager.merge(Player);
    }

    /**
     * Deletes a Player entity from the database.
     *
     * @param Player The Player entity to be deleted.
     */
    public void delete(Player Player) {
        entityManager.remove(entityManager.contains(Player) ? Player : entityManager.merge(Player));
    }

    /**
     * Searches for Player entities in the database by username, ignoring case.
     *
     * @param username The username to search for.
     * @return A list of Player entities whose username contains the specified string, ignoring case.
     */
    public List<Player> findByUsernameContainingIgnoreCase(String username) {
        return entityManager.createQuery("SELECT u FROM Player u WHERE lower(u.username) LIKE lower(concat('%', :username, '%'))", Player.class)
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
    public List<PlayerGameInfo> getPlayerGamesInfo(String uid) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("get_player_game_info");
        query.registerStoredProcedureParameter("player_uid", String.class, ParameterMode.IN);
        query.setParameter("player_uid", uid);

        List<PlayerGameInfo> playerGamesInfo = new ArrayList<>();
        List<Object[]> results = query.getResultList();
        for (Object[] result : results) {
            PlayerGameInfo playerGameInfo = new PlayerGameInfo();
            playerGameInfo.setName((String) result[0]);
            playerGameInfo.setScore((int) result[2]);
            playerGameInfo.setPlayed_on((String) result[1]);
            playerGameInfo.setId((int) result[3]);
            playerGamesInfo.add(playerGameInfo);
        }

        return playerGamesInfo;
    }

}