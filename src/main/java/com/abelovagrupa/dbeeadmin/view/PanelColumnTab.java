package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.column.DataType;
import com.abelovagrupa.dbeeadmin.model.index.Index;
import com.abelovagrupa.dbeeadmin.model.index.IndexType;
import com.abelovagrupa.dbeeadmin.model.index.IndexedColumn;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class PanelColumnTab implements Initializable{

    @FXML
    TableView<Column> columnTable;

    @FXML
    TableColumn<Column,String> columnNameColumn;

    @FXML
    TableColumn<Column,DataType> columnDataTypeColumn;

    @FXML
    TableColumn<Column,Integer> columnSizeColumn;

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

    ObservableList<Column> columnsData = FXCollections.observableArrayList(new Column());

    // Controllers
    PanelIndexTab indexTabController;

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
        columnNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        columnNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columnNameColumn.setOnEditCommit(event -> {
            Column column = event.getRowValue();
            column.setName(event.getNewValue());
        });

        // Setting data type column properties
        ObservableList<DataType> dataTypes = FXCollections.observableArrayList(DataType.values());
        columnDataTypeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        columnDataTypeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(dataTypes));
        columnDataTypeColumn.setOnEditCommit(event -> {
            Column column = event.getRowValue();
            column.setType(event.getNewValue());
        });
        // Setting column size properties for certain types
        columnSizeColumn.setCellValueFactory(cellData -> cellData.getValue().sizeProperty().asObject());
        columnSizeColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        columnSizeColumn.setOnEditCommit(event -> {
            Column column = event.getRowValue();
            column.setSize(event.getNewValue());
        });
        // Setting checkbox columns
        setCheckBoxes();

        // Setting default expression column properties
        columnDefaultColumn.setCellValueFactory(cellData -> cellData.getValue().defaultValueProperty());
        columnDefaultColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        columnDefaultColumn.setOnEditCommit(event -> {
            Column column = event.getRowValue();
            column.setDefaultValue(event.getNewValue());
        });

        // Setting up listener for data change in column table
        columnTable.getSelectionModel().selectedItemProperty().addListener((observable,oldSelection,newSelection) -> {
            if(newSelection == null) return;
            int index = columnTable.getItems().indexOf(newSelection);

            // Column name property listener
            newSelection.nameProperty().addListener((obs,oldVal,newVal) -> {
                int lastIndex = columnTable.getItems().size() -1;
                if(index != lastIndex) return;
                columnTable.getItems().add(new Column());
            });
            // Column type property listener
            newSelection.typeProperty().addListener((obs,oldVal,newVal) -> {
                columnSizeColumn.setEditable(DataType.hasVariableLength(newVal));

                int lastIndex = columnTable.getItems().size() -1;
                if(index != lastIndex) return;
                columnTable.getItems().add(new Column());
            });
            // Column default value property listener
            newSelection.defaultValueProperty().addListener((obs,oldVal,newVal) -> {
                int lastIndex = columnTable.getItems().size() -1;
                if(index != lastIndex) return;
                columnTable.getItems().add(new Column());
            });

        });

    }



    public void setColumnsWidth(){
        // Current solution is to set columns widths to all sum up to 1 - table width
        ReadOnlyDoubleProperty tableWidthProperty = columnTable.widthProperty();
        columnNameColumn.prefWidthProperty().bind(tableWidthProperty.multiply(0.35));
        columnDataTypeColumn.prefWidthProperty().bind(tableWidthProperty.multiply(0.20));
        columnSizeColumn.prefWidthProperty().bind(tableWidthProperty.multiply(0.035));
        columnPKColumn.prefWidthProperty().bind(tableWidthProperty.multiply(0.035));
        columnNNColumn.prefWidthProperty().bind(tableWidthProperty.multiply(0.035));
        columnPKColumn.prefWidthProperty().bind(tableWidthProperty.multiply(0.035));
        columnUQColumn.prefWidthProperty().bind(tableWidthProperty.multiply(0.035));
        columnZFColumn.prefWidthProperty().bind(tableWidthProperty.multiply(0.035));
        columnAIColumn.prefWidthProperty().bind(tableWidthProperty.multiply(0.035));
        columnGColumn.prefWidthProperty().bind(tableWidthProperty.multiply(0.035));
        columnDefaultColumn.prefWidthProperty().bind(tableWidthProperty.multiply(0.2));

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
        columnPKColumn.setCellValueFactory(cellData -> cellData.getValue().primaryKeyProperty());
        // Creating a custom checkBox that has a listener for checking
        columnPKColumn.setCellFactory(col ->{
            return new CheckBoxTableCell<Column, Boolean>() {
                private final CheckBox checkBox = new CheckBox();
                {
                    checkBox.setOnAction(event -> {
                        int row = getIndex();
                        int tableSize = columnTable.getItems().size();
                        if (row >= 0 && row < tableSize) {
                            Column column = columnTable.getItems().get(row);
                            boolean newValue = checkBox.isSelected();
                            column.setPrimaryKey(newValue);
                            //Checking not null if the primary key checkbox is selected
                            if(newValue){
                                // Checking not null box
                                column.setNotNull(newValue);
                                columnTable.refresh();

                                // Creating a PRIMARY Index in index tab
                                if(indexTabController != null && !indexTabController.primaryExists()){
                                    Index primaryIndex = new Index();
                                    primaryIndex.setName("PRIMARY");
                                    primaryIndex.setType(IndexType.PRIMARY);
                                    primaryIndex.setIndexedColumns(new LinkedList<>());
                                    IndexedColumn newColumn = new IndexedColumn();
                                    newColumn.setColumn(column);
                                    newColumn.setIndex(primaryIndex);
                                    primaryIndex.getIndexedColumns().add(newColumn);
                                    indexTabController.primaryIndex = primaryIndex;
                                    int indexTableSize = indexTabController.indexTable.getItems().size() - 1;
                                    indexTabController.indexTable.getItems().set(indexTableSize,primaryIndex);
                                    indexTabController.indexTable.getItems().add(new Index());
                                }else if(indexTabController != null){
                                    IndexedColumn newColumn = new IndexedColumn();
                                    newColumn.setColumn(column);
                                    indexTabController.primaryIndex.getIndexedColumns().add(newColumn);
                                    newColumn.setIndex(indexTabController.primaryIndex);
                                }

                            }
                            // Add new table row if its last
                            if(row == tableSize -1){
                                columnTable.getItems().add(new Column());
                            }
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
        columnNNColumn.setCellValueFactory(cellData -> cellData.getValue().notNullProperty());
        columnNNColumn.setCellFactory(col ->{
            return new CheckBoxTableCell<Column, Boolean>() {
                private final CheckBox checkBox = new CheckBox();
                {
                    checkBox.setOnAction(event -> {
                        int row = getIndex();
                        int tableSize = columnTable.getItems().size();
                        if (row >= 0 && row < tableSize) {
                            Column column = columnTable.getItems().get(row);
                            boolean newValue = checkBox.isSelected();
                            column.setNotNull(newValue);
                            if(row == tableSize -1){
                                columnTable.getItems().add(new Column());
                            }
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
        columnUQColumn.setCellValueFactory(cellData -> cellData.getValue().uniqueProperty());
        columnUQColumn.setCellFactory(col ->{
            return new CheckBoxTableCell<Column, Boolean>() {
                private final CheckBox checkBox = new CheckBox();
                {
                    checkBox.setOnAction(event -> {
                        int row = getIndex();
                        int tableSize = columnTable.getItems().size();
                        if (row >= 0 && row < tableSize) {
                            Column column = columnTable.getItems().get(row);
                            boolean newValue = checkBox.isSelected();
                            column.setUnique(newValue);
                            if(row == tableSize -1){
                                columnTable.getItems().add(new Column());
                            }
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

        columnAIColumn.setCellValueFactory(cellData -> cellData.getValue().autoIncrementProperty());
        columnAIColumn.setCellFactory(col ->{
            return new CheckBoxTableCell<Column, Boolean>() {
                private final CheckBox checkBox = new CheckBox();
                {
                    checkBox.setOnAction(event -> {
                        int row = getIndex();
                        int tableSize = columnTable.getItems().size();
                        if (row >= 0 && row < tableSize) {
                            Column column = columnTable.getItems().get(row);
                            boolean newValue = checkBox.isSelected();
                            column.setAutoIncrement(newValue);
                            if(row == tableSize - 1){
                                columnTable.getItems().add(new Column());
                            }
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

        columnZFColumn.setCellValueFactory(cellData -> cellData.getValue().zeroFillProperty());
        columnZFColumn.setCellFactory(col ->{
            return new CheckBoxTableCell<Column, Boolean>() {
                private final CheckBox checkBox = new CheckBox();
                {
                    checkBox.setOnAction(event -> {
                        int row = getIndex();
                        int tableSize = columnTable.getItems().size();
                        if (row >= 0 && row < tableSize) {
                            Column column = columnTable.getItems().get(row);
                            boolean newValue = checkBox.isSelected();
                            column.setZeroFill(newValue);
                            if(row == tableSize -1){
                                columnTable.getItems().add(new Column());
                            }
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

        columnGColumn.setCellValueFactory(cellData -> cellData.getValue().generationExpressionProperty());
        columnGColumn.setCellFactory(col ->{
            return new CheckBoxTableCell<Column, Boolean>() {
                private final CheckBox checkBox = new CheckBox();
                {
                    checkBox.setOnAction(event -> {
                        int row = getIndex();
                        int tableSize = columnTable.getItems().size();
                        if (row >= 0 && row < tableSize) {
                            Column column = columnTable.getItems().get(row);
                            boolean newValue = checkBox.isSelected();
                            column.setGenerationExpression(newValue);
                            if(row == tableSize -1){
                                columnTable.getItems().add(new Column());
                            }
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

    public List<Column> getTableColumns(){
        columnsData.removeIf(this::emptyProperties);
        return columnsData;
    }

    public boolean emptyProperties(Column column){
        // For some reason nameProperties of empty rows are not null
        return column.getName() == null || column.getType() == null;
    }
}
