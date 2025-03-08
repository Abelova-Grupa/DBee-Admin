package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.model.schema.Charset;
import com.abelovagrupa.dbeeadmin.model.schema.Collation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

public class PanelSchemaCreation implements Initializable {

    @FXML
    public ComboBox<Charset> cbCharset;

    @FXML
    public ComboBox<Collation> cbCollation;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Charset> charsetObservableList = FXCollections.observableArrayList(Charset.values());
        ObservableList<Collation> collationObservableList = FXCollections.observableArrayList(Collation.values());

        cbCharset.setItems(charsetObservableList);
        cbCollation.setItems(collationObservableList);
    }
}
