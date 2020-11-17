package com.greatfinds.cs201.db.em;

import com.greatfinds.cs201.db.MediaTitle;
import com.greatfinds.cs201.db.Post;
import com.greatfinds.cs201.db.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@WebListener
public class EMF implements ServletContextListener {

    private static EntityManagerFactory emf;

    public static void createDBIfNotExists() {
        try {
            //register
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "root");
             Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS GreatFindsDB");
            System.out.println("MySQL database created");
            connection.setCatalog("mysql");//switch out of db (into internal db) for JPA
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Couldn't create database");
        }
    }

    public void createTestUsers() {
        List<User> users = Arrays.asList(
                new User("Test User 1", "abc", "def"),
                new User("Test User 2", "123", "456")
        );
        EntityManager entityManager = createEntityManager();
        entityManager.getTransaction().begin();
        for (User user : users) {
            user.setFollowedTags(Collections.singleton("all"));
            entityManager.persist(user);
        }
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public void createTestPosts() {
        EntityManager entityManager = createEntityManager();
        List<User> users = entityManager.createNamedQuery("getAllUsers", User.class).getResultList();
        List<MediaTitle> titles = Arrays.asList(
                new MediaTitle("Fast and Furious", "action"),
                new MediaTitle("Paddington", "comedy"),
                new MediaTitle("The Martian", "scifi")
        );
        titles.forEach(entityManager::persist);
        List<Post> posts = Arrays.asList(
                new Post(users.get(0), titles.get(0), "Vin Diesel is so cool", 4,
                        new HashSet<>(Arrays.asList("all", "testTag1"))),
                new Post(users.get(1), titles.get(0), "The cars don't go fast enough", 1,
                        new HashSet<>(Arrays.asList("all", "testTag1"))),
                new Post(users.get(1), titles.get(1), "Not a big fan of the bear", 2,
                        new HashSet<>(Arrays.asList("all", "testTag3"))),
                new Post(users.get(1), titles.get(2), "Potatoes are my jam", 3,
                        new HashSet<>(Arrays.asList("all", "testTag4")))

        );
        entityManager.getTransaction().begin();
        posts.forEach(entityManager::persist);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
//        createDBIfNotExists(); // uncomment if ran for the first time
        emf = Persistence.createEntityManagerFactory("greatFindsMySQL");
        createTestUsers();
        createTestPosts();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        emf.close();
    }

    public static EntityManager createEntityManager() {
        if (emf == null) {
            throw new IllegalStateException("Servlet context is not initialized yet.");
        }

        return emf.createEntityManager();
    }

}