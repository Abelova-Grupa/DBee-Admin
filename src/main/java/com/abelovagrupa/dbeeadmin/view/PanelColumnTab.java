package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.column.DataType;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class PanelColumnTab implements Initializable{

    @FXML
    TableView<Column> columnTable;

    @FXML
    TableColumn<Column,String> columnNameColumn;

    @FXML
    TableColumn<Column,DataType> columnDataTypeColumn;

    @FXML
    TableColumn<Column,Boolean> columnPKColumn;

    @FXML
    TableColumn<Column,Boolean> columnNNColumn;

    @FXML
    TableColumn<Column,Boolean> columnUQColumn;

    @FXML
    TableColumn<Column,Boolean> columnZFColumn;

    @FXML
    TableColumn<Column,Boolean> columnAIColumn;

    @FXML
    TableColumn<Column,Boolean> columnGColumn;

    @FXML
    TableColumn<Column,String> columnDefaultColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setColumnWidth();
        setColumnsReorderable(false);
        setColumnResizable(false);
    }

    public void setColumnWidth(){
        // Current solution is to set columns widths to all sum up to 1 - table width
        ReadOnlyDoubleProperty tableWidthProperty = columnTable.widthProperty();
        columnNameColumn.prefWidthProperty().bind(tableWidthProperty.multiply(0.35));
        columnDataTypeColumn.prefWidthProperty().bind(tableWidthProperty.multiply(0.20));
        columnPKColumn.prefWidthProperty().bind(tableWidthProperty.multiply(0.035));
        columnNNColumn.prefWidthProperty().bind(tableWidthProperty.multiply(0.035));
        columnPKColumn.prefWidthProperty().bind(tableWidthProperty.multiply(0.035));
        columnUQColumn.prefWidthProperty().bind(tableWidthProperty.multiply(0.035));
        columnZFColumn.prefWidthProperty().bind(tableWidthProperty.multiply(0.035));
        columnAIColumn.prefWidthProperty().bind(tableWidthProperty.multiply(0.035));
        columnGColumn.prefWidthProperty().bind(tableWidthProperty.multiply(0.035));
        columnDefaultColumn.prefWidthProperty().bind(tableWidthProperty.multiply(0.235));

    }

    public void setColumnsReorderable(boolean isReorderable){
        for(TableColumn<?,?> column : columnTable.getColumns()){
            column.setReorderable(isReorderable);
        }
    }

    public void setColumnResizable(boolean isResizable){
        for(TableColumn<?,?> column : columnTable.getColumns()){
            column.setResizable(isResizable);
        }
    }
}
