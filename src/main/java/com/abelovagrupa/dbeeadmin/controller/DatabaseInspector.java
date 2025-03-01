package com.abelovagrupa.dbeeadmin.controller;

import com.abelovagrupa.dbeeadmin.connection.DatabaseConnection;
import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.column.DataType;
import com.abelovagrupa.dbeeadmin.model.foreignkey.ForeignKey;
import com.abelovagrupa.dbeeadmin.model.index.Index;
import com.abelovagrupa.dbeeadmin.model.index.IndexedColumn;
import com.abelovagrupa.dbeeadmin.model.schema.Charset;
import com.abelovagrupa.dbeeadmin.model.schema.Collation;
import com.abelovagrupa.dbeeadmin.model.table.DBEngine;
import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.model.table.Table;
import com.abelovagrupa.dbeeadmin.model.trigger.Trigger;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DatabaseInspector {


    // TODO: Singleton? Static? New Thread?

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

        try(PreparedStatement ps = connection.prepareStatement(query)){
            ps.setString(1,schema.getName());
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Table table = new Table();
                table.setSchema(schema);
                table.setName(rs.getString("TABLE_NAME"));
                // FIXME: Cannot invoke "String.toUpperCase()" if the return value of rs.getString("ENGINE") is null
                table.setDbEngine(DBEngine.valueOf(rs.getString("ENGINE").toUpperCase()));
                table.setColumns(getColumns(table));
                // A lot more table attributes, implementation depends on scope

                tables.add(table);
            }

        }catch(SQLException ex){
            System.err.println("Error retrieving tables: "+ ex.getMessage());
        }
        return tables;
    }

    public Table getTableByName(Schema schema, String tableName) {

        Table table = null;
        String query = "SELECT * FROM information_schema.TABLES WHERE TABLE_SCHEMA =? AND TABLE_NAME=?;";

        try(PreparedStatement ps = connection.prepareStatement(query)){
            ps.setString(1, schema.getName());
            ps.setString(2, tableName);

            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                table = new Table();
                table.setSchema(schema);
                table.setName(rs.getString("TABLE_NAME"));
                table.setDbEngine(DBEngine.valueOf(rs.getString("ENGINE").toUpperCase()));
                table.setColumns(getColumns(table));
            }


        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
        return table;
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
//                    column.setGenerated(extra != null && extra.contains("generated"));

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
//                    column.setGenerated(extra != null && extra.contains("generated"));

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

    /**
     * Retrieves a list of current users databases.
     * @return List of databases from current connection.
     */
    public List<Schema> getDatabases(){
        List<Schema> databases = new LinkedList<>();

        try {
            String query = "SELECT SCHEMA_NAME, DEFAULT_CHARACTER_SET_NAME," +
                " DEFAULT_COLLATION_NAME FROM information_schema.schemata;";
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            while(rs.next()){
                // DO NOT CHANGE ORDER, FRAGILE TO ORDER CHANGE
                Schema newDatabase = new Schema();
                newDatabase.setName(rs.getString("SCHEMA_NAME"));
                newDatabase.setCharset(Charset.valueOf(rs.getString("DEFAULT_CHARACTER_SET_NAME").toUpperCase()));
                newDatabase.setCollation(Collation.valueOf(rs.getString("DEFAULT_COLLATION_NAME").toUpperCase()));
                newDatabase.setTables(getTables(newDatabase));
                newDatabase.setTableCount(newDatabase.getTableCount());
                newDatabase.setDatabaseSize(getDatabaseSize(newDatabase.getName()));

                databases.add(newDatabase);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return databases;
    }

    /**
     * Returns (maps) a single schema.
     * @param name Schema name
     * @return Mapped schema
     */
    public Schema getDatabaseByName(String name) {

        Schema schema = null;
        try {
            String query = "SELECT SCHEMA_NAME, DEFAULT_CHARACTER_SET_NAME," +
                " DEFAULT_COLLATION_NAME FROM information_schema.schemata WHERE SCHEMA_NAME=" + name;
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            if(rs.next()){
                schema = new Schema.SchemaBuilder(
                    rs.getString("SCHEMA_NAME"),
                    Charset.valueOf(rs.getString("DEFAULT_CHARACTER_SET_NAME").toUpperCase()),
                    Collation.valueOf(rs.getString("DEFAULT_COLLATION_NAME").toUpperCase())
                ).build();

                schema.setTables(getTables(schema));
                schema.setTableCount(schema.getTableCount());
                schema.setDatabaseSize(getDatabaseSize(schema.getName()));

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return schema;

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

    public float getDatabaseSize(String databaseName){

        float databaseSize = -1;

        String query = "SELECT table_schema \"DB Name\",\n" +
                "        ROUND(SUM(data_length + index_length) / 1024 / 1024, 1) \"DB Size in MB\"\n" +
                "FROM information_schema.tables\n" +
                "WHERE table_schema=?;";

        try(PreparedStatement ps = connection.prepareStatement(query)){
            ps.setString(1,databaseName);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                databaseSize =  rs.getFloat("DB Size in MB");
            }

        }catch (SQLException ex){
            throw new RuntimeException(ex);
        }

        return databaseSize;
    }

}
