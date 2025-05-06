package com.abelovagrupa.dbeeadmin.model.column;

import com.abelovagrupa.dbeeadmin.model.table.Table;
import javafx.beans.property.*;

import java.util.*;
import java.util.function.BiFunction;

public class Column {

    private String name;
    private boolean primaryKey;
    private boolean notNull;
    private boolean unique;
    private boolean binary;
    private boolean unsigned;
    private boolean zeroFill;
    private boolean autoIncrement;
    private int size;
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
        if(nameProperty != null && nameProperty.get() != null)
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

    public int getSize() {
        if(sizeProperty != null)
            return sizeProperty.get();
        else return size;
    }

    public void setSize(int size) {
        if(sizeProperty != null)
            sizeProperty.set(size);
        this.size = size;
    }

    public IntegerProperty sizeProperty(){
        if(size == 0){
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
            (type == column.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, primaryKey, notNull, unique, binary, unsigned, zeroFill, autoIncrement, size, generationExpression, defaultValue, comment, type);
    }

    public static Column deepCopy(Column column) {
        Column deepColumn = new Column(
                column.getName(),
                column.isPrimaryKey(),
                column.isNotNull(),
                column.isUnique(),
                column.isBinary(),
                column.isUnsigned(),
                column.isZeroFill(),
                column.isAutoIncrement(),
                column.getSize(),
                column.isGenerationExpression(),
                column.getDefaultValue(),
                column.getComment(),
                column.getType(),
                column.getTable()
        );
        // Create separate method
        column.sizeProperty().set(column.getSize());
        return deepColumn;
    }

    public static boolean containsByAttributes(List<Column> columns, Column column){
        for(Column col : columns){
            if(matchesByAttributes(col, column)) return true;
        }
        return false;
    }

    public static boolean matchesByAttributes(Column a, Column b) {
        return Objects.equals(a.getName(), b.getName()) &&
                a.isPrimaryKey() == b.isPrimaryKey() &&
                a.isNotNull() == b.isNotNull() &&
                a.isUnique() == b.isUnique() &&
                a.isBinary() == b.isBinary() &&
                a.isUnsigned() == b.isUnsigned() &&
                a.isZeroFill() == b.isZeroFill() &&
                a.isAutoIncrement() == b.isAutoIncrement() &&
                Objects.equals(a.getSize(), b.getSize()) &&
                a.isGenerationExpression() == b.isGenerationExpression() &&
                Objects.equals(a.getDefaultValue(), b.getDefaultValue()) &&
                Objects.equals(a.getComment(), b.getComment()) &&
                Objects.equals(a.getType(), b.getType()) &&
                Objects.equals(a.getTable(), b.getTable());
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

    public static BiFunction<Column,Column, HashMap<String,Object[]>> columnAttributeComparator = (c1, c2) -> {
        HashMap<String,Object[]> diffs = new HashMap<>();
        if (!Objects.equals(c1.getName(), c2.getName())) {
            diffs.put("name", new Object[]{c1.getName(), c2.getName()});
        }
        if (c1.isPrimaryKey() != c2.isPrimaryKey()) {
            diffs.put("primaryKey", new Object[]{c1.isPrimaryKey(), c2.isPrimaryKey()});
        }
        if (c1.isNotNull() != c2.isNotNull()) {
            diffs.put("notNull", new Object[]{c1.isNotNull(), c2.isNotNull()});
        }
        if (c1.isUnique() != c2.isUnique()) {
            diffs.put("unique", new Object[]{c1.isUnique(), c2.isUnique()});
        }
        if (c1.isBinary() != c2.isBinary()) {
            diffs.put("binary", new Object[]{c1.isBinary(), c2.isBinary()});
        }
        if (c1.isUnsigned() != c2.isUnsigned()) {
            diffs.put("unsigned", new Object[]{c1.isUnsigned(), c2.isUnsigned()});
        }
        if (c1.isZeroFill() != c2.isZeroFill()) {
            diffs.put("zeroFill", new Object[]{c1.isZeroFill(), c2.isZeroFill()});
        }
        if (c1.isAutoIncrement() != c2.isAutoIncrement()) {
            diffs.put("autoIncrement", new Object[]{c1.isAutoIncrement(), c2.isAutoIncrement()});
        }
        if (!Objects.equals(c1.getSize(), c2.getSize())) {
            diffs.put("size", new Object[]{c1.getSize(), c2.getSize()});
        }
        if (c1.isGenerationExpression() != c2.isGenerationExpression()) {
            diffs.put("generationExpression", new Object[]{c1.isGenerationExpression(), c2.isGenerationExpression()});
        }
        if (!Objects.equals(c1.getDefaultValue(), c2.getDefaultValue())) {
            diffs.put("defaultValue", new Object[]{c1.getDefaultValue(), c2.getDefaultValue()});
        }
        if (!Objects.equals(c1.getComment(), c2.getComment())) {
            diffs.put("comment", new Object[]{c1.getComment(), c2.getComment()});
        }
        if (!Objects.equals(c1.getType(), c2.getType())) {
            diffs.put("type", new Object[]{c1.getType(), c2.getType()});
        }
        if (!Objects.equals(c1.getTable(), c2.getTable())) {
            diffs.put("table", new Object[]{c1.getTable(), c2.getTable()});
        }

        return diffs;
    };

}
