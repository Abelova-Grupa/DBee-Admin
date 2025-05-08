package com.abelovagrupa.dbeeadmin.model.foreignkey;

import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.util.Pair;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

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

    public static ForeignKeyColumns deepCopy(ForeignKeyColumns foreignKeyColumnPair) {
        if(foreignKeyColumnPair.getFirst() != null && foreignKeyColumnPair.getSecond() != null){
            return new ForeignKeyColumns(
                    Column.deepCopy(foreignKeyColumnPair.first),
                    Column.deepCopy(foreignKeyColumnPair.second));
        }else if (foreignKeyColumnPair.getFirst() != null){
            return new ForeignKeyColumns(Column.deepCopy(foreignKeyColumnPair.first),null);
        }else {
            return new ForeignKeyColumns(null,Column.deepCopy(foreignKeyColumnPair.second));
        }

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

    public static BiFunction<ForeignKeyColumns, ForeignKeyColumns, HashMap<String, Object[]>> foreignKeyColumnComparator = (fkc1, fkc2) -> {
        HashMap<String, Object[]> diffs = new HashMap<>();

        if (!Objects.equals(fkc1.getFirst(), fkc2.getFirst())) {
            diffs.put("referencingColumn", new Object[]{fkc1.getFirst(), fkc2.getFirst()});
        }
        if (!Objects.equals(fkc1.getSecond(), fkc2.getSecond())) {
            diffs.put("referencedColumn", new Object[]{fkc1.getSecond(), fkc2.getSecond()});
        }

        return diffs;
    };

    public static boolean containsByAttributes(List<ForeignKeyColumns> foreignKeyColumnsList, ForeignKeyColumns foreignKeyColumn){
        for(ForeignKeyColumns fkc : foreignKeyColumnsList){
            if(matchesByAttributes(fkc, foreignKeyColumn)) return true;
        }
        return false;
    }

    public static boolean matchesByAttributes(ForeignKeyColumns a, ForeignKeyColumns b) {
        return Objects.equals(a.getFirst(), b.getFirst()) &&
                Objects.equals(a.getSecond(), b.getSecond());
    }

}
