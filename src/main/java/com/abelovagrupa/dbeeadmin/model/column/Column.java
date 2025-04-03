package com.abelovagrupa.dbeeadmin.model.column;

import com.abelovagrupa.dbeeadmin.model.table.Table;
import javafx.beans.property.*;

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
    private boolean generationExpression;
    private String defaultValue;
    private String comment;
    private DataType type;
    private Table table;

    // TableView properties
    // Attribute setters will update these properties if they exist
    private  StringProperty nameProperty;
    private IntegerProperty sizeProperty;
    private  BooleanProperty primaryKeyProperty;
    private  BooleanProperty notNullProperty;
    private  BooleanProperty uniqueProperty;
    private  BooleanProperty zeroFillProperty;
    private  BooleanProperty autoIncrementProperty;
    private  BooleanProperty generationExpressionProperty;
    private  StringProperty defaultValueProperty;
    private  ObjectProperty<DataType> typeProperty;

    // Foreign key column table properties
    private StringProperty columnNameProperty;
    private StringProperty referencedColumnProperty;


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
                  boolean generationExpression,
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

    public boolean isPrimaryKey() {
        if(primaryKeyProperty != null)
            return primaryKeyProperty.get();
        else return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        if(primaryKeyProperty != null){
            primaryKeyProperty.set(primaryKey);
        }
        this.primaryKey = primaryKey;
    }

    public BooleanProperty primaryKeyProperty(){
        if(primaryKeyProperty == null){
            primaryKeyProperty = new SimpleBooleanProperty(this,"primaryKey",primaryKey);
        }
        return primaryKeyProperty;
    }

    public boolean isNotNull() {
        if(notNullProperty != null)
            return notNullProperty.get();
        else return notNull;
    }

    public void setNotNull(boolean notNull) {
        if(notNullProperty != null)
            notNullProperty.set(notNull);
        this.notNull = notNull;
    }

    public BooleanProperty notNullProperty(){
        if(notNullProperty == null){
            notNullProperty = new SimpleBooleanProperty(this,"notNull",notNull);
        }
        return notNullProperty;
    }

    public boolean isUnique() {
        if(uniqueProperty != null)
            return uniqueProperty.get();
        else return unique;
    }

    public void setUnique(boolean unique) {
        if(uniqueProperty != null)
            uniqueProperty.set(unique);
        this.unique = unique;
    }

    public BooleanProperty uniqueProperty(){
        if(uniqueProperty == null)
            uniqueProperty = new SimpleBooleanProperty(this,"unique",unique);
        return uniqueProperty;
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
        if(zeroFillProperty != null)
            return zeroFillProperty.get();
        else return zeroFill;
    }

    public void setZeroFill(boolean zeroFill) {
        if(zeroFillProperty != null)
            zeroFillProperty.set(zeroFill);
        this.zeroFill = zeroFill;
    }

    public BooleanProperty zeroFillProperty(){
        if(zeroFillProperty == null)
            zeroFillProperty = new SimpleBooleanProperty(this,"zeroFill",zeroFill);
        return zeroFillProperty;
    }

    public boolean isAutoIncrement() {
        if(autoIncrementProperty != null)
            return autoIncrementProperty.get();
        else return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        if(autoIncrementProperty != null)
            autoIncrementProperty.set(autoIncrement);
        this.autoIncrement = autoIncrement;
    }

    public BooleanProperty autoIncrementProperty(){
        if(autoIncrementProperty == null)
            autoIncrementProperty = new SimpleBooleanProperty(this,"autoIncrement",autoIncrement);
        return autoIncrementProperty;
    }

    public DataType getType() {
        if(typeProperty != null)
            return typeProperty.get();
        else return type;
    }

    public void setType(DataType type) {
        if(typeProperty != null)
            typeProperty.set(type);
        this.type = type;
    }

    public ObjectProperty<DataType> typeProperty(){
        if(typeProperty == null)
            typeProperty = new SimpleObjectProperty<DataType>(this,"type",type);
        return typeProperty;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Integer getSize() {
        if(sizeProperty != null)
            return sizeProperty.get();
        else return size;
    }

    public void setSize(Integer size) {
        if(sizeProperty != null)
            sizeProperty.set(size);
        this.size = size;
    }

    public IntegerProperty sizeProperty(){
        if(size == null){
            sizeProperty = new SimpleIntegerProperty(this,"size",0);
        }
        if(sizeProperty == null){
            sizeProperty = new SimpleIntegerProperty(this,"size",size);
        }
        return sizeProperty;
    }

    public boolean isGenerationExpression() {
        if(generationExpressionProperty != null)
            return generationExpressionProperty.get();
        else return generationExpression;
    }

    public void setGenerationExpression(boolean generationExpression) {
        if(generationExpressionProperty != null)
            generationExpressionProperty.set(generationExpression);
        this.generationExpression = generationExpression;
    }

    public BooleanProperty generationExpressionProperty(){
        if(generationExpressionProperty == null)
            generationExpressionProperty = new SimpleBooleanProperty(this,"generationExpression",generationExpression);
        return generationExpressionProperty;
    }

    public String getDefaultValue() {
        if(defaultValueProperty != null)
            return defaultValueProperty.get();
        else return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        if(defaultValueProperty != null)
            defaultValueProperty.set(defaultValue);
        this.defaultValue = defaultValue;
    }

    public StringProperty defaultValueProperty(){
        if(defaultValueProperty == null)
            defaultValueProperty = new SimpleStringProperty(this,"defaultValue",defaultValue);
        return defaultValueProperty;
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
        private boolean generationExpression;
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

        public ColumnBuilder setGenerationExpression(boolean generationExpression) {
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
