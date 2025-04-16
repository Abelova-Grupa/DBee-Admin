package com.abelovagrupa.dbeeadmin.services;

import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.foreignkey.ForeignKey;
import com.abelovagrupa.dbeeadmin.model.index.Index;
import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.model.table.Table;
import com.abelovagrupa.dbeeadmin.model.trigger.Trigger;
import com.abelovagrupa.dbeeadmin.model.view.Algorithm;
import com.abelovagrupa.dbeeadmin.model.view.View;

import java.sql.SQLException;

public class QueryProcessor {

    private static QueryProcessor instance;

    public static void dropSchema(Schema schema, boolean preview) throws SQLException {
        String query = DDLGenerator.dropDatabase(schema);
        QueryExecutor.executeQuery(query, preview);
    }

    public static void createTable(Table table, boolean preview){
        String query = DDLGenerator.createTableCreationQuery(table);
        QueryExecutor.executeQuery(query,preview);
    }

    public static void dropTable(Table table, boolean preview){
        String query = DDLGenerator.createTableDropQuery(table);
        QueryExecutor.executeQuery(query,preview);
    }

    public static void truncateTable(Table table, boolean preview){
        String query = DDLGenerator.createTableTruncateQuery(table);
        QueryExecutor.executeQuery(query,preview);
    }

    public static void createColumn(Column column, boolean preview){
        String query = DDLGenerator.createColumnAdditionQuery(column);
        QueryExecutor.executeQuery(query,preview);
    }

    public static void dropColumn(Column column, boolean preview){
        String query = DDLGenerator.createColumnDropQuery(column);
        QueryExecutor.executeQuery(query,preview);
    }

    public static void renameColumn(Column column, String newName, boolean preview){
        String query = DDLGenerator.createColumnRenameQuery(column,newName);
        QueryExecutor.executeQuery(query,preview);
    }
    // I think modifyColumn method is invalid
    public static void alterColumn(Column column,boolean preview){
        String query = DDLGenerator.createColumnAlterQuery(column);
        QueryExecutor.executeQuery(query,preview);
    }

    public static void createForeignKey(ForeignKey foreignKey, boolean preview){
        String query = DDLGenerator.createForeignKeyCreationQuery(foreignKey);
        QueryExecutor.executeQuery(query,preview);
    }

    public static void dropForeignKey(ForeignKey foreignKey, boolean preview){
        String query = DDLGenerator.createForeignKeyDropQuery(foreignKey);
        QueryExecutor.executeQuery(query,preview);
    }

    public static void renameForeignKey(ForeignKey foreignKey, String newName, boolean preview){
        String query = DDLGenerator.createForeignKeyRenameQuery(foreignKey,newName);
        QueryExecutor.executeQuery(query,preview);
    }

    public static void alterForeignKey(ForeignKey oldForeignKey, ForeignKey newForeignKey, boolean preview){
        String query = DDLGenerator.createForeignKeyAlterQuery(oldForeignKey,newForeignKey);
        QueryExecutor.executeQuery(query,preview);
    }

    public static void createIndex(Index index, boolean preview){
        String query = DDLGenerator.createIndexCreationQuery(index);
        QueryExecutor.executeQuery(query,preview);
    }

    public static void renameIndex(Index index, String newName, boolean preview){
        String query = DDLGenerator.createIndexRenameQuery(index,newName);
        QueryExecutor.executeQuery(query,preview);
    }

    public static void dropIndex(Index index, boolean preview){
        String query = DDLGenerator.createIndexDropQuery(index);
        QueryExecutor.executeQuery(query,preview);
    }

    public static void alterIndex(Index oldIndex, Index newIndex, boolean preview){
        String query = DDLGenerator.createIndexAlterQuery(oldIndex,newIndex);
        QueryExecutor.executeQuery(query,preview);
    }

    public static void createTrigger(Trigger trigger, boolean preview){
        String query = DDLGenerator.createTriggerCreationQuery(trigger);
        QueryExecutor.executeQuery(query,preview);
    }

    public static void dropTrigger(Trigger trigger, boolean preview){
        String query = DDLGenerator.createTriggerDropQuery(trigger);
        QueryExecutor.executeQuery(query,preview);
    }

    public static void createView(View view, Algorithm algorithm, boolean preview){
        String query = DDLGenerator.createViewCreationQuery(view,algorithm);
        QueryExecutor.executeQuery(query,preview);
    }

}
