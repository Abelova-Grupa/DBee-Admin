package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.connection.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.net.URL;
import java.sql.*;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.abelovagrupa.dbeeadmin.view.PanelResults.*;

public class PanelEditor implements Initializable {

    private PanelCenter centerPanelController;

    @FXML
    CodeArea codeArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.textProperty().addListener((_, _, newText) -> {
            codeArea.setStyleSpans(0, computeHighlighting(newText));
        });
    }

    public PanelCenter getCenterPanelController() {
        return centerPanelController;
    }

    public void setCenterPanelController(PanelCenter centerPanelController) {
        this.centerPanelController = centerPanelController;
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

    private void executeQuery(String sql) {

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getInstance().getConnection();

            statement = connection.createStatement();

            boolean isResultSet = statement.execute(sql);

            // NOTE: This method requires to
            // TODO: Find a way to set a selected schema (for which I created the State class).

            // Process the result (if it's a SELECT query)
            if (isResultSet) {
                resultSet = statement.getResultSet();

            printResultSet(resultSet);

            } else {
                // For non-SELECT queries (INSERT, UPDATE, DELETE), print the number of rows affected
                int rowsAffected = statement.getUpdateCount();
                System.out.println("Rows affected: " + rowsAffected);
            }

        } catch (SQLException e) {
            System.err.println("Error executing SQL query: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }

    }

    public void runScript(ActionEvent actionEvent) {
        executeQuery(codeArea.getText());
    }

    /**
     * Prints a ResultSet to the standard output. For testing purposes.
     * @param resultSet to be printed to std out
     */
    public static void printResultSet(ResultSet resultSet) {
        try {
            // Get metadata about the ResultSet
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Print column headers
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnLabel(i) +"\t");
            }
            System.out.println();

            // Print rows of data
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(resultSet.getString(i) + "\t");
                }
                System.out.println();
            }
        } catch (SQLException e) {
            System.err.println("Error printing ResultSet: " + e.getMessage());
        }
    }




}
