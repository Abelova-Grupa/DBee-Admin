package com.abelovagrupa.dbeeadmin.services;

import com.abelovagrupa.dbeeadmin.connection.DatabaseConnection;
import com.abelovagrupa.dbeeadmin.util.Pair;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryExecutor {

    public static Pair<ResultSet, Integer> executeQuery(String sql) {

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Integer rowsAffected = null;

        try {
            connection = DatabaseConnection.getInstance().getConnection();

            statement = connection.createStatement();

            boolean isResultSet = statement.execute(sql);

            // TODO: Find a way to set a selected schema (for which I created the State class).

            // Process the result (if it's a SELECT query)
            if (isResultSet) {
                resultSet = statement.getResultSet();
                System.out.println("Getting rs");

            } else {
                // For non-SELECT queries (INSERT, UPDATE, DELETE), print the number of rows affected
                rowsAffected = statement.getUpdateCount();
                System.out.println("Rows affected: " + rowsAffected);
            }

        } catch (SQLException e) {
            System.err.println("Error executing SQL query: " + e.getMessage());
        }

        return Pair.of(resultSet, rowsAffected);

    }

}
