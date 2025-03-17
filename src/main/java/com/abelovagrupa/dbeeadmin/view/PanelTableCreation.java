package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.Main;
import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.model.table.DBEngine;
import com.abelovagrupa.dbeeadmin.model.table.Table;
import com.abelovagrupa.dbeeadmin.services.DDLGenerator;
import com.abelovagrupa.dbeeadmin.util.AlertManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class PanelTableCreation implements Initializable {

    @FXML
    ScrollPane scrollColumns;

    @FXML
    VBox scrollContent;

    @FXML
    ComboBox<DBEngine> cbEngines;

    @FXML
    TextField txtTableName;

    List<PanelColumnTab> columnControllers;

    public void addColumn() throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("panelColumnTab.fxml"));
        HBox column = loader.load();
        columnControllers.add(loader.getController());
        scrollContent.getChildren().add(column);

    }

    public void createTable() {

        if(txtTableName.getText().isEmpty()) AlertManager.showErrorDialog(null, null, "Table name must not be empty.");

        // TODO: Import schema from programstate instead of this.
        Schema tempSchema = new Schema.SchemaBuilder(txtTableName.getText().split("\\.")[0], null, null).build();
        Table tempTable = new Table.TableBuilder(null, txtTableName.getText().split("\\.")[1], tempSchema, null).build();
        List<Column> columns = new LinkedList<>();
        for (PanelColumnTab c : columnControllers) {
            columns.add(c.getColumn(tempTable));
        }
        tempTable.setColumns(columns);
        try {
            DDLGenerator.createTable(tempTable, true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize column controllers
        columnControllers = new ArrayList<>();

        ObservableList<DBEngine> engines = FXCollections.observableArrayList(DBEngine.values());
        cbEngines.setItems(engines);


    }
}
