package com.abelovagrupa.dbeeadmin.model.foreignkey;

import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.model.table.Table;
import com.abelovagrupa.dbeeadmin.util.Pair;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ForeignKey {
    private String name;
    private Schema referencingSchema;
    private Table referencingTable;
    private Schema referencedSchema;
    private Table referencedTable;
    private Action onDeleteAction;
    private Action onUpdateAction;
    private String comment;

    // columnPairs is a list of pairs where each pair is <referencingColumn,referenced Column>
    // These column pairs are specially used in ForeignKeyColumns table in foreign key tab
    private List<ForeignKeyColumns> columnPairs;
    private List<Column> referencingColumns;
    private List<Column> referencedColumns;

    // Foreign key table properties
    private StringProperty nameProperty;
    private StringProperty referencedTableProperty;

    public ForeignKey() {
    }

    public ForeignKey(String name, Schema referencingSchema, Table referencingTable, Schema referencedSchema, Table referencedTable, List<ForeignKeyColumns> columnPairs, Action onDeleteAction, Action onUpdateAction,String comment) {
        this.name = name;
        this.referencingSchema = referencingSchema;
        this.referencingTable = referencingTable;
        this.referencedSchema = referencedSchema;
        this.referencedTable = referencedTable;
        this.columnPairs = columnPairs;
        this.onDeleteAction = onDeleteAction;
        this.onUpdateAction = onUpdateAction;
        this.comment = comment;
    }

    public String getName() {
        if(nameProperty != null)
            return nameProperty.get();
        else return name;
    }

    public void setName(String name) {
        if(nameProperty != null)
            nameProperty.set(name);
        this.name = name;
    }

    public Schema getReferencingSchema() {
        return referencingSchema;
    }

    public void setReferencingSchema(Schema referencingSchema) {
        this.referencingSchema = referencingSchema;
    }

    public Table getReferencingTable() {
        return referencingTable;
    }

    public void setReferencingTable(Table referencingTable) {
        this.referencingTable = referencingTable;
    }

    public List<Column> getReferencingColumns() {
        List<Column> referencingColumns = new LinkedList<>();
        for(Pair<Column,Column> pair : getColumnPairs()){
            referencingColumns.add(pair.getFirst());
        }
        this.referencingColumns = referencingColumns;
        return this.referencingColumns;
    }

    public void setReferencingColumns(List<Column> referencingColumns) {
        this.referencingColumns = referencingColumns;
        for(int i = 0; i < this.referencingColumns.size(); i++){
            if(getColumnPairs().get(i) == null){
                getColumnPairs().set(i, new ForeignKeyColumns());
            }
            getColumnPairs().get(i).setFirst(this.referencingColumns.get(i));
        }
    }

    public Schema getReferencedSchema() {
        return referencedSchema;
    }

    public void setReferencedSchema(Schema referencedSchema) {
        this.referencedSchema = referencedSchema;
    }

    public Table getReferencedTable() {
        return referencedTable;
    }

    public void setReferencedTable(Table referencedTable) {
        this.referencedTable = referencedTable;
    }

    public List<Column> getReferencedColumns() {
        List<Column> referencedColumns = new LinkedList<>();
        for(Pair<Column,Column> pair : getColumnPairs()){
            referencedColumns.add(pair.getSecond());
        }
        this.referencedColumns = referencedColumns;
        return this.referencedColumns;
    }

    public void setReferencedColumns(List<Column> referencedColumns) {
        // When setting the referenced column list, set the second elements in list of pairs
        this.referencedColumns = referencedColumns;
        for(int i = 0; i < referencedColumns.size(); i++){
            if(getColumnPairs().get(i) == null){
                getColumnPairs().set(i,new ForeignKeyColumns());
            }
            getColumnPairs().get(i).setSecond(referencedColumns.get(i));
        }
    }

    public List<ForeignKeyColumns> getColumnPairs() {
        // Singleton list of referencing/ed columns for one foreign key
        if(columnPairs == null){
            columnPairs = new LinkedList<>();
        }
        return columnPairs;
    }

    public void setColumnPairs(List<ForeignKeyColumns> columnPairs) {
        this.columnPairs = columnPairs;
    }

    public Action getOnDeleteAction() {
        return onDeleteAction;
    }

    public void setOnDeleteAction(Action onDeleteAction) {
        this.onDeleteAction = onDeleteAction;
    }

    public Action getOnUpdateAction() {
        return onUpdateAction;
    }

    public void setOnUpdateAction(Action onUpdateAction) {
        this.onUpdateAction = onUpdateAction;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    // Table properties
    public StringProperty nameProperty(){
        if(nameProperty == null){
            nameProperty = new SimpleStringProperty(this,"name",name);
        }
        return nameProperty;
    }

    public StringProperty referencedTableProperty(){
        if(referencedTableProperty == null){
            referencedTableProperty = new SimpleStringProperty(this,"referencedTable","");
        }
        return referencedTableProperty;
    }

    public void setReferencedTableProperty(String referencedTable){
        referencedTableProperty().set(referencedTable);
    }

    @Override
    public String toString() {
        return "ForeignKey{" +
                "name='" + name + '\'' +
                ", referencedSchema=" + referencedSchema +
                ", referencedTable=" + referencedTable +
                ", columnPairs" + columnPairs +
                ", onDeleteAction=" + onDeleteAction +
                ", onUpdateAction=" + onUpdateAction +
                ", comment" + comment +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ForeignKey that = (ForeignKey) o;
        return Objects.equals(name, that.name) && Objects.equals(referencingSchema, that.referencingSchema) && Objects.equals(referencingTable, that.referencingTable) && Objects.equals(referencedSchema, that.referencedSchema) && Objects.equals(referencedTable, that.referencedTable) && onDeleteAction == that.onDeleteAction && onUpdateAction == that.onUpdateAction && Objects.equals(comment, that.comment) && Objects.equals(columnPairs, that.columnPairs) && Objects.equals(referencingColumns, that.referencingColumns) && Objects.equals(referencedColumns, that.referencedColumns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, referencingSchema, referencingTable, referencedSchema, referencedTable, onDeleteAction, onUpdateAction, comment, columnPairs, referencingColumns, referencedColumns);
    }
}