package com.abelovagrupa.dbeeadmin.model;

import javafx.print.Collation;

public class Column {

    private String name;
    private boolean isPrimaryKey;
    private boolean notNull;
    private boolean unique;
    private boolean binary;
    private boolean unsigned;
    private boolean zeroFill;
    private boolean autoIncrement;
    private boolean generated;
    private DataType type;
    private Table table;

    public Column() {
    }

    public Column(String name, boolean isPrimaryKey, boolean notNull, boolean unique, boolean binary, boolean unsigned, boolean zeroFill, boolean autoIncrement, boolean generated, DataType type, Table table) {
        this.name = name;
        this.isPrimaryKey = isPrimaryKey;
        this.notNull = notNull;
        this.unique = unique;
        this.binary = binary;
        this.unsigned = unsigned;
        this.zeroFill = zeroFill;
        this.autoIncrement = autoIncrement;
        this.generated = generated;
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

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
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

    @Override
    public String toString() {
        return "Column{" +
                "name='" + name + '\'' +
                ", isPrimaryKey=" + isPrimaryKey +
                ", notNull=" + notNull +
                ", unique=" + unique +
                ", binary=" + binary +
                ", unsigned=" + unsigned +
                ", zeroFill=" + zeroFill +
                ", autoIncrement=" + autoIncrement +
                ", generated=" + generated +
                ", type=" + type +
                ", table=" + table +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Column column)) return false;

        return isPrimaryKey() == column.isPrimaryKey() && isNotNull() == column.isNotNull() && isUnique() == column.isUnique() && isBinary() == column.isBinary() && isUnsigned() == column.isUnsigned() && isZeroFill() == column.isZeroFill() && isAutoIncrement() == column.isAutoIncrement() && isGenerated() == column.isGenerated() && getName().equals(column.getName())  && getType() == column.getType();
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + Boolean.hashCode(isPrimaryKey());
        result = 31 * result + Boolean.hashCode(isNotNull());
        result = 31 * result + Boolean.hashCode(isUnique());
        result = 31 * result + Boolean.hashCode(isBinary());
        result = 31 * result + Boolean.hashCode(isUnsigned());
        result = 31 * result + Boolean.hashCode(isZeroFill());
        result = 31 * result + Boolean.hashCode(isAutoIncrement());
        result = 31 * result + Boolean.hashCode(isGenerated());
        result = 31 * result + getType().hashCode();
        return result;
    }
}
