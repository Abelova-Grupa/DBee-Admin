package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.column.DataType;
import com.abelovagrupa.dbeeadmin.model.table.Table;
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

    public TextField txtName;
    public ComboBox<DataType> cbDataTypes;
    public TextField txtSize;
    public CheckBox cbZerofill;
    public CheckBox cbPrimary;
    public CheckBox cbNotNull;
    public CheckBox cbAutoIncrement;
    public CheckBox cbUnique;
    public TextField txtDefault;
    public TextField txtComment;
    public FontIcon btnDelete;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<DataType> datatypes = FXCollections.observableArrayList(DataType.values());
        cbDataTypes.setItems(datatypes);
    }

    public Column getColumnString(Table table) {
        return new Column.ColumnBuilder(txtName.getText(), cbDataTypes.getValue(), table)
            .setPrimaryKey(cbPrimary.isSelected())
            .setSize(txtSize.getText().isEmpty() ? null : Integer.parseInt(txtSize.getText()))
            .setZeroFill(cbZerofill.isSelected())
            .setNotNull(cbNotNull.isSelected())
            .setAutoIncrement(cbAutoIncrement.isSelected())
            .setUnique(cbUnique.isSelected())
            .setDefaultValue(txtDefault.getText().isEmpty() ? null : txtDefault.getText())
            .setComment(txtComment.getText().isEmpty() ? null : txtComment.getText())
            .build();
    }


}
