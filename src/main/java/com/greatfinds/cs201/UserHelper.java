package com.greatfinds.cs201;

import com.greatfinds.cs201.db.User;

import javax.ejb.Stateless;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

@Stateless
public class UserHelper {

    @Inject
    private EntityManager entityManager;

    @Inject
    private BeanManager beanManager;

    public void registerUser(String displayName, String email, String pwd) {
        User user = new User(displayName, email, pwd);
        registerUser(user);
    }

    public void registerUser(User user) {
        user.setFollowedTags(Collections.singleton("all"));
        beginSession();
        entityManager.persist(user);
        commit();
        beanManager.fireEvent(user);
    }

    public User getCompleteUser(User user) {
        TypedQuery<User> query = entityManager.createNamedQuery("getUser", User.class);
        query.setParameter("email", user.getEmail());
        return query.getSingleResult();
    }

    // return true if user with specified email exists in DB
    public boolean userExists(String email) {
        TypedQuery<Long> query = entityManager.createNamedQuery("userExists", Long.class);
        query.setParameter("email", email);
        return query.getSingleResult() == 1;
    }

    // return true if user with email & password match record in database
    public boolean userMatch(String email, String pwd) {
        TypedQuery<Long> query = entityManager.createNamedQuery("userMatches", Long.class);
        query.setParameter("email", email);
        query.setParameter("pwd", pwd);
        return query.getSingleResult() == 1;
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