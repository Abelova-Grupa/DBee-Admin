package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.column.DataType;
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
        columnNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columnNameColumn.setOnEditCommit(event -> {
            Column column = event.getRowValue();
            column.setName(event.getNewValue());
        });

        // Setting data type column properties
        ObservableList<DataType> dataTypes = FXCollections.observableArrayList(DataType.values());
        columnDataTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        columnDataTypeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(dataTypes));
        columnDataTypeColumn.setOnEditCommit(event -> {
            Column column = event.getRowValue();
            column.setType(event.getNewValue());
        });

        // Setting checkbox columns
        setCheckBoxes();

        // Setting default expression column properties
        columnDefaultColumn.setCellValueFactory(new PropertyValueFactory<>("defaultValue"));
        columnDefaultColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columnDefaultColumn.setOnEditCommit(event -> {
            Column column = event.getRowValue();
            column.setDefaultValue(event.getNewValue());
        });
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
        columnPKColumn.setCellValueFactory(new PropertyValueFactory<>("primaryKey"));
        // Creating a custom checkBox that has a listener for checking
        columnPKColumn.setCellFactory(col ->{
            return new CheckBoxTableCell<Column, Boolean>() {
                private final CheckBox checkBox = new CheckBox();
                {
                    checkBox.setOnAction(event -> {
                        int row = getIndex();
                        if (row >= 0 && row < columnTable.getItems().size()) {
                            Column column = columnTable.getItems().get(row);
                            boolean newValue = checkBox.isSelected();
                            column.setPrimaryKey(newValue);
                        }
                    });
                }

                // CheckBox rendering logic
                @Override
                public void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        checkBox.setSelected(item != null && item);
                        setGraphic(checkBox);
                    }
                }
            };
        });

        columnNNColumn.setCellFactory(col ->{
            return new CheckBoxTableCell<Column, Boolean>() {
                private final CheckBox checkBox = new CheckBox();
                {
                    checkBox.setOnAction(event -> {
                        int row = getIndex();
                        if (row >= 0 && row < columnTable.getItems().size()) {
                            Column column = columnTable.getItems().get(row);
                            boolean newValue = checkBox.isSelected();
                            column.setNotNull(newValue);
                        }
                    });
                }

                // CheckBox rendering logic
                @Override
                public void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        checkBox.setSelected(item != null && item);
                        setGraphic(checkBox);
                    }
                }
            };
        });

        columnUQColumn.setCellFactory(col ->{
            return new CheckBoxTableCell<Column, Boolean>() {
                private final CheckBox checkBox = new CheckBox();
                {
                    checkBox.setOnAction(event -> {
                        int row = getIndex();
                        if (row >= 0 && row < columnTable.getItems().size()) {
                            Column column = columnTable.getItems().get(row);
                            boolean newValue = checkBox.isSelected();
                            column.setUnique(newValue);
                        }
                    });
                }

                // CheckBox rendering logic
                @Override
                public void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        checkBox.setSelected(item != null && item);
                        setGraphic(checkBox);
                    }
                }
            };
        });

        columnAIColumn.setCellFactory(col ->{
            return new CheckBoxTableCell<Column, Boolean>() {
                private final CheckBox checkBox = new CheckBox();
                {
                    checkBox.setOnAction(event -> {
                        int row = getIndex();
                        if (row >= 0 && row < columnTable.getItems().size()) {
                            Column column = columnTable.getItems().get(row);
                            boolean newValue = checkBox.isSelected();
                            column.setAutoIncrement(newValue);
                        }
                    });
                }

                // CheckBox rendering logic
                @Override
                public void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        checkBox.setSelected(item != null && item);
                        setGraphic(checkBox);
                    }
                }
            };
        });

        columnZFColumn.setCellFactory(col ->{
            return new CheckBoxTableCell<Column, Boolean>() {
                private final CheckBox checkBox = new CheckBox();
                {
                    checkBox.setOnAction(event -> {
                        int row = getIndex();
                        if (row >= 0 && row < columnTable.getItems().size()) {
                            Column column = columnTable.getItems().get(row);
                            boolean newValue = checkBox.isSelected();
                            column.setZeroFill(newValue);
                        }
                    });
                }

                // CheckBox rendering logic
                @Override
                public void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        checkBox.setSelected(item != null && item);
                        setGraphic(checkBox);
                    }
                }
            };
        });

        columnGColumn.setCellFactory(col ->{
            return new CheckBoxTableCell<Column, Boolean>() {
                private final CheckBox checkBox = new CheckBox();
                {
                    checkBox.setOnAction(event -> {
                        int row = getIndex();
                        if (row >= 0 && row < columnTable.getItems().size()) {
                            Column column = columnTable.getItems().get(row);
                            boolean newValue = checkBox.isSelected();
                            column.setGenerationExpression(newValue);
                        }
                    });
                }

                // CheckBox rendering logic
                @Override
                public void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        checkBox.setSelected(item != null && item);
                        setGraphic(checkBox);
                    }
                }
            };
        });
    }
}
