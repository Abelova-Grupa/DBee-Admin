package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.model.index.Index;
import com.abelovagrupa.dbeeadmin.model.index.IndexType;
import com.abelovagrupa.dbeeadmin.model.index.IndexedColumn;
import com.abelovagrupa.dbeeadmin.model.index.Order;
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
import java.util.ResourceBundle;

public class PanelIndexTab implements Initializable {

    @FXML
    TableView<Index> indexTable;

    @FXML
    TableColumn<Index,String> indexNameColumn;

    @FXML
    TableColumn<Index, IndexType> indexTypeColumn;

    @FXML
    TableView<IndexedColumn> indexColumnTable;

    @FXML
    TableColumn<IndexedColumn,Boolean> indexedColumnsCheckColumn;

    @FXML
    TableColumn<IndexedColumn,String> indexedColumnsNameColumn;

    @FXML
    TableColumn<IndexedColumn,Integer> indexedColumnsNoColumn;

    @FXML
    TableColumn<IndexedColumn, Order> indexedColumnOrderColumn;

    @FXML
    TableColumn<IndexedColumn,Integer> indexedColumnLength;

    ObservableList<Index> indexData = FXCollections.observableArrayList(new Index());

    ObservableList<IndexedColumn> indexedColumnData = FXCollections.observableArrayList();

    Index selectedIndex;

    PanelColumnTab columnTabController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setting up index table properties
        indexTable.setEditable(true);
        indexTable.setItems(indexData);

        // Setting up indexed column table properties
        indexColumnTable.setEditable(true);
        indexColumnTable.setItems(indexedColumnData);

        // Setting column properties for both tables
        setColumnsWidth();
        setColumnsResizable(false);
        setColumnsReorderable(false);
        setColumnsEditable(true);

        // Setting up properties for columns name column of index table
        indexNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        indexNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        indexNameColumn.setOnEditCommit(event -> {
            Index index = event.getRowValue();
            index.setName(event.getNewValue());
        });

        // Loading combo box with index types and setting up properties for index type
        ObservableList<IndexType> indexTypes = FXCollections.observableArrayList(IndexType.values());
        indexTypeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        indexTypeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(indexTypes));
        indexTypeColumn.setOnEditCommit(event -> {
            Index index = event.getRowValue();
            index.setType(event.getNewValue());
        });

        // Setting up selection listener on index table
        indexTable.getSelectionModel().selectedItemProperty().addListener((_, oldSelection, newSelection) -> {
            if(newSelection == null) return;
            selectedIndex = newSelection;
            // Creating a list of indexedColumns on first selection
            if(selectedIndex.getIndexedColumns() == null){
                selectedIndex.setIndexedColumns(new LinkedList<>());
            }
            int index = indexTable.getItems().indexOf(newSelection);

            newSelection.nameProperty().addListener((_,oldVal,newVal) -> {
                int lastIndex = indexTable.getItems().size() - 1;
                if(index != lastIndex) return;
                indexTable.getItems().add(new Index());
            });

            newSelection.typeProperty().addListener((_,oldVal,newVal) -> {
                int lastIndex = indexTable.getItems().size() - 1;
                if(index != lastIndex) return;
                indexTable.getItems().add(new Index());
            });

            // Loading indexed columns of a selected index
            for(IndexedColumn tableColumn : indexedColumnData){
                if(selectedIndex.getIndexedColumns().contains(tableColumn)){
                    tableColumn.setCheckedColumnProperty(true);
                }else {
                    tableColumn.setCheckedColumnProperty(false);
                }
            }

        });

        // Setting up checkbox for checking columns of an index
        indexedColumnsCheckColumn.setCellValueFactory(cellData -> cellData.getValue().checkedColumnProperty());
        indexedColumnsCheckColumn.setCellFactory(col ->{
            return new CheckBoxTableCell<IndexedColumn, Boolean>() {
                private final CheckBox checkBox = new CheckBox();
                {
                    checkBox.setOnAction(event -> {
                        int row = getIndex();
                        int tableSize = indexColumnTable.getItems().size();
                        if (row >= 0 && row < tableSize) {
                            IndexedColumn indexedColumn = indexColumnTable.getItems().get(row);
                            boolean newValue = checkBox.isSelected();
                            indexedColumn.setCheckedColumnProperty(newValue);
                            //If the checkBox is at true add new indexed column to index
                            if(newValue){
                                selectedIndex.getIndexedColumns().add(indexedColumn);
                                indexedColumn.setIndex(selectedIndex);
                            }else {
                                selectedIndex.getIndexedColumns().remove(indexedColumn);
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

        // Setting up properties for indexed column name of indexedColumn tables
        indexedColumnsNameColumn.setCellValueFactory(cellData -> cellData.getValue().columnNameProperty());
        indexedColumnsNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        indexedColumnsNameColumn.setEditable(false);

        // Setting up properties for indexed column order number of indexedColumn tables
        indexedColumnsNoColumn.setCellValueFactory(cellData -> cellData.getValue().orderNumberProperty().asObject());
        indexedColumnsNoColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        indexedColumnsNoColumn.setOnEditCommit(event -> {
            IndexedColumn indexedColumn = event.getRowValue();
            indexedColumn.setOrderNumber(event.getNewValue());
        });

        ObservableList<Order> orderTypes = FXCollections.observableArrayList(Order.values());
        indexedColumnOrderColumn.setCellValueFactory(cellData -> cellData.getValue().orderProperty());
        indexedColumnOrderColumn.setCellFactory(ComboBoxTableCell.forTableColumn(orderTypes));
        indexedColumnOrderColumn.setOnEditCommit(event -> {
            IndexedColumn indexedColumn = event.getRowValue();
            indexedColumn.setOrder(event.getNewValue());
        });

        indexedColumnLength.setCellValueFactory(cellData -> cellData.getValue().lengthProperty().asObject());
        indexedColumnLength.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        indexedColumnLength.setOnEditCommit(event -> {
            IndexedColumn indexedColumn = event.getRowValue();
            indexedColumn.setLength(event.getNewValue());
        });

    }

    private void setColumnsEditable(boolean editable) {
        for(TableColumn<?,?> column : indexTable.getColumns()){
            column.setEditable(editable);
        }

        for(TableColumn<?,?> column : indexColumnTable.getColumns()){
            column.setEditable(editable);
        }

    }

    public void setColumnsWidth(){
        // Setting width of index table
        ReadOnlyDoubleProperty indexTableWidthProperty = indexTable.widthProperty();
        indexNameColumn.prefWidthProperty().bind(indexTableWidthProperty.multiply(0.3));
        indexTypeColumn.prefWidthProperty().bind(indexTableWidthProperty.multiply(0.7));

        ReadOnlyDoubleProperty indexedColumnTableWidthProperty = indexColumnTable.widthProperty();
        indexedColumnsCheckColumn.prefWidthProperty().bind(indexedColumnTableWidthProperty.multiply(0.075));
        indexedColumnsNameColumn.prefWidthProperty().bind(indexedColumnTableWidthProperty.multiply(0.25));
        indexedColumnsNoColumn.prefWidthProperty().bind(indexedColumnTableWidthProperty.multiply(0.075));
        indexedColumnOrderColumn.prefWidthProperty().bind(indexedColumnTableWidthProperty.multiply(0.2));
        indexedColumnLength.prefWidthProperty().bind(indexedColumnTableWidthProperty.multiply(0.4));

    }

    public void setColumnsReorderable(boolean reorderable){
        for(TableColumn<?,?> column : indexTable.getColumns()){
            column.setReorderable(reorderable);
        }
        for(TableColumn<?,?> column : indexColumnTable.getColumns()){
            column.setReorderable(reorderable);
        }
    }

    public void setColumnsResizable(boolean resizable){
        for(TableColumn<?,?> column : indexTable.getColumns()){
            column.setResizable(resizable);
        }

        for(TableColumn<?,?> column : indexColumnTable.getColumns()){
            column.setResizable(resizable);
        }
    }

    public boolean emptyProperties(Index index){
        return index.getName() == null || index.getType() == null;

    }

    public boolean primaryExists() {
        for(Index index: indexTable.getItems()){
            if(index.getName() == null) continue;
            if(index.getName().equals("PRIMARY") && index.getType().equals(IndexType.PRIMARY)){
                return true;
            }
        }
        return false;
    }
}
