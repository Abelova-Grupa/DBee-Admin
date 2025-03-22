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
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PanelTableCreation implements Initializable {

    @FXML
    ComboBox<DBEngine> cbEngines;

    @FXML
    TextField txtTableName;

    @FXML
    TabPane tableAttributeTabPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Initialize column tab
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("panelColumnTab.fxml"));
            Tab columnsTab = loader.load();
            tableAttributeTabPane.getTabs().add(columnsTab);
            tableAttributeTabPane.getSelectionModel().select(columnsTab);

            // Initialize index tab
            loader = new FXMLLoader(Main.class.getResource("panelIndexTab.fxml"));
            Tab indexTab = loader.load();
            tableAttributeTabPane.getTabs().add(indexTab);

            // Initialize foreign key tab
            loader = new FXMLLoader(Main.class.getResource("panelFKTab.fxml"));
            Tab foreignKeyTab = loader.load();
            tableAttributeTabPane.getTabs().add(foreignKeyTab);

            // Initialize trigger tab
            loader = new FXMLLoader(Main.class.getResource("panelTriggerTab.fxml"));
            Tab triggerTab = loader.load();
            tableAttributeTabPane.getTabs().add(triggerTab);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Filling comboBox with engine values
        ObservableList<DBEngine> engines = FXCollections.observableArrayList(DBEngine.values());
        cbEngines.setItems(engines);

    }

//    public void createTable() {
//
//        if(txtTableName.getText().isEmpty())
//        {
//            AlertManager.showErrorDialog(null, null, "Table name must not be empty.");
//            return;
//        }
//
//        // TODO: Import schema from programstate instead of this.
//        Schema tempSchema = new Schema.SchemaBuilder(txtTableName.getText().split("\\.")[0], null, null).build();
//        Table tempTable = new Table.TableBuilder(null, txtTableName.getText().split("\\.")[1], tempSchema, null).build();
//        List<Column> columns = new LinkedList<>();
//        for (PanelColumnTab c : columnControllers) {
//            if(c.isDeleted()) continue;
//            columns.add(c.getColumn(tempTable));
//        }
//        tempTable.setColumns(columns);
//        try {
//            DDLGenerator.createTable(tempTable, true);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }


}
