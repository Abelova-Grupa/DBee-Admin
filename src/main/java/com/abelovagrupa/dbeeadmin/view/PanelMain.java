package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.Main;
import com.abelovagrupa.dbeeadmin.services.QueryExecutor;
import com.abelovagrupa.dbeeadmin.util.Pair;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
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

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PanelMain implements Initializable {

    // Main panel components
    @FXML
    SplitPane leftPane;

    @FXML
    SplitPane centerPane;

    @FXML
    SplitPane rightPane;

    PanelBrowser browserController;

    PanelInfo infoController;

    PanelEditor editorController;

    PanelResults resultsController;

    PanelHelp helpController;

    @FXML
    Button btnConnection;

    @FXML
    private SplitPane centralPanel;

    @FXML
    private VBox rightPanel;

    // Left panel components

    @FXML
    List<TreeView<String>> schemaViews;

//    @FXML
//    TreeView<String> treeView1;
//
//    @FXML
//    TreeView<String> treeView2;

    // Center panel components

    @FXML
    private TabPane editorPanel;

    @FXML
    private SplitPane resultsPanel;

    @FXML
    private TableView<ObservableList<String>>  resultsTable;

    @FXML
    CodeArea codeArea;

    @FXML
    ScrollPane historyPane;

    @FXML
    VBox resultContainer;

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

            loadBrowser();
            loadInfo();
            loadEditor();
            loadResults();
            loadHelp();

        // Left panel initialization
//        schemaViews = List.of(treeView1,treeView2);
//
//        for (TreeView<String> schemaView : schemaViews){
//
//            TreeItem<String> schema = new TreeItem<>("Schema",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database.png").toExternalForm())));
//            schemaView.setRoot(schema);
//            TreeItem<String> tableBranch = new TreeItem<>("Tables",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));
//            TreeItem<String> viewBranch = new TreeItem<>("Views",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));
//            TreeItem<String>  procedureBranch = new TreeItem<>("Stored Procedures",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));
//            TreeItem<String> functionBranch = new TreeItem<>("Functions",new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));
//
//            TreeItem<String> table1 = new TreeItem<>("Table 1");
//            TreeItem<String> table2 = new TreeItem<>("Table 2");
//
//            tableBranch.getChildren().addAll(table1,table2);
//            schema.getChildren().addAll(tableBranch,viewBranch,procedureBranch,functionBranch);
//
//            schemaView.setPrefHeight(24);
//            schemaView.getRoot().addEventHandler(TreeItem.branchExpandedEvent(), event -> {
//                Platform.runLater(() -> schemaView.setPrefHeight(schemaView.getExpandedItemCount() * 24));
//            });
//
//            schemaView.getRoot().addEventHandler(TreeItem.branchCollapsedEvent(), event -> {
//                Platform.runLater(() -> schemaView.setPrefHeight(schemaView.getExpandedItemCount() * 24));
//            });

//        }

        // Central panel initialization

//        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
//        codeArea.textProperty().addListener((_, _, newText) -> {
//            codeArea.setStyleSpans(0, computeHighlighting(newText));
//        });

    }

    public PanelBrowser getBrowserController() {
        return browserController;
    }

    public void setBrowserController(PanelBrowser browserController) {
        this.browserController = browserController;
    }

    public PanelInfo getInfoController() {
        return infoController;
    }

    public void setInfoController(PanelInfo infoController) {
        this.infoController = infoController;
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

    public PanelHelp getHelpController() {
        return helpController;
    }

    public void setHelpController(PanelHelp helpController) {
        this.helpController = helpController;
    }

    public void loadBrowser(){

        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("panelBrowser.fxml"));
            setBrowserController(fxmlLoader.getController());
            Parent root = fxmlLoader.load();
            leftPane.getItems().add(root);


        }catch(IOException e){
            throw new RuntimeException(e);
        }

    }

    public void loadInfo(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("panelInfo.fxml"));
            setInfoController(fxmlLoader.getController());
            Parent root = fxmlLoader.load();
            leftPane.getItems().add(root);

        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    public void loadEditor(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("panelEditor.fxml"));
            setEditorController(fxmlLoader.getController());
            Parent root = fxmlLoader.load();
            centerPane.getItems().add(root);

        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    public void loadResults(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("panelResults.fxml"));
            setResultsController(fxmlLoader.getController());
            Parent root = fxmlLoader.load();
            centerPane.getItems().add(root);

        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    public void loadHelp(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("panelHelp.fxml"));
            setHelpController(fxmlLoader.getController());
            Parent root = fxmlLoader.load();
            rightPane.getItems().add(root);

        }catch(IOException e){
            throw new RuntimeException(e);
        }
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

//    public void runScript(ActionEvent actionEvent) {
//        if(codeArea.getText() == null || codeArea.getText().isEmpty()) return;
//        Pair<ResultSet, Integer> result = QueryExecutor.executeQuery(codeArea.getText());
//        printHistory((result.getSecond() != null) ? result.getSecond() : 0, result.getFirst() != null);
//        try {
//            printResultSetToTable(result.getFirst());
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    // Miscellaneous and Other methods

//    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
//        Matcher matcher = PATTERN.matcher(text.toUpperCase());
//        int lastKwEnd = 0;
//        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
//        while (matcher.find()) {
//            String styleClass =
//                matcher.group("KEYWORD") != null ? "keyword" :
//                    matcher.group("STRING") != null ? "string" :
//                        matcher.group("NUMBER") != null ? "number" :
//                            matcher.group("COMMENT") != null ? "comment" :
//                                null;
//            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
//            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
//            lastKwEnd = matcher.end();
//        }
//        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
//        return spansBuilder.create();
//    }

//    // TODO: Think of a more appropriate name
//    private void printHistory(Integer rowsAffected, boolean isSelect) {
//        String text = (isSelect) ? "(DQL) SELECT RETURNED DATA " : "ROWS AFFECTED: ";
//        if(rowsAffected != null && rowsAffected != 0)
//            text = text + rowsAffected + " ";
//        text = text + "@ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
//        resultContainer.getChildren().add(new Label(text));
//    }

//    // TODO: Think of a more appropriate name
//    @SuppressWarnings({"unchecked"})
//    private void printResultSetToTable(ResultSet resultSet) throws SQLException {
//        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
//        int columnCount = resultSetMetaData.getColumnCount();
//
//        // Clear old columns
//        resultsTable.getColumns().clear();
//
//        // Dynamically create columns (I've spent WAY more time on this than I should, yet it still looks like shit)
//        for (int i = 1; i <= columnCount; i++) {
//            String columnName = resultSetMetaData.getColumnName(i);
//            TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnName);
//            int colIndex = i - 1; // Index of column in ResultSet (0-based)
//
//            // Set cell value factory to get the correct column data from each row in the ObservableList
//            column.setCellValueFactory(cellData -> {
//                return new SimpleStringProperty(cellData.getValue().get(colIndex));
//            });
//            resultsTable.getColumns().add(column);
//        }
//
//        // Loading data to this uncanny collection
//        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
//        while (resultSet.next()) {
//            ObservableList<String> row = FXCollections.observableArrayList();
//            for (int i = 1; i <= columnCount; i++) {
//                row.add(resultSet.getString(i));
//            }
//            data.add(row);
//        }
//        resultsTable.setItems(data);
//    }


    // TODO: MOVE!
    /**
     * Prints a ResultSet to the standard output. For testing purposes.
     * @param resultSet to be printed to std out
     */
//    public static void printResultSet(ResultSet resultSet) {
//        if(resultSet == null) return;
//        try {
//            // Get metadata about the ResultSet
//            ResultSetMetaData metaData = resultSet.getMetaData();
//            int columnCount = metaData.getColumnCount();
//
//            // Print column headers
//            for (int i = 1; i <= columnCount; i++) {
//                System.out.print(metaData.getColumnLabel(i) +"\t");
//            }
//            System.out.println();
//
//            // Print rows of data
//            while (resultSet.next()) {
//                for (int i = 1; i <= columnCount; i++) {
//                    System.out.print(resultSet.getString(i) + "\t");
//                }
//                System.out.println();
//            }
//        } catch (SQLException e) {
//            System.err.println("Error printing ResultSet: " + e.getMessage());
//        }
//    }

}
