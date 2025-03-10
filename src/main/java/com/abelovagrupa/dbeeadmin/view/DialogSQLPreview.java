package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.IOException;

public class DialogSQLPreview extends Dialog<String> {
    @FXML
    private ScrollPane scrollContent;

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

        // Set the dialog's content to the root of the FXML
        this.getDialogPane().setContent(scrollContent);

        // Add buttons (Apply and Cancel)
        this.getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);

        // Set up the result converter (this determines what the dialog returns)
        this.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.APPLY) {
                return "SQL Preview Applied"; // Replace with whatever result you need
            }
            return null;
        });

        // Set the title of the dialog
        this.setTitle("SQL Preview");
    }

    // Method to set the content for the ScrollPane, i.e., SQL preview data
    public void setSqlPreviewContent(String sqlContent) {
        Label label = new Label(sqlContent);
        scrollContent.setContent(label);  // Set the SQL preview content into the ScrollPane
    }

}
