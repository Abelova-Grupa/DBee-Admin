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
    Label tableName;

    List<BorderPane> attributes = new LinkedList<>();

    @FXML
    VBox attributeContainer;

    @FXML
    Label columnName;

    @FXML
    Label columnType;

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
}
