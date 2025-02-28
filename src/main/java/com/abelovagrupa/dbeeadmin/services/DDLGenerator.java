package com.abelovagrupa.dbeeadmin.services;

import com.abelovagrupa.dbeeadmin.connection.DatabaseConnection;
import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.model.table.Table;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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
            // Append name
            queryBuilder.append(c.getName());
            queryBuilder.append(" ");

            // Append type (Either normal or binary)
            if(c.isBinary()) queryBuilder.append("BINARY");
            else queryBuilder.append(c.getType().name());

            // Append size
            if(c.getSize() != null) {
                queryBuilder.append("(");
                queryBuilder.append(c.getSize());
                queryBuilder.append(") ");
            }

            if(c.isUnsigned()) queryBuilder.append("UNSIGNED ");

            // Append optional parameters
            if(c.isPrimaryKey()) queryBuilder.append("PRIMARY KEY ");
            if(c.isUnique()) queryBuilder.append("UNIQUE ");
            if(c.isAutoIncrement()) queryBuilder.append("AUTO_INCREMENT ");
            if(c.isZeroFill()) queryBuilder.append("ZEROFILL ");
            if(c.getDefaultValue() != null) {
                queryBuilder.append("DEFAULT ");
                queryBuilder.append(c.getDefaultValue());
            }
            if(c.getGenerationExpression() != null) {
                queryBuilder.append("GENERATED ALWAYS AS ");
                queryBuilder.append(c.getGenerationExpression());
            }
            queryBuilder.setLength(queryBuilder.length() - 1); // Cut trailing space
            queryBuilder.append(",\n");
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

}
