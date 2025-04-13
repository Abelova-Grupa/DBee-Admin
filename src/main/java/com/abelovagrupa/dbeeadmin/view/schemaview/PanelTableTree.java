package com.abelovagrupa.dbeeadmin.view.schemaview;

import javafx.scene.control.TreeItem;

import java.util.HashMap;

public class PanelTableTree {

    HashMap<String, TreeItem<String>> columnNodesHashMap;

    HashMap<String, TreeItem<String>> indexNodesHashMap;

    HashMap<String, TreeItem<String>> foreignKeyNodesHashMap;

    HashMap<String, TreeItem<String>> triggerNodesHashMap;

    public PanelTableTree() {
    }

    public HashMap<String, TreeItem<String>> getColumnNodesHashMap() {
        if(columnNodesHashMap == null)
            columnNodesHashMap = new HashMap<>();
        return columnNodesHashMap;
    }

    public void setColumnNodesHashMap(HashMap<String, TreeItem<String>> columnNodesHashMap) {
        this.columnNodesHashMap = columnNodesHashMap;
    }

    public HashMap<String, TreeItem<String>> getIndexNodesHashMap() {
        if(indexNodesHashMap == null)
            indexNodesHashMap = new HashMap<>();
        return indexNodesHashMap;
    }

    public void setIndexNodesHashMap(HashMap<String, TreeItem<String>> indexNodesHashMap) {
        this.indexNodesHashMap = indexNodesHashMap;
    }

    public HashMap<String, TreeItem<String>> getForeignKeyNodesHashMap() {
        if(foreignKeyNodesHashMap == null)
            foreignKeyNodesHashMap = new HashMap<>();
        return foreignKeyNodesHashMap;
    }

    public void setForeignKeyNodesHashMap(HashMap<String, TreeItem<String>> foreignKeyNodesHashMap) {
        this.foreignKeyNodesHashMap = foreignKeyNodesHashMap;
    }

    public HashMap<String, TreeItem<String>> getTriggerNodesHashMap() {
        if(triggerNodesHashMap == null)
            triggerNodesHashMap = new HashMap<>();
        return triggerNodesHashMap;
    }

    public void setTriggerNodesHashMap(HashMap<String, TreeItem<String>> triggerKeyNodesHashMap) {
        this.triggerNodesHashMap = triggerKeyNodesHashMap;
    }
}
