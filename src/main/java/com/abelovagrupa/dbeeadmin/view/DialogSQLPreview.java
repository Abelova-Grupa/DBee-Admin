package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;

public class DialogSQLPreview extends Dialog<String> {
    @FXML
    ScrollPane scrollContent;

    @FXML
    Label lblTitle;

    @FXML
    TextArea taEditor;

    public DialogSQLPreview(String sql) {
        // Load FXML and initialize the dialog
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("dialogSQLPreview.fxml")); // Adjust path as necessary
        loader.setController(this);

        try {
            // Load the FXML layout into the dialog
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML", e);
        }

        /*
        * I am still unsure why is it necessary to set following components in controller even though they are
        * declared in fxml
        */


        this.getDialogPane().setHeader(lblTitle);

        // Set the dialog's content to the root of the FXML
        this.getDialogPane().setContent(scrollContent);

        // Add buttons (Apply and Cancel)
        this.getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);

        // Set up the result converter (this determines what the dialog returns)
        this.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.APPLY) {
                return "SQL Preview Finished";
            }
            return null;
        });

        this.setTitle("SQL Preview");
        this.setSqlPreviewContent(sql);
        this.getDialogPane().setMinWidth(500);

    }

    public void setSqlPreviewContent(String sqlContent) {
        taEditor.setText(sqlContent);
    }

}
