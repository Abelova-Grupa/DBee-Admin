package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.controller.DatabaseInspector;
import com.abelovagrupa.dbeeadmin.model.column.Column;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class PanelFKTab implements Initializable {

    @FXML
    TableView<ForeignKey> foreignKeyTable;

    @FXML
    TableColumn<ForeignKey,String> fkNameColumn;

    @FXML
    TableColumn<ForeignKey,String> referencedTableFK;

    @FXML
    TableView<ForeignKeyColumns> foreignKeyColumnsTable;

    @FXML
    TableColumn<ForeignKeyColumns,Boolean> foreignKeyColumnChecked;

    @FXML
    TableColumn<ForeignKeyColumns,String> foreignKeyColumnName;

    @FXML
    TableColumn<ForeignKeyColumns,String> referencedColumnFK;

    ObservableList<ForeignKey> foreignKeyData = FXCollections.observableArrayList(new ForeignKey(),new ForeignKey());

    ObservableList<ForeignKeyColumns> foreignKeyColumnsData = FXCollections.observableArrayList(new LinkedList<ForeignKeyColumns>());

    Schema selectedSchema = ProgramState.getInstance().getSelectedSchema();

    Table selectedTable = ProgramState.getInstance().getSelectedTable();

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
        fkNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        fkNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        fkNameColumn.setOnEditCommit(event -> {
            ForeignKey foreignKey = event.getRowValue();
            foreignKey.setName(event.getNewValue());
        });

        List<String> schemaTablesNames;
        ObservableList<String> cbReferencedTable = FXCollections.observableArrayList();
        if(selectedSchema != null){
            schemaTablesNames = DatabaseInspector.getInstance().getTableNames(selectedSchema);
            for(String schemaTableName : schemaTablesNames){
                cbReferencedTable.add(selectedSchema.getName()+"."+schemaTableName);
            }
        }
        referencedTableFK.setCellValueFactory(cellData -> cellData.getValue().referencedTableProperty());
        referencedTableFK.setCellFactory(ComboBoxTableCell.forTableColumn(cbReferencedTable));
        referencedTableFK.setOnEditCommit(event -> {
            if(event.getNewValue() == null || event.getNewValue().isEmpty()) return;
            ForeignKey foreignKey = event.getRowValue();
            foreignKey.referencedTableProperty().set(event.getNewValue());
            Schema schema = DatabaseInspector.getInstance().getDatabaseByName(event.getNewValue().split("\\.")[0]);
            foreignKey.setReferencedSchema(schema);
            selectedSchema = schema;
            Table table = DatabaseInspector.getInstance().getTableByName(schema,event.getNewValue().split("\\.")[1]);
            foreignKey.setReferencedTable(table);
            selectedTable = table;
        });

        foreignKeyColumnChecked.setCellValueFactory(cellData -> cellData.getValue().checkedColumnProperty());
        foreignKeyColumnChecked.setCellFactory(col ->{
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

        foreignKeyColumnName.setCellValueFactory(cellData -> cellData.getValue().columnNameProperty());
        foreignKeyColumnName.setCellFactory(TextFieldTableCell.forTableColumn());
        foreignKeyColumnName.setOnEditCommit(event -> {
            Column column = DatabaseInspector.getInstance().getColumnByName(selectedTable,event.getNewValue());
            Pair<Column,Column> selectedPair = foreignKeyColumnsData.get(event.getTablePosition().getRow());
            selectedPair.setFirst(column);
        });

        ObservableList<String> cbReferencedColumns = FXCollections.observableArrayList();

        referencedColumnFK.setCellValueFactory(cellData -> cellData.getValue().referencedColumnProperty());
        referencedColumnFK.setCellFactory(ComboBoxTableCell.forTableColumn(cbReferencedColumns));
        referencedColumnFK.setOnEditCommit(event -> {
            Column column = DatabaseInspector.getInstance().getColumnByName(selectedTable,event.getNewValue());
            Pair<Column,Column> selectedPair = foreignKeyColumnsData.get(event.getTablePosition().getRow());
            selectedPair.setSecond(column);
        });

        //Selection listeners
        foreignKeyTable.getSelectionModel().selectedItemProperty().addListener((obs,oldSelection,newSelection) -> {
            if(newSelection == null || oldSelection == newSelection) return;
            selectedForeignKey = newSelection;

            if(selectedForeignKey.getColumnPairs() == null){
                selectedForeignKey.setColumnPairs(new LinkedList<ForeignKeyColumns>());

                for(int i=0; i < selectedTable.getColumns().size();i++){
                    Column column = selectedTable.getColumns().get(i);
                    ForeignKeyColumns newPair = new ForeignKeyColumns();
                    newPair.setFirst(column);
                    foreignKeyColumnsData.add(newPair);
                    foreignKeyColumnsTable.getItems().get(i).setColumnNameProperty(column.getName());
                }
            }
            if(selectedForeignKey.referencedTableProperty().get().isEmpty()){
                cbReferencedColumns.setAll();
            }

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


    }

    public void setColumnsWidth(){
        ReadOnlyDoubleProperty foreignKeyTableWidthProperty = foreignKeyTable.widthProperty();
        fkNameColumn.prefWidthProperty().bind(foreignKeyTableWidthProperty.multiply(0.4));
        referencedTableFK.prefWidthProperty().bind(foreignKeyTableWidthProperty.multiply(0.6));

        ReadOnlyDoubleProperty foreignKeyColumnsTableWidthProperty = foreignKeyColumnsTable.widthProperty();
        foreignKeyColumnChecked.prefWidthProperty().bind(foreignKeyColumnsTableWidthProperty.multiply(0.075));
        foreignKeyColumnName.prefWidthProperty().bind(foreignKeyColumnsTableWidthProperty.multiply(0.3));
        referencedColumnFK.prefWidthProperty().bind(foreignKeyColumnsTableWidthProperty.multiply(0.625));
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
