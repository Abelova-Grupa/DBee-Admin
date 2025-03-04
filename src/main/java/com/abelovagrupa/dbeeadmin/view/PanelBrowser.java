package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.Main;
import com.abelovagrupa.dbeeadmin.controller.DatabaseInspector;
import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.model.table.Table;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
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
                    TreeItem<String> selectedTable = schemaView.getSelectionModel().getSelectedItem();
                    if(selectedTable != null){
                        if(getTreeItemDepth(selectedTable) == 3 && (isChildOf(selectedTable,tableBranch))){
                            infoController.getTableName().setText(selectedTable.getValue());
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
