package com.greatfinds.cs201.db.em;

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
import java.util.Collections;

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
        User user = new User("Test User", "abc", "def");
        user.setFollowedTags(Collections.singleton("all"));
        EntityManager entityManager = createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(user);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
//        createDBIfNotExists(); // uncomment if ran for the first time
        emf = Persistence.createEntityManagerFactory("greatFindsMySQL");
        createTestUsers();
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