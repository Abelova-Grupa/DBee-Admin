package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.util.AlertManager;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.w3c.dom.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class PanelConnection implements Initializable {

    @FXML
    Button btnTestConnection;

    @FXML
    Button btnConnect;

    @FXML
    TextField txtHost;

    @FXML
    TextField txtPort;

    @FXML
    TextField txtUsername;

    @FXML
    PasswordField pwdPassword;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Dotenv dotenv = Dotenv.load();
        String dbUrl = dotenv.get("DB_URL");
        String dbUsername = dotenv.get("DB_USERNAME");
        String dbPassword = dotenv.get("DB_PASSWORD");

        if(dbUrl == null || dbUsername == null) return;

        String urlWithoutPrefix = dbUrl.replace("jdbc:mysql://", "");
        String[] parts = urlWithoutPrefix.split(":");

        if(parts.length == 2) {
            txtHost.setText(parts[0]);
            txtPort.setText(parts[1].replace("/", ""));
        } else return;

        txtUsername.setText(dbUsername);
        pwdPassword.setText(dbPassword);

    }

    public void connect(ActionEvent actionEvent) {
        AlertManager.showInformationDialog(null, null, "Not implemented yet.");
    }

    public void testConnection(ActionEvent actionEvent) {
        AlertManager.showInformationDialog(null, null, "Not implemented yet.");
    }
}
