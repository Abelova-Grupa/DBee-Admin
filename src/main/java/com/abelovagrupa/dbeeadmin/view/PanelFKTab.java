package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.foreignkey.ForeignKey;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.net.URL;
import java.util.ResourceBundle;

public class PanelFKTab implements Initializable {

    @FXML
    TableView<ForeignKey> foreignKeyTable;

    @FXML
    TableColumn<ForeignKey,String> foreignKeyNameColumn;

    @FXML
    TableColumn<ForeignKey,String> referencedTableFK;

    @FXML
    TableView<Column> foreignKeyColumnsTable;

    @FXML
    TableColumn<Column,Boolean> foreignKeyColumnChecked;

    @FXML
    TableColumn<Column,String> foreignKeyColumnName;

    @FXML
    TableColumn<Column,String> referencedColumnNameFK;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setColumnsWidth();
        setColumnsReorderable(false);
        setColumnsResizable(false);
        foreignKeyColumnChecked.setCellFactory(CheckBoxTableCell.forTableColumn(foreignKeyColumnChecked));
    }

    public void setColumnsWidth(){
        ReadOnlyDoubleProperty foreignKeyTableWidthProperty = foreignKeyTable.widthProperty();
        foreignKeyNameColumn.prefWidthProperty().bind(foreignKeyTableWidthProperty.multiply(0.4));
        referencedTableFK.prefWidthProperty().bind(foreignKeyTableWidthProperty.multiply(0.6));

        ReadOnlyDoubleProperty foreignKeyColumnsTableWidthProperty = foreignKeyColumnsTable.widthProperty();
        foreignKeyColumnChecked.prefWidthProperty().bind(foreignKeyColumnsTableWidthProperty.multiply(0.075));
        foreignKeyColumnName.prefWidthProperty().bind(foreignKeyColumnsTableWidthProperty.multiply(0.3));
        referencedColumnNameFK.prefWidthProperty().bind(foreignKeyColumnsTableWidthProperty.multiply(0.625));
    }

    public void setColumnsReorderable(boolean isReorderable){
        for(TableColumn<?,?> column : foreignKeyTable.getColumns()){
            column.setReorderable(isReorderable);
        }
        for(TableColumn<?,?> column : foreignKeyColumnsTable.getColumns()){
            column.setReorderable(isReorderable);
        }
    }

    public void setColumnsResizable(boolean isResizable){
        for(TableColumn<?,?> column : foreignKeyTable.getColumns()){
            column.setResizable(isResizable);
        }

        for(TableColumn<?,?> column : foreignKeyColumnsTable.getColumns()){
            column.setResizable(isResizable);
        }
    }
}
