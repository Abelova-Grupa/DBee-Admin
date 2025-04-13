package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.Main;
import com.abelovagrupa.dbeeadmin.util.SyntaxHighlighter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.IOException;
import java.util.Objects;

public class DialogSQLPreview extends Dialog<Boolean> {
    @FXML
    ScrollPane scrollContent;

    @FXML
    CodeArea codeArea;

    @FXML
    Label lblTitle;

    /**
     * Creates a new SQL Preview window which can execute sql.
     * Note: It doesn't render the window; To do that call .show() or .showAndWait()
     * @param sql Initial sql to be previewed, edited and executed.
     */
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
                return true;
            }
            if(buttonType == ButtonType.CANCEL) {
                return false;
            }
            return null;
        });

        // Set up the code area
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.textProperty().addListener((_, _, newText) -> {
            codeArea.setStyleSpans(0, SyntaxHighlighter.computeHighlighting(newText));
        });


        this.setTitle("SQL Preview");
        this.setSqlPreviewContent(sql);
        this.getDialogPane().setMinWidth(500);
        this.getDialogPane().getScene().getStylesheets().add(Objects.requireNonNull(Main.class.getResource("styles.css")).toExternalForm());

    }

    public void setSqlPreviewContent(String sqlContent) {
        codeArea.appendText(sqlContent);
    }

}
