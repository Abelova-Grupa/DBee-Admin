package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.model.index.*;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.util.converter.IntegerStringConverter;

import java.net.URL;
import java.util.*;

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

    // Index options

    @FXML
    ComboBox<IndexStorageType> cbStorageType;

    @FXML
    TextField keyBlockSizeTxtField;

    @FXML
    TextField parserTxtField;

    @FXML
    CheckBox checkBoxVisible;

    @FXML
    TextArea optionTxtArea;

    List<Index> commitedIndexData = new LinkedList<>();

    ObservableList<Index> indexData = FXCollections.observableArrayList(commitedIndexData);

    ObservableList<IndexedColumn> indexedColumnData = FXCollections.observableArrayList();

    Index selectedIndex;

    Index primaryIndex;

    Optional<IndexedColumn> selectedIndexedColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setting up index table properties
        indexData.add(new Index());
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
        setColumnsSortable(false);

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
            if(newSelection == null || newSelection == oldSelection) return;
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
            for(IndexedColumn indexColumn : indexedColumnData){
                Optional<IndexedColumn> selectedIndexColumn = selectedIndex.getIndexedColumns().stream().filter(i -> i.getColumn().getName().equals(indexColumn.getColumn().getName())).findFirst();
                if(selectedIndexColumn.isPresent()){
                    indexColumn.setCheckedColumnProperty(selectedIndexColumn.get().checkedColumnProperty().get());
                    indexColumn.setOrderNumber(selectedIndexColumn.get().getOrderNumber());
                    indexColumn.setOrderNumberProperty(selectedIndexColumn.get().getOrderNumber());
                    indexColumn.setOrder(selectedIndexColumn.get().getOrder());
                    indexColumn.setLength(selectedIndexColumn.get().getLength());
                }else {
                    indexColumn.setCheckedColumnProperty(false);
                    indexColumn.setOrderNumberProperty(0);
                    indexColumn.setOrderProperty(null);
                    indexColumn.setLengthProperty(0);
                }
            }
            // Loading index options
            // Loading combo box with index storage types for index options
            ObservableList<IndexStorageType> indexStorageTypes = FXCollections.observableArrayList(IndexStorageType.values());
            // This will keep the first null combo box option
            cbStorageType.getItems().addAll(indexStorageTypes);
            cbStorageType.setOnAction(event -> {
                IndexStorageType indexStorageType = cbStorageType.getSelectionModel().getSelectedItem();
                selectedIndex.setStorageType(indexStorageType);
            });
            // Setting up change listeners on index option components
            keyBlockSizeTxtField.focusedProperty().addListener((obs,oldVal,newVal) -> {
                if(newVal) return;
                if(selectedIndex.getKeyBlockSize() != Integer.parseInt(keyBlockSizeTxtField.getText())){
                    selectedIndex.setKeyBlockSize(Integer.parseInt(keyBlockSizeTxtField.getText()));
                }
            });
            parserTxtField.focusedProperty().addListener((obs,oldVal,newVal) -> {
                // If the focus is lost check if the input is different from selected index attribute
                if(newVal) return;
                if(selectedIndex.getParser() == null && parserTxtField.getText() == null) return;
                if(selectedIndex.getParser() == null && parserTxtField.getText() != null){
                    selectedIndex.setParser(parserTxtField.getText());
                }
                if(!selectedIndex.getParser().equals(parserTxtField.getText())){
                    selectedIndex.setParser(parserTxtField.getText());
                }

            });
            checkBoxVisible.setOnAction(event -> {
                selectedIndex.setVisible(checkBoxVisible.isSelected());
            });
            optionTxtArea.focusedProperty().addListener((obs,oldVal,newVal) -> {
                if(newVal) return;
                if(selectedIndex.getComment() == null && optionTxtArea.getText() == null) return;
                if(selectedIndex.getComment() == null && optionTxtArea.getText() != null){
                    selectedIndex.setComment(optionTxtArea.getText());
                }
                if(!selectedIndex.getComment().equals(optionTxtArea.getText())){
                    selectedIndex.setComment(optionTxtArea.getText());
                }
            });

            // Loading data from selected index into index options
            cbStorageType.getSelectionModel().select(selectedIndex.getStorageType());
            keyBlockSizeTxtField.setText(String.valueOf(selectedIndex.getKeyBlockSize()));
            parserTxtField.setText(selectedIndex.getParser());
            checkBoxVisible.setSelected(selectedIndex.isVisible());
            optionTxtArea.setText(selectedIndex.getComment());
        });


        indexColumnTable.getSelectionModel().selectedItemProperty().addListener((_,oldSelection,newSelection) -> {
            if(newSelection == null || newSelection == oldSelection) return;

            selectedIndexedColumn = selectedIndex.getIndexedColumns().stream().filter(s -> s.getColumn().getName().equals(newSelection.getColumn().getName())).findFirst();
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
                            IndexedColumn indexedColumnCopy = IndexedColumn.deepCopy(indexedColumn);
                            if(newValue){
                                selectedIndex.getIndexedColumns().add(indexedColumnCopy);
                                indexedColumnCopy.setCheckedColumnProperty(true);
                                indexedColumnCopy.setIndex(selectedIndex);
                            }else {
                                indexedColumnCopy.setCheckedColumnProperty(false);
                                selectedIndex.getIndexedColumns().remove(indexedColumnCopy);
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
            indexedColumn.setOrderNumberProperty(event.getNewValue());
            selectedIndexedColumn.ifPresent(column -> column.setOrderNumber(event.getNewValue()));
        });

        ObservableList<Order> orderTypes = FXCollections.observableArrayList(Order.values());
        indexedColumnOrderColumn.setCellValueFactory(cellData -> cellData.getValue().orderProperty());
        indexedColumnOrderColumn.setCellFactory(ComboBoxTableCell.forTableColumn(orderTypes));
        indexedColumnOrderColumn.setOnEditCommit(event -> {
            IndexedColumn indexedColumn = event.getRowValue();
            indexedColumn.setOrder(event.getNewValue());
            indexedColumn.setOrderProperty(event.getNewValue());
            selectedIndexedColumn.ifPresent(column -> column.setOrder(event.getNewValue()));
        });

        indexedColumnLength.setCellValueFactory(cellData -> cellData.getValue().lengthProperty().asObject());
        indexedColumnLength.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        indexedColumnLength.setOnEditCommit(event -> {
            IndexedColumn indexedColumn = event.getRowValue();
            indexedColumn.setLength(event.getNewValue());
            indexedColumn.setLengthProperty(event.getNewValue());
            selectedIndexedColumn.ifPresent(column -> column.setLength(event.getNewValue()));
        });

        // Initializing context menus for each table row
        indexTable.setRowFactory(tv -> {
            TableRow<Index> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem deleteItem = new MenuItem("Delete index");
            deleteItem.setOnAction(tblClick -> deleteSelectedIndex(row.getItem()));

            contextMenu.getItems().addAll(deleteItem);

            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.SECONDARY && !emptyProperties(row.getItem())) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                } else {
                    contextMenu.hide();
                }
            });

            return row;
        });

    }

    private void deleteSelectedIndex(Index item) {
        indexData.remove(item);
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

    private void setColumnsSortable(boolean sortable){
        for(TableColumn<?,?> indexColumns: indexTable.getColumns()){
            indexColumns.setSortable(sortable);
        }
        for(TableColumn<?,?> indexedColumnsColumns : indexColumnTable.getColumns()){
            indexedColumnsColumns.setSortable(sortable);
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

    public List<Index> getTableIndexes(){
        //Removing empty row(last table row)
        List<Index> indexes = new ArrayList<>(indexData);
        indexes.removeLast();
        indexes.removeIf(s -> s.getName().equals("PRIMARY") && s.getType().equals(IndexType.PRIMARY));
        indexes.removeIf(this::emptyProperties);
        return indexes;
    }

}
