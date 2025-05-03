package com.abelovagrupa.dbeeadmin.util;

import com.abelovagrupa.dbeeadmin.Main;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class AlertManager {

    // This approach was employed to prevent dependency hell. And to make it look nice.

    public static void showConfirmationDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        display(alert, title, header, content);
    }

    public static void showInformationDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        display(alert, title, header, content);
    }

    public static void showWarningDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        display(alert, title, header, content);
    }

    public static void showErrorDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        display(alert, title, header, content);
    }

    private static void display(Alert alert, String title, String header, String content) {
        if(content == null) content = "";

        alert.setTitle((title == null) ? "DBee Admin" : title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(Objects.requireNonNull(Main.class.getResource("images/bee.png")).toExternalForm()));
        alert.showAndWait();
    }

}
