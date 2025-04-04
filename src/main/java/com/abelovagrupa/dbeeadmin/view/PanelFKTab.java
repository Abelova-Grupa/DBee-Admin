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

    ObservableList<ForeignKey> foreignKeyData = FXCollections.observableArrayList(new ForeignKey(),new ForeignKey());

    ObservableList<ForeignKeyColumns> foreignKeyColumnsData = FXCollections.observableArrayList(new ForeignKeyColumns());

    Schema selectedSchema = ProgramState.getInstance().getSelectedSchema();

    Table selectedTable = ProgramState.getInstance().getSelectedTable();

    ForeignKey selectedForeignKey;

    ObservableList<String> cbReferencedColumns = FXCollections.observableArrayList();

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

        // Filling up referencedTable comboBox with values based on selected schema
        ObservableList<String> cbReferencedTable = FXCollections.observableArrayList();
        if(selectedSchema != null){
            List<String> schemaTablesNames = DatabaseInspector.getInstance().getTableNames(selectedSchema);
            for(String schemaTableName : schemaTablesNames){
                cbReferencedTable.add(selectedSchema.getName()+"."+schemaTableName);
            }
        }
        // Setting up properties for referenced table column of fk table
        fkReferencedTableColumn.setCellValueFactory(cellData -> cellData.getValue().referencedTableProperty());
        fkReferencedTableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(cbReferencedTable));
        fkReferencedTableColumn.setOnEditCommit(event -> {
            if(event.getNewValue() == null || event.getNewValue().isEmpty()) return;
            ForeignKey foreignKey = event.getRowValue();
            // Adding referenced schema to foreign key
            Schema schema = DatabaseInspector.getInstance().getDatabaseByName(event.getNewValue().split("\\.")[0]);
            foreignKey.setReferencedSchema(schema);
            selectedSchema = schema;

            // Adding referenced table to foreign key
            Table referencedTable = DatabaseInspector.getInstance().getTableByName(schema,event.getNewValue().split("\\.")[1]);
            foreignKey.setReferencedTable(referencedTable);
            selectedTable = referencedTable;
            // Setting table property (String)
            foreignKey.setReferencedTableProperty(event.getNewValue());

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

        // Setting up properties of foreign key column name of foreign key columns table
        fkColumnNameTableColumn.setCellValueFactory(cellData -> cellData.getValue().columnNameProperty());
        fkColumnNameTableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        fkColumnNameTableColumn.setOnEditCommit(event -> {
            Column column = DatabaseInspector.getInstance().getColumnByName(selectedTable,event.getNewValue());
            Pair<Column,Column> selectedPair = foreignKeyColumnsData.get(event.getTablePosition().getRow());
            selectedPair.setFirst(column);
        });


        fkReferencedColumnTableColumn.setCellValueFactory(cellData -> cellData.getValue().referencedColumnProperty());
        fkReferencedColumnTableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(cbReferencedColumns));
        fkReferencedColumnTableColumn.setOnEditCommit(event -> {
            Column column = DatabaseInspector.getInstance().getColumnByName(selectedTable,event.getNewValue());
            Pair<Column,Column> selectedPair = foreignKeyColumnsData.get(event.getTablePosition().getRow());
            selectedPair.setSecond(column);
        });

        //Selection listeners
        foreignKeyTable.getSelectionModel().selectedItemProperty().addListener((obs,oldSelection,newSelection) -> {
            if(newSelection == null || oldSelection == newSelection) return;
            selectedForeignKey = newSelection;

            if(selectedForeignKey.getColumnPairs() == null) {
                selectedForeignKey.setColumnPairs(new LinkedList<ForeignKeyColumns>());
            }

            if(!selectedForeignKey.referencedTableProperty().get().isEmpty()){
                Table referencedTable = ProgramState.getInstance().getSelectedTable();

                for(int i=0; i < selectedTable.getColumns().size();i++){
                    Column column = selectedTable.getColumns().get(i);
                    ForeignKeyColumns newPair = new ForeignKeyColumns();
                    newPair.setFirst(column);
                    foreignKeyColumnsData.add(newPair);
                    foreignKeyColumnsTable.getItems().get(i).setColumnNameProperty(column.getName());
                }

                List<String> referencedColumnNames = referencedTable.getColumns().stream().map(Column::getName).toList();
                cbReferencedColumns.setAll(referencedColumnNames);

            }else {
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
}
