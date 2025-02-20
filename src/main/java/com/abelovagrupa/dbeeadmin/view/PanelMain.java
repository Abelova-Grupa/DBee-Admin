package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class PanelMain {
    @FXML
    Button btnConnection;


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
