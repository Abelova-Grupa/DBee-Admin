package com.abelovagrupa.dbeeadmin.services;

import com.abelovagrupa.dbeeadmin.connection.DatabaseConnection;
import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.column.DataType;
import com.abelovagrupa.dbeeadmin.model.foreignkey.ForeignKey;
import com.abelovagrupa.dbeeadmin.model.index.Index;
import com.abelovagrupa.dbeeadmin.model.index.IndexType;
import com.abelovagrupa.dbeeadmin.model.index.IndexedColumn;
import com.abelovagrupa.dbeeadmin.model.schema.Charset;
import com.abelovagrupa.dbeeadmin.model.schema.Collation;
import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.model.table.Table;
import com.abelovagrupa.dbeeadmin.model.trigger.Trigger;
import com.abelovagrupa.dbeeadmin.model.view.Algorithm;
import com.abelovagrupa.dbeeadmin.model.view.View;
import com.abelovagrupa.dbeeadmin.util.AlertManager;
import com.abelovagrupa.dbeeadmin.view.DialogSQLPreview;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A utility class that generates DDL statements from domain objects, and persists changes
 * to the database.
 */
public class DDLGenerator {

    public static final Logger logger = LogManager.getRootLogger();

    //MySql documentation: CREATE SCHEMA is a synonym for CREATE DATABASE as of MySQL 5.0.2.

    /**
     * Creates new empty database/schema.<br>
     * <i>MySql documentation: CREATE SCHEMA is a synonym for CREATE DATABASE as of MySQL 5.0.2.</i>
     *
     * @param schema Schema object with mandatory name, collation and charset
     * @throws SQLException             on SQL error
     * @throws IllegalArgumentException if schema name, collation or charset are not set
     */
    public static String createDatabase(Schema schema) throws SQLException {

        // Validate
        if (schema.getName() == null) throw new IllegalArgumentException("Undefined schema name.");
        if (schema.getCollation() == null) throw new IllegalArgumentException("Undefined schema collation.");
        if (schema.getCharset() == null) throw new IllegalArgumentException("Undefined schema character set.");

        // Create query
        String query = "CREATE DATABASE " + schema.getName() + "\n";
        if (!schema.getCharset().equals(Charset.DEFAULT)) {
            query += " CHARACTER SET " + schema.getCharset().name() + "\n";
            if (!schema.getCollation().equals(Collation.DEFAULT)) {
                query += "COLLATE " + schema.getCollation().name() + ";";
            }
        }
        query += ";";
        // Bad implementation
        String finalQuery = query;

        // If preview is selected (user gets to decide whether sql should be executed)
        return query;

    }

    // Extracted method
    private static void executeUpdate(String query) {
        try {
            Connection conn = DatabaseConnection.getInstance().getConnection();
            Statement st = conn.createStatement();
            st.executeUpdate(query);
        } catch (SQLException e) {
            AlertManager.showErrorDialog("Error executing SQL", null, e.getMessage());
        }
    }


    /**
     * Creates a database table from a Table object.<br/>
     * <i>Note: Table must have a name, schema and at least one column set.</i>
     *
     * @param table Table object to be persisted to the database. Used as P. O. Pattern.
     * @throws SQLException             on SQL error.
     * @throws IllegalArgumentException if schema does not have a name, schema and at least one column set.
     */

    public static String createTableCreationQuery(Table table) {
        if (table.getSchema() == null) throw new IllegalArgumentException("Schema is not set.");
        if (table.getName() == null) throw new IllegalArgumentException("Undefined table name.");
        if (table.getColumns() == null || table.getColumns().isEmpty())
            throw new IllegalArgumentException("Table must have at least one column.");

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("CREATE TABLE IF NOT EXISTS ");
        queryBuilder.append(table.getSchema().getName());
        queryBuilder.append('.');
        queryBuilder.append(table.getName()).append("( \n");

        for (Column c : table.getColumns()) {
            queryBuilder.append(convertColumnToSQL(c));
        }

        // TODO: Engine
        queryBuilder.setLength(queryBuilder.length() - 2); // Cut trailing comma and enter
        queryBuilder.append("\n)");

        return queryBuilder.toString();

    }

    public static String createTableAlterQuery(Table table){
        return "ALTER TABLE " + table.getSchema().getName()+ "." + table.getName();
    }

    public static String createSchemaDropQuery(Schema schema) {
        if (schema.getName().isEmpty() || schema.getName() == null)
            throw new IllegalArgumentException("Schema name is not set!");
        // Build
        return "DROP DATABASE IF EXISTS " + schema.getName();
    }

    public static String createTableDropQuery(Table table) {
        if (table.getName().isEmpty() || table.getName() == null)
            throw new IllegalArgumentException("Table name is not set!");
        if (table.getSchema() == null)
            throw new IllegalArgumentException("Schema is not set!");
        if (table.getSchema().getName().isEmpty() || table.getSchema().getName() == null)
            throw new IllegalArgumentException("Schema name is not set!");

        // Build
        return "DROP TABLE IF EXISTS " + table.getSchema().getName() + "." + table.getName();
    }

    public static String createTableTruncateQuery(Table table) {
        // Validate
        if (table.getName().isEmpty() || table.getName() == null)
            throw new IllegalArgumentException("Table name is not set!");

        // Build
        return "TRUNCATE TABLE " + table.getName();
    }

    public static String createColumnAdditionQuery(Column column) {
        // Validate
        if (column.getName().isEmpty() || column.getName() == null)
            throw new IllegalArgumentException("Column name is not set!");
        if (column.getTable().getName().isEmpty() || column.getTable().getName() == null)
            throw new IllegalArgumentException("Table name is not set!");
        if (column.getTable().getSchema() == null)
            throw new IllegalArgumentException("Schema is not set!");
        if (column.getTable().getSchema().getName().isEmpty() || column.getTable().getSchema().getName() == null)
            throw new IllegalArgumentException("Schema name is not set!");

//        StringBuilder queryBuilder = new StringBuilder("ALTER TABLE ");
//        queryBuilder.append(column.getTable().getSchema().getName());
//        queryBuilder.append(".");
//        queryBuilder.append(column.getTable().getName());
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("ADD COLUMN ");

        queryBuilder.append(convertColumnToSQL(column));
        queryBuilder.setLength(queryBuilder.length() - 2);

        return queryBuilder.toString();
    }

    public static String createColumnDropQuery(Column column) {
        // Validate
        if (column.getName().isEmpty() || column.getName() == null)
            throw new IllegalArgumentException("Column name is not set!");
        if (column.getTable().getName().isEmpty() || column.getTable().getName() == null)
            throw new IllegalArgumentException("Table name is not set!");
        if (column.getTable().getSchema() == null)
            throw new IllegalArgumentException("Schema is not set!");
        if (column.getTable().getSchema().getName().isEmpty() || column.getTable().getSchema().getName() == null)
            throw new IllegalArgumentException("Schema name is not set!");

        return "DROP COLUMN " + column.getName();
    }

    public static String createColumnRenameQuery(Column column, String newName) {
        // Validate
        if (column.getName().isEmpty() || column.getName() == null)
            throw new IllegalArgumentException("Column name is not set!");
        if (column.getTable().getName().isEmpty() || column.getTable().getName() == null)
            throw new IllegalArgumentException("Table name is not set!");
        if (column.getTable().getSchema() == null)
            throw new IllegalArgumentException("Schema is not set!");
        if (column.getTable().getSchema().getName().isEmpty() || column.getTable().getSchema().getName() == null)
            throw new IllegalArgumentException("Schema name is not set!");
        return
            "RENAME COLUMN " +
            column.getName() +
            " TO " +
            newName;
    }

    public static String createColumnAlterQuery(Column column) {
        // Validate
        if (column.getName().isEmpty() || column.getName() == null)
            throw new IllegalArgumentException("Column name is not set!");
        if (column.getTable().getName().isEmpty() || column.getTable().getName() == null)
            throw new IllegalArgumentException("Table name is not set!");
        if (column.getTable().getSchema() == null)
            throw new IllegalArgumentException("Schema is not set!");
        if (column.getTable().getSchema().getName().isEmpty() || column.getTable().getSchema().getName() == null)
            throw new IllegalArgumentException("Schema name is not set!");

        StringBuilder queryBuilder = new StringBuilder("MODIFY COLUMN \n");
        queryBuilder.append(convertColumnToSQL(column));
        queryBuilder.setLength(queryBuilder.length() - 1);

        return queryBuilder.toString();
    }

    public static String createForeignKeyCreationQuery(ForeignKey foreignKey) {
        if (foreignKey == null || foreignKey.getName() == null || foreignKey.getName().isEmpty())
            throw new IllegalArgumentException("Foreign key is not set");
        if (foreignKey.getReferencingTable() == null)
            throw new IllegalArgumentException("Referencing table is not set");
        if (foreignKey.getReferencingSchema() == null)
            throw new IllegalArgumentException("Referencing schema is not set");
        Table table = foreignKey.getReferencingTable();
        Schema schema = foreignKey.getReferencingSchema();
        if (schema == null || schema.getName() == null || schema.getName().isEmpty())
            throw new IllegalArgumentException("Schema is not set");
        if (table == null || table.getName() == null || table.getName().isEmpty())
            throw new IllegalArgumentException("Table is not set");
        if (foreignKey.getReferencedColumns() == null || foreignKey.getReferencedColumns().isEmpty())
            throw new IllegalArgumentException("Referenced columns are not set");
        if (foreignKey.getReferencingColumns() == null || foreignKey.getReferencingColumns().isEmpty())
            throw new IllegalArgumentException("Referencing columns are not set");

        StringBuilder queryBuilder = new StringBuilder("ADD CONSTRAINT ").append(foreignKey.getName()).append("\n");
        queryBuilder.append("FOREIGN KEY (");
        for (Column referencingColumn : foreignKey.getReferencingColumns()) {
            queryBuilder.append(referencingColumn.getName());
            queryBuilder.append(", ");
        }
        queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length() - 1);
        queryBuilder.append(")\n");
        queryBuilder.append("REFERENCES ").append(foreignKey.getReferencedSchema().getName()).append(".").append(foreignKey.getReferencedTable().getName());
        queryBuilder.append(" (");
        for (Column referencedColumn : foreignKey.getReferencedColumns()) {
            queryBuilder.append(referencedColumn.getName());
            queryBuilder.append(", ");
        }
        queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length() - 1);
        queryBuilder.append(")\n");
        queryBuilder.append("ON DELETE ").append(foreignKey.getOnDeleteAction().toString().replace("_", " ")).append("\n");
        queryBuilder.append("ON UPDATE ").append(foreignKey.getOnUpdateAction().toString().replace("_", " "));

        return queryBuilder.toString();
    }

    public static String createForeignKeyDropQuery(ForeignKey foreignKey) {
        if (foreignKey == null || foreignKey.getName() == null || foreignKey.getName().isEmpty())
            throw new IllegalArgumentException("Foreign key is not set");
        Schema schema = foreignKey.getReferencingSchema();
        Table table = foreignKey.getReferencingTable();
        if (schema == null || schema.getName() == null || schema.getName().isEmpty())
            throw new IllegalArgumentException("Schema is not set");
        if (table == null || table.getName() == null || table.getName().isEmpty())
            throw new IllegalArgumentException("Table is not set");

        return "DROP FOREIGN KEY " + foreignKey.getName();
    }

    public static String createForeignKeyRenameQuery(ForeignKey foreignKey, String newName) {
        if (foreignKey == null || foreignKey.getName() == null || foreignKey.getName().isEmpty())
            throw new IllegalArgumentException("Foreign key is not set");
        if (foreignKey.getReferencedColumns() == null || foreignKey.getReferencedColumns().isEmpty())
            throw new IllegalArgumentException("Referenced columns are not set");
        if (foreignKey.getReferencingColumns() == null || foreignKey.getReferencingColumns().isEmpty())
            throw new IllegalArgumentException("Referencing columns are not set");
        Schema schema = foreignKey.getReferencingSchema();
        Table table = foreignKey.getReferencedTable();
        if (schema == null || schema.getName() == null || schema.getName().isEmpty())
            throw new IllegalArgumentException("Schema is not set");
        if (table == null || table.getName() == null || table.getName().isEmpty())
            throw new IllegalArgumentException("Table is not set");


        String dropQueryBuilder = "ALTER TABLE " + schema.getName() + "." + table.getName() + "\n" +
            "DROP FOREIGN KEY " + foreignKey.getName() + ";" + "\n";

//        String query = dropQueryBuilder.toString();
//        Connection conn = DatabaseConnection.getInstance().getConnection();
//        Statement st = conn.createStatement();
//        st.addBatch(query);

        StringBuilder newQueryBuilder = new StringBuilder("ALTER TABLE ");
        newQueryBuilder.append(schema.getName()).append(".").append(table.getName()).append("\n");
        newQueryBuilder.append("ADD CONSTRAINT ").append(newName).append("\n");
        newQueryBuilder.append("FOREIGN KEY (");
        for (Column referencingColumn : foreignKey.getReferencingColumns()) {
            newQueryBuilder.append(referencingColumn.getName());
            newQueryBuilder.append(", ");
        }
        newQueryBuilder.append(")\n");
        newQueryBuilder.append("REFERENCES ").append(foreignKey.getReferencedSchema()).append(".").append(foreignKey.getReferencedTable());
        newQueryBuilder.append(" (");
        for (Column referencedColumn : foreignKey.getReferencedColumns()) {
            newQueryBuilder.append(referencedColumn.getName());
            newQueryBuilder.append(", ");
        }
        newQueryBuilder.append(");");

        return dropQueryBuilder + newQueryBuilder + " ;";
    }

    public static String createForeignKeyAlterQuery(ForeignKey oldForeignKey, ForeignKey newForeignKey) {
        return createForeignKeyDropQuery(oldForeignKey) + "\n" + createForeignKeyCreationQuery(newForeignKey);
    }

    public static String createIndexCreationQuery(Index index) {
        if (index == null) throw new IllegalArgumentException("Index is not set");
        if (index.getTable().getSchema() == null) throw new IllegalArgumentException("Schema is not set");
        if (index.getTable() == null) throw new IllegalArgumentException("Table is not set");

        StringBuilder queryBuilder = new StringBuilder("ADD ")
                .append(
            !index.getType().equals(IndexType.INDEX) ? index.getType() + " " : " "
        ).append("INDEX ").append(index.getName()).append(" (");

        Comparator<IndexedColumn> indexComparator = new Comparator<IndexedColumn>() {
            @Override
            public int compare(IndexedColumn o1, IndexedColumn o2) {
                return Integer.compare(o1.getOrderNumber(), o2.getOrderNumber());
            }
        };

        List<IndexedColumn> sortedIndexedColumns = index.getIndexedColumns();
        sortedIndexedColumns.sort(indexComparator);
//        System.out.println(sortedIndexedColumns);

        for (int i = 0; i < sortedIndexedColumns.size(); i++) {
            IndexedColumn indexedColumn = sortedIndexedColumns.get(i);
            queryBuilder.append(indexedColumn.getColumn().getName());
            if (indexedColumn.getLength() != 0) queryBuilder.append("(").append(indexedColumn.getLength()).append(")");
            if (i != sortedIndexedColumns.size() - 1) queryBuilder.append(", ");
        }

        queryBuilder.append(") USING ").append(index.getStorageType()).append(index.isVisible() ? " VISIBLE " : " INVISIBLE ").append("\n ");
        if (index.getKeyBlockSize() != 0)
            queryBuilder.append("KEY_BLOCK_SIZE = ").append(index.getKeyBlockSize()).append(" ");
        if (index.getParser() != null && !index.getParser().isEmpty())
            queryBuilder.append(" WITH PARSER ").append(index.getParser()).append(" ");

        if (index.getComment() != null && !index.getComment().isEmpty())
            queryBuilder.append("\nCOMMENT ").append("'").append(index.getComment()).append("'");

        return queryBuilder.toString();
    }

    public static String createIndexRenameQuery(Index index, String newName) {
        if (index == null) throw new IllegalArgumentException("Index is not set");
        if (index.getTable().getSchema() == null) throw new IllegalArgumentException("Schema is not set");
        if (index.getTable() == null) throw new IllegalArgumentException("Table is not set");

        return "RENAME INDEX " + index.getName() + " TO " + newName;

    }

    public static String createIndexDropQuery(Index index) {
        if (index == null) throw new IllegalArgumentException("Index is not set");
        if (index.getTable() == null) throw new IllegalArgumentException("Table is not set");
        if (index.getTable().getSchema() == null) throw new IllegalArgumentException("Schema is not set");

        return "DROP INDEX " + index.getName();
    }

    public static String createIndexAlterQuery(Index oldIndex, Index newIndex) {
        return createIndexDropQuery(oldIndex) + "\n" + createIndexCreationQuery(newIndex);
    }

    public static String createTriggerCreationQuery(Trigger trigger) {
        if (trigger == null) throw new IllegalArgumentException("Trigger is not set");
        if (trigger.getTable() == null) throw new IllegalArgumentException("Table is not set");
        if (trigger.getTable().getSchema() == null) throw new IllegalArgumentException("Schema is not set");

        String queryBuilder = "CREATE TRIGGER " + trigger.getName() + " " +
            trigger.getTiming() + " " +
            trigger.getEvent() + " ON " +
            trigger.getTable().getName() +
            " FOR EACH ROW " +
            trigger.getStatement() +
            ";";
        return queryBuilder;
    }

    public static String createTriggerDropQuery(Trigger trigger) {
        if (trigger == null) throw new IllegalArgumentException("Trigger is not set");

        return "DROP TRIGGER IF EXISTS " + trigger.getTable().getSchema().getName() + "." + trigger.getName() + ";";
    }

    public static String createViewCreationQuery(View view, Algorithm algorithm) {
        String queryBuilder = "CREATE\n\tALGORITHM = " + algorithm.name() + "\n" +
            "VIEW " +
            view.getSchema().getName() +
            "." +
            view.getName() +
            " AS\n" +
            view.getDefinition() + ";";

        return queryBuilder;
    }

    public static void createTable(Table table, boolean preview) throws SQLException {

        // Validate
        if (table.getSchema() == null) throw new IllegalArgumentException("Schema is not set.");
        if (table.getName() == null) throw new IllegalArgumentException("Undefined table name.");
        if (table.getColumns() == null || table.getColumns().isEmpty())
            throw new IllegalArgumentException("Table must have at least one column.");

        // Check for primary key
        boolean hasprimaryKey = false;
        for (Column c : table.getColumns()) {
            if (c.isPrimaryKey()) {
                hasprimaryKey = true;
                break;
            }
        }
        //Table does not need to have a primary key column
//        if(!hasprimaryKey) throw new IllegalArgumentException("Table must contain a primary key.");

        // Create query builder (because we don't want a new string in each iteration of a for loop)
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("CREATE TABLE IF NOT EXISTS ");
        queryBuilder.append(table.getSchema().getName());
        queryBuilder.append('.');
        queryBuilder.append(table.getName());
        queryBuilder.append(" (\n");

        for (Column c : table.getColumns()) {
            queryBuilder.append(convertColumnToSQL(c));
        }

        // TODO: Engine
        queryBuilder.setLength(queryBuilder.length() - 2); // Cut trailing comma and enter
        queryBuilder.append("\n);");
        String query = queryBuilder.toString();

        if (preview)
            new DialogSQLPreview(query).showAndWait().ifPresent(b -> {
                if (b) executeUpdate(query);
            });
        else executeUpdate(query);

    }

    /**
     * Converts column data to sql string used for creating a new column. DRY principle applied!
     * <br>
     * For example, Column("id", DataType.LONG, true, true)<br>
     * would be converted to <i>id long PRIMARY KEY AUTO_INCREMENT,</i><br><br>
     * <b>NOTE:</b> Each line shall include a trailing comma, which must be removed if the column is the final
     * one to be added to the table or if it is the sole column being added.
     *
     * @param c Column object to be converted to SQL.
     * @return String containing SQL for column creation.
     */
    private static String convertColumnToSQL(Column c) {

        StringBuilder sql = new StringBuilder();
        // Append name
        sql.append(c.getName());
        sql.append(" ");

        // Append type (Either normal or binary)
        if (c.isBinary()) sql.append("BINARY");
        else sql.append(c.getType().name());

        // Append size
        if (c.getSize() != null && DataType.hasVariableLength(c.getType())) {
            sql.append("(");
            sql.append(c.getSize());
            sql.append(")");
        }

        // In any case append space
        sql.append(" ");

        if (c.isUnsigned()) sql.append("UNSIGNED ");

        // Append optional parameters
        if (c.isPrimaryKey()) sql.append("PRIMARY KEY ");
        if (c.isNotNull()) sql.append("NOT NULL ");
        if (c.isUnique()) sql.append("UNIQUE ");
        if (c.isAutoIncrement()) sql.append("AUTO_INCREMENT ");
        if (c.isZeroFill()) sql.append("ZEROFILL ");
        if (c.getDefaultValue() != null) {
            sql.append("DEFAULT ");
            sql.append(c.getDefaultValue()).append(" ");
        }
        if (c.isGenerationExpression()) {
            sql.append("GENERATED ALWAYS AS (");
            sql.append(c.getDefaultValue()).append(") ");
        }
        sql.setLength(sql.length() - 1); // Cut trailing space
        sql.append(",\n");

        return sql.toString();
    }

    /**
     * Drops the schema/database
     *
     * @param schema Schema to be dropped
     * @throws SQLException
     */
    public static String dropDatabase(Schema schema) throws SQLException {

        // Validate
        if (schema.getName().isEmpty() || schema.getName() == null)
            throw new IllegalArgumentException("Schema name is not set!");

        // Build
        return "DROP DATABASE IF EXISTS " + schema.getName();
    }

    /**
     * Drops the table
     *
     * @param table Table to be dropped.
     * @throws SQLException
     */
    public static void dropTable(Table table, boolean preview) throws SQLException {

        // Validate
        if (table.getName().isEmpty() || table.getName() == null)
            throw new IllegalArgumentException("Table name is not set!");
        if (table.getSchema() == null)
            throw new IllegalArgumentException("Schema is not set!");
        if (table.getSchema().getName().isEmpty() || table.getSchema().getName() == null)
            throw new IllegalArgumentException("Schema name is not set!");

        // Build
        String query = "DROP TABLE IF EXISTS " + table.getSchema().getName() + "." + table.getName();

        // Execute
        if (preview)
            new DialogSQLPreview(query).showAndWait().ifPresent(b -> {
                if (b) executeUpdate(query);
            });
        else executeUpdate(query);
    }

    /**
     * Truncates the database
     *
     * @param schema Database to be truncated.
     * @throws SQLException
     */
    public static void truncateDatabase(Schema schema, boolean preview) throws SQLException {
        /* MySQL doesn't have a single command to truncate the whole schema?? Wtf? */
        throw new UnsupportedOperationException("Not yet implemented...");
    }

    /**
     * Truncates (erases all data but keeps the structure) the table.
     *
     * @param table Table to be truncated, schema must be set.
     * @throws SQLException
     */
    public static void truncateTable(Table table, boolean preview) throws SQLException {

        // Validate
        if (table.getName().isEmpty() || table.getName() == null)
            throw new IllegalArgumentException("Table name is not set!");

        // Build
        String query = "TRUNCATE TABLE " + table.getName();

        // Execute
        if (preview)
            new DialogSQLPreview(query).showAndWait().ifPresent(b -> {
                if (b) executeUpdate(query);
            });
        else executeUpdate(query);

    }

    /**
     * Table alteration: Adds a new column to the table.
     *
     * @param table  Table that is being altered.
     * @param column Column to be added.
     * @throws SQLException
     */
    public static void addColumn(Table table, Column column, boolean preview) throws SQLException {

        // Validate
        if (column.getName().isEmpty() || column.getName() == null)
            throw new IllegalArgumentException("Column name is not set!");
        if (table.getName().isEmpty() || table.getName() == null)
            throw new IllegalArgumentException("Table name is not set!");
        if (table.getSchema() == null)
            throw new IllegalArgumentException("Schema is not set!");
        if (table.getSchema().getName().isEmpty() || table.getSchema().getName() == null)
            throw new IllegalArgumentException("Schema name is not set!");

        StringBuilder queryBuilder = new StringBuilder("ALTER TABLE ");
        queryBuilder.append(table.getSchema().getName());
        queryBuilder.append(".");
        queryBuilder.append(table.getName());
        queryBuilder.append("ADD COLUMN \n");

        queryBuilder.append(convertColumnToSQL(column));
        queryBuilder.setLength(queryBuilder.length() - 1);

        String query = queryBuilder.toString();
        if (preview)
            new DialogSQLPreview(query).showAndWait().ifPresent(b -> {
                if (b) executeUpdate(query);
            });
        else executeUpdate(query);
    }

    /**
     * Table alteration: Drops a column from the table.
     *
     * @param table  Table that is being altered.
     * @param column Column to be dropped.
     * @throws SQLException
     */
    public static void dropColumn(Table table, Column column, boolean preview) throws SQLException {

        // Validate
        if (column.getName().isEmpty() || column.getName() == null)
            throw new IllegalArgumentException("Column name is not set!");
        if (table.getName().isEmpty() || table.getName() == null)
            throw new IllegalArgumentException("Table name is not set!");
        if (table.getSchema() == null)
            throw new IllegalArgumentException("Schema is not set!");
        if (table.getSchema().getName().isEmpty() || table.getSchema().getName() == null)
            throw new IllegalArgumentException("Schema name is not set!");

        String query = "ALTER TABLE " + table.getSchema().getName() +
            "." +
            table.getName() +
            " DROP COLUMN " +
            column.getName();
        if (preview)
            new DialogSQLPreview(query).showAndWait().ifPresent(b -> {
                if (b) executeUpdate(query);
            });
        else executeUpdate(query);
    }

    /**
     * Table alteration: Renames a column.
     *
     * @param table   Table that is being altered.
     * @param column  Column, with set name.
     * @param newName New name for the column.
     * @throws SQLException
     */
    public static void renameColumn(Table table, Column column, String newName, boolean preview) throws SQLException {

        // Validate
        if (column.getName().isEmpty() || column.getName() == null)
            throw new IllegalArgumentException("Column name is not set!");
        if (table.getName().isEmpty() || table.getName() == null)
            throw new IllegalArgumentException("Table name is not set!");
        if (table.getSchema() == null)
            throw new IllegalArgumentException("Schema is not set!");
        if (table.getSchema().getName().isEmpty() || table.getSchema().getName() == null)
            throw new IllegalArgumentException("Schema name is not set!");

        String query = "ALTER TABLE " + table.getSchema().getName() +
            "." +
            table.getName() +
            "RENAME COLUMN " +
            column.getName() +
            " TO " +
            newName;
        if (preview)
            new DialogSQLPreview(query).showAndWait().ifPresent(b -> {
                if (b) executeUpdate(query);
            });
        else executeUpdate(query);
    }

    /**
     * Table alteration: Modifies a column by replacing its parameters.<br>
     * Name of the column should stay the same and parameters shall be updated before calling this method.
     *
     * @param table  Table that is being altered.
     * @param column Column with new parameters, but with <b>old name</b>.
     * @throws SQLException
     */
    public static void modifyColumn(Table table, Column column, boolean preview) throws SQLException {

        // Validate
        if (column.getName().isEmpty() || column.getName() == null)
            throw new IllegalArgumentException("Column name is not set!");
        if (table.getName().isEmpty() || table.getName() == null)
            throw new IllegalArgumentException("Table name is not set!");
        if (table.getSchema() == null)
            throw new IllegalArgumentException("Schema is not set!");
        if (table.getSchema().getName().isEmpty() || table.getSchema().getName() == null)
            throw new IllegalArgumentException("Schema name is not set!");

        StringBuilder queryBuilder = new StringBuilder("ALTER TABLE ");
        queryBuilder.append(table.getSchema().getName());
        queryBuilder.append(".");
        queryBuilder.append(table.getName());
        queryBuilder.append("MODIFY COLUMN \n");

        queryBuilder.append(convertColumnToSQL(column));
        queryBuilder.setLength(queryBuilder.length() - 1);

        String query = queryBuilder.toString();
        if (preview)
            new DialogSQLPreview(query).showAndWait().ifPresent(b -> {
                if (b) executeUpdate(query);
            });
        else executeUpdate(query);
    }

    /**
     * Generates foreign key SQL string in the following form:<br>
     * <code>fk_name FOREIGN KEY(cols) REFERENCES refSchema.refTable(refCols)
     * ON DELETE action
     * ON UPDATE action</code><br>
     * Add 'ADD CONSTRAINT' and end statement with ',' or ';' where needed.
     *
     * @param foreignKey foreign key to be converted to sql
     * @return SQL string
     */
    public static String convertForeignKeyToSQL(ForeignKey foreignKey) {
        StringBuilder queryBuilder = new StringBuilder(foreignKey.getName());

        queryBuilder.append(" ").append("FOREIGN KEY (");
        for (Column referencingColumn : foreignKey.getReferencingColumns()) {
            queryBuilder.append(referencingColumn.getName()).append(", ");
        }
        queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length()); // remove trailing comma
        queryBuilder.append(")\n");

        queryBuilder.append("REFERENCES ")
            .append(foreignKey.getReferencedSchema().getName())
            .append(".")
            .append(foreignKey.getReferencedTable().getName())
            .append(" (");
        for (Column referencedColumn : foreignKey.getReferencedColumns()) {
            queryBuilder.append(referencedColumn.getName()).append(", ");
        }
        queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length()); // remove trailing comma
        queryBuilder.append(")\n");

        queryBuilder.append("ON DELETE ")
            .append(foreignKey.getOnDeleteAction().toString().replace("_", " "))
            .append("\n");

        queryBuilder.append("ON UPDATE ")
            .append(foreignKey.getOnUpdateAction().toString().replace("_", " "));

        return queryBuilder.toString();
    }


    public static void addForeignKey(Schema schema, Table table, ForeignKey foreignKey, boolean preview) throws SQLException {
        if (schema == null || schema.getName() == null || schema.getName().isEmpty())
            throw new IllegalArgumentException("Schema is not set");
        if (table == null || table.getName() == null || table.getName().isEmpty())
            throw new IllegalArgumentException("Table is not set");
        if (foreignKey == null || foreignKey.getName() == null || foreignKey.getName().isEmpty())
            throw new IllegalArgumentException("Foreign key is not set");
        if (foreignKey.getReferencedColumns() == null || foreignKey.getReferencedColumns().isEmpty())
            throw new IllegalArgumentException("Referenced columns are not set");
        if (foreignKey.getReferencingColumns() == null || foreignKey.getReferencingColumns().isEmpty())
            throw new IllegalArgumentException("Referencing columns are not set");

        StringBuilder queryBuilder = new StringBuilder("ALTER TABLE ");
        queryBuilder.append(schema.getName());
        queryBuilder.append(".");
        queryBuilder.append(table.getName());
        queryBuilder.append("\n");
        queryBuilder.append("ADD CONSTRAINT ").append(foreignKey.getName()).append("\n");
        queryBuilder.append("FOREIGN KEY (");
        for (Column referencingColumn : foreignKey.getReferencingColumns()) {
            queryBuilder.append(referencingColumn.getName());
            queryBuilder.append(", ");
        }
        queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length() - 1);
        queryBuilder.append(")\n");
        queryBuilder.append("REFERENCES ").append(foreignKey.getReferencedSchema().getName()).append(".").append(foreignKey.getReferencedTable().getName());
        queryBuilder.append(" (");
        for (Column referencedColumn : foreignKey.getReferencedColumns()) {
            queryBuilder.append(referencedColumn.getName());
            queryBuilder.append(", ");
        }
        queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length() - 1);
        queryBuilder.append(")\n");
        queryBuilder.append("ON DELETE ").append(foreignKey.getOnDeleteAction().toString().replace("_", " ")).append("\n");
        queryBuilder.append("ON UPDATE ").append(foreignKey.getOnUpdateAction().toString().replace("_", " "));
        queryBuilder.append(";");

        String query = queryBuilder.toString();
        if (preview)
            new DialogSQLPreview(query).showAndWait().ifPresent(b -> {
                if (b) executeUpdate(query);
            });
        else executeUpdate(query);

    }

    public static void addForeignKeys(Schema schema, Table table, List<ForeignKey> foreignKeys, boolean preview) throws SQLException {

        StringBuilder allStatements = new StringBuilder("ALTER TABLE ");
        allStatements.append(schema.getName());
        allStatements.append(".");
        allStatements.append(table.getName());
        allStatements.append("\n");

        for (var foreignKey : foreignKeys) {

            if (schema == null || schema.getName() == null || schema.getName().isEmpty())
                throw new IllegalArgumentException("Schema is not set");
            if (table == null || table.getName() == null || table.getName().isEmpty())
                throw new IllegalArgumentException("Table is not set");
            if (foreignKey == null || foreignKey.getName() == null || foreignKey.getName().isEmpty())
                throw new IllegalArgumentException("Foreign key is not set");
            if (foreignKey.getReferencedColumns() == null || foreignKey.getReferencedColumns().isEmpty())
                throw new IllegalArgumentException("Referenced columns are not set");
            if (foreignKey.getReferencingColumns() == null || foreignKey.getReferencingColumns().isEmpty())
                throw new IllegalArgumentException("Referencing columns are not set");

            StringBuilder queryBuilder = new StringBuilder();

            queryBuilder.append("ADD CONSTRAINT ").append(foreignKey.getName()).append("\n");
            queryBuilder.append("FOREIGN KEY (");
            for (Column referencingColumn : foreignKey.getReferencingColumns()) {
                queryBuilder.append(referencingColumn.getName());
                queryBuilder.append(", ");
            }
            queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length() - 1);
            queryBuilder.append(")\n");
            queryBuilder.append("REFERENCES ").append(foreignKey.getReferencedSchema().getName()).append(".").append(foreignKey.getReferencedTable().getName());
            queryBuilder.append(" (");
            for (Column referencedColumn : foreignKey.getReferencedColumns()) {
                queryBuilder.append(referencedColumn.getName());
                queryBuilder.append(", ");
            }
            queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length() - 1);
            queryBuilder.append(")\n");
            queryBuilder.append("ON DELETE ").append(foreignKey.getOnDeleteAction().toString().replace("_", " ")).append("\n");
            queryBuilder.append("ON UPDATE ").append(foreignKey.getOnUpdateAction().toString().replace("_", " "));
            queryBuilder.append(",\n");

            allStatements.append(queryBuilder);
        }

        allStatements.setLength(allStatements.length() - 2);
        allStatements.append(";");

        String query = allStatements.toString();

        if (preview)
            new DialogSQLPreview(query).showAndWait().ifPresent(b -> {
                if (b) executeUpdate(query);
            });
        else executeUpdate(query);

    }

    public static void renameForeignKey(Schema schema, Table table, ForeignKey foreignKey, String newName, boolean preview) throws SQLException {
        if (schema == null || schema.getName() == null || schema.getName().isEmpty())
            throw new IllegalArgumentException("Schema is not set");
        if (table == null || table.getName() == null || table.getName().isEmpty())
            throw new IllegalArgumentException("Table is not set");
        if (foreignKey == null || foreignKey.getName() == null || foreignKey.getName().isEmpty())
            throw new IllegalArgumentException("Foreign key is not set");
        if (foreignKey.getReferencedColumns() == null || foreignKey.getReferencedColumns().isEmpty())
            throw new IllegalArgumentException("Referenced columns are not set");
        if (foreignKey.getReferencingColumns() == null || foreignKey.getReferencingColumns().isEmpty())
            throw new IllegalArgumentException("Referencing columns are not set");

        String query = "ALTER TABLE " + schema.getName() + "." + table.getName() + "\n" +
            "DROP FOREIGN KEY " + foreignKey.getName() + ";" + "\n";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        Statement st = conn.createStatement();
        st.addBatch(query);

        StringBuilder newQueryBuilder = new StringBuilder("ALTER TABLE ");
        newQueryBuilder.append(schema.getName()).append(".").append(table.getName()).append("\n");
        newQueryBuilder.append("ADD CONSTRAINT ").append(newName).append("\n");
        newQueryBuilder.append("FOREIGN KEY (");
        for (Column referencingColumn : foreignKey.getReferencingColumns()) {
            newQueryBuilder.append(referencingColumn.getName());
            newQueryBuilder.append(", ");
        }
        newQueryBuilder.append(")\n");
        newQueryBuilder.append("REFERENCES ").append(foreignKey.getReferencedSchema()).append(".").append(foreignKey.getReferencedTable());
        newQueryBuilder.append(" (");
        for (Column referencedColumn : foreignKey.getReferencedColumns()) {
            newQueryBuilder.append(referencedColumn.getName());
            newQueryBuilder.append(", ");
        }
        newQueryBuilder.append(");");

        query = newQueryBuilder.toString();
        st.addBatch(query);
        st.executeBatch(); // E jebiga, ne mogu ovde da izvucem jer je radjeno kao batch
    }

    public static void dropForeignKey(Schema schema, Table table, ForeignKey foreignKey, boolean preview) throws SQLException {
        if (schema == null || schema.getName() == null || schema.getName().isEmpty())
            throw new IllegalArgumentException("Schema is not set");
        if (table == null || table.getName() == null || table.getName().isEmpty())
            throw new IllegalArgumentException("Table is not set");
        if (foreignKey == null || foreignKey.getName() == null || foreignKey.getName().isEmpty())
            throw new IllegalArgumentException("Foreign key is not set");

        String query = "ALTER TABLE " + schema.getName() + "." + table.getName() + "\n" +
            " DROP FOREIGN KEY " + foreignKey.getName() + ";";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        Statement st = conn.createStatement();
        if (preview)
            new DialogSQLPreview(query).showAndWait().ifPresent(b -> {
                if (b) executeUpdate(query);
            });
        else executeUpdate(query);

    }

    public static void modifyForeignKey(Schema schema, Table table, ForeignKey oldForeignKey, ForeignKey newForeignKey) throws SQLException {
        if (schema == null || schema.getName() == null || schema.getName().isEmpty())
            throw new IllegalArgumentException("Schema is not set");
        if (table == null || table.getName() == null || table.getName().isEmpty())
            throw new IllegalArgumentException("Table is not set");
        if (oldForeignKey == null || oldForeignKey.getName() == null || oldForeignKey.getName().isEmpty())
            throw new IllegalArgumentException("Old foreign key is not set");
        if (newForeignKey == null || newForeignKey.getName() == null || newForeignKey.getName().isEmpty())
            throw new IllegalArgumentException("New foreign key is not set");

        dropForeignKey(schema, table, oldForeignKey, false);
        addForeignKey(schema, table, newForeignKey, false);
    }


    /**
     * Generates index SQL string in the following form:<br>
     * <code>[UNIQUE] INDEX user_idx USING BTREE (cols)
     * KEY_BLOCK_SIZE = 69 VISIBLE COMMENT 'Comment text';</code><br>
     * Add 'ALTER TABLE ... ADD' and end statement with ',' or ';' where needed.
     *
     * @param index index to be converted to SQL.
     * @return SQL in the form written above
     */
    public static String convertIndexToSQL(Index index) {
        StringBuilder queryBuilder = new StringBuilder();

        // Optional index type (e.g., FULLTEXT, UNIQUE), skip if just "INDEX"
        try {
            if (!index.getType().equals(IndexType.INDEX)) {
                queryBuilder.append(index.getType()).append(" ");
            }
        } catch (NullPointerException e) {
            logger.error("IndexType of {} is null; Default value set.", index.getName());
        }


        queryBuilder.append("INDEX ").append(index.getName())
            .append(" USING ").append(index.getStorageType()).append(" (");
        // Sort indexed columns by order number
        List<IndexedColumn> sortedIndexedColumns = new ArrayList<>(index.getIndexedColumns());
        sortedIndexedColumns.sort(Comparator.comparingInt(IndexedColumn::getOrderNumber));

        for (int i = 0; i < sortedIndexedColumns.size(); i++) {
            IndexedColumn indexedColumn = sortedIndexedColumns.get(i);
            queryBuilder.append(indexedColumn.getColumn().getName());
            if (indexedColumn.getLength() != 0) {
                queryBuilder.append("(").append(indexedColumn.getLength()).append(")");
            }
            if (i < sortedIndexedColumns.size() - 1) {
                queryBuilder.append(", ");
            }
        }

        queryBuilder.append(")");

        if (index.getKeyBlockSize() != 0) {
            queryBuilder.append(" KEY_BLOCK_SIZE = ").append(index.getKeyBlockSize());
        }

        if (index.getParser() != null && !index.getParser().isEmpty()) {
            queryBuilder.append(" WITH PARSER ").append(index.getParser());
        }

        queryBuilder.append(index.isVisible() ? " VISIBLE" : " INVISIBLE");

        if (index.getComment() != null && !index.getComment().isEmpty()) {
            queryBuilder.append(" COMMENT '").append(index.getComment()).append("'");
        }

        queryBuilder.append(";");

        return queryBuilder.toString();
    }

    public static void addIndex(Schema schema, Table table, Index index, boolean preview) throws SQLException {
        if (schema == null) throw new IllegalArgumentException("Schema is not set");
        if (table == null) throw new IllegalArgumentException("Table is not set");
        if (index == null) throw new IllegalArgumentException("Index is not set");

        StringBuilder queryBuilder = new StringBuilder("ALTER TABLE ");
        queryBuilder.append(schema.getName()).append(".").append(table.getName()).append("\n");
        queryBuilder.append("ADD ").append(
            !index.getType().equals(IndexType.INDEX) ? index.getType() + " " : " "
        ).append("INDEX ").append(index.getName()).append(" USING ").append(index.getStorageType()).append(" (");

        Comparator<IndexedColumn> indexComparator = new Comparator<IndexedColumn>() {
            @Override
            public int compare(IndexedColumn o1, IndexedColumn o2) {
                return Integer.compare(o1.getOrderNumber(), o2.getOrderNumber());
            }
        };

        List<IndexedColumn> sortedIndexedColumns = index.getIndexedColumns();
        sortedIndexedColumns.sort(indexComparator);
//        System.out.println(sortedIndexedColumns);

        for (int i = 0; i < sortedIndexedColumns.size(); i++) {
            IndexedColumn indexedColumn = sortedIndexedColumns.get(i);
            queryBuilder.append(indexedColumn.getColumn().getName());
            if (indexedColumn.getLength() != 0) queryBuilder.append("(").append(indexedColumn.getLength()).append(")");
            if (i != sortedIndexedColumns.size() - 1) queryBuilder.append(", ");
        }

        queryBuilder.append(")\n ");
        if (index.getKeyBlockSize() != 0)
            queryBuilder.append("KEY_BLOCK_SIZE = ").append(index.getKeyBlockSize()).append(" ");
        if (index.getParser() != null && !index.getParser().isEmpty())
            queryBuilder.append(" WITH PARSER ").append(index.getParser()).append(" ");
        queryBuilder.append(index.isVisible() ? "VISIBLE " : "INVISIBLE ");
        if (index.getComment() != null && !index.getComment().isEmpty())
            queryBuilder.append("\nCOMMENT ").append("'").append(index.getComment()).append("';");
        else queryBuilder.append(";");

        String query = queryBuilder.toString();
        if (preview)
            new DialogSQLPreview(query).showAndWait().ifPresent(b -> {
                if (b) executeUpdate(query);
            });
        else executeUpdate(query);

    }

    public static void addIndexes(Schema schema, Table table, List<Index> indexList, boolean preview) {
        if (schema == null) throw new IllegalArgumentException("Schema is not set");
        if (table == null) throw new IllegalArgumentException("Table is not set");
        if (indexList == null) throw new IllegalArgumentException("Index list is not set");

        StringBuilder allStatements = new StringBuilder("ALTER TABLE ");
        allStatements.append(schema.getName()).append(".").append(table.getName()).append("\n");
        for (var index : indexList) {

            StringBuilder queryBuilder = new StringBuilder();

            queryBuilder.append("ADD ").append(
                !index.getType().equals(IndexType.INDEX) ? index.getType() + " " : ""
            ).append("INDEX ").append(index.getName()).append(" USING ").append(index.getStorageType()).append(" (");

            Comparator<IndexedColumn> indexComparator = new Comparator<IndexedColumn>() {
                @Override
                public int compare(IndexedColumn o1, IndexedColumn o2) {
                    if (o1.getOrderNumber() == o2.getOrderNumber()) return 0;
                    else if (o1.getOrderNumber() < o2.getOrderNumber()) return -1;
                    else return 1;
                }
            };

            List<IndexedColumn> unsortedIndexedColumns = index.getIndexedColumns();
            // Unsupported operation for immutable lists
            List<IndexedColumn> sortedIndexedColumns = new ArrayList<>(unsortedIndexedColumns);
            sortedIndexedColumns.sort(indexComparator);
//        System.out.println(sortedIndexedColumns);

            for (int i = 0; i < sortedIndexedColumns.size(); i++) {
                IndexedColumn indexedColumn = sortedIndexedColumns.get(i);
                queryBuilder.append(indexedColumn.getColumn().getName());
                if (indexedColumn.getLength() != 0)
                    queryBuilder.append("(").append(indexedColumn.getLength()).append(")");
                if (i != sortedIndexedColumns.size() - 1) queryBuilder.append(", ");
            }

            queryBuilder.append(") ");
            if (index.getKeyBlockSize() != 0)
                queryBuilder.append("KEY_BLOCK_SIZE = ").append(index.getKeyBlockSize()).append(" ");
            if (index.getParser() != null && !index.getParser().isEmpty())
                queryBuilder.append(" WITH PARSER ").append(index.getParser()).append(" ");
            queryBuilder.append(index.isVisible() ? "VISIBLE " : "INVISIBLE ");
            if (index.getComment() != null && !index.getComment().isEmpty())
                queryBuilder.append("\nCOMMENT ").append("'").append(index.getComment()).append("',");
            else {
                queryBuilder.setLength(queryBuilder.length() - 1);
                queryBuilder.append(",\n");
            }

            allStatements.append(queryBuilder);
        }

        // Cut the last comma
        allStatements.setLength(allStatements.length() - 2);
        allStatements.append(';');

        String query = allStatements.toString();
        if (preview)
            new DialogSQLPreview(query).showAndWait().ifPresent(b -> {
                if (b) executeUpdate(query);
            });
        else executeUpdate(query);
    }

    public static void renameIndex(Schema schema, Table table, Index index, String newName, boolean preview) throws SQLException {
        if (schema == null) throw new IllegalArgumentException("Schema is not set");
        if (table == null) throw new IllegalArgumentException("Table is not set");
        if (index == null) throw new IllegalArgumentException("Index is not set");

        String query = "ALTER TABLE " + schema.getName() + table.getName() + " " +
            "RENAME INDEX " + index.getName() + " TO " + newName + ";";
        if (preview)
            new DialogSQLPreview(query).showAndWait().ifPresent(b -> {
                if (b) executeUpdate(query);
            });
        else executeUpdate(query);
    }

    public static void dropIndex(Schema schema, Table table, Index index, boolean preview) throws SQLException {
        if (schema == null) throw new IllegalArgumentException("Schema is not set");
        if (table == null) throw new IllegalArgumentException("Table is not set");
        if (index == null) throw new IllegalArgumentException("Index is not set");

        StringBuilder queryBuilder = new StringBuilder("ALTER TABLE ").append(schema.getName())
            .append(".").append(table.getName()).append("\n");
        queryBuilder.append("DROP INDEX ").append(index.getName());

        String query = queryBuilder.toString();
        if (preview)
            new DialogSQLPreview(query).showAndWait().ifPresent(b -> {
                if (b) executeUpdate(query);
            });
        else executeUpdate(query);
    }

    public static void modifyIndex(Schema schema, Table table, Index oldIndex, Index newIndex) throws SQLException {
        if (schema == null) throw new IllegalArgumentException("Schema is not set");
        if (table == null) throw new IllegalArgumentException("Table is not set");
        if (oldIndex == null) throw new IllegalArgumentException("Index is not set");
        if (newIndex == null) throw new IllegalArgumentException("Index is not set");

        dropIndex(schema, table, oldIndex, false);
        addIndex(schema, table, newIndex, false);
    }

    public static void createTrigger(Schema schema, Table table, Trigger trigger, boolean preview) throws SQLException {
        if (schema == null) throw new IllegalArgumentException("Schema is not set");
        if (table == null) throw new IllegalArgumentException("Table is not set");
        if (trigger == null) throw new IllegalArgumentException("Trigger is not set");

        String query = "CREATE TRIGGER " + trigger.getName() + " " +
            trigger.getTiming() + " " +
            trigger.getEvent() + " ON " +
            trigger.getTable().getName() +
            " FOR EACH ROW " +
            trigger.getStatement() +
            ";";

        if (preview)
            new DialogSQLPreview(query).showAndWait().ifPresent(b -> {
                if (b) executeUpdate(query);
            });
        else executeUpdate(query);
    }

    public static void dropTrigger(Trigger trigger, boolean preview) throws SQLException {
        if (trigger == null) throw new IllegalArgumentException("Trigger is not set");

        String query = "DROP TRIGGER IF EXISTS " + trigger.getTable().getSchema().getName() + "." + trigger.getName();

        if (preview)
            new DialogSQLPreview(query).showAndWait().ifPresent(b -> {
                if (b) executeUpdate(query);
            });
        else executeUpdate(query);
    }

    public static void createView(View view, Algorithm algorithm, boolean preview) {

        // TOOD: Validate

        // Build query

        String query = "CREATE\n\tALGORITHM = " + algorithm.name() + "\n" +
            "VIEW " +
            view.getSchema().getName() +
            "." +
            view.getName() +
            " AS\n" +
            view.getDefinition();

        // Connect to schema
        DatabaseConnection.getInstance().setCurrentSchema(view.getSchema());

        if (preview)
            new DialogSQLPreview(query).showAndWait().ifPresent(b -> {
                if (b) executeUpdate(query);
            });
        else executeUpdate(query);

        // Reset connection to the whole dbms (Might not be needed and deleted later)
        DatabaseConnection.getInstance().setCurrentSchema(null);

    }

}
