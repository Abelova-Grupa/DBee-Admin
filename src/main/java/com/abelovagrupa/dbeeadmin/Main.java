package com.abelovagrupa.dbeeadmin;

import com.abelovagrupa.dbeeadmin.connection.DatabaseConnection;
import com.abelovagrupa.dbeeadmin.util.AlertManager;
import com.abelovagrupa.dbeeadmin.view.PanelMain;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        // Temporary solution
        if(DatabaseConnection.getInstance().getConnection() == null) {
            AlertManager.showErrorDialog(null, "Fatal: connection refused.",
                "Check the server availability and try again.");
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