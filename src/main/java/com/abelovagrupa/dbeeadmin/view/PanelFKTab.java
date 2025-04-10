package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.controller.DatabaseInspector;
import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.foreignkey.Action;
import com.abelovagrupa.dbeeadmin.model.foreignkey.ForeignKey;
import com.abelovagrupa.dbeeadmin.model.foreignkey.ForeignKeyColumns;
import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.model.table.Table;
import com.abelovagrupa.dbeeadmin.services.ProgramState;
import com.abelovagrupa.dbeeadmin.util.Pair;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PanelFKTab implements Initializable {

    @FXML
    TableView<ForeignKey> foreignKeyTable;

    @FXML
    TableColumn<ForeignKey,String> fkNameTableColumn;

    @FXML
    TableColumn<ForeignKey,String> fkReferencedTableColumn;

    @FXML
    TableView<ForeignKeyColumns> foreignKeyColumnsTable;

    @FXML
    TableColumn<ForeignKeyColumns,Boolean> fkcheckedTableColumn;

    @FXML
    TableColumn<ForeignKeyColumns,String> fkColumnNameTableColumn;

    @FXML
    TableColumn<ForeignKeyColumns,String> fkReferencedColumnTableColumn;

    // Foreign key options
    @FXML
    ComboBox<Action> cbOnUpdate;

    @FXML
    ComboBox<Action> cbOnDelete;

    @FXML
    TextArea fkCommentTxtArea;

    ObservableList<ForeignKey> foreignKeyData = FXCollections.observableArrayList(new ForeignKey());

    ObservableList<ForeignKeyColumns> foreignKeyColumnsData = FXCollections.observableArrayList(new ForeignKeyColumns());

    ObservableList<String> cbReferencedTable = FXCollections.observableArrayList();

    ObservableList<String> cbReferencedColumns = FXCollections.observableArrayList();

    Schema foreignKeySchema = ProgramState.getInstance().getSelectedSchema();

    Table referencingTable = ProgramState.getInstance().getSelectedTable();

    Table referencedTable;

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
        fkNameTableColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        fkNameTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        fkNameTableColumn.setOnEditCommit(event -> {
            ForeignKey foreignKey = event.getRowValue();
            foreignKey.setName(event.getNewValue());
        });

        // Setting up properties for referenced table column of fk table
        fkReferencedTableColumn.setCellValueFactory(cellData -> cellData.getValue().referencedTableProperty());
        fkReferencedTableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(cbReferencedTable));
        fkReferencedTableColumn.setOnEditCommit(event -> {
            if(event.getNewValue() == null || event.getNewValue().isEmpty()) return;
            ForeignKey foreignKey = event.getRowValue();
            foreignKey.setReferencingTable(referencingTable);

            // Adding referenced schema to foreign key
            Schema referencedSchema = DatabaseInspector.getInstance().getDatabaseByName(event.getNewValue().split("\\.")[0]);
            foreignKey.setReferencedSchema(referencedSchema);
            foreignKeySchema = referencedSchema;

            // Adding referenced table to foreign key
            referencedTable = DatabaseInspector.getInstance().getTableByName(referencedSchema,event.getNewValue().split("\\.")[1]);
            foreignKey.setReferencedTable(referencedTable);

            // Setting table property (String)
            foreignKey.setReferencedTableProperty(event.getNewValue());

            // Filling up referenced columns in foreign key columns table after choosing a referenced table
            List<String> referencedColumnNames = referencedTable.getColumns().stream().map(Column::getName).toList();
            cbReferencedColumns.setAll(referencedColumnNames);
        });

        // Setting up properties of foreign key column checkbox in foreign key columns table
        fkcheckedTableColumn.setCellValueFactory(cellData -> cellData.getValue().checkedColumnProperty());
        fkcheckedTableColumn.setCellFactory(col ->{
            return new CheckBoxTableCell<ForeignKeyColumns, Boolean>() {
                private final CheckBox checkBox = new CheckBox();
                {
                    checkBox.setOnAction(event -> {
                        int row = getIndex();
                        int tableSize = foreignKeyColumnsTable.getItems().size();
                        if (row >= 0 && row < tableSize) {
                            ForeignKeyColumns foreignKeyColumnPair = foreignKeyColumnsTable.getItems().get(row);
                            boolean newValue = checkBox.isSelected();
                            foreignKeyColumnPair.setCheckedColumnProperty(newValue);
                            //If the checkBox is at true add new indexed column to index
                            if(newValue){
                                selectedForeignKey.getColumnPairs().add(foreignKeyColumnPair);
//                                selectedForeignKey.getReferencingColumns().add(foreignKeyColumnPair.getFirst());
//                                selectedForeignKey.getReferencedColumns().add(foreignKeyColumnPair.getSecond());
                            }else {
                                selectedForeignKey.getColumnPairs().remove(foreignKeyColumnPair);
//                                selectedForeignKey.getReferencingColumns().remove(foreignKeyColumnPair.getFirst());
//                                selectedForeignKey.getReferencedColumns().remove(foreignKeyColumnPair.getSecond());
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

        // Setting up properties of foreign key column name of foreign key columns table
        fkColumnNameTableColumn.setEditable(false);
        fkColumnNameTableColumn.setCellValueFactory(cellData -> cellData.getValue().columnNameProperty());
        fkColumnNameTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        fkColumnNameTableColumn.setOnEditCommit(event -> {
            Column column = DatabaseInspector.getInstance().getColumnByName(referencingTable,event.getNewValue());
            Pair<Column,Column> selectedPair = foreignKeyColumnsData.get(event.getTablePosition().getRow());
            selectedPair.setFirst(column);
        });

        fkReferencedColumnTableColumn.setCellValueFactory(cellData -> cellData.getValue().referencedColumnProperty());
        fkReferencedColumnTableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(cbReferencedColumns));
        fkReferencedColumnTableColumn.setOnEditCommit(event -> {
            Column column = DatabaseInspector.getInstance().getColumnByName(referencedTable,event.getNewValue());
            ForeignKeyColumns selectedPair = foreignKeyColumnsData.get(event.getTablePosition().getRow());
            selectedPair.setSecond(column);
            selectedPair.setReferencedColumnProperty(column.getName());
        });

        //Selection listeners
        foreignKeyTable.getSelectionModel().selectedItemProperty().addListener((obs,oldSelection,newSelection) -> {
            if(newSelection == null || oldSelection == newSelection) return;
            selectedForeignKey = newSelection;

            int index = foreignKeyTable.getItems().indexOf(newSelection);
            newSelection.nameProperty().addListener((_,oldVal,newVal) -> {
                int lastIndex = foreignKeyTable.getItems().size() - 1;
                if(index != lastIndex) return;
                foreignKeyTable.getItems().add(new ForeignKey());
            });

            newSelection.referencedTableProperty().addListener((_,oldVal,newVal) -> {
                int lastIndex = foreignKeyTable.getItems().size() - 1;
                if(index != lastIndex) return;
                foreignKeyTable.getItems().add(new ForeignKey());
            });
        });

        // Filling onUpdate and onDelete combo boxes with Action enum
        ObservableList<Action> actions = FXCollections.observableArrayList(Action.values());
        cbOnUpdate.setItems(actions);
        cbOnDelete.setItems(actions);
        cbOnUpdate.setOnAction(event -> {
            Action action = cbOnUpdate.getSelectionModel().getSelectedItem();
            if(selectedForeignKey != null){
                selectedForeignKey.setOnUpdateAction(action);
            }
        });
        cbOnDelete.setOnAction(event -> {
            Action action = cbOnDelete.getSelectionModel().getSelectedItem();
            if(selectedForeignKey != null){
                selectedForeignKey.setOnDeleteAction(action);
            }
        });

        // Setting up change listener on fk comment text field
        fkCommentTxtArea.focusedProperty().addListener((obs,oldVal,newVal) -> {
            // If the focus is lost check if the input is different from selected fk attribute
            if(newVal || selectedForeignKey == null) return;
            if(selectedForeignKey.getComment() == null && fkCommentTxtArea.getText() == null) return;
            if(selectedForeignKey.getComment() == null && fkCommentTxtArea.getText() != null) {
                selectedForeignKey.setComment(fkCommentTxtArea.getText());
            }
            if(!selectedForeignKey.getComment().equals(fkCommentTxtArea.getText())){
                selectedForeignKey.setComment(fkCommentTxtArea.getText());
            }
        });
    }

    public void setColumnsWidth(){
        ReadOnlyDoubleProperty foreignKeyTableWidthProperty = foreignKeyTable.widthProperty();
        fkNameTableColumn.prefWidthProperty().bind(foreignKeyTableWidthProperty.multiply(0.4));
        fkReferencedTableColumn.prefWidthProperty().bind(foreignKeyTableWidthProperty.multiply(0.6));

        ReadOnlyDoubleProperty foreignKeyColumnsTableWidthProperty = foreignKeyColumnsTable.widthProperty();
        fkcheckedTableColumn.prefWidthProperty().bind(foreignKeyColumnsTableWidthProperty.multiply(0.075));
        fkColumnNameTableColumn.prefWidthProperty().bind(foreignKeyColumnsTableWidthProperty.multiply(0.3));
        fkReferencedColumnTableColumn.prefWidthProperty().bind(foreignKeyColumnsTableWidthProperty.multiply(0.625));
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

    public List<ForeignKey> getTableForeignKeys(){
        //Removing empty row(last table row)
        List<ForeignKey> foreignKeys = new ArrayList<>(foreignKeyData);
        foreignKeys.removeLast();
        return foreignKeys;
    }
}
