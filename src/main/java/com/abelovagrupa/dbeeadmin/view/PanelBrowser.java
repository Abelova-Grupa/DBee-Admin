package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.Main;
import com.abelovagrupa.dbeeadmin.controller.DatabaseInspector;
import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.model.table.Table;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
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

    private List<Schema> schemas;

    private List<PanelSchemaTree> schemaControllers;

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
                vboxBrowser.getChildren().add(schemaView);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
