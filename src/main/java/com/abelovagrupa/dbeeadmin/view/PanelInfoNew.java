package com.abelovagrupa.dbeeadmin.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.ResourceBundle;

public class PanelInfoNew implements Initializable {

    @FXML
    public VBox infoContent;

    /**
     * Adds a key-value row to the info panel.
     * @param key String key
     * @param value String value
     * @param title If title is set to true, font will be larger, value will be bold, and separator will be added.
     *              To be used only for title i.e. for "SELECTED TABLE: TABLE_NAME"
     * @param arguments String varargs for primary (PRIMARY_KEY), foreign (FOREIGN_KEY) and unique (UNIQUE) keys.
     *                  Adds an icon for easier to read display.
     */
    public void addProperty(String key, String value, boolean title, String... arguments)
    {
        HBox propertyContainer = new HBox();
        Label keyLabel = new Label(key);
        Label valueLabel = new Label(value);

        // TODO: Add icons for primary key, foreign key, and unique value
        for(var a : arguments) {
            if(a.equals("PRIMARY_KEY")) {
                keyLabel.setText(keyLabel.getText() + " (\uD83D\uDD11)");
            }

            if(a.equals("FOREIGN_KEY")) {
                keyLabel.setText(keyLabel.getText() + " (FK)");
            }

            if(a.equals("UNIQUE")) {
                keyLabel.setText(keyLabel.getText() + " (U)");
            }
        }

        // If title is selected, make fonts bigger; To be used for 'Selected table: <table_name>'
        if(title) {
            keyLabel.setFont(Font.font("System", 15));
            valueLabel.setFont(Font.font("System", FontWeight.BOLD, 15));
        }

        // Empty panel used to separate key and value evenly.
        Pane filler = new Pane();
        HBox.setHgrow(filler, Priority.ALWAYS);


        // Add key, separator and then value
        propertyContainer.getChildren().addAll(keyLabel, filler, valueLabel);

        // Add property to the info panels content vbox.
        infoContent.getChildren().add(propertyContainer);

        // Purely aesthetic; Adds a separator if the title is selected
        if(title) {
            Separator separator = new Separator();
            separator.setOrientation(Orientation.HORIZONTAL);
            infoContent.getChildren().add(separator);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
