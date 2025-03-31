package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.services.ProgramState;
import com.abelovagrupa.dbeeadmin.util.AlertManager;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;

public class panelDump {

    public CheckBox cbComments;
    public CheckBox cbCreateOptions;
    public CheckBox cbAllowKeywords;
    public CheckBox cbQuoteNames;
    public CheckBox cbNoData;
    public CheckBox cbLockTables;
    public CheckBox cbDumpDate;
    public CheckBox cbFlushLogs;
    public CheckBox cbHexBlob;
    public CheckBox cbCompress;
    public CheckBox cbFlushPrivileges;
    public CheckBox cbDisableKeys;
    public CheckBox cbForce;
    public CheckBox cbOrderByPrimary;
    public CheckBox cbTzUlc;
    public CheckBox cbAddLocks;
    public CheckBox cbCompleteInsert;
    public CheckBox cbExtendedInsert;
    public CheckBox cbInsertIgnore;
    public CheckBox cbReplace;
    public TextField txtArguments;
    public Button btnSelectPath;
    public Label lblStatus;
    public Button btnStart;

    /**
     * Method that constructs mysqldump command from the GUI form.
     * @return mysql command
     */
    public String constructStatement(String path) {
        StringBuilder command = new StringBuilder("mysqldump ");

        // Get user credentials
        Dotenv dotenv = Dotenv.configure().directory("src/main/resources/com/abelovagrupa/dbeeadmin/.env").load();
        String dbUsername = dotenv.get("DB_USERNAME");
        String dbPassword = dotenv.get("DB_PASSWORD");

        command.append("--user=").append(dbUsername).append(" ");
        command.append("--password=").append(dbPassword).append(" ");

        // Build the command
        if (cbComments.isSelected()) command.append("--comments ");
        if (cbCreateOptions.isSelected()) command.append("--create-options ");
        if (cbAllowKeywords.isSelected()) command.append("--allow-keywords ");
        if (cbQuoteNames.isSelected()) command.append("--quote-names ");
        if (cbNoData.isSelected()) command.append("--no-data ");
        if (cbLockTables.isSelected()) command.append("--lock-tables ");
        if (cbDumpDate.isSelected()) command.append("--dump-date ");
        if (cbFlushLogs.isSelected()) command.append("--flush-logs ");
        if (cbHexBlob.isSelected()) command.append("--hex-blob ");
        if (cbCompress.isSelected()) command.append("--compress ");
        if (cbFlushPrivileges.isSelected()) command.append("--flush-privileges ");
        if (cbDisableKeys.isSelected()) command.append("--disable-keys ");
        if (cbForce.isSelected()) command.append("--force ");
        if (cbOrderByPrimary.isSelected()) command.append("--order-by-primary ");
        if (cbTzUlc.isSelected()) command.append("--tz-utc ");
        if (cbAddLocks.isSelected()) command.append("--add-locks ");
        if (cbCompleteInsert.isSelected()) command.append("--complete-insert ");
        if (cbExtendedInsert.isSelected()) command.append("--extended-insert ");
        if (cbInsertIgnore.isSelected()) command.append("--insert-ignore ");
        if (cbReplace.isSelected()) command.append("--replace ");

        String arguments = txtArguments.getText();

        // If user added extra arguments, append them to the command
        if (!arguments.isEmpty()) command.append(arguments).append(" ");

        command.append(ProgramState.getInstance().getSelectedSchema().getName()).append(" ");

        if (!path.isEmpty()) command.append("> ").append("\"").append(path).append("\"");
        else {
            AlertManager.showErrorDialog(null, null, "Invalid path.");
            return null;
        }

        return command.toString().trim();
    }


    public void executeDump() {
        try {
            String command = constructStatement(choosePath());
            AlertManager.showConfirmationDialog(null, null, command);

            String os = System.getProperty("os.name").toLowerCase();
            String[] cmd;

            if (os.contains("win")) {
                // For Windows (Beta male users), use cmd.exe
                cmd = new String[]{"cmd.exe", "/c", command};
            } else {
                // For Unix/Linux/Mac (Giga chads, Sigma males), use bash
                cmd = new String[]{"/bin/bash", "-c", command};
            }

            ProcessBuilder processBuilder = new ProcessBuilder(cmd);
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            process.waitFor();

            AlertManager.showInformationDialog(null, "Success.", "Dump finished.");
        } catch (Exception e) {
            AlertManager.showErrorDialog(null, "Dump failed.", e.getMessage());
        }
    }

    public String choosePath() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL Files", "*.sql"));
        fileChooser.setInitialFileName("dump.sql");

        Stage stage = new Stage();
        File selectedFile = fileChooser.showSaveDialog(stage);

        if (selectedFile != null) {
            return selectedFile.getAbsolutePath();
        }
        return null;
    }

}
