package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.connection.DatabaseConnection;
import com.abelovagrupa.dbeeadmin.util.AlertManager;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

        String dbUrl;
        String dbUsername;
        String dbPassword;

        try{
            // Catches initial exception when there is no .env file in project
            Dotenv dotenv = Dotenv.configure().directory("src/main/resources/com/abelovagrupa/dbeeadmin/.env").load();
            dbUrl = dotenv.get("DB_URL");
            dbUsername = dotenv.get("DB_USERNAME");
            dbPassword = dotenv.get("DB_PASSWORD");

            if(dbUrl == null || dbUsername == null) return;

            String urlWithoutPrefix = dbUrl.replace("jdbc:mysql://", "");
            String[] parts = urlWithoutPrefix.split(":");

            if(parts.length == 2) {
                txtHost.setText(parts[0]);
                txtPort.setText(parts[1].replace("/", ""));
            } else return;


        }catch (Exception ex){
            // Initializing parameters to empty strings
            dbUrl = "";
            dbUsername = "";
            dbPassword = "";

        }

        txtUsername.setText(dbUsername);
        pwdPassword.setText(dbPassword);

    }

    public void connect(ActionEvent actionEvent) {
//        AlertManager.showInformationDialog(null, null, "Not implemented yet.");
        // TODO: Better exception handling and displaying
        String dbUrl = DatabaseConnection.getInstance().generateDbUrl(txtHost.getText(),txtPort.getText());
        DatabaseConnection.getInstance().setConnection(dbUrl,txtUsername.getText(),pwdPassword.getText());
        AlertManager.showInformationDialog("DB-Admin",null,"Connection established");
    }

    public void testConnection(ActionEvent actionEvent) {
//        AlertManager.showInformationDialog(null, null, "Not implemented yet.");
        // TODO: Better exception handling and displaying
        String dbUrl = DatabaseConnection.getInstance().generateDbUrl(txtHost.getText(),txtPort.getText());
        DatabaseConnection.getInstance().isConnectionValid(dbUrl,txtUsername.getText(),pwdPassword.getText());
        AlertManager.showInformationDialog("DB-Admin",null,"Successfully made the MYSQL Connection");
    }
}
