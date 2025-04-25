package com.abelovagrupa.dbeeadmin.model.index;

import com.abelovagrupa.dbeeadmin.model.column.DataType;
import com.abelovagrupa.dbeeadmin.model.table.Table;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import kotlin.NotImplementedError;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public class Index {

    private String name;
    private IndexType type;
    private IndexStorageType storageType;
    private int keyBlockSize;
    private String parser;
    private boolean visible;
    private List<IndexedColumn> indexedColumns;
    private boolean unique;
    private String comment;
    private Table table;

    // Index table properties
    private StringProperty nameProperty;
    private ObjectProperty<IndexType> typeProperty;

    public Index() {
    }

    public Index(String name, IndexType type, IndexStorageType storageType, int keyBlockSize, String parser, boolean visible, List<IndexedColumn> indexedColumns, boolean unique,Table table) {
        this.name = name;
        this.type = type;
        this.storageType = storageType;
        this.keyBlockSize = keyBlockSize;
        this.parser = parser;
        this.visible = visible;
        this.indexedColumns = indexedColumns;
        this.unique = unique;
        this.table = table;
    }

    public static boolean containsByAttributes(List<Index> indexes, Index index) {
        for(Index indx : indexes){
            if(matchesByAttributes(indx,index)) return true;
        }
        return false;
    }

    private static boolean matchesByAttributes(Index a, Index b) {
        return Objects.equals(a.getName(), b.getName()) &&
                Objects.equals(a.getType(), b.getType()) &&
                Objects.equals(a.getStorageType(), b.getStorageType()) &&
                Objects.equals(a.getKeyBlockSize(), b.getKeyBlockSize()) &&
                Objects.equals(a.getParser(), b.getParser()) &&
                a.isVisible() == b.isVisible() &&
                Objects.equals(a.getIndexedColumns(), b.getIndexedColumns()) &&
                a.isUnique() == b.isUnique() &&
                Objects.equals(a.getTable(), b.getTable());
    }

    public static Index deepCopy(Index index) {
        return new Index(
                index.getName(),
                index.getType(),
                index.getStorageType(),
                index.getKeyBlockSize(),
                index.getParser(),
                index.isVisible(),
                index.getIndexedColumns(),
                index.isUnique(),
                index.getTable()
        );
    }

    public String getName() {
        if(nameProperty != null)
            return nameProperty.get();
        else return name;
    }

    public void setName(String name) {
        if(nameProperty != null){
            nameProperty.set(name);
        }
        this.name = name;
    }

    public StringProperty nameProperty(){
        if(nameProperty == null){
            nameProperty = new SimpleStringProperty(this,"name",name);
        }
        return nameProperty;
    }

    public IndexType getType() {
        if(typeProperty != null){
            return typeProperty.get();
        }
        else return type;
    }

    public void setType(IndexType type) {
        if(typeProperty != null){
            typeProperty.set(type);
        }
        this.type = type;
    }

    public ObjectProperty<IndexType> typeProperty(){
        if(typeProperty == null){
            typeProperty = new SimpleObjectProperty<>(this,"type",type);
        }
        return typeProperty;
    }

    public IndexStorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(IndexStorageType storageType) {
        this.storageType = storageType;
    }

    public int getKeyBlockSize() {
        return keyBlockSize;
    }

    public void setKeyBlockSize(int keyBlockSize) {
        this.keyBlockSize = keyBlockSize;
    }

    public String getParser() {
        return parser;
    }

    public void setParser(String parser) {
        this.parser = parser;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public List<IndexedColumn> getIndexedColumns() {
        return indexedColumns;
    }

    public void setIndexedColumns(List<IndexedColumn> indexedColumns) {
        this.indexedColumns = indexedColumns;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    @Override
    public String toString() {
        return "Index{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", storageType=" + storageType +
                ", keyBlockSize=" + keyBlockSize +
                ", parser='" + parser + '\'' +
                ", visible=" + visible +
                ", indexedColumns=" + indexedColumns +
                ", unique=" + unique +
                ", comment=" + comment +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Index index)) return false;

        return getKeyBlockSize() == index.getKeyBlockSize() &&
                isVisible() == index.isVisible() &&
                isUnique() == index.isUnique() &&
                Objects.equals(getName(), index.getName()) &&
                getType() == index.getType() &&
                getStorageType() == index.getStorageType() &&
                Objects.equals(getParser(), index.getParser()) &&
                Objects.equals(getComment(),index.getComment()) &&
                Objects.equals(getIndexedColumns(), index.getIndexedColumns());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(getName());
        result = 31 * result + Objects.hashCode(getType());
        result = 31 * result + Objects.hashCode(getStorageType());
        result = 31 * result + getKeyBlockSize();
        result = 31 * result + Objects.hashCode(getParser());
        result = 31 * result + Boolean.hashCode(isVisible());
        result = 31 * result + Objects.hashCode(getIndexedColumns());
        result = 31 * result + Boolean.hashCode(isUnique());
        return result;
    }

    public static BiFunction<Index, Index, HashMap<String, Object[]>> indexAttributeComparator = (i1, i2) -> {
        HashMap<String, Object[]> diffs = new HashMap<>();

        if (!Objects.equals(i1.getName(), i2.getName())) {
            diffs.put("name", new Object[]{i1.getName(), i2.getName()});
        }
        if (!Objects.equals(i1.getType(), i2.getType())) {
            diffs.put("type", new Object[]{i1.getType(), i2.getType()});
        }
        if (!Objects.equals(i1.getStorageType(), i2.getStorageType())) {
            diffs.put("storageType", new Object[]{i1.getStorageType(), i2.getStorageType()});
        }
        if (i1.getKeyBlockSize() != i2.getKeyBlockSize()) {
            diffs.put("keyBlockSize", new Object[]{i1.getKeyBlockSize(), i2.getKeyBlockSize()});
        }
        if (!Objects.equals(i1.getParser(), i2.getParser())) {
            diffs.put("parser", new Object[]{i1.getParser(), i2.getParser()});
        }
        if (i1.isVisible() != i2.isVisible()) {
            diffs.put("visible", new Object[]{i1.isVisible(), i2.isVisible()});
        }
        if (!Objects.equals(i1.getIndexedColumns(), i2.getIndexedColumns())) {
            diffs.put("indexedColumns", new Object[]{i1.getIndexedColumns(), i2.getIndexedColumns()});
        }
        if (i1.isUnique() != i2.isUnique()) {
            diffs.put("unique", new Object[]{i1.isUnique(), i2.isUnique()});
        }
        if (!Objects.equals(i1.getComment(), i2.getComment())) {
            diffs.put("comment", new Object[]{i1.getComment(), i2.getComment()});
        }
        if (!Objects.equals(i1.getTable(), i2.getTable())) {
            diffs.put("table", new Object[]{i1.getTable(), i2.getTable()});
        }

        return diffs;
    };

}
