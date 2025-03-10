package com.abelovagrupa.dbeeadmin.controller;

import com.abelovagrupa.dbeeadmin.connection.DatabaseConnection;
import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.column.DataType;
import com.abelovagrupa.dbeeadmin.model.foreignkey.Action;
import com.abelovagrupa.dbeeadmin.model.foreignkey.ForeignKey;
import com.abelovagrupa.dbeeadmin.model.index.Index;
import com.abelovagrupa.dbeeadmin.model.index.IndexStorageType;
import com.abelovagrupa.dbeeadmin.model.index.IndexedColumn;
import com.abelovagrupa.dbeeadmin.model.index.Order;
import com.abelovagrupa.dbeeadmin.model.schema.Charset;
import com.abelovagrupa.dbeeadmin.model.schema.Collation;
import com.abelovagrupa.dbeeadmin.model.table.DBEngine;
import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.model.table.Table;
import com.abelovagrupa.dbeeadmin.model.trigger.Trigger;

import java.sql.*;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DatabaseInspector {


    // TODO: Singleton? Static? New Thread?

    private final Connection connection;

    private static DatabaseInspector instance;

//    public DatabaseInspector(DatabaseConnection databaseConnection) {
//        this.connection = databaseConnection.getConnection();
//    }

    private DatabaseInspector(){
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    public static DatabaseInspector getInstance() {
        if (instance == null) {
            instance = new DatabaseInspector();
        }
        return instance;
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
        String query = "SELECT TABLE_NAME, ENGINE FROM information_schema.TABLES WHERE TABLE_SCHEMA = ?;";

        try(PreparedStatement ps = connection.prepareStatement(query)){
            ps.setString(1,schema.getName());
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Table table = new Table();
                table.setSchema(schema);
                table.setName(rs.getString("TABLE_NAME"));
                Optional<String> engineNullable = Optional.ofNullable(rs.getString("ENGINE"));
                if(engineNullable.isPresent()){
                    table.setDbEngine(DBEngine.valueOf(rs.getString("ENGINE").toUpperCase()));
                }
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
        String query = "SELECT TABLE_NAME, ENGINE FROM information_schema.TABLES WHERE TABLE_SCHEMA =? AND TABLE_NAME=?;";

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
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    if(!columnName.equals(name)) continue;
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
                Optional<String> charsetNullable = Optional.ofNullable(rs.getString("DEFAULT_CHARACTER_SET_NAME"));
                if(charsetNullable.isPresent()){
                    newDatabase.setCharset(Charset.valueOf(rs.getString("DEFAULT_CHARACTER_SET_NAME").toUpperCase()));
                }
                Optional<String> collationNullable = Optional.ofNullable(rs.getString("DEFAULT_COLLATION_NAME"));
                if(collationNullable.isPresent()){
                    newDatabase.setCollation(Collation.valueOf(rs.getString("DEFAULT_COLLATION_NAME").toUpperCase()));

                }
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
                " DEFAULT_COLLATION_NAME FROM information_schema.schemata WHERE SCHEMA_NAME=?;";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1,name);
            ResultSet rs = stmt.executeQuery();


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

    public List<ForeignKey> getForeignKeys(Schema schema, Table table) {
        if(schema == null) throw new IllegalArgumentException("Schema is not set");
        if(table == null) throw new IllegalArgumentException("Table is not set");

        List<ForeignKey> foreignKeys = new LinkedList<>();
        String query = "SELECT CONSTRAINT_NAME,TABLE_SCHEMA,TABLE_NAME, REFERENCED_TABLE_SCHEMA, REFERENCED_TABLE_NAME \n" +
                "FROM information_schema.KEY_COLUMN_USAGE\n" +
                "WHERE TABLE_SCHEMA=? AND TABLE_NAME=?;";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, schema.getName());
            stmt.setString(2, table.getName());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String keyName = rs.getString("CONSTRAINT_NAME");
                    String schemaName = rs.getString("TABLE_SCHEMA");
                    String tableName = rs.getString("TABLE_NAME");
                    Optional<String> referencedSchemaName = Optional.ofNullable(rs.getString("REFERENCED_TABLE_SCHEMA"));
                    Optional<String> referencedTableName = Optional.ofNullable(rs.getString("REFERENCED_TABLE_NAME"));

                    Schema referencingSchema = getDatabaseByName(schemaName);
                    Table referencingTable = getTableByName(referencingSchema,tableName);

                    Schema referencedSchema = getDatabaseByName(referencedSchemaName.orElse(null));
                    Table referencedTable = null;

                    if(referencedTableName.isPresent()){
                        referencedTable = getTableByName(referencedSchema,referencedTableName.get());
                    }

                    //Mapping attributes into foreignKey object
                    ForeignKey newForeignKey = new ForeignKey();
                    newForeignKey.setName(keyName);
                    newForeignKey.setReferencingSchema(referencingSchema);
                    newForeignKey.setReferencingTable(referencingTable);
                    newForeignKey.setReferencedSchema(referencedSchema);
                    newForeignKey.setReferencedTable(referencedTable);

                    String columnQuery = "SELECT COLUMN_NAME, REFERENCED_COLUMN_NAME\n" +
                            "FROM information_schema.KEY_COLUMN_USAGE\n" +
                            "WHERE TABLE_SCHEMA=? AND TABLE_NAME=? AND CONSTRAINT_NAME=?;";
                    PreparedStatement ps = connection.prepareStatement(columnQuery);
                    ps.setString(1,schemaName);
                    ps.setString(2,tableName);
                    ps.setString(3,keyName);

                    ResultSet columnsRs = ps.executeQuery();

                    newForeignKey.setReferencingColumns(new LinkedList<>());
                    newForeignKey.setReferencedColumns(new LinkedList<>());

                    while(columnsRs.next()){
                        Optional<String> referencingColumn = Optional.ofNullable(columnsRs.getString("COLUMN_NAME"));
                        Optional<String> referencedColumn = Optional.ofNullable(columnsRs.getString("REFERENCED_COLUMN_NAME"));

                        if(referencingColumn.isPresent()){
                            newForeignKey.getReferencingColumns()
                                    .add(getColumnByName(referencingTable,referencingColumn.get()));
                        }
                        if(referencedColumn.isPresent()){
                            newForeignKey.getReferencedColumns()
                                    .add(getColumnByName(referencedTable,referencedColumn.get()));
                        }

                    }

                    String actionQuery = "SELECT UPDATE_RULE, DELETE_RULE\n" +
                            "FROM information_schema.REFERENTIAL_CONSTRAINTS\n" +
                            "WHERE CONSTRAINT_NAME=?;";

                    PreparedStatement actionStmt = connection.prepareStatement(actionQuery);
                    actionStmt.setString(1,keyName);

                    ResultSet actionRs = actionStmt.executeQuery();
                    if(actionRs.next()){
                        String onUpdate = actionRs.getString("UPDATE_RULE").replace(" ","_");
                        newForeignKey.setOnUpdateAction(Action.valueOf(onUpdate));
                        String onDelete = actionRs.getString("DELETE_RULE").replace(" ","_");
                        newForeignKey.setOnDeleteAction(Action.valueOf(onDelete));
                    }

                    foreignKeys.add(newForeignKey);
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return foreignKeys;
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

    public List<Index> getIndexes(Schema schema, Table table){
        if(schema == null) throw new IllegalArgumentException("Schema is not set");
        if(table == null) throw new IllegalArgumentException("Table is not set");

        List<Index> indexes = new LinkedList<>();
        String query = "SELECT \n" +
                "    INDEX_NAME, \n" +
                "    NON_UNIQUE, \n" +
                "    INDEX_TYPE,\n" +
                "    IS_VISIBLE\n" +
                "FROM INFORMATION_SCHEMA.STATISTICS\n" +
                "WHERE TABLE_SCHEMA = ? \n" +
                "AND TABLE_NAME = ?;";

        //INDEX_NAME,NON_UNIQUE,IS_VISIBLE,INDEX_TYPE
        try(PreparedStatement indexStmt = connection.prepareStatement(query);){
            indexStmt.setString(1,schema.getName());
            indexStmt.setString(2,table.getName());
            ResultSet indexRs = indexStmt.executeQuery();
            List<String> indexNames = new LinkedList<>();
            while(indexRs.next()){
                Index index = new Index();
                String indexName = indexRs.getString("INDEX_NAME");
                if(indexNames.contains(indexName)){
                    continue;
                }else{
                    indexNames.add(indexName);
                }
                index.setName(indexName);
                index.setUnique(indexRs.getInt("NON_UNIQUE") == 0);
                index.setVisible(indexRs.getString("IS_VISIBLE").equals("YES"));
                Optional<String> storageType = Optional.ofNullable(indexRs.getString("INDEX_TYPE"));
                if(storageType.isPresent()){
                    index.setStorageType(IndexStorageType.valueOf(storageType.get().toUpperCase()));
                }
                index.setIndexedColumns(new LinkedList<>());
                String columnQuery = "SELECT \n" +
                        "    COLUMN_NAME, \n" +
                        "    SEQ_IN_INDEX, \n" +
                        "    COLLATION\n" +
                        "FROM INFORMATION_SCHEMA.STATISTICS\n" +
                        "WHERE TABLE_SCHEMA = ? \n" +
                        "AND TABLE_NAME = ? AND INDEX_NAME = ?;";
                try(PreparedStatement columnsStmt = connection.prepareStatement(columnQuery)){
                    columnsStmt.setString(1,schema.getName());
                    columnsStmt.setString(2,table.getName());
                    columnsStmt.setString(3,index.getName());
                    ResultSet columnRs = columnsStmt.executeQuery();

                    while(columnRs.next()){
                        IndexedColumn indexedColumn = new IndexedColumn();
                        String columnName = columnRs.getString("COLUMN_NAME");
                        indexedColumn.setColumn(getColumnByName(table,columnName));
                        indexedColumn.setIndex(index);
                        indexedColumn.setOrderNumber(columnRs.getInt("SEQ_IN_INDEX"));
                        Optional<String> order = Optional.ofNullable(columnRs.getString("COLLATION"));
                        if(order.isPresent()){
                            if(order.get().equals("A")) indexedColumn.setOrder(Order.ASC);
                            else if (order.get().equals("D")) indexedColumn.setOrder(Order.DESC);
                        }
                        index.getIndexedColumns().add(indexedColumn);

                    }


                }catch(SQLException ex){
                    throw new RuntimeException(ex);
                }

                indexes.add(index);
            }


        }catch(SQLException ex){
            throw new RuntimeException(ex);
        }
        return indexes;
    }

    public List<IndexedColumn> getIndexedColumns(Schema schema, Table table, Index index) {
        List<IndexedColumn> indexedColumns = new LinkedList<>();
        String columnQuery = "SELECT \n" +
                "    COLUMN_NAME, \n" +
                "    SEQ_IN_INDEX, \n" +
                "    COLLATION\n" +
                "FROM INFORMATION_SCHEMA.STATISTICS\n" +
                "WHERE TABLE_SCHEMA = ? \n" +
                "AND TABLE_NAME = ? AND INDEX_NAME = ?;";
        try(PreparedStatement columnsStmt = connection.prepareStatement(columnQuery)){
            columnsStmt.setString(1,schema.getName());
            columnsStmt.setString(2,table.getName());
            columnsStmt.setString(3,index.getName());
            ResultSet columnRs = columnsStmt.executeQuery();

            while(columnRs.next()){
                IndexedColumn indexedColumn = new IndexedColumn();
                String columnName = columnRs.getString("COLUMN_NAME");
                indexedColumn.setColumn(getColumnByName(table,columnName));
//                indexedColumn.setIndex(index);
                indexedColumn.setOrderNumber(columnRs.getInt("SEQ_IN_INDEX"));
                Optional<String> order = Optional.ofNullable(columnRs.getString("COLLATION"));
                if(order.isPresent()){
                    if(order.get().equals("A")) indexedColumn.setOrder(Order.ASC);
                    else if (order.get().equals("D")) indexedColumn.setOrder(Order.DESC);
                }
                indexedColumns.add(indexedColumn);
            }


        }catch(SQLException ex){
            throw new RuntimeException(ex);
        }
        return indexedColumns;
    }



//    public static void main(String[] args) {
//        DatabaseInspector di = new DatabaseInspector();
//        Schema schema = di.getDatabaseByName("veslanje");
//        Table table = di.getTableByName(schema,"klub_takmicenje");
//        System.out.println(di.getForeignKeys(schema,table));
//    }

}
