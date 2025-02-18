package com.abelovagrupa.dbeeadmin.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
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

        //For now the treeViews are statically added, will change

        schemaViews = List.of(treeView1,treeView2);

        for (TreeView<String> schemaView : schemaViews){

            TreeItem<String> schema = new TreeItem<>("Schema",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database.png").toExternalForm())));
            schemaView.setRoot(schema);
            TreeItem<String> tableBranch = new TreeItem<>("Tables",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));
            TreeItem<String> viewBranch = new TreeItem<>("Views",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));
            TreeItem<String>  procedureBranch = new TreeItem<>("Stored Procedures",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));
            TreeItem<String> functionBranch = new TreeItem<>("Functions",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));

            TreeItem<String> table1 = new TreeItem<>("Table 1");
            TreeItem<String> table2 = new TreeItem<>("Table 2");

            tableBranch.getChildren().addAll(table1,table2);

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
