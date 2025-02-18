package com.abelovagrupa.dbeeadmin.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class PanelLeft implements Initializable {

    @FXML
    List<TreeView<String>> schemaViews;

    @FXML
    TreeView<String> treeView1;
    @FXML
    TreeView<String> treeView2;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        schemaViews = List.of(treeView1,treeView2);

        for (TreeView<String> schemaView : schemaViews){

            TreeItem<String> schema = new TreeItem<>("Schema");
            schemaView.setRoot(schema);
            TreeItem<String> tableBranch = new TreeItem<>("Tables");
            TreeItem<String> viewBranch = new TreeItem<>("Views");
            TreeItem<String>  procedureBranch = new TreeItem<>("Stored Procedures");
            TreeItem<String> functionBranch = new TreeItem<>("Functions");

            

            schema.getChildren().addAll(tableBranch,viewBranch,procedureBranch,functionBranch);


        }

//
    }

    @FXML
    public void selectTreeItem(){

        TreeItem<String> selectedTreeItem = schemaViews.get(0).getSelectionModel().getSelectedItem();
        System.out.println(selectedTreeItem.getValue());
    }

}
