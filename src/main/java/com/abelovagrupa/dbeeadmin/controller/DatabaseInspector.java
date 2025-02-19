package com.abelovagrupa.dbeeadmin.controller;

import com.abelovagrupa.dbeeadmin.connection.DatabaseConnection;
import com.abelovagrupa.dbeeadmin.model.DBEngine;
import com.abelovagrupa.dbeeadmin.model.Schema;
import com.abelovagrupa.dbeeadmin.model.Table;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

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


    

    // TODO: Add methods that accept domain objects as arguments instead of strings.
}
