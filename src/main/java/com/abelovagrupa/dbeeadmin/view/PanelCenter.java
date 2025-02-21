package com.abelovagrupa.dbeeadmin.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PanelCenter implements Initializable {

    @FXML
    private TabPane editorPanel;

    private PanelEditor editorPanelController;

    @FXML
    private SplitPane resultsPanel;

    private PanelResults resultsPanelController;

    private PanelMain mainPanelController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/abelovagrupa/dbeeadmin/panelEditor.fxml"));
            editorPanel = loader.load();
            editorPanelController = loader.getController();

            loader = new FXMLLoader(getClass().getResource("/com/abelovagrupa/dbeeadmin/panelResults.fxml"));
            resultsPanel = loader.load();
            resultsPanelController = loader.getController();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public PanelMain getMainPanelController() {
        return mainPanelController;
    }

    public void setMainPanelController(PanelMain mainPanelController) {
        this.mainPanelController = mainPanelController;
    }
}
