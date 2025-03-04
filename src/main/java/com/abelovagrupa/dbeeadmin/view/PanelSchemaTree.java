package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.Main;
import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.ResourceBundle;

public class PanelSchemaTree {

    PanelBrowser browserController;

    Schema schema;


    @FXML
    TreeView schemaView;

    public PanelBrowser getBrowserController() {
        return browserController;
    }

    public void setBrowserController(PanelBrowser browserController) {
        this.browserController = browserController;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }



}
