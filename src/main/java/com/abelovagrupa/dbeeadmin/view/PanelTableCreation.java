package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.Main;
import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.table.DBEngine;
import com.abelovagrupa.dbeeadmin.model.table.Table;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PanelTableCreation implements Initializable {

    @FXML
    ScrollPane scrollColumns;

    @FXML
    VBox scrollContent;

    @FXML
    ComboBox<DBEngine> cbEngines;

    List<PanelColumnTab> columnControllers;

    public void addColumn() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("panelColumnTab.fxml"));
        HBox column = loader.load();
        columnControllers.add(loader.getController());
        scrollContent.getChildren().add(column);

    }

    public void createTable() {
        // TODO: lele
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize column controllers
        columnControllers = new ArrayList<>();

        ObservableList<DBEngine> engines = FXCollections.observableArrayList(DBEngine.values());
        cbEngines.setItems(engines);
    }
}
