package com.abelovagrupa.dbeeadmin.view;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;

public class PanelHelp {

    public Button btnWiki;
    public WebView webView;
    private PanelMain mainController;

    public PanelMain getMainController() {
        return mainController;
    }

    public void setMainController(PanelMain mainController) {
        this.mainController = mainController;
    }

    public void loadWiki(ActionEvent actionEvent) {
        webView.getEngine().load("https://github.com/Abelova-Grupa/DBee-Admin/wiki");
    }
}
