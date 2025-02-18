package com.abelovagrupa.dbeeadmin.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PanelEditor implements Initializable {

    @FXML
    CodeArea codeArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.textProperty().addListener((_, _, newText) -> {
            codeArea.setStyleSpans(0, computeHighlighting(newText));
        });
    }

    private static final String[] SQL_KEYWORDS = {
        "SELECT", "FROM", "WHERE", "INSERT", "INTO", "VALUES", "UPDATE", "SET", "DELETE", "CREATE", "TABLE",
        "ALTER", "DROP", "JOIN", "ON", "AND", "OR", "NOT", "NULL", "ORDER", "BY", "GROUP", "HAVING", "LIMIT"
    };

    /*
     Dear reader, please don't think that I have gone insane for all of this regex matching madness was written by AI.
     There is a much easier way of implementing syntax highlighting and this was done just for experimenting purposes.
     */

    // TODO: Write a simpler syntax highlighting. Avoid using richtextfx. Remember wise words of T. A. Davis.

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", SQL_KEYWORDS) + ")\\b";
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
