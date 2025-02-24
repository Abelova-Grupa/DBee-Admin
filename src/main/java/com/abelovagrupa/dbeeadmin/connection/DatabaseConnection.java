package com.abelovagrupa.dbeeadmin.connection;

import com.abelovagrupa.dbeeadmin.util.AlertManager;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // TODO: Split url to host & port

    private static DatabaseConnection instance;
    // Connection cannot be final, in case that a user wants to switch username or url of connection
    private Connection connection;

    private DatabaseConnection() {
        try {
        // Load data from .env file
            Dotenv dotenv = Dotenv.configure().directory("src/main/resources/com/abelovagrupa/dbeeadmin/.env").load();
            String dbUrl = dotenv.get("DB_URL");
            String dbUsername = dotenv.get("DB_USERNAME");
            String dbPassword = dotenv.get("DB_PASSWORD");

            assert dbUrl != null;
            assert dbUsername != null;
            setConnection(dbUrl,dbUsername,dbPassword);
            System.out.println("Database connection established.");
//        } catch (SQLException  | AssertionError e ) {
//            throw new RuntimeException(e);
        }catch(Exception e){
            //Catches exception that is caused by no .env file
            System.err.println(e.getMessage());
            connection = null;
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(String dbUrl, String dbUsername, String dbPassword){
        try {
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            setConnectionParameters(dbUrl,dbUsername,dbPassword);
        } catch (SQLException  | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves connection parameters to env file.
     * @param dbUrl full database url
     * @param dbUsername user with full privileges
     * @param dbPassword password
     * @throws IOException on .env writing failure
     */
    public void setConnectionParameters(String dbUrl, String dbUsername, String dbPassword) throws IOException {
        String filePath = "src/main/resources/com/abelovagrupa/dbeeadmin/.env";
        System.out.println("Connection parameters set.");
        // System.out.println("Writing to file: " + filePath);
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filePath, false));
        fileWriter.write("DB_URL=" + dbUrl + "\n");
        fileWriter.write("DB_USERNAME=" + dbUsername + "\n");
        fileWriter.write("DB_PASSWORD=" + dbPassword + "\n");
        fileWriter.close();
    }

    public boolean isConnectionValid(String dbUrl, String dbUsername, String dbPassword) {
        try {
            setConnection(dbUrl, dbUsername, dbPassword);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String generateDbUrl(String host,String port){
        // If connection parameters are null and user changes them, test for prefix
        if(host.contains("jdbc:mysql://")){
            return host+":"+port;
        }else return "jdbc:mysql://"+host+":" + port;
    }
}
