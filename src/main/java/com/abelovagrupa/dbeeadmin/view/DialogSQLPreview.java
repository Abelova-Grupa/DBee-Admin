package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DialogSQLPreview extends Dialog<Boolean> {
    @FXML
    ScrollPane scrollContent;

    @FXML
    CodeArea codeArea;

    @FXML
    Label lblTitle;

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
            return null;
        });

        // Setup the code area
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.textProperty().addListener((_, _, newText) -> {
            codeArea.setStyleSpans(0, computeHighlighting(newText));
        });


        this.setTitle("SQL Preview");
        this.setSqlPreviewContent(sql);
        this.getDialogPane().setMinWidth(500);
        this.getDialogPane().getScene().getStylesheets().add(Main.class.getResource("styles.css").toExternalForm());

    }

    public void setSqlPreviewContent(String sqlContent) {
        codeArea.appendText(sqlContent);
    }

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", PanelScript.SQL_KEYWORDS) + ")\\b";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String NUMBER_PATTERN = "\\b\\d+\\b";
    private static final String COMMENT_PATTERN = "--[^\n]*" + "|" + "/\\*[^\\*]*\\*/";

    private static final Pattern PATTERN = Pattern.compile(
        "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text.toUpperCase());
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("STRING") != null ? "string" :
                        matcher.group("NUMBER") != null ? "number" :
                            matcher.group("COMMENT") != null ? "comment" :
                                null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

}
