package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.model.column.DataType;
import com.abelovagrupa.dbeeadmin.model.table.DBEngine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class PanelColumnTab implements Initializable {

    public TextField fxName;
    public ComboBox<DataType> cbDataTypes;
    public TextField txtSize;
    public CheckBox cbZerofill;
    public CheckBox cbPrimary;
    public CheckBox cbNotNull;
    public CheckBox cbAutoIncrement;
    public CheckBox cbUnique;
    public TextField txtDefault;
    public TextField cbComment;
    public FontIcon btnDelete;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<DataType> datatypes = FXCollections.observableArrayList(DataType.values());
        cbDataTypes.setItems(datatypes);
    }
}
