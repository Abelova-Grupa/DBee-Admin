package com.abelovagrupa.dbeeadmin.model.index;

import com.abelovagrupa.dbeeadmin.model.column.Column;

import java.util.Objects;

public class IndexedColumn {

    private Column column;
    private int orderNumber;
    private Order order;
    private int length;
    private Index index;

    public IndexedColumn() {
    }

    public IndexedColumn(Column column, int orderNumber, Order order, int length, Index index) {
        this.column = column;
        this.orderNumber = orderNumber;
        this.order = order;
        this.length = length;
        this.index = index;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Index getIndex() {
        return index;
    }

    public void setIndex(Index index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "IndexedColumn{" +
                "column=" + column +
                ", orderNumber=" + orderNumber +
                ", order=" + order +
                ", length=" + length +
//                ", index=" + index +
                '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndexedColumn that)) return false;

        return getOrderNumber() == that.getOrderNumber() && getLength() == that.getLength() && getColumn().equals(that.getColumn()) && getOrder() == that.getOrder() && getIndex().equals(that.getIndex());
    }

    @Override
    public int hashCode() {
        int result = getColumn().hashCode();
        result = 31 * result + getOrderNumber();
        result = 31 * result + Objects.hashCode(getOrder());
        result = 31 * result + getLength();
        result = 31 * result + getIndex().hashCode();
        return result;
    }
}
