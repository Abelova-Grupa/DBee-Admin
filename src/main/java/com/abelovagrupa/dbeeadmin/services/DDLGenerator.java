package com.abelovagrupa.dbeeadmin.services;

import com.abelovagrupa.dbeeadmin.connection.DatabaseConnection;
import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.model.table.Table;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A utility class that generates DDL statements from domain objects, and persists changes
 * to the database.
 */
public class DDLGenerator {

    //MySql documentation: CREATE SCHEMA is a synonym for CREATE DATABASE as of MySQL 5.0.2.

    /**Creates new empty database/schema.<br>
     * <i>MySql documentation: CREATE SCHEMA is a synonym for CREATE DATABASE as of MySQL 5.0.2.</i>
     * @param schema Schema object with mandatory name, collation and charset
     * @throws SQLException on SQL error
     * @throws IllegalArgumentException if schema name, collation or charset are not set
     */
    public static void createDatabase(Schema schema) throws SQLException {

        // Validate
        if(schema.getName() == null) throw new IllegalArgumentException("Undefined schema name.");
        if(schema.getCollation() == null) throw new IllegalArgumentException("Undefined schema collation.");
        if(schema.getCharset() == null) throw new IllegalArgumentException("Undefined schema character set.");

        // Create query
        String query = "CREATE DATABASE " + schema.getName() + "\n" +
            "CHARACTER SET " + schema.getCharset().name() + "\n" +
            "COLLATE " + schema.getCollation().name() + ";";

        Connection conn = DatabaseConnection.getInstance().getConnection();
        Statement st = conn.createStatement();
        st.executeUpdate(query);

    }


    /**
     * Creates a database table from a Table object.<br/>
     * <i>Note: Table must have a name, schema and at least one column set.</i>
     * @param table Table object to be persisted to the database. Used as P. O. Pattern.
     * @throws SQLException on SQL error.
     * @throws IllegalArgumentException if schema does not have a name, schema and at least one column set.
     */
    public static void createTable(Table table) throws SQLException {

        // Validate
        if(table.getSchema() == null) throw new IllegalArgumentException("Schema is not set.");
        if(table.getName() == null) throw new IllegalArgumentException("Undefined table name.");
        if(table.getColumns() == null || table.getColumns().isEmpty())
            throw new IllegalArgumentException("Table must have at least one column.");


        // Create query builder (because we don't want a new string in each iteration of a for loop)
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("CREATE TABLE IF NOT EXISTS ");
        queryBuilder.append(table.getSchema().getName());
        queryBuilder.append('.');
        queryBuilder.append(table.getName());
        queryBuilder.append(" (\n");

        for(Column c : table.getColumns()) {
            queryBuilder.append(convertColumnToSQL(c));
        }

        // TODO: Engine
        queryBuilder.setLength(queryBuilder.length() - 2); // Cut trailing comma and enter
        queryBuilder.append("\n);");
        String query = queryBuilder.toString();
        System.out.println(query);

        // Execute query (UNCOMMENT AFTER TESTS!)
//        Connection conn = DatabaseConnection.getInstance().getConnection();
//        Statement st = conn.createStatement();
//        st.executeUpdate(query);

    }

    /**
     * Converts column data to sql string used for creating a new column. DRY principle applied!
     * <br>
     * For example, Column("id", DataType.LONG, true, true)<br>
     * would be converted to <i>id long PRIMARY KEY AUTO_INCREMENT,</i><br><br>
     * <b>NOTE:</b> Each line shall include a trailing comma, which must be removed if the column is the final
     * one to be added to the table or if it is the sole column being added.
     * @param c Column object to be converted to SQL.
     * @return String containing SQL for column creation.
     */
    private static String convertColumnToSQL(Column c) {

        StringBuilder sql = new StringBuilder();
        // Append name
        sql.append(c.getName());
        sql.append(" ");

        // Append type (Either normal or binary)
        if(c.isBinary()) sql.append("BINARY");
        else sql.append(c.getType().name());

        // Append size
        if(c.getSize() != null) {
            sql.append("(");
            sql.append(c.getSize());
            sql.append(")");
        }

        // In any case append space
        sql.append(" ");

        if(c.isUnsigned()) sql.append("UNSIGNED ");

        // Append optional parameters
        if(c.isPrimaryKey()) sql.append("PRIMARY KEY ");
        if(c.isUnique()) sql.append("UNIQUE ");
        if(c.isAutoIncrement()) sql.append("AUTO_INCREMENT ");
        if(c.isZeroFill()) sql.append("ZEROFILL ");
        if(c.getDefaultValue() != null) {
            sql.append("DEFAULT ");
            sql.append(c.getDefaultValue());
        }
        if(c.getGenerationExpression() != null) {
            sql.append("GENERATED ALWAYS AS ");
            sql.append(c.getGenerationExpression());
        }
        sql.setLength(sql.length() - 1); // Cut trailing space
        sql.append(",\n");

        return sql.toString();
    }

    public static void dropDatabase(Schema schema) throws SQLException {

        // Validate
        if(schema.getName().isEmpty() || schema.getName() == null)
            throw new IllegalArgumentException("Schema name is not set!");

        // Build
        String query = "DROP DATABASE IF EXISTS " + schema.getName();

        // Execute
        Connection conn = DatabaseConnection.getInstance().getConnection();
        Statement st = conn.createStatement();
        st.executeUpdate(query);
    }

    public static void dropTable(Table table) throws SQLException {

        // Validate
        if(table.getName().isEmpty() || table.getName() == null)
            throw new IllegalArgumentException("Table name is not set!");
        if(table.getSchema() == null)
            throw new IllegalArgumentException("Schema is not set!");
        if(table.getSchema().getName().isEmpty() || table.getSchema().getName() == null)
            throw new IllegalArgumentException("Schema name is not set!");

        // Build
        String query = "DROP TABLE IF EXISTS " + table.getSchema().getName() + "." + table.getName();

        // Execute
        Connection conn = DatabaseConnection.getInstance().getConnection();
        Statement st = conn.createStatement();
        st.executeUpdate(query);
    }

    public static void truncateDatabase(Schema schema) throws SQLException {
        /* MySQL doesn't have a single command to truncate the whole schema?? Wtf? */
        throw new UnsupportedOperationException("Not yet implemented...");
    }

    public static void truncateTable(Table table) throws SQLException {

        // Validate
        if(table.getName().isEmpty() || table.getName() == null)
            throw new IllegalArgumentException("Table name is not set!");

        // Build
        String query = "TRUNCATE TABLE " + table.getName();

        // Execute
        Connection conn = DatabaseConnection.getInstance().getConnection();
        Statement st = conn.createStatement();
        st.executeUpdate(query);

    }

    public static void addColumn(Table table, Column column) throws SQLException {

        // Validate
        if(column.getName().isEmpty() || column.getName() == null)
            throw new IllegalArgumentException("Column name is not set!");
        if(table.getName().isEmpty() || table.getName() == null)
            throw new IllegalArgumentException("Table name is not set!");
        if(table.getSchema() == null)
            throw new IllegalArgumentException("Schema is not set!");
        if(table.getSchema().getName().isEmpty() || table.getSchema().getName() == null)
            throw new IllegalArgumentException("Schema name is not set!");

        StringBuilder queryBuilder = new StringBuilder("ALTER TABLE ");
        queryBuilder.append(table.getSchema().getName());
        queryBuilder.append(".");
        queryBuilder.append(table.getName());
        queryBuilder.append("ADD COLUMN \n");

        queryBuilder.append(convertColumnToSQL(column));
        queryBuilder.setLength(queryBuilder.length() - 1);

        String query = queryBuilder.toString();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        Statement st = conn.createStatement();
        st.executeUpdate(query);
    }

    public static void dropColumn(Table table, Column column) throws SQLException {

        // Validate
        if(column.getName().isEmpty() || column.getName() == null)
            throw new IllegalArgumentException("Column name is not set!");
        if(table.getName().isEmpty() || table.getName() == null)
            throw new IllegalArgumentException("Table name is not set!");
        if(table.getSchema() == null)
            throw new IllegalArgumentException("Schema is not set!");
        if(table.getSchema().getName().isEmpty() || table.getSchema().getName() == null)
            throw new IllegalArgumentException("Schema name is not set!");

        StringBuilder queryBuilder = new StringBuilder("ALTER TABLE ");
        queryBuilder.append(table.getSchema().getName());
        queryBuilder.append(".");
        queryBuilder.append(table.getName());
        queryBuilder.append("DROP COLUMN ");
        queryBuilder.append(column.getName());

        String query = queryBuilder.toString();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        Statement st = conn.createStatement();
        st.executeUpdate(query);
    }

    public static void renameColumn(Table table, Column column, String newName) throws SQLException {

        // Validate
        if(column.getName().isEmpty() || column.getName() == null)
            throw new IllegalArgumentException("Column name is not set!");
        if(table.getName().isEmpty() || table.getName() == null)
            throw new IllegalArgumentException("Table name is not set!");
        if(table.getSchema() == null)
            throw new IllegalArgumentException("Schema is not set!");
        if(table.getSchema().getName().isEmpty() || table.getSchema().getName() == null)
            throw new IllegalArgumentException("Schema name is not set!");

        StringBuilder queryBuilder = new StringBuilder("ALTER TABLE ");
        queryBuilder.append(table.getSchema().getName());
        queryBuilder.append(".");
        queryBuilder.append(table.getName());
        queryBuilder.append("RENAME COLUMN ");
        queryBuilder.append(column.getName());
        queryBuilder.append(" TO ");
        queryBuilder.append(newName);

        String query = queryBuilder.toString();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        Statement st = conn.createStatement();
        st.executeUpdate(query);
    }

    public static void modifyColumn(Table table, Column column) throws SQLException {

        // Validate
        if(column.getName().isEmpty() || column.getName() == null)
            throw new IllegalArgumentException("Column name is not set!");
        if(table.getName().isEmpty() || table.getName() == null)
            throw new IllegalArgumentException("Table name is not set!");
        if(table.getSchema() == null)
            throw new IllegalArgumentException("Schema is not set!");
        if(table.getSchema().getName().isEmpty() || table.getSchema().getName() == null)
            throw new IllegalArgumentException("Schema name is not set!");

        StringBuilder queryBuilder = new StringBuilder("ALTER TABLE ");
        queryBuilder.append(table.getSchema().getName());
        queryBuilder.append(".");
        queryBuilder.append(table.getName());
        queryBuilder.append("MODIFY COLUMN \n");

        queryBuilder.append(convertColumnToSQL(column));
        queryBuilder.setLength(queryBuilder.length() - 1);

        String query = queryBuilder.toString();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        Statement st = conn.createStatement();
        st.executeUpdate(query);
    }
}
