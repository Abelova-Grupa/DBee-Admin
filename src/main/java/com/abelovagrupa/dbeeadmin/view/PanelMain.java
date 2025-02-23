package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.Main;
import com.abelovagrupa.dbeeadmin.connection.DatabaseConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PanelMain implements Initializable {

    // Main panel components

    @FXML
    Button btnConnection;

    @FXML
    private HBox leftPanel;

    @FXML
    private SplitPane centralPanel;

    @FXML
    private VBox rightPanel;

    // Left panel components

    @FXML
    List<TreeView<String>> schemaViews;

    @FXML
    TreeView<String> treeView1;

    @FXML
    TreeView<String> treeView2;

    // Center panel components

    @FXML
    private TabPane editorPanel;

    @FXML
    private SplitPane resultsPanel;

    @FXML
    CodeArea codeArea;

    // Right panel components

    // ...

    // Other

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

        // Left panel initialization

        schemaViews = List.of(treeView1,treeView2);

        for (TreeView<String> schemaView : schemaViews){

            TreeItem<String> schema = new TreeItem<>("Schema",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database.png").toExternalForm())));
            schemaView.setRoot(schema);
            TreeItem<String> tableBranch = new TreeItem<>("Tables",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));
            TreeItem<String> viewBranch = new TreeItem<>("Views",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));
            TreeItem<String>  procedureBranch = new TreeItem<>("Stored Procedures",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));
            TreeItem<String> functionBranch = new TreeItem<>("Functions",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));

            TreeItem<String> table1 = new TreeItem<>("Table 1");
            TreeItem<String> table2 = new TreeItem<>("Table 2");

            tableBranch.getChildren().addAll(table1,table2);
            schema.getChildren().addAll(tableBranch,viewBranch,procedureBranch,functionBranch);

            schemaView.setPrefHeight(24);
            schemaView.getRoot().addEventHandler(TreeItem.branchExpandedEvent(), event -> {
                Platform.runLater(() -> schemaView.setPrefHeight(schemaView.getExpandedItemCount() * 24));
            });

            schemaView.getRoot().addEventHandler(TreeItem.branchCollapsedEvent(), event -> {
                Platform.runLater(() -> schemaView.setPrefHeight(schemaView.getExpandedItemCount() * 24));
            });

        }

        // Central panel initialization

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.textProperty().addListener((_, _, newText) -> {
            codeArea.setStyleSpans(0, computeHighlighting(newText));
        });

    }

    // Event handling methods

    public void openConnectionSettings(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("panelConnection.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setTitle("DBee Admin - Connection Settings");
        stage.setScene(scene);

        stage.setOnShown(event -> {
            // Get the screen's bounds (width and height)
            double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
            double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

            // Get the stage width and height
            double stageWidth = stage.getWidth();
            double stageHeight = stage.getHeight();

            // Calculate the center position
            stage.setX((screenWidth - stageWidth) / 2);
            stage.setY((screenHeight - stageHeight) / 2);
        });
        ;
        stage.show();
    }

    public void runScript(ActionEvent actionEvent) {
        executeQuery(codeArea.getText());
    }

    // Miscellaneous and Other methods

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

    // TODO: MOVE!
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

    // TODO: MOVE!
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
