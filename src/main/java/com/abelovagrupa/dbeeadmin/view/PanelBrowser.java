package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.Main;
import com.abelovagrupa.dbeeadmin.controller.DatabaseInspector;
import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.foreignkey.ForeignKey;
import com.abelovagrupa.dbeeadmin.model.index.Index;
import com.abelovagrupa.dbeeadmin.model.index.IndexedColumn;
import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.model.table.Table;
import javafx.application.Platform;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PanelBrowser implements Initializable {

    private PanelMain mainController;

    private List<PanelSchemaTree> schemaControllers;

    private PanelInfo infoController;


    @FXML
    VBox vboxBrowser;

    public List<PanelSchemaTree> getSchemaControllers() {
        return schemaControllers;
    }

    public void setSchemaControllers(List<PanelSchemaTree> schemaControllers) {
        this.schemaControllers = schemaControllers;
    }

    public PanelMain getMainController() {
        return mainController;
    }

    public void setMainController(PanelMain mainController) {
        this.mainController = mainController;
    }

    public PanelInfo getInfoController() {
        return infoController;
    }

    public void setInfoController(PanelInfo infoController) {
        this.infoController = infoController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TODO: WRITE COMMENTS FROM THIS METHOD
        try {
            schemaControllers = new LinkedList<>();
            List<String> schemaNames = DatabaseInspector.getInstance().getDatabaseNames();
            for(String schemaName : schemaNames){
                // Loading each schema treeView
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("panelSchemaTree.fxml"));
                TreeView<String> schemaView = loader.load();
                schemaView.setFixedCellSize(24);
                schemaControllers.add(loader.getController());

                TreeItem<String> schemaNode = new TreeItem<>(schemaName,new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database.png").toExternalForm())));
                schemaView.setRoot(schemaNode);
                TreeItem<String> tableBranch = new TreeItem<>("Tables",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));
                TreeItem<String> viewBranch = new TreeItem<>("Views",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));
                TreeItem<String>  procedureBranch = new TreeItem<>("Stored Procedures",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));
                TreeItem<String> functionBranch = new TreeItem<>("Functions",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));


                // Creating an initial tableDummy so that the tableBranch can be expandable
                TreeItem<String> tableDummyNode = new TreeItem<>("Dummy table");
                tableBranch.getChildren().add(tableDummyNode);

                schemaNode.getChildren().addAll(tableBranch,viewBranch,procedureBranch,functionBranch);
                schemaView.setPrefHeight(24);
                schemaView.getRoot().addEventHandler(TreeItem.branchExpandedEvent(), event -> {
                    Platform.runLater(() -> schemaView.setPrefHeight(schemaView.getExpandedItemCount() * 24));
                });

                schemaView.getRoot().addEventHandler(TreeItem.branchCollapsedEvent(), event -> {
                    Platform.runLater(() -> schemaView.setPrefHeight(schemaView.getExpandedItemCount() * 24));
                });

                ChangeListener<Boolean> tableBranchListener = new ChangeListener<>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        if(newValue){
                            Schema schema = DatabaseInspector.getInstance().getDatabaseByName(schemaName);
                            List<String> tableNames = DatabaseInspector.getInstance().getTableNames(schema);
                            tableBranch.getChildren().remove(tableDummyNode);
                            for(String tableName: tableNames){

                                TreeItem<String> tableNode = new TreeItem<>(tableName,new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-table.png").toExternalForm())));
                                TreeItem<String> columnBranch = new TreeItem<>("Columns",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/columns.png").toExternalForm())));
                                TreeItem<String> columnDummy = new TreeItem<>("Column Dummy");
                                columnBranch.getChildren().add(columnDummy);

                                ChangeListener<Boolean> columnBranchListener = new ChangeListener<Boolean>() {
                                    @Override
                                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                        if(newValue){
                                            columnBranch.getChildren().remove(columnDummy);
                                            Table table = DatabaseInspector.getInstance().getTableByName(schema,tableName);
                                            for(Column column : table.getColumns()){
                                                TreeItem<String> columnNode = new TreeItem<>(column.getName());
                                                columnBranch.getChildren().add(columnNode);
                                            }
                                            columnBranch.expandedProperty().removeListener(this);
                                        }
                                    }
                                };
                                columnBranch.expandedProperty().addListener(columnBranchListener);

                                TreeItem<String> indexBranch = new TreeItem<>("Indexes",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/indexes.png").toExternalForm())));
                                TreeItem<String> indexDummy = new TreeItem<>("Index dummy");
                                indexBranch.getChildren().add(indexDummy);

                                ChangeListener<Boolean> indexBranchListener = new ChangeListener<Boolean>() {
                                    @Override
                                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                        if(newValue){
                                            indexBranch.getChildren().remove(indexDummy);
                                            Table table = DatabaseInspector.getInstance().getTableByName(schema,tableName);
                                            List<Index> indexes = DatabaseInspector.getInstance().getIndexes(schema,table);
                                            for(Index index : indexes){
                                                TreeItem<String> indexNode = new TreeItem<>(index.getName());
                                                indexBranch.getChildren().add(indexNode);
                                            }
                                             indexBranch.expandedProperty().removeListener(this);
                                        }
                                    }
                                };
                                indexBranch.expandedProperty().addListener(indexBranchListener);

                                TreeItem<String> foreignKeyBranch = new TreeItem<>("Foreign Keys",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/foreignkeys.png").toExternalForm())));
                                TreeItem<String> foreignKeyDummy = new TreeItem<>("ForeignKeyDummy");
                                foreignKeyBranch.getChildren().add(foreignKeyDummy);

                                ChangeListener<Boolean> foreignKeyListener = new ChangeListener<Boolean>() {
                                    @Override
                                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                        if(newValue){
                                            foreignKeyBranch.getChildren().remove(foreignKeyDummy);
                                            Table table = DatabaseInspector.getInstance().getTableByName(schema,tableName);
                                            List<ForeignKey> foreignKeys = DatabaseInspector.getInstance().getForeignKeys(schema,table);
                                            for(ForeignKey foreignKey: foreignKeys){
                                                TreeItem<String> foreignKeyNode = new TreeItem<>(foreignKey.getName());
                                                foreignKeyBranch.getChildren().add(foreignKeyNode);
                                            }
                                            foreignKeyBranch.expandedProperty().removeListener(this);
                                        }
                                    }
                                };
                                foreignKeyBranch.expandedProperty().addListener(foreignKeyListener);

                                TreeItem<String> triggersBranch = new TreeItem<>("Triggers",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/triggers.png").toExternalForm())));

                                tableNode.getChildren().addAll(columnBranch,indexBranch,foreignKeyBranch,triggersBranch);
                                tableBranch.getChildren().add(tableNode);
                            }
                            tableBranch.expandedProperty().removeListener(this);
                        }
                    }
                };

                tableBranch.expandedProperty().addListener(tableBranchListener);
                // TODO: REFACTOR
                schemaView.setOnMouseClicked(event -> {
                    TreeItem<String> selectedItem = schemaView.getSelectionModel().getSelectedItem();
                    Schema schema = DatabaseInspector.getInstance().getDatabaseByName(schemaName);
                        // Table selected
                        Optional<Table> selectedTable = schema.getTables().stream().filter(s -> s.getName().equals(selectedItem.getValue())).findFirst();
                        if(getTreeItemDepth(selectedItem) == 3 && (isChildOf(selectedItem,tableBranch))){
                            infoController.getColumnInfoPanel().setVisible(false);
                            infoController.getIndexInfoPanel().setVisible(false);
                            infoController.getTableInfoPanel().setVisible(true);
                            infoController.getTableName().setText(selectedItem.getValue());
                            infoController.getAttributeContainer().getChildren().clear();
                            infoController.setAttributes(new LinkedList<>());

                            for(Column column : selectedTable.get().getColumns()){
                                Label attributeName = new Label(column.getName());
                                Label attributeType = new Label(column.getType().toString());
                                BorderPane attributePane = new BorderPane();
                                attributePane.setLeft(attributeName);
                                attributePane.setRight(attributeType);
                                infoController.addAttributePane(attributePane);
                            }
                        }

                        if(selectedItem.getParent().getValue().equals("Columns") && getTreeItemDepth(selectedItem) == 5){
                            // TODO: make it efficient, for now it works
                            Table table = DatabaseInspector.getInstance().getTableByName(schema,selectedItem.getParent().getParent().getValue());
                            Optional<Column> selectedColumn = table.getColumns().stream().filter(s -> s.getName().equals(selectedItem.getValue())).findFirst();
                            if(selectedColumn.isPresent()){
                                infoController.getTableInfoPanel().setVisible(false);
                                infoController.getIndexInfoPanel().setVisible(false);
                                infoController.getColumnName().setText(selectedColumn.get().getName());
                                infoController.getColumnType().setText(selectedColumn.get().getType().toString());
                                infoController.getColumnInfoPanel().setVisible(true);
                            }
                        }

                        if(selectedItem.getParent().getValue().equals("Indexes") && getTreeItemDepth(selectedItem) == 5){
                            // TODO: make it efficient, for now it works
                            String indexName = selectedItem.getValue();
                            Table table = DatabaseInspector.getInstance().getTableByName(schema,selectedItem.getParent().getParent().getValue());
                            List<Index> indexes = DatabaseInspector.getInstance().getIndexes(schema,table);
                            Optional<Index> selectedIndex = indexes.stream().filter(s -> s.getName().equals(indexName)).findFirst();
                            if(selectedIndex.isPresent()){
                                List<IndexedColumn> indexedColumns = selectedIndex.get().getIndexedColumns();
                                infoController.getTableInfoPanel().setVisible(false);
                                infoController.getColumnInfoPanel().setVisible(false);
                                infoController.getIndexInfoPanel().setVisible(true);
                                infoController.getIndexColumnContainer().getChildren().clear();
                                infoController.getIndexName().setText(selectedIndex.get().getName());
                                infoController.getVisibleName().setText(selectedIndex.get().isVisible()+"");
                                infoController.getUniqueName().setText(selectedIndex.get().isUnique()+"");
                                infoController.getTypeName().setText(selectedIndex.get().getStorageType()+"");

                                for(IndexedColumn indexColumn : indexedColumns){
                                    BorderPane columnField = new BorderPane();
                                    columnField.setCenter(new Label(indexColumn.getColumn().getName()));
                                    infoController.getIndexColumnContainer().getChildren().add(columnField);
                                }

                            }

                        }
                });
                vboxBrowser.getChildren().add(schemaView);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isChildOf(TreeItem<?> item, TreeItem<?> potentialParent){

        if(item == null || potentialParent == null)
            return false;

        TreeItem<?> parent = item.getParent();

        if(parent == potentialParent){
            return true;
        }

        while(parent != null){
            if(parent == potentialParent)
                return true;
            parent = parent.getParent();
        }
        return false;
    }

    public static int getTreeItemDepth(TreeItem<?> item) {
        int depth = 0;
        TreeItem<?> current = item;

        while (current != null) {
            depth++;
            current = current.getParent();
        }

        return depth;
    }

}
