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
                    cbCollation.getItems().clear();
                    switch (newValue){
                        case DEFAULT -> {
                            cbCollation.getItems().add(Collation.DEFAULT);
                        }
                        case ARMSCII8 -> {
                            cbCollation.getItems().addAll(Collation.DEFAULT,Collation.ARMSCII8_BIN,Collation.ARMSCII8_GENERAL_CI);
                        }
                        case ASCII -> {
                            cbCollation.getItems().addAll(Collation.DEFAULT,Collation.ASCII_BIN,Collation.ASCII_GENERAL_CI);
                        }
                        case BIG5 -> {
                            cbCollation.getItems().clear();
                            cbCollation.getItems().addAll(Collation.DEFAULT,Collation.BIG5_BIN,Collation.BIG5_CHINESE_CI);
                        }
                        case BINARY -> {
                            cbCollation.getItems().addAll(Collation.DEFAULT,Collation.BINARY);
                        }
                        case CP1250 -> {
                            cbCollation.getItems().addAll(Collation.DEFAULT,
                                    Collation.CP1250_BIN,
                                    Collation.CP1250_CROATIAN_CI,
                                    Collation.CP1250_CZECH_CS,
                                    Collation.CP1250_GENERAL_CI);
                        }
                        case CP1251 -> {
                            cbCollation.getItems().addAll(Collation.DEFAULT,
                                    Collation.CP1251_BIN,
                                    Collation.CP1251_BULGARIAN_CI,Collation.CP1251_GENERAL_CI,
                                    Collation.CP1251_GENERAL_CS, Collation.CP1251_UKRAINIAN_CI);
                        }
                        case CP1256 -> {
                            cbCollation.getItems().addAll(Collation.DEFAULT,Collation.CP1256_BIN,Collation.CP1256_GENERAL_CI);
                        }
                        case CP1257 -> {
                            cbCollation.getItems().addAll(Collation.DEFAULT, Collation.CP1257_BIN,Collation.CP1257_GENERAL_CI,Collation.CP1257_LITHUANIAN_CI);
                        }
                        case CP850 -> {
                            cbCollation.getItems().addAll(Collation.DEFAULT,Collation.CP850_BIN,Collation.CP850_GENERAL_CI);
                        }
                        case CP852 -> {
                            cbCollation.getItems().addAll(Collation.DEFAULT,Collation.CP852_BIN,Collation.CP852_GENERAL_CI);
                        }
                        case CP866 -> {
                            cbCollation.getItems().addAll(Collation.DEFAULT,Collation.CP866_BIN,Collation.CP866_GENERAL_CI);
                        }
                        case CP932 -> {
                            cbCollation.getItems().clear();
                            cbCollation.getItems().addAll(Collation.DEFAULT,Collation.CP932_BIN,Collation.CP932_JAPANESE_CI);
                        }
                        case DEC8 -> {
                            cbCollation.getItems().addAll(Collation.DEFAULT,Collation.DEC8_BIN,Collation.DEC8_SWEDISH_CI);
                        }
                        case EUCJPMS -> {
                            cbCollation.getItems().addAll(Collation.DEFAULT,Collation.EUCJPMS_BIN,Collation.EUCJPMS_JAPANESE_CI);
                        }
                        case EUCKR -> {
                            cbCollation.getItems().addAll(Collation.DEFAULT,Collation.EUCKR_BIN,Collation.EUCKR_KOREAN_CI);
                        }
                        case GB18030 -> {
                            cbCollation.getItems().addAll(Collation.DEFAULT,Collation.GB18030_BIN,Collation.GB18030_CHINESE_CI);
                        }
                        case GB2312 -> {
                            cbCollation.getItems().addAll(Collation.DEFAULT,Collation.GB2312_BIN,Collation.GB2312_CHINESE_CI);
                        }
                        case GBK -> cbCollation.getItems().addAll(Collation.DEFAULT,Collation.GBK_BIN,Collation.GBK_CHINESE_CI);
                        case GEOSTD8 -> cbCollation.getItems().addAll(Collation.DEFAULT,Collation.GEOSTD8_BIN,Collation.GEOSTD8_GENERAL_CI);
                        case GREEK -> cbCollation.getItems().addAll(Collation.DEFAULT,Collation.GREEK_BIN,Collation.GREEK_GENERAL_CI);
                        case HEBREW -> cbCollation.getItems().addAll(Collation.DEFAULT,Collation.HEBREW_BIN,Collation.HEBREW_GENERAL_CI);
                        case HP8 -> cbCollation.getItems().addAll(Collation.DEFAULT,Collation.HP8_BIN,Collation.HP8_ENGLISH_CI);
                        case KEYBCS2 -> cbCollation.getItems().addAll(Collation.DEFAULT,Collation.KEYBCS2_BIN,Collation.KEYBCS2_GENERAL_CI);
                        case KOI8R ->  cbCollation.getItems().addAll(Collation.DEFAULT,Collation.KOI8R_BIN,Collation.KOI8R_GENERAL_CI);
                        case KOI8U -> cbCollation.getItems().addAll(Collation.DEFAULT,Collation.KOI8U_BIN,Collation.KOI8U_GENERAL_CI);
                        case LATIN1 -> cbCollation.getItems().addAll(Collation.DEFAULT,Collation.LATIN1_BIN,Collation.LATIN1_GENERAL_CI,Collation.LATIN1_GENERAL_CS,Collation.LATIN1_GERMAN1_CI,Collation.LATIN1_GERMAN2_CI,Collation.LATIN1_DANISH_CI,Collation.LATIN1_SPANISH_CI,Collation.LATIN1_SWEDISH_CI);

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
