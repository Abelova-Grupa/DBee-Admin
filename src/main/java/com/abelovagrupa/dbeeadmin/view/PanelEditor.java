package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class PanelEditor implements Initializable {

    private PanelMain mainController;

    private PanelResults resultsController;

    private PanelScript scriptController;

    @FXML
    TabPane editorTabs;

    public static final Logger logger = LogManager.getRootLogger();

    public PanelMain getMainController() {
        return mainController;
    }

    public void setMainController(PanelMain mainController) {
        this.mainController = mainController;
    }

    public PanelResults getResultsController() {
        return resultsController;
    }

    public void setResultsController(PanelResults resultsController) {
        this.resultsController = resultsController;
    }

    public PanelScript getScriptController() {
        return scriptController;
    }

    public void setScriptController(PanelScript scriptController) {
        this.scriptController = scriptController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            long startTime = System.nanoTime();
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("panelScript.fxml"));
            Tab scriptTab = loader.load();
            setScriptController(loader.getController());
            scriptController.setEditorController(this);
            editorTabs.getTabs().add(scriptTab);
            editorTabs.getSelectionModel().select(scriptTab);
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            logger.info("Editor initialization time: {} ns", duration);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a blank script tab.
     */
    public void createNewScript(){
        try {
            // Each time the method is called a new controller is created i tried making all scripts share
            //the first scriptController but i didn't find a solution
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("panelScript.fxml"));
            Tab scriptTab = loader.load();
            PanelScript newScriptController = loader.getController();
            newScriptController.setEditorController(this);
            newScriptController.setResultsController(resultsController);
            editorTabs.getTabs().add(scriptTab);
            editorTabs.getSelectionModel().select(scriptTab);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new script tab with sql.
     * @param sql SQL code to be displayed in new tab.
     */
    public void createNewScript(String sql){

        if(sql == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("panelScript.fxml"));
            Tab scriptTab = loader.load();
            PanelScript newScriptController = loader.getController();
            newScriptController.setEditorController(this);
            newScriptController.setResultsController(resultsController);
            editorTabs.getTabs().add(scriptTab);
            editorTabs.getSelectionModel().select(scriptTab);

            // Set SQL

            newScriptController.codeArea.appendText(sql);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
