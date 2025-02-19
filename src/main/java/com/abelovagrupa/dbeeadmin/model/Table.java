package com.abelovagrupa.dbeeadmin.model;

import javafx.print.Collation;

import java.util.List;
import java.util.Objects;

public class Table {

    private String name;
    private List<Column> columns;
    private List<Index> indexes;
    private List<ForeignKey> foreignKeys;
    private List<Trigger> triggers;
    private  DBEngine dbEngine;
    private Charset charset;
    private Collation collation;
    private Schema schema;

    public Table() {
    }

    public Table(String name, List<Column> columns, List<Index> indexes, List<ForeignKey> foreignKeys, List<Trigger> triggers, DBEngine dbEngine, Charset charset, Collation collation, Schema schema) {
        this.name = name;
        this.columns = columns;
        this.indexes = indexes;
        this.foreignKeys = foreignKeys;
        this.triggers = triggers;
        this.dbEngine = dbEngine;
        this.charset = charset;
        this.collation = collation;
        this.schema = schema;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public List<Index> getIndexes() {
        return indexes;
    }

    public void setIndexes(List<Index> indexes) {
        this.indexes = indexes;
    }

    public List<ForeignKey> getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(List<ForeignKey> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    public List<Trigger> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<Trigger> triggers) {
        this.triggers = triggers;
    }

    public DBEngine getDbEngine() {
        return dbEngine;
    }

    public void setDbEngine(DBEngine dbEngine) {
        this.dbEngine = dbEngine;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public Collation getCollation() {
        return collation;
    }

    public void setCollation(Collation collation) {
        this.collation = collation;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    @Override
    public String toString() {
        return "Table{" +
                "name='" + name + '\'' +
                ", columns=" + columns +
                ", indexes=" + indexes +
                ", foreignKeys=" + foreignKeys +
                ", triggers=" + triggers +
                ", dbEngine=" + dbEngine +
                ", charset=" + charset +
                ", collation=" + collation +
                ", schema=" + schema +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Table table)) return false;

        return getName().equals(table.getName()) && getColumns().equals(table.getColumns()) && getIndexes().equals(table.getIndexes()) && Objects.equals(getForeignKeys(), table.getForeignKeys()) && Objects.equals(getTriggers(), table.getTriggers()) && getDbEngine() == table.getDbEngine() && getCharset() == table.getCharset() && getCollation() == table.getCollation() && getSchema().equals(table.getSchema());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getColumns().hashCode();
        result = 31 * result + getIndexes().hashCode();
        result = 31 * result + Objects.hashCode(getForeignKeys());
        result = 31 * result + Objects.hashCode(getTriggers());
        result = 31 * result + getDbEngine().hashCode();
        result = 31 * result + getCharset().hashCode();
        result = 31 * result + getCollation().hashCode();
        result = 31 * result + getSchema().hashCode();
        return result;
    }
}
