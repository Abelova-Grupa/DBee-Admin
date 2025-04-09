package com.abelovagrupa.dbeeadmin.model.foreignkey;

import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.util.Pair;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ForeignKeyColumns extends Pair<Column,Column>{

    // Table properties
    private BooleanProperty checkedColumnProperty;
    private StringProperty columnNameProperty;
    private StringProperty referencedColumnProperty;

    public ForeignKeyColumns(Column referencingColumn, Column referencedColumn){
        this.first = referencingColumn;
        this.second = referencedColumn;
    }

    public ForeignKeyColumns(){

    }

    public BooleanProperty checkedColumnProperty(){
        if(checkedColumnProperty == null){
            checkedColumnProperty = new SimpleBooleanProperty(this,"checkedColumn",false);
        }
        return checkedColumnProperty;
    }

    public void setCheckedColumnProperty(boolean checked){
        checkedColumnProperty().set(checked);
    }

    public StringProperty columnNameProperty(){
        if(columnNameProperty == null){
            columnNameProperty = new SimpleStringProperty(this,"columnName","");
        }
        return columnNameProperty;
    }

    public void setColumnNameProperty(String columnName){
        columnNameProperty().set(columnName);
    }

    public StringProperty referencedColumnProperty(){
        if(referencedColumnProperty == null){
            referencedColumnProperty = new SimpleStringProperty(this,"referencedColumn","");
        }
        return referencedColumnProperty;
    }

    public void setReferencedColumnProperty(String columnName){
        referencedColumnProperty().set(columnName);
    }

}
