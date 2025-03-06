package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.services.FileExporter;
import com.abelovagrupa.dbeeadmin.services.QueryExecutor;
import com.abelovagrupa.dbeeadmin.util.Pair;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.input.KeyCode;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PanelScript implements Initializable {

    private PanelEditor editorController;

    private PanelResults resultsController;

    @FXML
    CodeArea codeArea;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.textProperty().addListener((_, _, newText) -> {
            codeArea.setStyleSpans(0, computeHighlighting(newText));
        });

        // Setting shortcuts for editor.
        codeArea.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.F5) runLine();
            if(keyEvent.getCode() == KeyCode.F7) runScript();
        });
    }

    public PanelEditor getEditorController() {
        return editorController;
    }

    public void setEditorController(PanelEditor editorController) {
        this.editorController = editorController;
    }

    public PanelResults getResultsController() {
        return resultsController;
    }

    public void setResultsController(PanelResults resultsController) {
        this.resultsController = resultsController;
    }

    public void runLine() {
        if(codeArea.getText() == null || codeArea.getText().isEmpty() || codeArea.getSelectedText().isEmpty()) return;
        Pair<ResultSet, Integer> result = QueryExecutor.executeQuery(codeArea.getSelectedText());
        printHistory((result.getSecond() != null) ? result.getSecond() : 0, result.getFirst() != null);
        try {
            printResultSetToTable(result.getFirst());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void runScript() {
        if(codeArea.getText() == null || codeArea.getText().isEmpty()) return;
        Pair<ResultSet, Integer> result = QueryExecutor.executeQuery(codeArea.getText());
        printHistory((result.getSecond() != null) ? result.getSecond() : 0, result.getFirst() != null);
        try {
            printResultSetToTable(result.getFirst());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void printHistory(Integer rowsAffected, boolean isSelect) {
        String text = (isSelect) ? "(DQL) SELECT RETURNED DATA " : "ROWS AFFECTED: ";
        if(rowsAffected != null && rowsAffected != 0)
            text = text + rowsAffected + " ";
        text = text + "@ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        resultsController.getResultContainer().getChildren().add(new Label(text));
    }

    @SuppressWarnings({"unchecked"})
    private void printResultSetToTable(ResultSet resultSet) throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();

        // Clear old columns
        resultsController.getResultsTable().getColumns().clear();

        // Dynamically create columns (I've spent WAY more time on this than I should, yet it still looks like shit)
        for (int i = 1; i <= columnCount; i++) {
            String columnName = resultSetMetaData.getColumnName(i);
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnName);
            int colIndex = i - 1; // Index of column in ResultSet (0-based)

            // Set cell value factory to get the correct column data from each row in the ObservableList
            column.setCellValueFactory(cellData -> {
                return new SimpleStringProperty(cellData.getValue().get(colIndex));
            });
            resultsController.getResultsTable().getColumns().add(column);
        }

        // Loading data to this uncanny collection
        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        while (resultSet.next()) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= columnCount; i++) {
                row.add(resultSet.getString(i));
            }
            data.add(row);
        }
        resultsController.getResultsTable().setItems(data);
    }

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

    public void exportSQL() {
        FileExporter.exportSQL(codeArea.getText());
    }

}
