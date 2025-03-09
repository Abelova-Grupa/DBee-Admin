package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.Main;
import com.abelovagrupa.dbeeadmin.model.table.DBEngine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PanelTableCreation implements Initializable {

    @FXML
    ScrollPane scrollColumns;

    @FXML
    VBox scrollContent;

    @FXML
    ComboBox<DBEngine> cbEngines;

    public void addColumn() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("panelColumnTab.fxml"));
        HBox column = loader.load();
        // TODO: Add controller
        scrollContent.getChildren().add(column);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<DBEngine> engines = FXCollections.observableArrayList(DBEngine.values());
        cbEngines.setItems(engines);
    }
}
