package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.foreignkey.ForeignKey;
import com.abelovagrupa.dbeeadmin.model.index.IndexedColumn;
import com.abelovagrupa.dbeeadmin.util.Pair;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

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
    TableView<Pair<Column,Column>> foreignKeyColumnsTable;

    @FXML
    TableColumn<Pair<Column,Column>,Boolean> foreignKeyColumnChecked;

    @FXML
    TableColumn<Column,String> foreignKeyColumnName;

    @FXML
    TableColumn<Column,String> referencedColumnNameFK;

    ObservableList<ForeignKey> foreignKeyData = FXCollections.observableArrayList(new ForeignKey(),new ForeignKey());

    ObservableList<Pair<Column,Column>> foreignKeyColumnsData = FXCollections.observableArrayList(new Pair<Column,Column>());

    ForeignKey selectedForeignKey;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setting up foreign key table properties
        foreignKeyTable.setEditable(true);
        foreignKeyTable.setItems(foreignKeyData);

        // Setting up foreign key columns table properties
        foreignKeyColumnsTable.setEditable(true);
        foreignKeyColumnsTable.setItems(foreignKeyColumnsData);

        // Setting column properties for both tables
        setColumnsWidth();
        setColumnsReorderable(false);
        setColumnsResizable(false);

        // Setting up properties for foreign key name column of fk table
        foreignKeyNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        foreignKeyNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        foreignKeyNameColumn.setOnEditCommit(event -> {
            ForeignKey foreignKey = event.getRowValue();
            foreignKey.setName(event.getNewValue());
        });

        foreignKeyColumnChecked.setCellValueFactory(cellData -> cellData.getValue().checkedColumnProperty());
        foreignKeyColumnChecked.setCellFactory(col ->{
            return new CheckBoxTableCell<Pair<Column,Column>, Boolean>() {
                private final CheckBox checkBox = new CheckBox();
                {
                    checkBox.setOnAction(event -> {
                        int row = getIndex();
                        int tableSize = foreignKeyColumnsTable.getItems().size();
                        if (row >= 0 && row < tableSize) {
                            Pair<Column,Column> foreignKeyColumnPair = foreignKeyColumnsTable.getItems().get(row);
                            boolean newValue = checkBox.isSelected();
                            foreignKeyColumnPair.setCheckedColumnProperty(newValue);
                            //If the checkBox is at true add new indexed column to index
                            if(newValue){
                                selectedForeignKey.getColumnPairs().add(foreignKeyColumnPair);
                            }else {
                                selectedForeignKey.getColumnPairs().remove(foreignKeyColumnPair);
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
