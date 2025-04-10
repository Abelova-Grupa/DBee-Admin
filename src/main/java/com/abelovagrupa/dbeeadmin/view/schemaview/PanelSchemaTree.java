package com.abelovagrupa.dbeeadmin.view.schemaview;

import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.util.Pair;
import com.abelovagrupa.dbeeadmin.view.PanelBrowser;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.HashMap;

public class PanelSchemaTree {
    @FXML
    TreeView<String> schemaView;

    Schema schema;

    // example: schemaName : (tableNodes of schema, table nodes controller)
    HashMap<String, Pair<TreeItem<String>,PanelTableTree>> tableNodesHashMap;

    PanelBrowser browserController;

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

    public TreeView<String> getSchemaView() {
        return schemaView;
    }

    public void setSchemaView(TreeView<String> schemaView) {
        this.schemaView = schemaView;
    }

    public HashMap<String, Pair<TreeItem<String>, PanelTableTree>> getTableNodesHashMap() {
        if(tableNodesHashMap == null)
            tableNodesHashMap = new HashMap<>();
        return tableNodesHashMap;
    }

    public void setTableNodesHashMap(HashMap<String, Pair<TreeItem<String>, PanelTableTree>> tableNodesHashMap) {
        this.tableNodesHashMap = tableNodesHashMap;
    }
}