package com.abelovagrupa.dbeeadmin.model.table;

import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.foreignkey.ForeignKey;
import com.abelovagrupa.dbeeadmin.model.index.Index;
import com.abelovagrupa.dbeeadmin.model.trigger.Trigger;
import com.abelovagrupa.dbeeadmin.model.schema.Schema;

import java.util.List;
import java.util.Objects;

public class Table {

    private String name;
    private List<Column> columns;
    private List<Index> indexes;
    private List<ForeignKey> foreignKeys;
    private List<Trigger> triggers;
    private DBEngine dbEngine;
    private Schema schema;

    public Table() {
    }

    public Table(String name, List<Column> columns, List<Index> indexes, List<ForeignKey> foreignKeys, List<Trigger> triggers, DBEngine dbEngine, Schema schema) {
        this.name = name;
        this.columns = columns;
        this.indexes = indexes;
        this.foreignKeys = foreignKeys;
        this.triggers = triggers;
        this.dbEngine = dbEngine;
        this.schema = schema;
    }

    protected Table(TableBuilder tableBuilder) {
        this.name = tableBuilder.name;
        this.columns = tableBuilder.columns;
        this.indexes = tableBuilder.indexes;
        this.foreignKeys = tableBuilder.foreignKeys;
        this.triggers = tableBuilder.triggers;
        this.dbEngine = tableBuilder.dbEngine;
        this.schema = tableBuilder.schema;
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

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    @Override
    public String toString() {
        // Avoiding stackoverflow from circular dependency
        // toString methods from table column and schema are calling eachother
        return "Table{" +
                "name='" + name + '\'' +
                ", columns=" + columns +
                ", indexes=" + indexes +
                ", foreignKeys=" + foreignKeys +
                ", triggers=" + triggers +
                ", dbEngine=" + dbEngine +
//                ", schema=" + schema +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Table table)) return false;

        return getName().equals(table.getName()) && getColumns().equals(table.getColumns()) && getIndexes().equals(table.getIndexes()) && Objects.equals(getForeignKeys(), table.getForeignKeys()) && Objects.equals(getTriggers(), table.getTriggers()) && getDbEngine() == table.getDbEngine()  && getSchema().equals(table.getSchema());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
//        result = 31 * result + getColumns().hashCode();
//        result = 31 * result + getIndexes().hashCode();
//        result = 31 * result + Objects.hashCode(getForeignKeys());
//        result = 31 * result + Objects.hashCode(getTriggers());
        result = 31 * result + getDbEngine().hashCode();
        result = 31 * result + getSchema().hashCode();
        return result;
    }

    public static class TableBuilder {

        private String name;
        private List<Column> columns;
        private List<Index> indexes;
        private List<ForeignKey> foreignKeys;
        private List<Trigger> triggers;
        private DBEngine dbEngine;
        private Schema schema;

        public TableBuilder(List<Column> columns, String name, Schema schema, DBEngine dbEngine) {
            this.columns = columns;
            this.name = name;
            this.schema = schema;
            this.dbEngine = dbEngine;
        }

        public TableBuilder setIndexes(List<Index> indexes) {
            this.indexes = indexes;
            return this;
        }

        public TableBuilder setForeignKeys(List<ForeignKey> foreignKeys) {
            this.foreignKeys = foreignKeys;
            return this;
        }

        public TableBuilder setTriggers(List<Trigger> triggers) {
            this.triggers = triggers;
            return this;
        }

        public Table build() {
            return new Table(this);
        }

    }
}
