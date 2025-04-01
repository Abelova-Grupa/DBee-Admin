package com.abelovagrupa.dbeeadmin.model.index;

import com.abelovagrupa.dbeeadmin.model.column.DataType;
import javafx.beans.property.*;

import java.util.List;
import java.util.Objects;

public class Index {

    private String name;
    private IndexType type;
    private IndexStorageType storageType;
    private int keyBlockSize;
    private String parser;
    private boolean visible;
    private List<IndexedColumn> indexedColumns;
    private boolean unique;

    // Index table properties
    private StringProperty nameProperty;
    private ObjectProperty<IndexType> typeProperty;

    public Index() {
    }

    public Index(String name, IndexType type, IndexStorageType storageType, int keyBlockSize, String parser, boolean visible, List<IndexedColumn> indexedColumns, boolean unique) {
        this.name = name;
        this.type = type;
        this.storageType = storageType;
        this.keyBlockSize = keyBlockSize;
        this.parser = parser;
        this.visible = visible;
        this.indexedColumns = indexedColumns;
        this.unique = unique;
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
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Index index)) return false;

        return getKeyBlockSize() == index.getKeyBlockSize() && isVisible() == index.isVisible() && isUnique() == index.isUnique() && Objects.equals(getName(), index.getName()) && getType() == index.getType() && getStorageType() == index.getStorageType() && Objects.equals(getParser(), index.getParser()) && Objects.equals(getIndexedColumns(), index.getIndexedColumns());
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
}
