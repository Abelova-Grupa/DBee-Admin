package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.controller.DatabaseInspector;
import com.abelovagrupa.dbeeadmin.model.view.Algorithm;
import com.abelovagrupa.dbeeadmin.model.view.View;
import com.abelovagrupa.dbeeadmin.services.DDLGenerator;
import com.abelovagrupa.dbeeadmin.util.AlertManager;
import com.abelovagrupa.dbeeadmin.util.SyntaxHighlighter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class PanelViewCreation implements Initializable {

    @FXML
    public CodeArea codeArea;

    // Might change to ComboBox<Schema> but schema fetching is too expensive.
    @FXML
    public ComboBox<String> cbSchema;

    @FXML
    public TextField txtName;

    @FXML
    public ComboBox<Algorithm> cbAlgorithm;

    @FXML
    public Button btnPreview;

    @FXML
    public Button btnPersist;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initComboBoxes();

        // Setup the code area
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.textProperty().addListener((_, _, newText) -> {
            codeArea.setStyleSpans(0, SyntaxHighlighter.computeHighlighting(newText));
        });

        codeArea.appendText("-- Compose the DQL statements for the creation of the view here:\n");
    }

    private void initComboBoxes() {
        cbAlgorithm.getItems().addAll(Algorithm.values());

        // Resource heavy!
        cbSchema.getItems().addAll(DatabaseInspector.getInstance().getDatabaseNames());
    }

    public void preview(ActionEvent actionEvent) {
        if(!validate()) return;
        View view = new View(DatabaseInspector
            .getInstance()
            .getDatabaseByName(cbSchema.getSelectionModel().getSelectedItem()),
            txtName.getText(),
            codeArea.getText());
        DDLGenerator.createView(view, cbAlgorithm.getSelectionModel().getSelectedItem(), true);
    }

    public void persist(ActionEvent actionEvent) {
        if(!validate()) return;
        View view = new View(DatabaseInspector
            .getInstance()
            .getDatabaseByName(cbSchema.getSelectionModel().getSelectedItem()),
            txtName.getText(),
            codeArea.getText());
        DDLGenerator.createView(view, cbAlgorithm.getSelectionModel().getSelectedItem(), false);
    }

    private boolean validate() {
        if(cbSchema.getSelectionModel().getSelectedItem() == null){
            AlertManager.showErrorDialog(null, null, "Schema must be selected.");
            return false;
        }
        if(cbAlgorithm.getSelectionModel().getSelectedItem() == null) {
            AlertManager.showErrorDialog(null, null, "Algorithm must be selected.");
            return false;
        }
        if(Objects.equals(txtName.getText(), "")) {
            AlertManager.showErrorDialog(null, null, "View name must be set.");
            return false;
        }
        if(Objects.equals(codeArea.getText(), "")) {
            AlertManager.showErrorDialog(null, null, "View definition must be set.");
            return false;
        }
        return true;
    }
}
