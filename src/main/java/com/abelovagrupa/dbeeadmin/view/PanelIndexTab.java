package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.model.index.Index;
import com.abelovagrupa.dbeeadmin.model.index.IndexedColumn;
import com.abelovagrupa.dbeeadmin.model.index.Order;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;


import java.net.URL;
import java.util.ResourceBundle;

public class PanelIndexTab implements Initializable {

    @FXML
    TableView<Index> indexTable;

    @FXML
    TableColumn<Index,String> indexNameColumn;

    @FXML
    TableColumn<Index,String> referencedTableColumnIX;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setColumnsWidth();
        setColumnsResizable(false);
        setColumnsReorderable(false);
    }

    public void setColumnsWidth(){
        // Setting width of index table
        ReadOnlyDoubleProperty indexTableWidthProperty = indexTable.widthProperty();
        indexNameColumn.prefWidthProperty().bind(indexTableWidthProperty.multiply(0.3));
        referencedTableColumnIX.prefWidthProperty().bind(indexTableWidthProperty.multiply(0.7));

        ReadOnlyDoubleProperty indexedColumnTableWidthProperty = indexColumnTable.widthProperty();
        indexedColumnsCheckColumn.prefWidthProperty().bind(indexedColumnTableWidthProperty.multiply(0.075));
        indexedColumnsNameColumn.prefWidthProperty().bind(indexedColumnTableWidthProperty.multiply(0.25));
        indexedColumnsNoColumn.prefWidthProperty().bind(indexedColumnTableWidthProperty.multiply(0.075));
        indexedColumnOrderColumn.prefWidthProperty().bind(indexedColumnTableWidthProperty.multiply(0.2));
        indexedColumnLength.prefWidthProperty().bind(indexedColumnTableWidthProperty.multiply(0.45));

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
