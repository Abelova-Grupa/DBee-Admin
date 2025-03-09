package com.abelovagrupa.dbeeadmin.controller;

import com.abelovagrupa.dbeeadmin.connection.DatabaseConnection;
import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.model.table.Table;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class DatabaseInspectorTest {

    static DatabaseConnection databaseConnection;
    static DatabaseInspector databaseInspector;

    // Change for your database
    String schemaName = "ProjektovanjeSoftvera2";
    String tableName = "Activities";    // Belongs to schema above
    String columnName = "id";   // Belongs to table above

    @BeforeAll
    static void getConnection(){
        System.out.println("Preparing...");
        databaseConnection = DatabaseConnection.getInstance();
        databaseInspector = DatabaseInspector.getInstance();
    }

    @Test
    void getDatabaseNames() {
    }

    @Test
    void getTableNames() {
    }

    @Test
    void getTables() {
    }

    @Test
    @DisplayName("Should map all columns.")
    void getColumns() {

        Table temp = new Table(tableName,
            null,
            null,
            null,
            null,
            null,
            new Schema(schemaName,
                null,
                null,
                null,
                0,
                0));
        List<Column> columns = databaseInspector.getColumns(temp);
        columns.forEach(System.out::println);
    }

    @Test
    @DisplayName("Should return a column of a given name.")
    void getColumnByName() {
        Table temp = new Table(tableName,
            null,
            null,
            null,
            null,
            null,
            new Schema(schemaName,
                null,
                null,
                null,
                0,
                0));

        Column column = databaseInspector.getColumnByName(temp, columnName);
        System.out.println(column);
    }

    @Test
    void getForeignKeys() {
    }

    @Test
    void getIndexes() {
    }

    @Test
    void getIndexedColumns() {
    }

    @Test
    void getTriggers() {
    }

}