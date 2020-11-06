package em;

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
        try(Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "root");
            Statement stmt = connection.createStatement()){
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS GreatFindsDB");
            System.out.println("MySQL database created");
            connection.setCatalog("mysql");//switch out of db (into internal db) for JPA
        }catch (SQLException e){
            e.printStackTrace();
            System.err.println("Couldn't create database");
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        createDBIfNotExists();
        emf = Persistence.createEntityManagerFactory("greatFindsMySQL");
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