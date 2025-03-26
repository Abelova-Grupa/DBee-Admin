package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.model.foreignkey.Action;
import com.abelovagrupa.dbeeadmin.model.trigger.Event;
import com.abelovagrupa.dbeeadmin.model.trigger.Timing;
import com.abelovagrupa.dbeeadmin.model.trigger.Trigger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public List<Trigger> triggerList;
    public CheckBox cbEnabled;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initComboBoxes();

        // Setup the code area
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.textProperty().addListener((_, _, newText) -> {
            codeArea.setStyleSpans(0, computeHighlighting(newText));
        });

        codeArea.appendText("-- Define trigger statements here:\n");


        // Initialize trigger list
        triggerList = new LinkedList<>();
    }

    private void initComboBoxes() {
        cbAction.getItems().addAll(Event.values());
        cbTiming.getItems().addAll(Timing.values());
    }

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", PanelScript.SQL_KEYWORDS) + ")\\b";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String NUMBER_PATTERN = "\\b\\d+\\b";
    private static final String COMMENT_PATTERN = "--[^\n]*" + "|" + "/\\*[^\\*]*\\*/";

    private static final Pattern PATTERN = Pattern.compile(
        "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text.toUpperCase());
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("STRING") != null ? "string" :
                        matcher.group("NUMBER") != null ? "number" :
                            matcher.group("COMMENT") != null ? "comment" :
                                null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

//    public void addTrigger() {
//
//        // Validate here
//
//        triggerList.add(
//            new Trigger(txtName.getText(),
//            cbAction.getValue(),
//            cbTiming.getValue(),
//            null,
//            txtDescription.getText(),
//            LocalDateTime.now(),
//            cbEnabled.isSelected(),
//            null,
//            codeArea.getText())
//        );
//
//        refreshTriggerList();
//    }

    private void refreshTriggerList() {
        triggerListView.getItems().clear();
        for(var t : triggerList) {
            triggerListView.getItems().add(t);
        }
    }

}
