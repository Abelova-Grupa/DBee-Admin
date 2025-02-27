package com.abelovagrupa.dbeeadmin.model.column;

import com.abelovagrupa.dbeeadmin.model.table.Table;

import java.util.Objects;

public class Column {

    private String name;
    private boolean isPrimaryKey;
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
                  boolean isPrimaryKey,
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
        this.isPrimaryKey = isPrimaryKey;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.isPrimaryKey = primaryKey;
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
        return "Column{" + "name='" + name + '\'' +
            ", isPrimaryKey=" + isPrimaryKey +
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
            ", table=" + table +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Column column = (Column) o;
        return (isPrimaryKey == column.isPrimaryKey) &&
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
        return Objects.hash(name, isPrimaryKey, notNull, unique, binary, unsigned, zeroFill, autoIncrement, size, generationExpression, defaultValue, comment, type, table);
    }
}
