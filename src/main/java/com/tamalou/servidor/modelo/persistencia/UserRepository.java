package com.tamalou.servidor.modelo.persistencia;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.apache.catalina.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(User user) {
        entityManager.persist(user);
    }

    public User findById(String uid) {
        return entityManager.find(User.class, uid);
    }

    public List<User> findAll() {
        return entityManager.createQuery("SELECT u FROM User u", User.class)
                .getResultList();
    }

    public void update(User user) {
        entityManager.merge(user);
    }

    public void delete(User user) {
        entityManager.remove(entityManager.contains(user) ? user : entityManager.merge(user));
    }
}
