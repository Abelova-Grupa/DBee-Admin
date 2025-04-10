package com.abelovagrupa.dbeeadmin.view.schemaview;

import javafx.scene.control.TreeItem;

import java.util.HashMap;

public class PanelTableTree {

    HashMap<String, TreeItem<String>> columnNodesHashMap;

    HashMap<String, TreeItem<String>> indexNodesHashMap;

    HashMap<String, TreeItem<String>> foreignKeyNodesHashMap;

    HashMap<String, TreeItem<String>> triggerKeyNodesHashMap;

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

    public HashMap<String, TreeItem<String>> getTriggerKeyNodesHashMap() {
        return triggerKeyNodesHashMap;
    }

    public void setTriggerKeyNodesHashMap(HashMap<String, TreeItem<String>> triggerKeyNodesHashMap) {
        this.triggerKeyNodesHashMap = triggerKeyNodesHashMap;
    }
}
