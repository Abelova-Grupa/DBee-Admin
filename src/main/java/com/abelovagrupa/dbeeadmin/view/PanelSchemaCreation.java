package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.controller.DatabaseInspector;
import com.abelovagrupa.dbeeadmin.model.schema.Charset;
import com.abelovagrupa.dbeeadmin.model.schema.Collation;

import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.services.DDLGenerator;
import com.abelovagrupa.dbeeadmin.util.AlertManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class PanelSchemaCreation implements Initializable {

    @FXML
    public ComboBox<Charset> cbCharset;

    @FXML
    public ComboBox<Collation> cbCollation;

    @FXML
    public Button btnPreview;

    @FXML
    public Button btnPersist;

    @FXML
    public ImageView createDBImage;

    @FXML
    public TextField schemaNameTxtField;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Charset> charsetObservableList = FXCollections.observableArrayList(Charset.values());
//        ObservableList<Collation> collationObservableList = FXCollections.observableArrayList(Collation.values());

        cbCharset.setItems(charsetObservableList);
        cbCollation.setDisable(true);
        cbCharset.valueProperty().addListener(new ChangeListener<Charset>() {
            @Override
            public void changed(ObservableValue<? extends Charset> observableValue, Charset oldValue, Charset newValue) {
                if(newValue != oldValue){
                    cbCollation.setDisable(false);
                    switch (newValue){
                        case DEFAULT -> {
                            cbCollation.getItems().clear();
                            cbCollation.getItems().add(Collation.DEFAULT);
                        }
                        case ARMSCII8 -> {
                            cbCollation.getItems().clear();
                            cbCollation.getItems().addAll(Collation.ARMSCII8_BIN,Collation.ARMSCII8_GENERAL_CI);
                        }
                    }
                }
            }
        });
//       cbCollation.setItems(collationObservableList);
        createDBImage.setImage(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/create-database.png").toExternalForm()));
    }

    @FXML
    private void generate() {
        if(schemaNameTxtField.getText().isBlank()){
            AlertManager.showErrorDialog("Error","Schema name cannot be empty",null);
            return;
        }

        if(schemaNameTxtField.getText().contains(" ")) {
            AlertManager.showErrorDialog("Error","Schema name cannot have a blank character",null);
            return;
        }
        if(Character.isDigit(schemaNameTxtField.getText().charAt(0))) {
            AlertManager.showErrorDialog("Error","Schema name cannot have digits at the beginning",null);
            return;
        }
        if(schemaNameTxtField.getText().matches(".*[^a-zA-Z0-9_].*")){
            AlertManager.showErrorDialog("Error","Schema name cannot have special characters",null);
            return;
        }

        if(cbCharset.getSelectionModel().getSelectedItem() == null){
            AlertManager.showErrorDialog("Error","Charset is not selected",null);
            return;
        }

        if(cbCollation.getSelectionModel().getSelectedItem() == null){
            AlertManager.showErrorDialog("Error","Collation is not selected",null);
            return;
        }

        String schemaName = schemaNameTxtField.getText();
        Charset charset = cbCharset.getSelectionModel().getSelectedItem();
        Collation collation = cbCollation.getSelectionModel().getSelectedItem();

        try {
            DDLGenerator.createDatabase(new Schema(schemaName,charset,collation,null,0,0L),true);
            AlertManager.showConfirmationDialog("Success","Schema created",null);
        } catch (SQLException e) {
            AlertManager.showErrorDialog("Error","Exception while creating schema",e.toString());
        }

    }
}
