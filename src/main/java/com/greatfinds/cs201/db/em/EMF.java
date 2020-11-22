package com.greatfinds.cs201.db.em;

import com.greatfinds.cs201.TmdbHelper;
import com.greatfinds.cs201.db.MediaTitle;
import com.greatfinds.cs201.db.Post;
import com.greatfinds.cs201.db.User;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

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
import java.util.HashSet;
import java.util.List;

@WebListener
public class EMF implements ServletContextListener {

    private static EntityManagerFactory emf;
    private static EntityManager entityManager;

    public static EntityManager getEntityManager() {
        if (entityManager == null) entityManager = createEntityManager();
        return entityManager;
    }

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
        EntityManager entityManager = getEntityManager();
        entityManager.getTransaction().begin();
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            user.setFollowedTags(new HashSet<>(Arrays.asList("all", "testTag" + (i + 1))));
            entityManager.persist(user);
        }
        entityManager.getTransaction().commit();
    }

    public static void saveMovieTitles(EntityManager entityManager) {
        System.out.println("Getting and saving movie titles");
        TmdbMovies tmdbMovies = TmdbHelper.getTmdbApi().getMovies();
        int totalCnt = 0;
        int pageCnt = 1;
        for (int i = 1; i <= pageCnt; i++) {
            MovieResultsPage resultsPage = tmdbMovies.getNowPlayingMovies("en", i, "US");
            pageCnt = resultsPage.getTotalPages();
            List<MovieDb> movies = resultsPage.getResults();
            for (MovieDb movie : movies) {
                if (movie.getTitle() == null) continue;
                MediaTitle title = TmdbHelper.getMediaTitleFromMovieDb(movie);
                title.setImgUrl(movie.getPosterPath() == null ? TmdbHelper.imageNotFoundUrl : TmdbHelper.imageBaseUrl + movie.getPosterPath());
                entityManager.persist(title);
                ++totalCnt;
            }
        }
        System.out.println("Saved " + totalCnt + " movies.");
    }

    public void createTestPosts() {
        EntityManager entityManager = getEntityManager();
        List<User> users = entityManager.createNamedQuery("getAllUsers", User.class).getResultList();
        List<MediaTitle> titles = Arrays.asList(
                new MediaTitle("Fast and Furious", "Action"),
                new MediaTitle("Paddington", "Comedy"),
                new MediaTitle("The Martian", "Sci-fi")
        );
        titles.forEach(entityManager::persist);
//        saveMovieTitles(entityManager);
        List<Post> posts = Arrays.asList(
                new Post(users.get(0), titles.get(0), "Vin Diesel is so cool", 3,
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
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        createDBIfNotExists(); // uncomment if ran for the first time to create DB
        emf = Persistence.createEntityManagerFactory("greatFindsMySQL");
        createTestUsers();
        createTestPosts();
        if (entityManager != null) entityManager.close();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        emf.close();
    }

    public static EntityManagerFactory getEmf() {
        return emf;
    }

    public static EntityManager createEntityManager() {
        if (emf == null) {
            throw new IllegalStateException("Servlet context is not initialized yet.");
        }

        return emf.createEntityManager();
    }

}