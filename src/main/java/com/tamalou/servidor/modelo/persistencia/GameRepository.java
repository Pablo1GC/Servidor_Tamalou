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

    public void save(Game game) {
        entityManager.persist(game);
    }

    public Game findById(int id) {
        return entityManager.find(Game.class, id);
    }

    public List<Game> findAll() {
        return entityManager.createQuery("SELECT g FROM Game g", Game.class)
                .getResultList();
    }

    public void update(Game game) {
        entityManager.merge(game);
    }

    public void delete(Game game) {
        entityManager.remove(entityManager.contains(game) ? game : entityManager.merge(game));
    }
}
