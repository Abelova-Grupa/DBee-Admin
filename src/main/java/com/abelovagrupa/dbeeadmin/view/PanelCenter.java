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
            // Loading editorPanel and injecting editorController
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/abelovagrupa/dbeeadmin/panelEditor.fxml"));
            editorPanel = loader.load();
            editorPanelController = loader.getController();
            // Creating a reference to the center controller from editor controller
            editorPanelController.setCenterPanelController(this);

            // Loading resultsPanel and injecting resultsController
            loader = new FXMLLoader(getClass().getResource("/com/abelovagrupa/dbeeadmin/panelResults.fxml"));
            resultsPanel = loader.load();
            resultsPanelController = loader.getController();
            // Creating a reference to the center controller from results controller
            resultsPanelController.setCenterPanelController(this);

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
