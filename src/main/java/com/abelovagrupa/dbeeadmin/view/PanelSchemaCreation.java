package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.column.DataType;
import com.abelovagrupa.dbeeadmin.model.schema.Charset;
import com.abelovagrupa.dbeeadmin.model.schema.Collation;
import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.model.table.Table;
import com.abelovagrupa.dbeeadmin.services.DDLGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Charset> charsetObservableList = FXCollections.observableArrayList(Charset.values());
        ObservableList<Collation> collationObservableList = FXCollections.observableArrayList(Collation.values());

        cbCharset.setItems(charsetObservableList);
        cbCollation.setItems(collationObservableList);
        createDBImage.setImage(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/create-database.png").toExternalForm()));
    }
}
