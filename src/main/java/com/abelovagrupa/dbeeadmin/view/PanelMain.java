package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PanelMain implements Initializable {
    @FXML
    Button btnConnection;

    @FXML
    private HBox leftPanel;

    private PanelLeft leftPanelController;

    @FXML
    private SplitPane centralPanel;

    private PanelCenter centerPanelController;

    @FXML
    private VBox rightPanel;

    private PanelRight rightPanelController;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Linking controllers...
        try {
            // Loading leftPanel and injecting leftController
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/abelovagrupa/dbeeadmin/panelLeft.fxml"));
            leftPanel = loader.load();
            leftPanelController = loader.getController();

            // Loading centerPanel and injecting centerController
            loader = new FXMLLoader(getClass().getResource("/com/abelovagrupa/dbeeadmin/panelCenter.fxml"));
            centralPanel = loader.load();
            centerPanelController = loader.getController();

            // Loading rightPanel and injecting rightController
            loader = new FXMLLoader(getClass().getResource("/com/abelovagrupa/dbeeadmin/panelRight.fxml"));
            rightPanel = loader.load();
            rightPanelController = loader.getController();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void openConnectionSettings(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("panelConnection.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setTitle("DBee Admin - Connection Settings");
        stage.setScene(scene);

        stage.setOnShown(event -> {
            // Get the screen's bounds (width and height)
            double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
            double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

            // Get the stage width and height
            double stageWidth = stage.getWidth();
            double stageHeight = stage.getHeight();

            // Calculate the center position
            stage.setX((screenWidth - stageWidth) / 2);
            stage.setY((screenHeight - stageHeight) / 2);
        });
        ;
        stage.show();
    }


}
