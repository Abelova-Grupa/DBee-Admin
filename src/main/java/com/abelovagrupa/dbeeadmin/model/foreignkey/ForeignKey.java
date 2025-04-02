package com.abelovagrupa.dbeeadmin.model.foreignkey;

import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.model.table.Table;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;
import java.util.Objects;

public class ForeignKey {
    private String name;
    private Schema referencingSchema;
    private Table referencingTable;
    private List<Column> referencingColumns;
    private Schema referencedSchema;
    private Table referencedTable;
    private List<Column> referencedColumns;
    private Action onDeleteAction;
    private Action onUpdateAction;

    // Foreign key table properties
    private StringProperty nameProperty;
    private StringProperty referencedTableProperty;

    // Foreign key columns table properties
    private BooleanProperty checkedColumnProperty;
    private StringProperty columnNameProperty;
    private StringProperty referencedColumnProperty;

    public ForeignKey() {
    }

    public ForeignKey(String name, Schema referencingSchema, Table referencingTable, List<Column> referencingColumns, Schema referencedSchema, Table referencedTable, List<Column> referencedColumns, Action onDeleteAction, Action onUpdateAction) {
        this.name = name;
        this.referencingSchema = referencingSchema;
        this.referencingTable = referencingTable;
        this.referencingColumns = referencingColumns;
        this.referencedSchema = referencedSchema;
        this.referencedTable = referencedTable;
        this.referencedColumns = referencedColumns;
        this.onDeleteAction = onDeleteAction;
        this.onUpdateAction = onUpdateAction;
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
        return referencingColumns;
    }

    public void setReferencingColumns(List<Column> referencingColumns) {
        this.referencingColumns = referencingColumns;
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
        return referencedColumns;
    }

    public void setReferencedColumns(List<Column> referencedColumns) {
        this.referencedColumns = referencedColumns;
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

    // Table properties
    public StringProperty nameProperty(){
        if(nameProperty == null){
            nameProperty = new SimpleStringProperty(this,"name",name);
        }
        return nameProperty;
    }

    public StringProperty referencedTableProperty(){
        if(referencedTableProperty == null){
            referencedTableProperty = new SimpleStringProperty(this,"referencedTable",referencedSchema.getName()+"."+referencedTable.getName());
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
                ", referencingColumns=" + referencingColumns +
                ", referencedSchema=" + referencedSchema +
                ", referencedTable=" + referencedTable +
                ", referencedColumns=" + referencedColumns +
                ", onDeleteAction=" + onDeleteAction +
                ", onUpdateAction=" + onUpdateAction +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ForeignKey that)) return false;

        return Objects.equals(getName(), that.getName()) && Objects.equals(getReferencingSchema(), that.getReferencingSchema()) && Objects.equals(getReferencingTable(), that.getReferencingTable()) && Objects.equals(getReferencingColumns(), that.getReferencingColumns()) && Objects.equals(getReferencedSchema(), that.getReferencedSchema()) && Objects.equals(getReferencedTable(), that.getReferencedTable()) && Objects.equals(getReferencedColumns(), that.getReferencedColumns()) && getOnDeleteAction() == that.getOnDeleteAction() && getOnUpdateAction() == that.getOnUpdateAction();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, referencingColumns, referencedSchema, referencedTable, referencedColumns, onDeleteAction, onUpdateAction);
    }
}