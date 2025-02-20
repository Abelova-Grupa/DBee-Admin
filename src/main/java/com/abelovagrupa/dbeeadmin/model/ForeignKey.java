package com.abelovagrupa.dbeeadmin.model;

import java.util.List;
import java.util.Objects;

public class ForeignKey {
    private String name;
    private List<Column> referencingColumns;
    private Schema referencedSchema;
    private Table referencedTable;
    private List<Column> referencedColumns;
    private Action onDeleteAction;
    private Action onUpdateAction;

    public ForeignKey() {
    }

    public ForeignKey(String name, List<Column> referencingColumns, Schema referencedSchema, Table referencedTable, List<Column> referencedColumns, Action onDeleteAction, Action onUpdateAction) {
        this.name = name;
        this.referencingColumns = referencingColumns;
        this.referencedSchema = referencedSchema;
        this.referencedTable = referencedTable;
        this.referencedColumns = referencedColumns;
        this.onDeleteAction = onDeleteAction;
        this.onUpdateAction = onUpdateAction;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    public boolean equals(Object o) {
        if (!(o instanceof ForeignKey that)) return false;
        return Objects.equals(name, that.name) && Objects.equals(referencingColumns, that.referencingColumns) && Objects.equals(referencedSchema, that.referencedSchema) && Objects.equals(referencedTable, that.referencedTable) && Objects.equals(referencedColumns, that.referencedColumns) && onDeleteAction == that.onDeleteAction && onUpdateAction == that.onUpdateAction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, referencingColumns, referencedSchema, referencedTable, referencedColumns, onDeleteAction, onUpdateAction);
    }
}