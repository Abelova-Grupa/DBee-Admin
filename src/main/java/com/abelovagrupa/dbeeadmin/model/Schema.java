package com.abelovagrupa.dbeeadmin.model;


import javafx.print.Collation;

import java.util.List;
import java.util.Objects;

public class Schema {

    private String name;
    private Charset charset;
    private Collation collation;
    private List<Table> tables;
    private int tableCount;
    private double databaseSize;

    public Schema(String name) {

    }

    public Schema(String name, Charset charset, Collation collation, List<Table> tables, int tableCount, double databaseSize) {
        this.name = name;
        this.charset = charset;
        this.collation = collation;
        this.tables = tables;
        this.tableCount = tableCount;
        this.databaseSize = databaseSize;
    }

    @Override
    public String toString() {
        return "Schema{" +
                "name='" + name + '\'' +
                ", charset=" + charset +
                ", collation=" + collation +
                ", tables=" + tables +
                ", tableCount=" + tableCount +
                ", databaseSize=" + databaseSize +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Schema schema)) return false;

        return tableCount == schema.tableCount && Double.compare(databaseSize, schema.databaseSize) == 0 && name.equals(schema.name) && charset == schema.charset && collation == schema.collation && Objects.equals(tables, schema.tables);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + charset.hashCode();
        result = 31 * result + collation.hashCode();
        result = 31 * result + Objects.hashCode(tables);
        result = 31 * result + tableCount;
        result = 31 * result + Double.hashCode(databaseSize);
        return result;
    }
}
