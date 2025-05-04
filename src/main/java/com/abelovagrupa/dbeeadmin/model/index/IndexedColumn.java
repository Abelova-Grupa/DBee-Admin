package com.abelovagrupa.dbeeadmin.model.index;

import com.abelovagrupa.dbeeadmin.model.column.Column;
import javafx.beans.property.*;

import java.net.spi.InetAddressResolver;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class IndexedColumn {

    private Column column;
    private int orderNumber;
    private Order order;
    private int length;
    private Index index;

    // Indexed column table properties
    private BooleanProperty checkedColumnProperty;
    private StringProperty columnNameProperty;
    private IntegerProperty orderNumberProperty;
    private ObjectProperty<Order> orderProperty;
    private IntegerProperty lengthProperty;

    public IndexedColumn() {
    }

    public IndexedColumn(Column column, int orderNumber, Order order, int length, Index index) {
        this.column = column;
        this.orderNumber = orderNumber;
        this.order = order;
        this.length = length;
        this.index = index;
    }

    public BooleanProperty checkedColumnProperty() {
        if(checkedColumnProperty == null){
            checkedColumnProperty = new SimpleBooleanProperty(this,"checkedColumn",false);
        }
        return checkedColumnProperty;
    }

    public void setCheckedColumnProperty(boolean checked){
        this.checkedColumnProperty().set(checked);
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public StringProperty columnNameProperty(){
        if(columnNameProperty == null){
            columnNameProperty = new SimpleStringProperty(this,"columnName",column.getName());
        }
        return columnNameProperty;
    }

    public void setColumnNameProperty(String columnName){
        this.columnNameProperty.set(columnName);
    }

    public int getOrderNumber() {
        if(orderNumberProperty != null && orderNumberProperty.get() != 0){
            return orderNumberProperty.get();
        }
        else return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        if(orderNumberProperty != null ){
            orderNumberProperty.set(orderNumber);
        }
        this.orderNumber = orderNumber;
    }

    public IntegerProperty orderNumberProperty(){
        if(orderNumberProperty == null){
            orderNumberProperty = new SimpleIntegerProperty(this,"orderNumber",orderNumber);
        }
        return orderNumberProperty;
    }

    public void setOrderNumberProperty(int orderNumber){
        this.orderNumberProperty.set(orderNumber);
    }

    public Order getOrder() {
        if(orderProperty != null && orderProperty.get() != null){
            return orderProperty.get();
        }
        else return order;
    }

    public void setOrder(Order order) {
        if(orderProperty != null){
            orderProperty.set(order);
        }
        this.order = order;
    }

    public ObjectProperty<Order> orderProperty(){
        if(orderProperty == null){
            orderProperty = new SimpleObjectProperty<>(this,"order",order);
        }
        return orderProperty;
    }

    public void setOrderProperty(Order order){
        this.orderProperty.set(order);
    }

    public int getLength() {
        if(lengthProperty != null && lengthProperty.get() != 0){
            return lengthProperty.get();
        }
        else return length;
    }

    public void setLength(int length) {
        if(lengthProperty != null){
            lengthProperty.set(length);
        }
        this.length = length;
    }

    public IntegerProperty lengthProperty(){
        if(lengthProperty == null){
            lengthProperty = new SimpleIntegerProperty(this,"length",length);
        }
        return lengthProperty;
    }

    public void setLengthProperty(int length){
        this.lengthProperty.set(length);
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

        // Table property case
        if(getIndex() == null){
            return getOrderNumber() == that.getOrderNumber() && getLength() == that.getLength() && getColumn().equals(that.getColumn()) && getOrder() == that.getOrder();
        }
        return getOrderNumber() == that.getOrderNumber() && getLength() == that.getLength() && getColumn().equals(that.getColumn()) && getOrder() == that.getOrder() && getIndex().equals(that.getIndex());
    }

    public static IndexedColumn deepCopy(IndexedColumn indexedColumn){
        return new IndexedColumn(
                Column.deepCopy(indexedColumn.getColumn()),
                indexedColumn.getOrderNumber(),
                indexedColumn.getOrder(),
                indexedColumn.getLength(),
                indexedColumn.getIndex()
        );
    }

    @Override
    public int hashCode() {
        int result = getColumn().hashCode();
        result = 31 * result + getOrderNumber();
        result = 31 * result + Objects.hashCode(getOrder());
        result = 31 * result + getLength();
//        result = 31 * result + getIndex().hashCode();
        return result;
    }
}
