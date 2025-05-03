package com.abelovagrupa.dbeeadmin.services;

import com.abelovagrupa.dbeeadmin.util.AlertManager;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/**
 * Utility class responsible for importing and exporting files.
 */
public class FileProcessor {
    
    public static final Logger logger = LogManager.getRootLogger();
    
    /**
     * Method that provides user with a file chooser dialog and reads SQL from the chosen file.
     * @return SQL code read from the file.
     */
    public static String importSQL() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open SQL file.");

        // Set the file extension filters
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("SQL File", "*.sql");
        FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All Files", "*.*");
        fileChooser.getExtensionFilters().addAll(extFilter, allFilter);

        File file = fileChooser.showOpenDialog(null);

        return readFile(file);
    }

    /**
     * Method that provides user with a file chooser dialog and saves SQL to the chosen file.
     * @param sql SQL code to be saved.
     */
    public static void exportSQL(String sql) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save SQL File");
        fileChooser.setInitialFileName("my_script.sql");

        // Set the file extension filters
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("SQL File", "*.sql");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show the Save File dialog
        File file = fileChooser.showSaveDialog(null);

        // If a file is selected, save the content
        if (file != null) {
            saveTextToFile(sql, file);
        }
    }

    private static void saveTextToFile(String content, File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
            AlertManager.showInformationDialog(null, null, "File saved successfully.");
        } catch (IOException e) {
            AlertManager.showErrorDialog(null, null, "Error saving file: " + e.getMessage());
        }
    }

    private static String readFile(File file) {

        if(file == null) return null;

        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            AlertManager.showErrorDialog(null, null, "Error reading file: " + e.getMessage());
        }
        return content.toString();
    }

    public static void saveTableToCSV(TableView<ObservableList<String>> tableView) {
        // Create a FileChooser to select the file path
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("output.csv");
        File selectedFile = fileChooser.showSaveDialog(null);

        if (selectedFile != null) {
            // If a file was selected, save the TableView data to CSV
            saveTableViewToCSV(tableView, selectedFile.getAbsolutePath());
        } else {
            logger.error("No file selected.");
        }
    }

    // Method to save data to CSV (same as before)
    private static void saveTableViewToCSV(TableView<ObservableList<String>> tableView, String filePath) {
        ObservableList<ObservableList<String>> items = tableView.getItems();
        StringBuilder csvContent = new StringBuilder();

        // Write the header (column names)
        for (TableColumn<?, ?> column : tableView.getColumns()) {
            csvContent.append(column.getText()).append(",");
        }
        csvContent.setLength(csvContent.length() - 1);  // Remove trailing comma
        csvContent.append("\n");

        // Write rows
        for (ObservableList<String> item : items) {
            for (String value : item) {
                csvContent.append(value != null ? value : "").append(",");
            }
            csvContent.setLength(csvContent.length() - 1);  // Remove trailing comma
            csvContent.append("\n");
        }

        // Write the content to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(csvContent.toString());
        } catch (IOException e) {
            logger.error("Error saving the CSV file.");
        }
    }

}
