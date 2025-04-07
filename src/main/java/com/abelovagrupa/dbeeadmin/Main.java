package com.abelovagrupa.dbeeadmin;

import com.abelovagrupa.dbeeadmin.connection.DatabaseConnection;
import com.abelovagrupa.dbeeadmin.view.PanelMain;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

//        // Temporary solution
//        if(DatabaseConnection.getInstance().getConnection() == null) {
//            AlertManager.showErrorDialog(null, "Fatal: connection refused.",
//                "Check the server availability and try again.");
//            return;
//        }

        if(DatabaseConnection.getInstance().getConnection() == null) {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("panelConnection.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stageConnection = new Stage();
            stageConnection.initModality(Modality.APPLICATION_MODAL);

            stageConnection.setTitle("DBee Admin - Connection Settings");
            stageConnection.setScene(scene);

            stageConnection.setOnShown(event -> {
                // Get the screen's bounds (width and height)
                double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
                double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

                // Get the stageConnection width and height
                double stageWidth = stageConnection.getWidth();
                double stageHeight = stageConnection.getHeight();

                // Calculate the center position
                stageConnection.setX((screenWidth - stageWidth) / 2);
                stageConnection.setY((screenHeight - stageHeight) / 2);
            });
            scene.getStylesheets().add(Main.class.getResource("styles.css").toExternalForm());
            stageConnection.getIcons().add(new Image(Main.class.getResource("images/bee.png").toExternalForm()));

            stageConnection.showAndWait();

            if(DatabaseConnection.getInstance().getConnection() == null)
                return;

        }

        stage.getIcons().add(new Image(getClass().getResource("images/bee.png").toExternalForm()));

        //Manually setting up the controller BUT FIRST LOAD THE FXML!
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"));
        PanelMain mainController = new PanelMain();

        fxmlLoader.setController(mainController);
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        stage.setTitle("DBee Admin");
        stage.setScene(scene);
        stage.setMaximized(true);
//        stage.show();
        Platform.runLater(stage::show);
    }

    public static void main(String[] args) {
        launch();
    }
}