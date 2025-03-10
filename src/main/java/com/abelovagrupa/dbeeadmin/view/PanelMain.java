package com.abelovagrupa.dbeeadmin.view;
import com.abelovagrupa.dbeeadmin.Main;
import com.abelovagrupa.dbeeadmin.services.FileExporter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PanelMain implements Initializable {

    @FXML
    SplitPane leftPane;

    @FXML
    SplitPane centerPane;

    @FXML
    SplitPane rightPane;

    PanelBrowser browserController;

    PanelInfo infoController;

    PanelEditor editorController;

    PanelResults resultsController;

    PanelHelp helpController;

    @FXML
    Button btnConnection;

    @FXML
    Button btnNewScript;

    @FXML
    Button btnNewTable;

    // TODO: Fix format issues with HelpPanel(Vbox)
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
            loadBrowser();
            loadInfo();
            loadEditor();
            loadResults();
            loadHelp();
            linkControllers();
    }

    public PanelBrowser getBrowserController() {
        return browserController;
    }

    public void setBrowserController(PanelBrowser browserController) {
        this.browserController = browserController;
    }

    public PanelInfo getInfoController() {
        return infoController;
    }

    public void setInfoController(PanelInfo infoController) {
        this.infoController = infoController;
    }

    public PanelEditor getEditorController() {
        return editorController;
    }

    public void setEditorController(PanelEditor editorController) {
        this.editorController = editorController;
    }

    public PanelResults getResultsController() {
        return resultsController;
    }

    public void setResultsController(PanelResults resultsController) {
        this.resultsController = resultsController;
    }

    public PanelHelp getHelpController() {
        return helpController;
    }

    public void setHelpController(PanelHelp helpController) {
        this.helpController = helpController;
    }

    public void loadBrowser(){

        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("panelBrowser.fxml"));
            Parent root = fxmlLoader.load();
            setBrowserController(fxmlLoader.getController());
            leftPane.getItems().add(root);
            browserController.setMainController(this);


        }catch(IOException e){
            throw new RuntimeException(e);
        }

    }

    public void loadInfo(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("panelInfo.fxml"));
            Parent root = fxmlLoader.load();
            setInfoController(fxmlLoader.getController());
            leftPane.getItems().add(root);
            infoController.setMainController(this);

        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }
    /**
     * Each load method first loads the fxml file with its root component and then extracts its controller
     * Sets the reference to main controller in the created controller
     */
    public void loadEditor(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("panelEditor.fxml"));
            Parent root = fxmlLoader.load();
            setEditorController(fxmlLoader.getController());
            centerPane.getItems().add(root);
            editorController.setMainController(this);

        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    public void loadResults(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("panelResults.fxml"));
            Parent root = fxmlLoader.load();
            setResultsController(fxmlLoader.getController());
            centerPane.getItems().add(root);
            resultsController.setMainController(this);

        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    public void loadHelp(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("panelHelp.fxml"));
            Parent root = fxmlLoader.load();
            setHelpController(fxmlLoader.getController());
            rightPane.getItems().add(root);
            helpController.setMainController(this);
            rightPane.setDividerPositions(0.15, 0.9);

        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * Links controllers with each other.
     * MainController is used to create this bond
     * Each Controller has the reference to main and other needed controllers
     */
    private void linkControllers() {
        // Add needed controller relations
        editorController.setResultsController(resultsController);
        editorController.getScriptController().setResultsController(resultsController);

        browserController.setInfoController(infoController);
        for(PanelSchemaTree schemaTree: browserController.getSchemaControllers()){
            schemaTree.setBrowserController(browserController);
        }

    }

    // Event handling methods

    public void newTableTab() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("panelTableCreation.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setTitle("DBee Admin - New Table");
        stage.setScene(scene);

        stage.setOnShown(event -> {
            // Get the screen's bounds (width and height)
            double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
            double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

            // Get the stage width and height
            double stageWidth = stage.getWidth();
            double stageHeight = stage.getHeight();

            // Calculate the center position
            stage.setX((screenWidth - stageWidth) / 2);
            stage.setY((screenHeight - stageHeight) / 2);
        });
        ;
        stage.show();
    }

    public void newSchemaTab(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("panelSchemaCreation.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setTitle("DBee Admin - New Schema");
        stage.setScene(scene);

        stage.setOnShown(event -> {
            // Get the screen's bounds (width and height)
            double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
            double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

            // Get the stage width and height
            double stageWidth = stage.getWidth();
            double stageHeight = stage.getHeight();

            // Calculate the center position
            stage.setX((screenWidth - stageWidth) / 2);
            stage.setY((screenHeight - stageHeight) / 2);
        });
        ;
        stage.show();
    }

    public void openConnectionSettings(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("panelConnection.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.setTitle("DBee Admin - Connection Settings");
        stage.setScene(scene);

        stage.setOnShown(event -> {
            // Get the screen's bounds (width and height)
            double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
            double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

            // Get the stage width and height
            double stageWidth = stage.getWidth();
            double stageHeight = stage.getHeight();

            // Calculate the center position
            stage.setX((screenWidth - stageWidth) / 2);
            stage.setY((screenHeight - stageHeight) / 2);
        });
        ;
        stage.show();
    }

    public void newScriptTab(){
        editorController.setResultsController(resultsController);
        editorController.createNewScript();
    }


}
