package com.twilio.authy2fa.models;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;

public class UserService {
    private EntityManager manager;

    public UserService() {
        this.manager = Persistence.createEntityManagerFactory("Authy2FA").createEntityManager();
    }

    public User find(long id) {
        return manager.find(User.class, id);
    }

    public User findByCredentials(String email, String password) {
        User user = null;

        try {
            user = (User) manager.createQuery("SELECT u FROM User u WHERE u.email = :email AND u.password = :password")
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .getSingleResult();
        } catch (NoResultException ex) {
            System.out.println(ex.getMessage());
        }

        return user;
    }

    public User save(User user) {
        getTransaction().begin();
        manager.persist(user);
        getTransaction().commit();

        return user;
    }

    private EntityTransaction getTransaction() {
        return manager.getTransaction();
    }
}
