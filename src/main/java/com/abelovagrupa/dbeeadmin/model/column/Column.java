package com.abelovagrupa.dbeeadmin.model.column;

import com.abelovagrupa.dbeeadmin.model.table.Table;

import java.util.Objects;

public class Column {

    private String name;
    private boolean primaryKey;
    private boolean notNull;
    private boolean unique;
    private boolean binary;
    private boolean unsigned;
    private boolean zeroFill;
    private boolean autoIncrement;
    private Integer size;
    private String generationExpression;
    private String defaultValue;
    private String comment;
    private DataType type;
    private Table table;

    public Column() {
    }

    public Column(String name,
                  boolean primaryKey,
                  boolean notNull,
                  boolean unique,
                  boolean binary,
                  boolean unsigned,
                  boolean zeroFill,
                  boolean autoIncrement,
                  Integer size,
                  String generationExpression,
                  String defaultValue,
                  String comment,
                  DataType type,
                  Table table) {
        this.name = name;
        this.primaryKey = primaryKey;
        this.notNull = notNull;
        this.unique = unique;
        this.binary = binary;
        this.unsigned = unsigned;
        this.zeroFill = zeroFill;
        this.autoIncrement = autoIncrement;
        this.size = size;
        this.generationExpression = generationExpression;
        this.defaultValue = defaultValue;
        this.comment = comment;
        this.type = type;
        this.table = table;
    }

    protected Column(ColumnBuilder columnBuilder) {
        this.name = columnBuilder.name;
        this.primaryKey = columnBuilder.primaryKey;
        this.notNull = columnBuilder.notNull;
        this.unique = columnBuilder.unique;
        this.binary = columnBuilder.binary;
        this.unsigned = columnBuilder.unsigned;
        this.zeroFill = columnBuilder.zeroFill;
        this.autoIncrement = columnBuilder.autoIncrement;
        this.size = columnBuilder.size;
        this.generationExpression = columnBuilder.generationExpression;
        this.defaultValue = columnBuilder.defaultValue;
        this.comment = columnBuilder.comment;
        this.type = columnBuilder.type;
        this.table = columnBuilder.table;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isBinary() {
        return binary;
    }

    public void setBinary(boolean binary) {
        this.binary = binary;
    }

    public boolean isUnsigned() {
        return unsigned;
    }

    public void setUnsigned(boolean unsigned) {
        this.unsigned = unsigned;
    }

    public boolean isZeroFill() {
        return zeroFill;
    }

    public void setZeroFill(boolean zeroFill) {
        this.zeroFill = zeroFill;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getGenerationExpression() {
        return generationExpression;
    }

    public void setGenerationExpression(String generationExpression) {
        this.generationExpression = generationExpression;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        // Avoiding stackoverflow from circular dependency
        // toString methods from table column and schema are calling each other
        return "Column{" + "name='" + name + '\'' +
            ", isPrimaryKey=" + primaryKey +
            ", notNull=" + notNull +
            ", unique=" + unique +
            ", binary=" + binary +
            ", unsigned=" + unsigned +
            ", zeroFill=" + zeroFill +
            ", autoIncrement=" + autoIncrement +
            ", size=" + size +
            ", generationExpression='" + generationExpression + '\'' +
            ", defaultValue='" + defaultValue + '\'' +
            ", comment='" + comment + '\'' +
            ", type=" + type +
//            ", table=" + table +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Column column = (Column) o;
        return (primaryKey == column.primaryKey) &&
            (notNull == column.notNull) &&
            (unique == column.unique) &&
            (binary == column.binary) &&
            (unsigned == column.unsigned) &&
            (zeroFill == column.zeroFill) &&
            (autoIncrement == column.autoIncrement) &&
            Objects.equals(name, column.name) &&
            Objects.equals(size, column.size) &&
            Objects.equals(generationExpression, column.generationExpression) &&
            Objects.equals(defaultValue, column.defaultValue) &&
            Objects.equals(comment, column.comment) &&
            (type == column.type) &&
            Objects.equals(table, column.table);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, primaryKey, notNull, unique, binary, unsigned, zeroFill, autoIncrement, size, generationExpression, defaultValue, comment, type, table);
    }

    // Builder
    public static class ColumnBuilder {

        private String name;
        private boolean primaryKey;
        private boolean notNull;
        private boolean unique;
        private boolean binary;
        private boolean unsigned;
        private boolean zeroFill;
        private boolean autoIncrement;
        private Integer size;
        private String generationExpression;
        private String defaultValue;
        private String comment;
        private DataType type;
        private Table table;

        public ColumnBuilder(String name, DataType type, Table table) {
            this.name = name;
            this.type = type;
            this.table = table;
        }

        public ColumnBuilder setPrimaryKey(boolean primaryKey) {
            this.primaryKey = primaryKey;
            this.notNull = true;
            return this;
        }

        public ColumnBuilder setNotNull(boolean notNull) {
            this.notNull = notNull;
            return this;
        }

        public ColumnBuilder setUnique(boolean unique) {
            this.unique = unique;
            return this;
        }

        public ColumnBuilder setBinary(boolean binary) {
            this.binary = binary;
            return this;
        }

        public ColumnBuilder setUnsigned(boolean unsigned) {
            this.unsigned = unsigned;
            return this;
        }

        public ColumnBuilder setZeroFill(boolean zeroFill) {
            this.zeroFill = zeroFill;
            return this;
        }

        public ColumnBuilder setAutoIncrement(boolean autoIncrement) {
            this.autoIncrement = autoIncrement;
            return this;
        }

        public ColumnBuilder setSize(Integer size) {
            this.size = size;
            return this;
        }

        public ColumnBuilder setGenerationExpression(String generationExpression) {
            this.generationExpression = generationExpression;
            return this;
        }

        public ColumnBuilder setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public ColumnBuilder setComment(String comment) {
            this.comment = comment;
            return this;
        }

        public Column build() {
            return new Column(this);
        }
    }

}
