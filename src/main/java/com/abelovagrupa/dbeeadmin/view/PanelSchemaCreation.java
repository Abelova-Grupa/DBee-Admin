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
                        case LATIN2 -> cbCollation.getItems().addAll(Collation.DEFAULT,Collation.LATIN2_BIN,Collation.LATIN2_GENERAL_CI,Collation.LATIN2_CROATIAN_CI,Collation.LATIN2_CZECH_CS,Collation.LATIN1_GERMAN2_CI,Collation.LATIN2_HUNGARIAN_CI);
                        case LATIN5 -> cbCollation.getItems().addAll(Collation.DEFAULT,Collation.LATIN5_BIN,Collation.LATIN5_TURKISH_CI);
                        case LATIN7 -> cbCollation.getItems().addAll(Collation.DEFAULT,Collation.LATIN7_BIN,Collation.LATIN7_ESTONIAN_CS,Collation.LATIN7_GENERAL_CI,Collation.LATIN7_GENERAL_CS);
                        case MACCE -> cbCollation.getItems().addAll(Collation.DEFAULT,Collation.MACCE_BIN,Collation.MACCE_GENERAL_CI);
                        case MACROMAN -> cbCollation.getItems().addAll(Collation.DEFAULT,Collation.MACROMAN_BIN,Collation.MACROMAN_GENERAL_CI);
                        case SJIS -> cbCollation.getItems().addAll(Collation.DEFAULT,Collation.SJIS_BIN,Collation.SJIS_JAPANESE_CI);
                        case SWE7 -> cbCollation.getItems().addAll(Collation.DEFAULT,Collation.SWE7_BIN,Collation.SWE7_SWEDISH_CI);
                        case TIS620 -> cbCollation.getItems().addAll(Collation.DEFAULT,Collation.TIS620_BIN,Collation.TIS620_THAI_CI);
                        case UCS2 -> cbCollation.getItems().addAll(
                                Collation.DEFAULT,
                                Collation.UCS2_BIN,
                                Collation.UCS2_CROATIAN_CI,
                                Collation.UCS2_CZECH_CI,
                                Collation.UCS2_DANISH_CI,
                                Collation.UCS2_ESPERANTO_CI,
                                Collation.UCS2_ESTONIAN_CI,
                                Collation.UCS2_GENERAL_CI,
                                Collation.UCS2_GENERAL_MYSQL500_CI,
                                Collation.UCS2_GERMAN2_CI,
                                Collation.UCS2_HUNGARIAN_CI,
                                Collation.UCS2_ICELANDIC_CI,
                                Collation.UCS2_LATVIAN_CI,
                                Collation.UCS2_LITHUANIAN_CI,
                                Collation.UCS2_PERSIAN_CI,
                                Collation.UCS2_POLISH_CI,
                                Collation.UCS2_ROMANIAN_CI,
                                Collation.UCS2_ROMAN_CI,
                                Collation.UCS2_SINHALA_CI,
                                Collation.UCS2_SLOVAK_CI,
                                Collation.UCS2_SLOVENIAN_CI,
                                Collation.UCS2_SPANISH2_CI,
                                Collation.UCS2_SPANISH_CI,
                                Collation.UCS2_SWEDISH_CI,
                                Collation.UCS2_TURKISH_CI,
                                Collation.UCS2_UNICODE_520_CI,
                                Collation.UCS2_UNICODE_CI
                        );
                        case UJIS -> cbCollation.getItems().addAll(Collation.DEFAULT,Collation.UJIS_BIN,Collation.UJIS_JAPANESE_CI);
                        case UTF16 -> cbCollation.getItems().addAll(
                                Collation.DEFAULT,
                                Collation.UTF16_BIN,
                                Collation.UTF16_CROATIAN_CI,
                                Collation.UTF16_CZECH_CI,
                                Collation.UTF16_DANISH_CI,
                                Collation.UTF16_ESPERANTO_CI,
                                Collation.UTF16_ESTONIAN_CI,
                                Collation.UTF16_GENERAL_CI,
                                Collation.UTF16_GERMAN2_CI,
                                Collation.UTF16_HUNGARIAN_CI,
                                Collation.UTF16_ICELANDIC_CI,
                                Collation.UTF16_LATVIAN_CI,
                                Collation.UTF16_LITHUANIAN_CI,
                                Collation.UTF16_PERSIAN_CI,
                                Collation.UTF16_POLISH_CI,
                                Collation.UTF16_ROMANIAN_CI,
                                Collation.UTF16_ROMAN_CI,
                                Collation.UTF16_SINHALA_CI,
                                Collation.UTF16_SLOVAK_CI,
                                Collation.UTF16_SLOVENIAN_CI,
                                Collation.UTF16_SPANISH2_CI,
                                Collation.UTF16_SPANISH_CI,
                                Collation.UTF16_SWEDISH_CI,
                                Collation.UTF16_TURKISH_CI,
                                Collation.UTF16_UNICODE_520_CI,
                                Collation.UTF16_UNICODE_CI
                        );
                        case UTF32 -> cbCollation.getItems().addAll(
                                Collation.DEFAULT,
                                Collation.UTF32_BIN,
                                Collation.UTF32_CROATIAN_CI,
                                Collation.UTF32_CZECH_CI,
                                Collation.UTF32_DANISH_CI,
                                Collation.UTF32_ESPERANTO_CI,
                                Collation.UTF32_ESTONIAN_CI,
                                Collation.UTF32_GENERAL_CI,
                                Collation.UTF32_GERMAN2_CI,
                                Collation.UTF32_HUNGARIAN_CI,
                                Collation.UTF32_ICELANDIC_CI,
                                Collation.UTF32_LATVIAN_CI,
                                Collation.UTF32_LITHUANIAN_CI,
                                Collation.UTF32_PERSIAN_CI,
                                Collation.UTF32_POLISH_CI,
                                Collation.UTF32_ROMANIAN_CI,
                                Collation.UTF32_ROMAN_CI,
                                Collation.UTF32_SINHALA_CI,
                                Collation.UTF32_SLOVAK_CI,
                                Collation.UTF32_SLOVENIAN_CI,
                                Collation.UTF32_SPANISH2_CI,
                                Collation.UTF32_SPANISH_CI,
                                Collation.UTF32_SWEDISH_CI,
                                Collation.UTF32_TURKISH_CI,
                                Collation.UTF32_UNICODE_520_CI,
                                Collation.UTF32_UNICODE_CI
                        );
                        case UTF8 -> cbCollation.getItems().addAll(
                                Collation.DEFAULT,
                                Collation.UTF8_BIN,
                                Collation.UTF8_CROATIAN_CI,
                                Collation.UTF8_CZECH_CI,
                                Collation.UTF8_DANISH_CI,
                                Collation.UTF8_ESPERANTO_CI,
                                Collation.UTF8_ESTONIAN_CI,
                                Collation.UTF8_GENERAL_CI,
                                Collation.UTF8_GENERAL_MYSQL500_CI,
                                Collation.UTF8_GERMAN2_CI,
                                Collation.UTF8_HUNGARIAN_CI,
                                Collation.UTF8_ICELANDIC_CI,
                                Collation.UTF8_LATVIAN_CI,
                                Collation.UTF8_LITHUANIAN_CI,
                                Collation.UTF8_PERSIAN_CI,
                                Collation.UTF8_POLISH_CI,
                                Collation.UTF8_ROMANIAN_CI,
                                Collation.UTF8_ROMAN_CI,
                                Collation.UTF8_SINHALA_CI,
                                Collation.UTF8_SLOVAK_CI,
                                Collation.UTF8_SLOVENIAN_CI,
                                Collation.UTF8_SPANISH2_CI,
                                Collation.UTF8_SPANISH_CI,
                                Collation.UTF8_SWEDISH_CI,
                                Collation.UTF8_TURKISH_CI,
                                Collation.UTF8_UNICODE_520_CI,
                                Collation.UTF8_UNICODE_CI
                        );
                        case UTF8MB4 -> cbCollation.getItems().addAll(
                                Collation.DEFAULT,
                                Collation.UTF8MB4_BIN,
                                Collation.UTF8MB4_CROATIAN_CI,
                                Collation.UTF8MB4_CZECH_CI,
                                Collation.UTF8MB4_DANISH_CI,
                                Collation.UTF8MB4_ESPERANTO_CI,
                                Collation.UTF8MB4_ESTONIAN_CI,
                                Collation.UTF8MB4_GENERAL_CI,
                                Collation.UTF8MB4_GERMAN2_CI,
                                Collation.UTF8MB4_GERMAN_CI,
                                Collation.UTF8MB4_HUNGARIAN_CI,
                                Collation.UTF8MB4_ICELANDIC_CI,
                                Collation.UTF8MB4_LATVIAN_CI,
                                Collation.UTF8MB4_LITHUANIAN_CI,
                                Collation.UTF8MB4_PERSIAN_CI,
                                Collation.UTF8MB4_POLISH_CI,
                                Collation.UTF8MB4_ROMANIAN_CI,
                                Collation.UTF8MB4_ROMAN_CI,
                                Collation.UTF8MB4_SINHALA_CI,
                                Collation.UTF8MB4_SLOVAK_CI,
                                Collation.UTF8MB4_SLOVENIAN_CI,
                                Collation.UTF8MB4_SPANISH2_CI,
                                Collation.UTF8MB4_SPANISH_CI,
                                Collation.UTF8MB4_SWEDISH_CI,
                                Collation.UTF8MB4_TURKISH_CI,
                                Collation.UTF8MB4_UNICODE_520_CI,
                                Collation.UTF8MB4_UNICODE_CI,
                                Collation.UTF8MB4_VIETNAMESE_CI,
                                Collation.UTF8MB4_0900_AI_CI,
                                Collation.UTF8MB4_0900_AS_CS,
                                Collation.UTF8MB4_0900_AS_CI,
                                Collation.UTF8MB4_0900_BIN,
                                Collation.UTF8MB4_0900_CROATIAN_CI,
                                Collation.UTF8MB4_0900_CZECH_CI,
                                Collation.UTF8MB4_0900_DANISH_CI,
                                Collation.UTF8MB4_0900_ESPERANTO_CI,
                                Collation.UTF8MB4_0900_ESTONIAN_CI,
                                Collation.UTF8MB4_0900_GERMAN2_CI,
                                Collation.UTF8MB4_0900_HUNGARIAN_CI,
                                Collation.UTF8MB4_0900_ICELANDIC_CI,
                                Collation.UTF8MB4_0900_JA_0900_AS_CS,
                                Collation.UTF8MB4_0900_LATVIAN_CI,
                                Collation.UTF8MB4_0900_LITHUANIAN_CI,
                                Collation.UTF8MB4_0900_POLISH_CI,
                                Collation.UTF8MB4_0900_ROMANIAN_CI,
                                Collation.UTF8MB4_0900_SINHALA_CI,
                                Collation.UTF8MB4_0900_SLOVAK_CI,
                                Collation.UTF8MB4_0900_SLOVENIAN_CI,
                                Collation.UTF8MB4_0900_SPANISH2_CI,
                                Collation.UTF8MB4_0900_SPANISH_CI,
                                Collation.UTF8MB4_0900_SWEDISH_CI,
                                Collation.UTF8MB4_0900_TURKISH_CI,
                                Collation.UTF8MB4_0900_VIETNAMESE_CI
                        );



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
