package com.greatfinds.cs201;

import com.greatfinds.cs201.db.User;

import javax.ejb.Stateless;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

@Stateless
public class UserHelper {

    @Inject
    private EntityManager entityManager;

    @Inject
    private BeanManager beanManager;

    public void registerUser(User user) {
        user.setFollowedTags(Collections.singleton("all"));
        beginSession();
        entityManager.persist(user);
        commit();
        beanManager.fireEvent(user);
    }

    public User getCompleteUser(User user) {
        return entityManager.createNamedQuery("getUser", User.class)
                .setParameter("email", user.getEmail())
                .getSingleResult();
    }

    // return true if user with specified email exists in DB
    public boolean userExists(String email) {
        return entityManager.createNamedQuery("userExists", Long.class)
                .setParameter("email", email)
                .getSingleResult() == 1;
    }

    // return true if user with email & password match record in database
    public boolean userMatch(String email, String pwd) {
        return entityManager.createNamedQuery("userMatches", Long.class)
                .setParameter("email", email)
                .setParameter("pwd", pwd)
                .getSingleResult() == 1;
    }

    public List<User> getAllUsers() {
        System.out.println("Getting users");
        return entityManager
                .createNamedQuery("getAllUsers", User.class)
                .getResultList();
    }

    private void commit() {
        entityManager.getTransaction().commit();
    }

    private void beginSession() {
        entityManager.getTransaction().begin();
    }
}