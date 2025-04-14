package com.abelovagrupa.dbeeadmin.services;

import com.abelovagrupa.dbeeadmin.connection.DatabaseConnection;
import com.abelovagrupa.dbeeadmin.util.AlertManager;
import com.abelovagrupa.dbeeadmin.util.Pair;
import com.abelovagrupa.dbeeadmin.view.DialogSQLPreview;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryExecutor {

    public static final Logger logger = LogManager.getRootLogger();

    public static Pair<ResultSet, Integer> executeBatch(String sql) {
        DatabaseConnection.getInstance().setCurrentSchema(ProgramState.getInstance().getSelectedSchema());
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Integer rowsAffected = null;

        try {
            // Get the database connection
            connection = DatabaseConnection.getInstance().getConnection();
            statement = connection.createStatement();

            // Disable auto-commit to ensure batch execution is handled in a transaction
            connection.setAutoCommit(false);

            // Split the SQL string
            String[] sqlStatements = sql.split(";");

            // Add each SQL statement to the batch
            for (String singleSql : sqlStatements) {
                singleSql = singleSql.trim();
                if (!singleSql.isEmpty()) {
                    statement.addBatch(singleSql);
                }
            }

            // Execute the batch
            int[] updateCounts = statement.executeBatch();

            // Iterate through the update counts to process each result
            for (int updateCount : updateCounts) {
                boolean isResultSet = statement.getMoreResults();

                // If it is a SELECT query, process the ResultSet
                if (isResultSet) {
                    resultSet = statement.getResultSet();
                } else {
                    if(rowsAffected == null) rowsAffected = 0;
                    rowsAffected += updateCount;
                }
            }

            // Commit the transaction if everything is successful
            connection.commit();
            connection.setAutoCommit(true);

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                logger.error("Error during rollback {}", rollbackEx.getMessage());
            }
            logger.error("Error executing SQL query: {}", e.getMessage());
            AlertManager.showErrorDialog(null, "Error executing SQL query:", e.getMessage());
        } finally {
            DatabaseConnection.getInstance().setCurrentSchema(null);
            try {
                DatabaseConnection.getInstance().getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                logger.fatal("Something extremely dire and profoundly fucked happened.");
            }
        }

        return Pair.of(resultSet, rowsAffected);
    }

    public static Pair<ResultSet, Integer> executeQuery(String sql) {
        DatabaseConnection.getInstance().setCurrentSchema(ProgramState.getInstance().getSelectedSchema());
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Integer rowsAffected = null;

        try {
            connection = DatabaseConnection.getInstance().getConnection();
            statement = connection.createStatement();
            boolean isResultSet = statement.execute(sql);

            // Process the result (if it's a SELECT query)
            if (isResultSet) {
                resultSet = statement.getResultSet();

            } else {
                // For non-SELECT queries (INSERT, UPDATE, DELETE), print the number of rows affected
                rowsAffected = statement.getUpdateCount();
            }

        } catch (SQLException e) {
            logger.error("Error executing SQL query: {}", e.getMessage());
            AlertManager.showErrorDialog(null, "Error executing SQL query:", e.getMessage());
        }
        DatabaseConnection.getInstance().setCurrentSchema(null);
        return Pair.of(resultSet, rowsAffected);

    }

    public static void executeQuery(String query, boolean preview){
        DatabaseConnection.getInstance().setCurrentSchema(ProgramState.getInstance().getSelectedSchema());
        Connection connection;

        try {
            connection = DatabaseConnection.getInstance().getConnection();
            Statement statement = connection.createStatement();

            if(preview)
                // Show dialog window and execute query
                new DialogSQLPreview(query).showAndWait().ifPresent(b -> {if(b) {
                    try {
                        statement.execute(query);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                });
            else {
                // just execute query
                statement.execute(query);
            }
        } catch (SQLException e) {
            logger.error("Error executing SQL query: {}", e.getMessage());
            AlertManager.showErrorDialog(null, "Error executing SQL query:", e.getMessage());
        }

    }

}
