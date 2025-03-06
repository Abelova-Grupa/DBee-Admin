package com.abelovagrupa.dbeeadmin.services;

import com.abelovagrupa.dbeeadmin.util.AlertManager;
import javafx.stage.FileChooser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileExporter {


    public static void exportSQL(String sql) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save SQL File");
        fileChooser.setInitialFileName("my_script.sql");

        // Set the file extension filters
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("SQL File", ".sql");
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

}
