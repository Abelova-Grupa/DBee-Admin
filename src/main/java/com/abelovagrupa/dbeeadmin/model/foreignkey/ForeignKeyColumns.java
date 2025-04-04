package com.abelovagrupa.dbeeadmin.model.foreignkey;

import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.util.Pair;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;

public class ForeignKeyColumns extends Pair<Column,Column>{

    private Column referencingColumn;
    private Column referencedColumn;

    public Column getReferencingColumn() {
        return referencingColumn;
    }

    public void setReferencingColumn(Column referencingColumn) {
        this.referencingColumn = referencingColumn;
    }

    public Column getReferencedColumn() {
        return referencedColumn;
    }

    public void setReferencedColumn(Column referencedColumn) {
        this.referencedColumn = referencedColumn;
    }

    // Table properties
    private BooleanProperty checkedColumnProperty;
    private StringProperty columnNameProperty;
    private StringProperty referencedColumnProperty;

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
