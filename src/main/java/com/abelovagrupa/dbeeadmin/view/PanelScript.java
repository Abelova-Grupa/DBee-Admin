package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.services.FileProcessor;
import com.abelovagrupa.dbeeadmin.services.QueryExecutor;
import com.abelovagrupa.dbeeadmin.util.Pair;
import com.abelovagrupa.dbeeadmin.util.SyntaxHighlighter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.input.KeyCode;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class PanelScript implements Initializable {

    private PanelEditor editorController;

    private PanelResults resultsController;

    @FXML
    CodeArea codeArea;

    public static final String[] SQL_KEYWORDS = {
        "SELECT", "INSERT", "UPDATE", "DELETE", "FROM", "WHERE", "GROUP", "HAVING", "ORDER", "INTO",
        "BY", "JOIN", "INNER", "LEFT", "RIGHT", "FULL", "OUTER", "ON", "AND", "OR", "NOT",
        "NULL", "IS", "BETWEEN", "LIKE", "IN", "EXISTS", "DISTINCT", "AS", "CREATE",
        "TABLE", "DROP", "ALTER", "ADD", "COLUMN", "CONSTRAINT", "PRIMARY", "KEY", "FOREIGN",
        "REFERENCES", "CHECK", "DEFAULT", "INDEX", "VIEW", "TRIGGER", "PROCEDURE",
        "FUNCTION", "TRANSACTION", "COMMIT", "ROLLBACK", "SAVEPOINT", "GRANT", "REVOKE",
        "USER", "DATABASE", "USE", "EXPLAIN", "DESCRIBE", "SHOW", "LIMIT", "OFFSET", "FETCH",
        "WITH", "INNER JOIN", "OUTER JOIN", "CROSS JOIN", "UNION", "INTERSECT", "EXCEPT",
        "CASE", "WHEN", "THEN", "ELSE", "END", "IF", "LOOP", "WHILE", "RETURN",
        "SET", "VALUES", "COLUMN_NAME", "DATABASE_NAME", "SCHEMA", "ISNULL", "COALESCE",
        "CAST", "CONVERT", "TRIM", "SUBSTRING", "LEN", "ROUND", "AVG", "COUNT",
        "SUM", "MAX", "MIN", "LIMIT", "OFFSET", "ASC", "DESC"
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.textProperty().addListener((_, _, newText) -> {
            codeArea.setStyleSpans(0, SyntaxHighlighter.computeHighlighting(newText));
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

    // DRZI VODU
    public static boolean hasMultipleSemicolons(String input) {
        int semicolonCount = 0;
        for (char c : input.toCharArray()) {
            if (c == ';') {
                semicolonCount++;
                if (semicolonCount > 1) return true;
            }
        }

        // Return false if semicolon appears once or not at all
        return false;
    }

    public void runScript() {
        if(codeArea.getText() == null || codeArea.getText().isEmpty()) return;

        // Drzi vodu
        Pair<ResultSet, Integer> result;
        if(hasMultipleSemicolons(codeArea.getText())) {
           result  = QueryExecutor.executeBatch(codeArea.getText());
        } else {
           result = QueryExecutor.executeQuery(codeArea.getText());
        }





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
        if(resultSet == null) return;
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

    public void exportSQL() {
        FileProcessor.exportSQL(codeArea.getText());
    }

    public void importSQLToEditor(ActionEvent actionEvent) {
        codeArea.clear();
        codeArea.appendText(FileProcessor.importSQL());
    }
}
