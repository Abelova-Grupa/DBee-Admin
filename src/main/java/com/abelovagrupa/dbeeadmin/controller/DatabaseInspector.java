package com.abelovagrupa.dbeeadmin.controller;

import com.abelovagrupa.dbeeadmin.connection.DatabaseConnection;
import com.abelovagrupa.dbeeadmin.model.*;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DatabaseInspector {

    private final Connection connection;

    public DatabaseInspector(DatabaseConnection databaseConnection) {
        this.connection = databaseConnection.getConnection();
    }

    public List<String> getDatabaseNames() {

        List<String> databaseNames = new LinkedList<>();

        String query = "SELECT SCHEMA_NAME FROM information_schema.SCHEMATA";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("List of Databases:");
            while (rs.next()) {
                databaseNames.add(rs.getString("SCHEMA_NAME"));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving database names: " + e.getMessage());
        }

        return databaseNames;
    }

    public List<String> getTableNames(Schema schema) {

        List<String> tableNames = new LinkedList<>();

        String query = "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, schema.getName());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tableNames.add(rs.getString("TABLE_NAME"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving table names: " + e.getMessage());
        }
        return tableNames;
    }

    public List<Table> getTables(Schema schema){

        List<Table> tables = new LinkedList<>();
        String query = "SELECT * FROM information_schema.TABLES WHERE TABLE_SCHEMA = ?;";

        try(PreparedStatement ps = connection.prepareStatement(query);){
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Table table = new Table();
                table.setName(rs.getString("TABLE_NAME"));
                table.setDbEngine(DBEngine.valueOf(rs.getString("ENGINE").toUpperCase()));

                // A lot more table attributes, implementation depends on scope

                tables.add(table);
            }

        }catch(SQLException ex){
            System.err.println("Error retrieving tables: "+ ex.getMessage());
        }
        return tables;
    }

    public Table getTableByName(Schema schema, String name) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Retrieves (maps) all columns of a given table.
     * @param table Table from which columns shall be returned
     * @return List of columns from the table.
     */
    public List<Column> getColumns(Table table) {

        List<Column> columns = new LinkedList<>();

        String query = "SELECT COLUMN_NAME, IS_NULLABLE, COLUMN_DEFAULT, " +
            "DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION, " +
            "COLUMN_KEY, EXTRA " +
            "FROM INFORMATION_SCHEMA.COLUMNS " +
            "WHERE TABLE_NAME = ? AND TABLE_SCHEMA = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, table.getName());
            stmt.setString(2, table.getSchema().getName());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    String dataType = rs.getString("DATA_TYPE");
                    boolean isNotNull = rs.getString("IS_NULLABLE").equals("NO");
                    String columnKey = rs.getString("COLUMN_KEY");
                    String extra = rs.getString("EXTRA");

                    // Map to the Column object
                    Column column = new Column();
                    column.setName(columnName);
                    column.setNotNull(isNotNull);

                    // Key?
                    column.setUnique(columnKey != null && columnKey.equals("UNI"));
                    column.setPrimaryKey(columnKey != null && columnKey.equals("PRI"));

                    // Binary
                    column.setBinary(dataType.equals("BINARY"));

                    // Extras
                    column.setAutoIncrement(extra != null && extra.contains("auto_increment"));
                    column.setUnsigned(extra != null && extra.contains("unsigned"));
                    column.setZeroFill(extra != null && extra.contains("zerofill"));
                    column.setGenerated(extra != null && extra.contains("generated"));

                    DataType type = DataType.valueOf(dataType.toUpperCase());
                    column.setType(type);

                    column.setTable(table);
                    columns.add(column);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return columns;
    }

    /**
     * Retrieves (maps) a single column of a given table.
     * @param table Table from which the column shall be returned
     * @param name Name of a column.
     * @return Column object from a given table with a given name.
     */
    public Column getColumnByName(Table table, String name) {
        Column column = new Column();

        String query = "SELECT COLUMN_NAME, IS_NULLABLE, COLUMN_DEFAULT, " +
            "DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, NUMERIC_PRECISION, " +
            "COLUMN_KEY, EXTRA " +
            "FROM INFORMATION_SCHEMA.COLUMNS " +
            "WHERE TABLE_NAME = ? AND TABLE_SCHEMA = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, table.getName());
            stmt.setString(2, table.getSchema().getName());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    String dataType = rs.getString("DATA_TYPE");
                    boolean isNotNull = rs.getString("IS_NULLABLE").equals("NO");
                    String columnKey = rs.getString("COLUMN_KEY");
                    String extra = rs.getString("EXTRA");

                    // Map to the Column object
                    column.setName(columnName);
                    column.setNotNull(isNotNull);

                    // Key?
                    column.setUnique(columnKey != null && columnKey.equals("UNI"));
                    column.setPrimaryKey(columnKey != null && columnKey.equals("PRI"));

                    // Binary
                    column.setBinary(dataType.equals("BINARY"));

                    // Extras
                    column.setAutoIncrement(extra != null && extra.contains("auto_increment"));
                    column.setUnsigned(extra != null && extra.contains("unsigned"));
                    column.setZeroFill(extra != null && extra.contains("zerofill"));
                    column.setGenerated(extra != null && extra.contains("generated"));

                    DataType type = DataType.valueOf(dataType.toUpperCase());
                    column.setType(type);

                    column.setTable(table);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return column;
    }

    public List<ForeignKey> getForeignKeys(Table table) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public List<Index> getIndexes(Table table) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public List<IndexedColumn> getIndexedColumns(Table table) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public List<Trigger> getTriggers(Table table) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

}
