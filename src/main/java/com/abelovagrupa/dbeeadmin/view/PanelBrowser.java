package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.Main;
import com.abelovagrupa.dbeeadmin.controller.DatabaseInspector;
import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.model.table.Table;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

    private List<Schema> schemas;

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
        try {
            schemaControllers = new LinkedList<>();
            schemas = DatabaseInspector.getInstance().getDatabases();
            for(Schema schema : schemas){
                // Loading each schema treeView
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("panelSchemaTree.fxml"));
                TreeView<String> schemaView = loader.load();
                schemaControllers.add(loader.getController());

                TreeItem<String> schemaNode = new TreeItem<>(schema.getName(),new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database.png").toExternalForm())));
                schemaView.setRoot(schemaNode);
                TreeItem<String> tableBranch = new TreeItem<>("Tables",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));
                TreeItem<String> viewBranch = new TreeItem<>("Views",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));
                TreeItem<String>  procedureBranch = new TreeItem<>("Stored Procedures",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));
                TreeItem<String> functionBranch = new TreeItem<>("Functions",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));

                for(Table table : schema.getTables()){
                    TreeItem<String> tableNode = new TreeItem<>(table.getName(),new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-table.png").toExternalForm())));

                    TreeItem<String> columnBranch = new TreeItem<>("Columns",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/columns.png").toExternalForm())));
                    TreeItem<String> indexBranch = new TreeItem<>("Indexes",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/indexes.png").toExternalForm())));
                    TreeItem<String> foreignKeyBranch = new TreeItem<>("Foreign Keys",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/foreignkeys.png").toExternalForm())));
                    TreeItem<String> triggersBranch = new TreeItem<>("Triggers",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/triggers.png").toExternalForm())));


                    for(Column column : table.getColumns()){
                        TreeItem<String> columnNode = new TreeItem<>(column.getName());
                        columnBranch.getChildren().add(columnNode);
                    }

                    tableNode.getChildren().addAll(columnBranch,indexBranch,foreignKeyBranch,triggersBranch);
                    tableBranch.getChildren().add(tableNode);
                }



                schemaNode.getChildren().addAll(tableBranch,viewBranch,procedureBranch,functionBranch);
                schemaView.setPrefHeight(24);
                schemaView.getRoot().addEventHandler(TreeItem.branchExpandedEvent(), event -> {
                    Platform.runLater(() -> schemaView.setPrefHeight(schemaView.getExpandedItemCount() * 24));
                });

                schemaView.getRoot().addEventHandler(TreeItem.branchCollapsedEvent(), event -> {
                    Platform.runLater(() -> schemaView.setPrefHeight(schemaView.getExpandedItemCount() * 24));
                });

                schemaView.setOnMouseClicked(event -> {
                    TreeItem<String> selectedItem = schemaView.getSelectionModel().getSelectedItem();
                    Optional<Table> selectedTable = schema.getTables().stream().filter(s -> s.getName().equals(selectedItem.getValue())).findFirst();
                    if(selectedTable.isPresent()){
                        if(getTreeItemDepth(selectedItem) == 3 && (isChildOf(selectedItem,tableBranch))){
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
