package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.Main;
import com.abelovagrupa.dbeeadmin.services.QueryExecutor;
import com.abelovagrupa.dbeeadmin.util.Pair;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PanelEditor implements Initializable {

    private PanelMain mainController;

    private PanelResults resultsController;

    private PanelScript scriptController;

    @FXML
    TabPane editorTabs;

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
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("panelScript.fxml"));
        Tab scriptTab = loader.load();
        setScriptController(loader.getController());
        scriptController.setEditorController(this);
        editorTabs.getTabs().add(scriptTab);
        editorTabs.getSelectionModel().select(scriptTab);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }











}
