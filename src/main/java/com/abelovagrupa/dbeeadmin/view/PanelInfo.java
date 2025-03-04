package com.abelovagrupa.dbeeadmin.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class PanelInfo {

    private PanelMain mainController;

    @FXML
    Label tableName;

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
}
