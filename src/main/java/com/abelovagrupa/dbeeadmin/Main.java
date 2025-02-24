package com.abelovagrupa.dbeeadmin;

import com.abelovagrupa.dbeeadmin.view.PanelMain;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //Manually setting up the controller
        PanelMain mainController = new PanelMain();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"));
        fxmlLoader.setController(mainController);
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        stage.setTitle("DBee Admin");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}