package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.model.trigger.Event;
import com.abelovagrupa.dbeeadmin.model.trigger.Timing;
import com.abelovagrupa.dbeeadmin.model.trigger.Trigger;
import com.abelovagrupa.dbeeadmin.util.AlertManager;
import com.abelovagrupa.dbeeadmin.util.SyntaxHighlighter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

// TODO: Add deselect functionality sometime in the future.
public class PanelTriggerTab implements Initializable {

    @FXML
    public TextField txtName;

    @FXML
    public ComboBox<Timing> cbTiming;

    @FXML
    public ComboBox<Event> cbAction;

    @FXML
    public CodeArea codeArea;

    @FXML
    public ListView<Trigger> triggerListView;

    @FXML
    public Button btnAdd;

    @FXML
    public TextField txtDescription;

    @FXML
    public CheckBox cbEnabled;

    @FXML
    public Button btnDelete;

    @FXML
    public Button btnSave;

    public Trigger selectedTrigger;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initComboBoxes();

        // Setup the code area
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.textProperty().addListener((_, _, newText) -> {
            codeArea.setStyleSpans(0, SyntaxHighlighter.computeHighlighting(newText));
        });

        codeArea.appendText("-- Define trigger statements here:\n");

    }

    private void initComboBoxes() {
        cbAction.getItems().addAll(Event.values());
        cbTiming.getItems().addAll(Timing.values());
    }

    public void addBlankTrigger() {

        for (var t : triggerListView.getItems())
            if(t.getEvent() == null || t.getTiming() == null) {
                AlertManager.showErrorDialog(null,
                    "Cannot add a trigger.",
                    "A blank trigger already exists. Please save it or discard any changes to add a new one.");
                return;
            }

        triggerListView.getItems().add(
            new Trigger("New trigger",
            null,
            null,
            null,
            null,
            LocalDateTime.now(),
            true,
            null,
            null)
        );

        triggerListView.getSelectionModel().select(triggerListView.getItems().size() - 1);
        setSelectedTrigger();
    }

    public void setSelectedTrigger() {
        selectedTrigger = triggerListView.getSelectionModel().getSelectedItem();
        loadTriggerToEditor(selectedTrigger);
    }

    private void loadTriggerToEditor(Trigger trigger) {
        txtName.setText(trigger.getName());
        txtDescription.setText(trigger.getTriggerDescription());
        cbEnabled.setSelected(trigger.isEnabled());
        codeArea.clear();
        codeArea.appendText(
            trigger.getStatement() == null ? "-- Define trigger statements here:" : trigger.getStatement()
        );
        if (trigger.getTiming() != null) {
            cbTiming.getSelectionModel().select(trigger.getTiming());
        } else cbTiming.setValue(null);
        if (trigger.getEvent() != null) {
            cbAction.getSelectionModel().select(trigger.getEvent());
        } else cbAction.setValue(null);
    }

    public void saveTrigger(ActionEvent actionEvent) {

        // Validate
        if (!isTriggerDataValid()) return;

        int selectedIndex = triggerListView.getSelectionModel().getSelectedIndex();

        Trigger trigger = new Trigger(txtName.getText(),
            cbAction.getValue(),
            cbTiming.getValue(),
            null,
            txtDescription.getText(),
            LocalDateTime.now(),
            cbEnabled.isSelected(),
            null,
            codeArea.getText());

        if(selectedIndex < 0)
            triggerListView.getItems().add(trigger);
        else
            triggerListView.getItems().set(selectedIndex, trigger);
    }

    private boolean isTriggerDataValid() {
        if (txtName.getText() == null || txtName.getText().isEmpty()) {
            AlertManager.showErrorDialog("Couldn't save Trigger", null, "Trigger name must be set.");
            return false;
        }
        if (cbTiming.getSelectionModel().getSelectedItem() == null) {
            AlertManager.showErrorDialog("Couldn't save Trigger", null, "Timing must be set.");
            return false;
        }
        if (cbAction.getSelectionModel().getSelectedItem() == null) {
            AlertManager.showErrorDialog("Couldn't save Trigger", null, "Event must be set.");
            return false;
        }
        return true;
    }

    /**
     * Method that will return a list of all the triggers present in Trigger creation tab.
     * @return trigger list
     */
    public List<Trigger> getTriggerList() {
        return triggerListView.getItems().stream().toList();
    }

    public void deleteTrigger(ActionEvent actionEvent) {
        triggerListView.getItems().remove(selectedTrigger);
        selectedTrigger = null;
    }
}
