package com.abelovagrupa.dbeeadmin.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class PanelResults {

    private PanelMain mainController;

    @FXML
    private TableView<ObservableList<String>> resultsTable;

    @FXML
    VBox resultContainer;

    public PanelMain getMainController() {
        return mainController;
    }

    public void setMainController(PanelMain mainController) {
        this.mainController = mainController;
    }

    public TableView<ObservableList<String>> getResultsTable() {
        return resultsTable;
    }

    public void setResultsTable(TableView<ObservableList<String>> resultsTable) {
        this.resultsTable = resultsTable;
    }

    public VBox getResultContainer() {
        return resultContainer;
    }

    public void setResultContainer(VBox resultContainer) {
        this.resultContainer = resultContainer;
    }

    // TODO: Think of a more appropriate name
    @SuppressWarnings({"unchecked"})
    public void printResultSetToTable(ResultSet resultSet) throws SQLException {
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();

        // Clear old columns
        resultsTable.getColumns().clear();

        // Dynamically create columns (I've spent WAY more time on this than I should, yet it still looks like shit)
        for (int i = 1; i <= columnCount; i++) {
            String columnName = resultSetMetaData.getColumnName(i);
            TableColumn<ObservableList<String>, String> column = new TableColumn<>(columnName);
            int colIndex = i - 1; // Index of column in ResultSet (0-based)

            // Set cell value factory to get the correct column data from each row in the ObservableList
            column.setCellValueFactory(cellData -> {
                return new SimpleStringProperty(cellData.getValue().get(colIndex));
            });
            resultsTable.getColumns().add(column);
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
        resultsTable.setItems(data);
    }

}
