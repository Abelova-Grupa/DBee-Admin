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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;


import java.net.URL;
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

    ObservableList<Index> indexData = FXCollections.observableArrayList(new Index(),new Index());

    ObservableList<IndexedColumn> indexedColumnData = FXCollections.observableArrayList(new IndexedColumn(), new IndexedColumn());

    Index selectedIndex;

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
        indexNameColumn.setCellValueFactory(new PropertyValueFactory<Index,String>("name"));
        indexNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        indexNameColumn.setOnEditCommit(event -> {
            Index index = event.getRowValue();
            index.setName(event.getNewValue());
        });

        // Loading combo box with index types and setting up properties for index type
        ObservableList<IndexType> indexTypes = FXCollections.observableArrayList(IndexType.values());
        indexTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        indexTypeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(indexTypes));
        indexTypeColumn.setOnEditCommit(event -> {
            Index index = event.getRowValue();
            index.setType(event.getNewValue());
        });

        // Setting up selection listener on index table
        indexTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != oldValue || newValue != null){
                selectedIndex = newValue;
            }
        });

        // Setting up checkbox for checking columns of an index
        indexedColumnsCheckColumn.setCellFactory(CheckBoxTableCell.forTableColumn(indexedColumnsCheckColumn));
        indexedColumnsCheckColumn.setOnEditCommit(event -> {
            IndexedColumn indexedColumn = event.getRowValue();
            selectedIndex.getIndexedColumns().add(indexedColumn);
        });


        // Setting up properties for indexed column name of indexedColumn tables
        indexedColumnsNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        indexedColumnsNameColumn.setEditable(false);

        // Setting up properties for indexed column order number of indexedColumn tables
        indexedColumnsNoColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        indexedColumnsNoColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        indexedColumnsNoColumn.setOnEditCommit(event -> {
            IndexedColumn indexedColumn = event.getRowValue();
            indexedColumn.setOrderNumber(event.getNewValue());
        });

        ObservableList<Order> orderTypes = FXCollections.observableArrayList(Order.values());
        indexedColumnOrderColumn.setCellValueFactory(new PropertyValueFactory<>("order"));
        indexedColumnOrderColumn.setCellFactory(ComboBoxTableCell.forTableColumn(orderTypes));
        indexedColumnOrderColumn.setOnEditCommit(event -> {
            IndexedColumn indexedColumn = event.getRowValue();
            indexedColumn.setOrder(event.getNewValue());
        });

        indexedColumnLength.setCellValueFactory(new PropertyValueFactory<>("length"));
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

    public void setColumnsReorderable(boolean isReorderable){
        for(TableColumn<?,?> column : indexTable.getColumns()){
            column.setReorderable(isReorderable);
        }
        for(TableColumn<?,?> column : indexColumnTable.getColumns()){
            column.setReorderable(isReorderable);
        }
    }

    public void setColumnsResizable(boolean isResizable){
        for(TableColumn<?,?> column : indexTable.getColumns()){
            column.setResizable(isResizable);
        }

        for(TableColumn<?,?> column : indexColumnTable.getColumns()){
            column.setResizable(isResizable);
        }
    }

}
