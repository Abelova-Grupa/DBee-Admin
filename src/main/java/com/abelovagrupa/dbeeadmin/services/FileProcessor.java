package com.abelovagrupa.dbeeadmin.services;

import com.abelovagrupa.dbeeadmin.util.AlertManager;
import javafx.stage.FileChooser;

import java.io.*;

/**
 * Utility class responsible for importing and exporting files.
 */
public class FileProcessor {


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

}
