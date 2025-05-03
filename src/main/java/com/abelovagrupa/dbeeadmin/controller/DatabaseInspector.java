package com.abelovagrupa.dbeeadmin.controller;

import com.abelovagrupa.dbeeadmin.connection.DatabaseConnection;
import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.column.DataType;
import com.abelovagrupa.dbeeadmin.model.foreignkey.Action;
import com.abelovagrupa.dbeeadmin.model.foreignkey.ForeignKey;
import com.abelovagrupa.dbeeadmin.model.foreignkey.ForeignKeyColumns;
import com.abelovagrupa.dbeeadmin.model.index.*;
import com.abelovagrupa.dbeeadmin.model.schema.Charset;
import com.abelovagrupa.dbeeadmin.model.schema.Collation;
import com.abelovagrupa.dbeeadmin.model.table.DBEngine;
import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.model.table.Table;
import com.abelovagrupa.dbeeadmin.model.trigger.Event;
import com.abelovagrupa.dbeeadmin.model.trigger.Timing;
import com.abelovagrupa.dbeeadmin.model.trigger.Trigger;
import com.abelovagrupa.dbeeadmin.model.view.View;
import com.abelovagrupa.dbeeadmin.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class DatabaseInspector {


    private static final Logger logger = LogManager.getRootLogger();

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

            while (rs.next()) {
                databaseNames.add(rs.getString("SCHEMA_NAME"));
            }

        } catch (SQLException e) {
            logger.error("Error retrieving database name: {}", e.getMessage());
        }

        return databaseNames;
    }

    public List<String> getTableNames(Schema schema) {

        List<String> tableNames = new LinkedList<>();

        String query = "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = ? AND table_type = 'BASE TABLE'";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, schema.getName());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tableNames.add(rs.getString("TABLE_NAME"));
                }
            }

        } catch (SQLException e) {
            logger.error("Error retrieving table names: {}", e.getMessage());
        }
        return tableNames;
    }

    public List<Table> getTables(Schema schema){

        List<Table> tables = new LinkedList<>();
        String query = "SELECT TABLE_NAME, ENGINE FROM information_schema.TABLES WHERE TABLE_SCHEMA = ? AND table_type = 'BASE TABLE';";

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
            logger.error("Error retrieving tables: {}", ex.getMessage());
        }
        return tables;
    }

    public Table getTableByName(Schema schema, String tableName) {

        Table table = null;
        String query = "SELECT TABLE_NAME, ENGINE FROM information_schema.TABLES WHERE TABLE_SCHEMA =? AND TABLE_NAME=? AND table_type = 'BASE TABLE'";

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
            logger.error(e.getMessage());
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
            "COLUMN_KEY, EXTRA, COLUMN_DEFAULT " +
            "FROM INFORMATION_SCHEMA.COLUMNS " +
            "WHERE TABLE_NAME = ? AND TABLE_SCHEMA = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, table.getName());
            stmt.setString(2, table.getSchema().getName());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    boolean isNotNull = rs.getString("IS_NULLABLE").equals("NO");
                    String columnKey = rs.getString("COLUMN_KEY");
                    String defaultValue = rs.getString("COLUMN_DEFAULT");
                    String extra = rs.getString("EXTRA");

                    String dataType = rs.getString("DATA_TYPE");
                    int size = switch (dataType.toLowerCase()) {
                        case "varchar", "char", "binary", "varbinary" -> rs.getInt("CHARACTER_MAXIMUM_LENGTH");
                        case "decimal", "numeric" ->
                            // Consider precision as size
                                rs.getInt("NUMERIC_PRECISION");
                        case "tinyint", "smallint", "mediumint", "int", "integer", "bigint", "float", "double",
                             "double precision", "bit", "date", "time", "datetime", "timestamp", "year", "tinyblob",
                             "blob", "mediumblob", "longblob", "tinytext", "text", "mediumtext", "longtext", "enum",
                             "set", "json", "point", "linestring_2d", "polygon_2d", "linestring_3d", "polygon_3d",
                             "multipoint", "geometry", "geometry_collection", "boolean", "bool" -> 0;
                        default -> 0;
                    };


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
                    column.setSize(size);
                    column.setDefaultValue(defaultValue);

                    column.setTable(table);
                    columns.add(column);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
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
            logger.error(e.getMessage());
        }

        return column;
    }


    public List<String> getColumnNames(Schema schema, Table table){
        List<String> columnNames = new LinkedList<>();
        String query = "SELECT COLUMN_NAME " +
                "FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, schema.getName());
            stmt.setString(2, table.getName());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String columnName = rs.getString("COLUMN_NAME");
                    columnNames.add(columnName);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return columnNames;
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
    // TODO: getForeignKeyByName method
    public List<ForeignKey> getForeignKeys(Schema schema, Table table) {
        if(schema == null) throw new IllegalArgumentException("Schema is not set");
        if(table == null) throw new IllegalArgumentException("Table is not set");

        List<ForeignKey> foreignKeys = new LinkedList<>();
        String query = "SELECT CONSTRAINT_NAME,TABLE_SCHEMA,TABLE_NAME, REFERENCED_TABLE_SCHEMA, REFERENCED_TABLE_NAME \n" +
                "FROM information_schema.KEY_COLUMN_USAGE\n" +
                "WHERE TABLE_SCHEMA=? AND TABLE_NAME=? AND REFERENCED_TABLE_SCHEMA IS NOT NULL;";

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
                    setColumnsOfForeignKey(schemaName,tableName,newForeignKey);
                    setForeignKeyActions(schemaName,tableName,newForeignKey);

                    foreignKeys.add(newForeignKey);
                }
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        return foreignKeys;
    }

    public ForeignKey getForeignKeyByName(Schema schema, Table table, String foreignKeyName){
        if(schema == null) throw new IllegalArgumentException("Schema is not set");
        if(table == null) throw new IllegalArgumentException("Table is not set");
        if(foreignKeyName == null) throw new IllegalArgumentException("Foreign key is not set");

        ForeignKey foreignKey = new ForeignKey();
        String query = "SELECT CONSTRAINT_NAME,TABLE_SCHEMA,TABLE_NAME, REFERENCED_TABLE_SCHEMA, REFERENCED_TABLE_NAME \n" +
                "FROM information_schema.KEY_COLUMN_USAGE\n" +
                "WHERE TABLE_SCHEMA=? AND TABLE_NAME=? AND REFERENCED_TABLE_SCHEMA IS NOT NULL AND CONSTRAINT_NAME=?;";
        try(PreparedStatement fkStmt = connection.prepareStatement(query)){
            fkStmt.setString(1,schema.getName());
            fkStmt.setString(2, table.getName());
            fkStmt.setString(3,foreignKeyName);

            ResultSet fkRs = fkStmt.executeQuery();
            if(fkRs.next()){
                String keyName = fkRs.getString("CONSTRAINT_NAME");
                String schemaName = fkRs.getString("TABLE_SCHEMA");
                String tableName = fkRs.getString("TABLE_NAME");
                Optional<String> referencedSchemaName = Optional.ofNullable(fkRs.getString("REFERENCED_TABLE_SCHEMA"));
                Optional<String> referencedTableName = Optional.ofNullable(fkRs.getString("REFERENCED_TABLE_NAME"));

                Schema referencingSchema = getDatabaseByName(schemaName);
                Table referencingTable = getTableByName(referencingSchema,tableName);

                Schema referencedSchema = getDatabaseByName(referencedSchemaName.orElse(null));
                Table referencedTable = null;

                if(referencedTableName.isPresent()){
                    referencedTable = getTableByName(referencedSchema,referencedTableName.get());
                }

                //Mapping attributes into foreignKey object
                foreignKey.setName(keyName);
                foreignKey.setReferencingSchema(referencingSchema);
                foreignKey.setReferencingTable(referencingTable);
                foreignKey.setReferencedSchema(referencedSchema);
                foreignKey.setReferencedTable(referencedTable);
                setColumnsOfForeignKey(schemaName,tableName,foreignKey);
                setForeignKeyActions(schemaName,tableName,foreignKey);
            }


        }catch (SQLException ex){
            throw new RuntimeException(ex);
        }

        return foreignKey;
    }
    // TODO: Remind yourself english grammar
    /**
     * Retrieves (maps) foreign key columns from result set to object attributes.
     * @param schemaName Name of foreign key schema
     * @param tableName Name of foreign key table.
     * @param foreignKey Object who's action attributes are set
     */
    public void setColumnsOfForeignKey(String schemaName, String tableName, ForeignKey foreignKey){
        String columnQuery = "SELECT COLUMN_NAME, REFERENCED_COLUMN_NAME\n" +
                "FROM information_schema.KEY_COLUMN_USAGE\n" +
                "WHERE TABLE_SCHEMA=? AND TABLE_NAME=? AND CONSTRAINT_NAME=?;";
        try(PreparedStatement ps = connection.prepareStatement(columnQuery)){
            ps.setString(1,schemaName);
            ps.setString(2,tableName);
            ps.setString(3,foreignKey.getName());

            ResultSet columnsRs = ps.executeQuery();

            while(columnsRs.next()){
                Optional<String> referencingColumnStr = Optional.ofNullable(columnsRs.getString("COLUMN_NAME"));
                Optional<String> referencedColumnStr = Optional.ofNullable(columnsRs.getString("REFERENCED_COLUMN_NAME"));

                if(referencingColumnStr.isPresent() && referencedColumnStr.isPresent()){
                    Column referencingColumn = getColumnByName(foreignKey.getReferencingTable(), referencingColumnStr.get());
                    Column referencedColumn = getColumnByName(foreignKey.getReferencedTable(), referencedColumnStr.get());
                    foreignKey.getColumnPairs().add(new ForeignKeyColumns(referencingColumn,referencedColumn));
                }

//                referencingColumnStr.ifPresent(s -> foreignKey.getReferencingColumns()
//                        .add(getColumnByName(foreignKey.getReferencingTable(), s)));
//
//                referencedColumnStr.ifPresent(s -> foreignKey.getReferencedColumns()
//                        .add(getColumnByName(foreignKey.getReferencedTable(), s)));

            }

        }catch (SQLException ex){
            throw new RuntimeException(ex);
        }

    }
    // TODO: Remind yourself english grammar
    /**
     * Retrieves (maps) foreign key actions from result set to object attributes.
     * @param schemaName Name of foreign key schema
     * @param tableName Name of foreign key table.
     * @param foreignKey Object who's action attributes are set
     */
    public void setForeignKeyActions(String schemaName, String tableName, ForeignKey foreignKey){
        String actionQuery = "SELECT UPDATE_RULE, DELETE_RULE\n" +
                "FROM information_schema.REFERENTIAL_CONSTRAINTS\n" +
                "WHERE CONSTRAINT_SCHEMA=? AND TABLE_NAME=? AND CONSTRAINT_NAME=?;";

        try(PreparedStatement actionStmt = connection.prepareStatement(actionQuery)){
            actionStmt.setString(1,schemaName);
            actionStmt.setString(2,tableName);
            actionStmt.setString(3,foreignKey.getName());
            ResultSet actionRs = actionStmt.executeQuery();
            if(actionRs.next()){
                String onUpdate = actionRs.getString("UPDATE_RULE").replace(" ","_");
                foreignKey.setOnUpdateAction(Action.valueOf(onUpdate));
                String onDelete = actionRs.getString("DELETE_RULE").replace(" ","_");
                foreignKey.setOnDeleteAction(Action.valueOf(onDelete));
            }
        }catch(SQLException ex){
            throw new RuntimeException(ex);
        }

    }

    public List<Trigger> getTriggers(Table table) {
        List<Trigger> triggers = new ArrayList<>();
        String schemaName = table.getSchema().getName();
        String tableName = table.getName();

        String sql = "SELECT TRIGGER_NAME, EVENT_MANIPULATION, ACTION_TIMING, " +
            "ACTION_STATEMENT, CREATED, DEFINER " +
            "FROM INFORMATION_SCHEMA.TRIGGERS " +
            "WHERE TRIGGER_SCHEMA = ? AND EVENT_OBJECT_TABLE = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, schemaName);
            stmt.setString(2, tableName);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("TRIGGER_NAME");
                    String eventStr = rs.getString("EVENT_MANIPULATION");
                    String timingStr = rs.getString("ACTION_TIMING");
                    String statement = rs.getString("ACTION_STATEMENT");
                    Timestamp createdTs = rs.getTimestamp("CREATED");
                    String definer = rs.getString("DEFINER");

                    // Convert SQL timestamp to LocalDateTime
                    LocalDateTime createdAt = createdTs != null ? createdTs.toLocalDateTime() : null;

                    // Assume triggers are enabled by default (MySQL < 8.0.22 doesn't expose this)
                    boolean isEnabled = true;

                    // Map string to enum safely
                    Event event = Event.valueOf(eventStr.toUpperCase());
                    Timing timing = Timing.valueOf(timingStr.toUpperCase());

                    Trigger trigger = new Trigger(
                        name,
                        event,
                        timing,
                        table,
                        statement,      // as description
                        createdAt,
                        isEnabled,
                        definer,
                        statement
                    );

                    triggers.add(trigger);
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to load triggers for table: {}", (Object) e.getStackTrace());
        }

        return triggers;
    }

    public Trigger getTriggerByName(Table table, String triggerName) {
        String schemaName = table.getSchema().getName();
        String tableName = table.getName();

        String sql = "SELECT TRIGGER_NAME, EVENT_MANIPULATION, ACTION_TIMING, " +
            "ACTION_STATEMENT, CREATED, DEFINER " +
            "FROM INFORMATION_SCHEMA.TRIGGERS " +
            "WHERE TRIGGER_SCHEMA = ? AND EVENT_OBJECT_TABLE = ? AND TRIGGER_NAME = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, schemaName);
            stmt.setString(2, tableName);
            stmt.setString(3, triggerName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("TRIGGER_NAME");
                    String eventStr = rs.getString("EVENT_MANIPULATION");
                    String timingStr = rs.getString("ACTION_TIMING");
                    String statement = rs.getString("ACTION_STATEMENT");
                    Timestamp createdTs = rs.getTimestamp("CREATED");
                    String definer = rs.getString("DEFINER");

                    // Convert SQL timestamp to LocalDateTime
                    LocalDateTime createdAt = createdTs != null ? createdTs.toLocalDateTime() : null;

                    // Assume triggers are enabled by default (MySQL < 8.0.22 doesn't expose this)
                    boolean isEnabled = true;

                    // Map string to enum safely
                    Event event = Event.valueOf(eventStr.toUpperCase());
                    Timing timing = Timing.valueOf(timingStr.toUpperCase());

                    return new Trigger(
                        name,
                        event,
                        timing,
                        table,
                        statement,      // as description
                        createdAt,
                        isEnabled,
                        definer,
                        statement
                    );
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to find trigger: {}", (Object) e.getStackTrace());
        }
        return null;
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
        List<Index> indexList = new LinkedList<>();

        String sql = "SHOW INDEX FROM " + table.getName() + " FROM " + schema.getName();

        DatabaseConnection.getInstance().setCurrentSchema(schema);
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try(Statement st = conn.createStatement()) {

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                String indexName = rs.getString("KEY_NAME");
                String storageTypeString = rs.getString("INDEX_TYPE");
                IndexStorageType storageType = IndexStorageType.valueOf(storageTypeString.toUpperCase());
                int keyBlockSize = rs.getInt("PACKED");
//                String parser = rs.getString("Parser");

                // Get visibility while remaining MariaDB/MySQL independant
                boolean visible;
                try{
                    // User has MySQL running and column is named VISIBLE
                    visible = "YES".equals(rs.getString("VISIBLE"));
                } catch (SQLException e) {
                    // User has MariaDB running and column is named IGNORED
                    logger.warn("Exception thrown while attempting to get VISIBLE column; Attempting to get IGNORED column (MariaDB)." );
                    visible = "NO".equals(rs.getString("IGNORED"));
                }
                boolean unique = rs.getInt("NON_UNIQUE") == 0; // 0 means unique, 1 means not unique



                Index index = new Index(indexName, null, storageType, keyBlockSize, null, visible, null, unique,table);
                List<IndexedColumn> indexedColumns = getIndexedColumns(schema, table, index);
                index.setIndexedColumns(indexedColumns);
                indexList.add(index);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        DatabaseConnection.getInstance().setCurrentSchema(null);
        return indexList;
    }

    public Index getIndexByName(Schema schema,Table table, String indexName){
        if(schema == null) throw new IllegalArgumentException("Schema is not set");
        if(table == null) throw new IllegalArgumentException("Table is not set");
        Index index = null;

        String sql = "SHOW INDEX FROM " + table.getName() + " FROM " + schema.getName() + " WHERE Key_name = '" + indexName + "'";

        DatabaseConnection.getInstance().setCurrentSchema(schema);
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try(Statement st = conn.createStatement()) {

            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                String storageTypeString = rs.getString("INDEX_TYPE");
                IndexStorageType storageType = IndexStorageType.valueOf(storageTypeString.toUpperCase());
                int keyBlockSize = rs.getInt("PACKED");
//                String parser = rs.getString("Parser");

                // Get visibility while remaining MariaDB/MySQL independant
                boolean visible;
                try{
                    // User has MySQL running and column is named VISIBLE
                    visible = "YES".equals(rs.getString("VISIBLE"));
                } catch (SQLException e) {
                    // User has MariaDB running and column is named IGNORED
                    logger.warn("Exception thrown while attempting to get VISIBLE column; Attempting to get IGNORED column (MariaDB)." );
                    visible = "NO".equals(rs.getString("IGNORED"));
                }

                boolean unique = rs.getInt("NON_UNIQUE") == 0; // 0 means unique, 1 means not unique



                index = new Index(indexName, null, storageType, keyBlockSize, null, visible, null, unique,table);
                List<IndexedColumn> indexedColumns = getIndexedColumns(schema, table, index);
                index.setIndexedColumns(indexedColumns);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        DatabaseConnection.getInstance().setCurrentSchema(null);
        return index;
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

    public List<Trigger> getTriggers(Schema schema) {
        List<Trigger> triggers = new LinkedList<>();
        String query = "SELECT TRIGGER_NAME, EVENT_MANIPULATION,ACTION_TIMING, EVENT_OBJECT_TABLE, ACTION_STATEMENT " +
                "FROM information_schema.TRIGGERS WHERE TRIGGER_SCHEMA = ?;";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, schema.getName());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Trigger trigger = new Trigger();
                trigger.setName(rs.getString("TRIGGER_NAME"));
                trigger.setEvent(Event.valueOf(rs.getString("EVENT_MANIPULATION").toUpperCase()));
                trigger.setTiming(Timing.valueOf(rs.getString("ACTION_TIMING").toUpperCase()));
                String tableName = rs.getString("EVENT_OBJECT_TABLE");
                Table table = DatabaseInspector.getInstance().getTableByName(schema,tableName);
                trigger.setTable(table);
                trigger.setStatement(rs.getString("ACTION_STATEMENT"));

                triggers.add(trigger);
            }
        } catch (SQLException ex) {
            logger.error("Error retrieving triggers: {}", ex.getMessage());
        }
        return triggers;
    }

    public Trigger getTriggerByName(Schema schema, String name) {
        Trigger trigger = new Trigger();

        String query = "SELECT TRIGGER_NAME, EVENT_MANIPULATION, ACTION_TIMING, " +
                "EVENT_OBJECT_TABLE, ACTION_STATEMENT, CREATED, " +
                "DEFINER " +
                "FROM INFORMATION_SCHEMA.TRIGGERS " +
                "WHERE TRIGGER_NAME = ? AND TRIGGER_SCHEMA = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setString(2, schema.getName());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    trigger.setName(rs.getString("TRIGGER_NAME"));

                    String eventType = rs.getString("EVENT_MANIPULATION");
                    String timingType = rs.getString("ACTION_TIMING");

                    if (eventType != null) {
                        trigger.setEvent(Event.valueOf(eventType.toUpperCase()));
                    }
                    if (timingType != null) {
                        trigger.setTiming(Timing.valueOf(timingType.toUpperCase()));
                    }


                    Table table = DatabaseInspector.getInstance().getTableByName(schema, rs.getString("EVENT_OBJECT_TABLE"));
                    if (table != null) {
                        trigger.setTable(table);
                    }

                    trigger.setStatement(rs.getString("ACTION_STATEMENT"));

                    if (rs.getTimestamp("CREATED") != null) {
                        trigger.setCreatedAt(rs.getTimestamp("CREATED").toLocalDateTime());
                    }

                    trigger.setDefiner(rs.getString("DEFINER"));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving trigger: {}", e.getMessage());
        }

        return trigger;
    }

    public List<String> getViewNames(Schema schema) {
        List<String> viewNames = new LinkedList<>();
        String schemaName = schema.getName();

        String query = "SELECT TABLE_NAME FROM information_schema.views WHERE TABLE_SCHEMA = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, schemaName);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    viewNames.add(rs.getString("TABLE_NAME"));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving view names: {}", e.getMessage());
        }

        return viewNames;
    }

    public List<View> getViews(@NotNull Schema schema) {
        List<View> views = new LinkedList<>();
        String schemaName = schema.getName();

        String query = "SELECT TABLE_NAME FROM information_schema.views WHERE TABLE_SCHEMA = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, schemaName);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    views.add(new View(
                        schema,
                        rs.getString("TABLE_NAME"),
                        rs.getString("VIEW_DEFINITION")
                    ));
                }
            }
        } catch (SQLException e) {
            logger.error("Error retrieving views: {}", e.getMessage());
        }

        return views;
    }
}
