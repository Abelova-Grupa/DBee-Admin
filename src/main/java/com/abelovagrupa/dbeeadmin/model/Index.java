package com.abelovagrupa.dbeeadmin.model;

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

    public Index() {
    }

    public Index(String name, IndexType type, IndexStorageType storageType, int keyBlockSize, String parser, boolean visible, List<IndexedColumn> indexedColumns) {
        this.name = name;
        this.type = type;
        this.storageType = storageType;
        this.keyBlockSize = keyBlockSize;
        this.parser = parser;
        this.visible = visible;
        this.indexedColumns = indexedColumns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IndexType getType() {
        return type;
    }

    public void setType(IndexType type) {
        this.type = type;
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
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Index index)) return false;

        return getKeyBlockSize() == index.getKeyBlockSize() && isVisible() == index.isVisible() && getName().equals(index.getName()) && getType() == index.getType() && getStorageType() == index.getStorageType() && Objects.equals(getParser(), index.getParser()) && getIndexedColumns().equals(index.getIndexedColumns());
    }

    @Override
    public int hashCode() {
        int result = getName().hashCode();
        result = 31 * result + getType().hashCode();
        result = 31 * result + Objects.hashCode(getStorageType());
        result = 31 * result + getKeyBlockSize();
        result = 31 * result + Objects.hashCode(getParser());
        result = 31 * result + Boolean.hashCode(isVisible());
        result = 31 * result + getIndexedColumns().hashCode();
        return result;
    }
}
