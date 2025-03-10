package com.abelovagrupa.dbeeadmin.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.LinkedList;
import java.util.List;

public class PanelInfo {

    private PanelMain mainController;

    @FXML
    AnchorPane tableInfoPanel;

    @FXML
    AnchorPane columnInfoPanel;

    @FXML
    AnchorPane clearInfoPanel;

    @FXML
    AnchorPane foreignKeyInfoPanel;

    @FXML
    Label tableName;

    List<BorderPane> attributes = new LinkedList<>();

    @FXML
    VBox attributeContainer;

    @FXML
    Label columnName;

    @FXML
    Label columnType;

    @FXML
    Label visibleName;

    @FXML
    Label uniqueName;

    @FXML
    Label indexName;

    @FXML
    AnchorPane indexInfoPanel;

    @FXML
    Label typeName;

    @FXML
    VBox indexColumnContainer;

    @FXML
    VBox keyContainer;

    @FXML
    Label refTable;

    @FXML
    Label foreignKeyName;

    public PanelMain getMainController() {
        return mainController;
    }

    public void setMainController(PanelMain mainController) {
        this.mainController = mainController;
    }

    public Label getTableName() {
        return tableName;
    }

    public void setTableName(Label tableName) {
        this.tableName = tableName;
    }

    public List<BorderPane> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<BorderPane> attributes) {
        this.attributes = attributes;
    }

    public VBox getAttributeContainer() {
        return attributeContainer;
    }

    public void setAttributeContainer(VBox attributeContainer) {
        this.attributeContainer = attributeContainer;
    }

    public void addAttributePane(BorderPane attributePane){
        attributes.add(attributePane);
        attributeContainer.getChildren().add(attributePane);
    }

    public AnchorPane getTableInfoPanel() {
        return tableInfoPanel;
    }

    public void setTableInfoPanel(AnchorPane tableInfoPanel) {
        this.tableInfoPanel = tableInfoPanel;
    }

    public AnchorPane getColumnInfoPanel() {
        return columnInfoPanel;
    }

    public void setColumnInfoPanel(AnchorPane columnInfoPanel) {
        this.columnInfoPanel = columnInfoPanel;
    }

    public Label getColumnName() {
        return columnName;
    }

    public void setColumnName(Label columnName) {
        this.columnName = columnName;
    }

    public Label getColumnType() {
        return columnType;
    }

    public void setColumnType(Label columnType) {
        this.columnType = columnType;
    }

    public Label getVisibleName() {
        return visibleName;
    }

    public void setVisibleName(Label visibleName) {
        this.visibleName = visibleName;
    }

    public Label getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(Label uniqueName) {
        this.uniqueName = uniqueName;
    }

    public Label getIndexName() {
        return indexName;
    }

    public void setIndexName(Label indexName) {
        this.indexName = indexName;
    }

    public AnchorPane getIndexInfoPanel() {
        return indexInfoPanel;
    }

    public void setIndexInfoPanel(AnchorPane indexInfoPanel) {
        this.indexInfoPanel = indexInfoPanel;
    }

    public Label getTypeName() {
        return typeName;
    }

    public void setTypeName(Label typeName) {
        this.typeName = typeName;
    }

    public VBox getIndexColumnContainer() {
        return indexColumnContainer;
    }

    public void setIndexColumnContainer(VBox indexColumnContainer) {
        this.indexColumnContainer = indexColumnContainer;
    }

    public VBox getKeyContainer() {
        return keyContainer;
    }

    public void setKeyContainer(VBox keyContainer) {
        this.keyContainer = keyContainer;
    }

    public AnchorPane getClearInfoPanel() {
        return clearInfoPanel;
    }

    public void setClearInfoPanel(AnchorPane clearInfoPanel) {
        this.clearInfoPanel = clearInfoPanel;
    }

    public AnchorPane getForeignKeyInfoPanel() {
        return foreignKeyInfoPanel;
    }

    public void setForeignKeyInfoPanel(AnchorPane foreignKeyInfoPanel) {
        this.foreignKeyInfoPanel = foreignKeyInfoPanel;
    }

    public Label getRefTable() {
        return refTable;
    }

    public void setRefTable(Label refTable) {
        this.refTable = refTable;
    }

    public Label getForeignKeyName() {
        return foreignKeyName;
    }

    public void setForeignKeyName(Label foreignKeyName) {
        this.foreignKeyName = foreignKeyName;
    }
}
