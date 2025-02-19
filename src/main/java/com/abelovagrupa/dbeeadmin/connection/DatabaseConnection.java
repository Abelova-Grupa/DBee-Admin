package com.abelovagrupa.dbeeadmin.connection;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        Dotenv dotenv = Dotenv.load();
        String dbUrl = dotenv.get("DB_URL");
        String dbUsername = dotenv.get("DB_USERNAME");
        String dbPassword = dotenv.get("DB_PASSWORD");

        try {
            connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            System.out.println("Database connection established");
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void writtingInEnvFile(String dbUrl, String dbUsername, String dbPassword) throws IOException {
        String filePath = ".env";
        System.out.println("Database connection established");
        System.out.println("Writing to file: " + filePath);
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filePath, false));
        fileWriter.write("DB_URL="+dbUrl + "\n");
        fileWriter.write("DB_USERNAME="+dbUsername + "\n");
        fileWriter.write("DB_PASSWORD="+dbPassword + "\n");
        fileWriter.close();
    }

}
