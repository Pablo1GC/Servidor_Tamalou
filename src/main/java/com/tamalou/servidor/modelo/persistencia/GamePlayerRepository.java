package com.tamalou.servidor.modelo.persistencia;

import com.tamalou.servidor.modelo.entidad.entidadesUsuario.GamePlayer;
import com.tamalou.servidor.modelo.entidad.entidadesUsuario.GamePlayerId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class GamePlayerRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(GamePlayer gamePlayer) {
        entityManager.persist(gamePlayer);
    }

    public GamePlayer findById(long gameId, String userId) {
        return entityManager.find(GamePlayer.class, new GamePlayerId(gameId, userId));
    }

    public List<GamePlayer> findAll() {
        return entityManager.createQuery("SELECT gp FROM GamePlayer gp", GamePlayer.class)
                .getResultList();
    }

    public void update(GamePlayer gamePlayer) {
        entityManager.merge(gamePlayer);
    }

    public void delete(GamePlayer gamePlayer) {
        entityManager.remove(entityManager.contains(gamePlayer) ? gamePlayer : entityManager.merge(gamePlayer));
    }
}
