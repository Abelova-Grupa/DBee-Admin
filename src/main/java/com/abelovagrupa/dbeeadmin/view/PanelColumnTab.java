package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.column.DataType;
import com.abelovagrupa.dbeeadmin.model.index.IndexType;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

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

    ObservableList<Column> columnsData = FXCollections.observableArrayList(new Column(),new Column());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setting up table properties
        columnTable.setEditable(true);
        columnTable.setItems(columnsData);

        // Setting up table column properties
        setColumnsWidth();
        setColumnsReorderable(false);
        setColumnsResizable(false);
        setColumnsEditable(true);

        // Setting column name properties
        columnNameColumn.setEditable(true);
        columnNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        // Setting data type column properties
        ObservableList<DataType> dataTypes = FXCollections.observableArrayList(DataType.values());
        columnDataTypeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(dataTypes));

        // Setting checkbox columns
        setCheckBoxes();

        // Setting default expression column properties
        columnDefaultColumn.setCellFactory(TextFieldTableCell.forTableColumn());

    }



    public void setColumnsWidth(){
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

    public void setColumnsResizable(boolean isResizable){
        for(TableColumn<?,?> column : columnTable.getColumns()){
            column.setResizable(isResizable);
        }
    }

    private void setColumnsEditable(boolean isEditable) {
        for(TableColumn<?,?> column : columnTable.getColumns()){
            column.setEditable(isEditable);
        }
    }

    private void setCheckBoxes(){
        columnPKColumn.setCellFactory(CheckBoxTableCell.forTableColumn(columnPKColumn));
        columnNNColumn.setCellFactory(CheckBoxTableCell.forTableColumn(columnNNColumn));
        columnUQColumn.setCellFactory(CheckBoxTableCell.forTableColumn(columnUQColumn));
        columnAIColumn.setCellFactory(CheckBoxTableCell.forTableColumn(columnAIColumn));
        columnZFColumn.setCellFactory(CheckBoxTableCell.forTableColumn(columnZFColumn));
        columnGColumn.setCellFactory(CheckBoxTableCell.forTableColumn(columnGColumn));
    }
}
