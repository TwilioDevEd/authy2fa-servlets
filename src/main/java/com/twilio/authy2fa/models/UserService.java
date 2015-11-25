package com.twilio.authy2fa.models;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class UserService {
    private EntityManager entityManager;

    public UserService() {
        Map<String, String> config = loadConfFromEnvironment();
        this.entityManager = Persistence.createEntityManagerFactory("Authy2FA", config).createEntityManager();
    }

    public User find(long id) {
        entityManager.getEntityManagerFactory().getCache().evictAll();
        User user = entityManager.find(User.class, id);
        entityManager.refresh(user);

        return user;
    }

    public User findByEmail(String email) {
        User user = null;

        try {
            user = (User) entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email")
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException ex) {
            System.out.println(ex.getMessage());
        }

        return user;
    }

    public User findByAuthyId(String authyId) {
        User user = null;

        try {
            user = (User) entityManager.createQuery("SELECT u FROM User u WHERE u.authyId = :authyId")
                    .setParameter("authyId", authyId)
                    .getSingleResult();
        } catch (NoResultException ex) {
            System.out.println(ex.getMessage());
        }

        return user;
    }

    public User create(User user) {
        getTransaction().begin();
        entityManager.persist(user);
        getTransaction().commit();

        return user;
    }

    public User update(User user) {
        getTransaction().begin();
        User updatedUser = entityManager.merge(user);
        getTransaction().commit();

        return updatedUser;
    }

    private EntityTransaction getTransaction() {
        return entityManager.getTransaction();
    }

    public Map<String, String> loadConfFromEnvironment() {
        Map<String, String> env = System.getenv();
        Map<String, String> config = new HashMap<>();
        for (String key : env.keySet()) {
            if (key.contains("DB_USER")) {
                config.put("javax.persistence.jdbc.user", env.get(key));
            }

            if (key.contains("DB_PASSWORD")) {
                config.put("javax.persistence.jdbc.password", env.get(key));
            }
        }

        return config;
    }
}
