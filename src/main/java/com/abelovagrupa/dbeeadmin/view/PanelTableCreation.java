package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.Main;
import com.abelovagrupa.dbeeadmin.controller.DatabaseInspector;

import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.foreignkey.ForeignKey;
import com.abelovagrupa.dbeeadmin.model.foreignkey.ForeignKeyColumns;
import com.abelovagrupa.dbeeadmin.model.index.Index;
import com.abelovagrupa.dbeeadmin.model.index.IndexedColumn;
import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.model.table.Table;
import com.abelovagrupa.dbeeadmin.services.DDLGenerator;
import com.abelovagrupa.dbeeadmin.services.ProgramState;
import com.abelovagrupa.dbeeadmin.util.AlertManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PanelTableCreation implements Initializable {

    @FXML
    ComboBox<String> cbSchema;

    @FXML
    TextField txtTableName;

    @FXML
    TabPane tableAttributeTabPane;

    @FXML
    Button applyBtn;

    @FXML
    Button revertBtn;

    Tab columnsTab;

    Tab indexTab;

    Tab foreignKeyTab;

    Tab triggerTab;
    //Tab Controllers
    PanelColumnTab columTabController;

    PanelIndexTab indexTabController;

    PanelFKTab foreignKeyTabController;

    PanelTriggerTab triggerTabController;
    // Other controllers
    PanelBrowser browserController;

    ObservableList<IndexedColumn> indexedColumns = FXCollections.observableArrayList();

    boolean[] tabLoaded;

    public PanelBrowser getBrowserController() {
        return browserController;
    }

    public void setBrowserController(PanelBrowser browserController) {
        this.browserController = browserController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // TabLoaded array indicates if the certain tabs of tableAttributeTabPane tabPane are loaded
            tabLoaded = new boolean[]{true,true,false,false};
            // Initialize column tab
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("panelColumnTab.fxml"));
            VBox columnsTabContent = loader.load();

            columTabController = loader.getController();
            columnsTab = new Tab("Columns");
            columnsTab.setClosable(false);

            columnsTab.setContent(columnsTabContent);
            tableAttributeTabPane.getTabs().add(columnsTab);
            tableAttributeTabPane.getSelectionModel().select(columnsTab);

            // Initialize index tab
            indexTab = new Tab("Indexes");
            indexTab.setClosable(false);
            tableAttributeTabPane.getTabs().add(indexTab);

            // Loading index tab content
            FXMLLoader indexLoader = new FXMLLoader(Main.class.getResource("panelIndexTab.fxml"));
            VBox indexTabContent = indexLoader.load();
            indexTabController = indexLoader.getController();
            indexTab.setContent(indexTabContent);

            // Initialize foreign key tab
            foreignKeyTab = new Tab("Foreign Keys");
            foreignKeyTab.setClosable(false);
            tableAttributeTabPane.getTabs().add(foreignKeyTab);

            // Initialize trigger tab
            triggerTab = new Tab("Triggers");
            triggerTab.setClosable(false);
            tableAttributeTabPane.getTabs().add(triggerTab);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Implementing lazy loading, tabs other than column tab will load its content
        // only if they are selected for the first time
        tableAttributeTabPane.getSelectionModel().selectedItemProperty().addListener(
                (_, _, newTab) -> {
                    // If selected tab is index tab load all columns from column tab
                    if(!newTab.equals(indexTab)) return;
                    indexedColumns.clear();
                    for (Column column : columTabController.columnsData){
                        IndexedColumn indexedColumn = new IndexedColumn();
                        indexedColumn.setColumn(column);
                        indexedColumns.add(indexedColumn);
                    }
                    indexTabController.indexedColumnData.setAll(indexedColumns);
                });
        // Implementing lazy loading of the foreign key tab on first selection
        tableAttributeTabPane.getSelectionModel().selectedItemProperty().addListener(
                (_, _, newTab) -> {
                    if(!newTab.equals(foreignKeyTab)) return;
                    if(!tabLoaded[2]){
                        try {
                            FXMLLoader fkLoader = new FXMLLoader(Main.class.getResource("panelFKTab.fxml"));
                            VBox foreignKeyTabContent = fkLoader.load();
                            foreignKeyTabController = fkLoader.getController();
                            foreignKeyTab.setContent(foreignKeyTabContent);
                            tabLoaded[2] = true;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if(columTabController.columnsData.size() > foreignKeyTabController.foreignKeyColumnsData.size()
                            && ProgramState.getInstance().getSelectedSchema() != null && ProgramState.getInstance().getSelectedTable() != null){
                        // If a table and schema are selected fill comboBox and referencing columns
                        foreignKeyTabController.foreignKeyColumnsData.clear();

                        List<String> schemaTablesNames = DatabaseInspector.getInstance().getTableNames(ProgramState.getInstance().getSelectedSchema());
                        foreignKeyTabController.cbReferencedTable.clear();
                        for(String schemaTableName : schemaTablesNames){
                            foreignKeyTabController.cbReferencedTable.add(ProgramState.getInstance().getSelectedSchema().getName()+"."+schemaTableName);
                        }

                        for(int i=0; i < ProgramState.getInstance().getSelectedTable().getColumns().size();i++){
                            Column column = ProgramState.getInstance().getSelectedTable().getColumns().get(i);
                            ForeignKeyColumns newPair = new ForeignKeyColumns();
                            newPair.setFirst(column);
                            foreignKeyTabController.foreignKeyColumnsData.add(newPair);
                            foreignKeyTabController.foreignKeyColumnsTable.getItems().get(i).setColumnNameProperty(column.getName());
                        }
                    }
                });
        // Implementing lazy loading of trigger tab on first selection
        tableAttributeTabPane.getSelectionModel().selectedItemProperty().addListener(
                (_, _, newTab) -> {
                    if(newTab.equals(triggerTab) && !tabLoaded[3]){
                        try {
                            FXMLLoader triggerLoader = new FXMLLoader(Main.class.getResource("panelTriggerTab.fxml"));
                            HBox triggerTabContent = triggerLoader.load();
                            triggerTabController = triggerLoader.getController();
                            triggerTab.setContent(triggerTabContent);
                            tabLoaded[3] = true;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

        // Filling comboBox with engine values
        cbSchema.getItems().addAll(DatabaseInspector.getInstance().getDatabaseNames());
    }

    public void handleTableChange(){
        // Recognise which tab is currently selected
        if(tableAttributeTabPane.getSelectionModel().getSelectedItem().equals(columnsTab)){
            // Creating the table if it doesn't exist
            List<Column> tableColumns = columTabController.getTableColumns();
            Table createdTable = createTable(tableColumns);
        }
        else if(tableAttributeTabPane.getSelectionModel().getSelectedItem().equals(indexTab)){
            List<Index> tableIndexes = indexTabController.getTableIndexes();
            createIndexes(tableIndexes);

        }else if(tableAttributeTabPane.getSelectionModel().getSelectedItem().equals(foreignKeyTab)){
            List<ForeignKey> tableForeignKeys = foreignKeyTabController.getTableForeignKeys();
            createForeignKeys(tableForeignKeys);
        }
    }

    private void createForeignKeys(List<ForeignKey> tableForeignKeys) {
        String tableName = txtTableName.getText();
        if(tableName.isBlank() || tableName.contains(" ")){
            AlertManager.showErrorDialog("Error","Table name is not valid",null);
            return;
        }
        String schemaName = cbSchema.getSelectionModel().getSelectedItem();
        if(schemaName == null){
            AlertManager.showErrorDialog("Error","Schema was not selected",null);
            return;
        }
        Schema schema = DatabaseInspector.getInstance().getDatabaseByName(schemaName);
        Table table = DatabaseInspector.getInstance().getTableByName(schema,tableName);

        for(ForeignKey foreignKey : tableForeignKeys){
            try {
                DDLGenerator.addForeignKey(schema,table,foreignKey,false);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void createIndexes(List<Index> tableIndexes){
        String tableName = txtTableName.getText();
        if(tableName.isBlank() || tableName.contains(" ")){
            AlertManager.showErrorDialog("Error","Table name is not valid",null);
            return;
        }

        String schemaName = cbSchema.getSelectionModel().getSelectedItem();
        if(schemaName == null){
            AlertManager.showErrorDialog("Error","Schema was not selected",null);
            return;
        }
        Schema schema = DatabaseInspector.getInstance().getDatabaseByName(schemaName);
        Table table = DatabaseInspector.getInstance().getTableByName(schema,tableName);

        for(Index index : tableIndexes){
            try {
                DDLGenerator.addIndex(schema,table,index,true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private Table createTable(List<Column> columns){
        String tableName = txtTableName.getText();
        if(tableName.isBlank()){
            AlertManager.showErrorDialog("Error","Table name cannot be empty",null);
            return null;
        }

        if(tableName.contains(" ")) {
            AlertManager.showErrorDialog("Error","Table name cannot have a blank character",null);
            return null;
        }
        if(Character.isDigit(tableName.charAt(0))) {
            AlertManager.showErrorDialog("Error","Table name cannot have digits at the beginning",null);
            return null;
        }
        if(tableName.matches(".*[^a-zA-Z0-9_].*")){
            AlertManager.showErrorDialog("Error","Table name cannot have special characters",null);
            return null;
        }
        String schemaName = cbSchema.getSelectionModel().getSelectedItem();
        if(schemaName == null){
            AlertManager.showErrorDialog("Error","Schema was not selected",null);
            return null;
        }
        Schema tableSchema = DatabaseInspector.getInstance().getDatabaseByName(schemaName);
        ProgramState.getInstance().setSelectedSchema(tableSchema);

        Table newTable = new Table();
        newTable.setName(tableName);
        tableSchema.getTables().add(newTable);
        newTable.setSchema(tableSchema);
        newTable.setColumns(columns);
        try {
            DDLGenerator.createTable(newTable,true);
            Table createdTable = DatabaseInspector.getInstance().getTableByName(tableSchema,newTable.getName());
            ProgramState.getInstance().setSelectedTable(createdTable);
            TreeItem<String> newTableNode = getBrowserController().loadTableTreeItem(tableSchema,createdTable.getName());
            // For now, it works
            TreeView<String> treeViewToChange = getBrowserController().vboxBrowser.getChildren()
                    .stream().filter(t -> t instanceof TreeView<?>)
                    .map(t -> (TreeView<String>) t)
                    .filter(t -> {
                        return t.getRoot().getValue().equals(tableSchema.getName());
                    })
                    .findFirst().get();

//            TreeView<String> treeViewToChange = (TreeView<String>) getBrowserController().vboxBrowser.getChildren().stream().filter(
//                    t -> {
//                        TreeView<String> treeview = (TreeView<String>) t;
//                        if(treeview.getRoot().getValue().equals(tableSchema.getName())) return true;
//                        else return false;
//                    }).findFirst().get();
            // Schema -> tableBranch("Tables") -> schema tables
            treeViewToChange.getRoot().getChildren().getFirst().getChildren().add(newTableNode);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return newTable;
    }

//    public void createTable() {
//
//        if(txtTableName.getText().isEmpty())
//        {
//            AlertManager.showErrorDialog(null, null, "Table name must not be empty.");
//            return;
//        }
//
//        // TODO: Import schema from programstate instead of this.
//        Schema tempSchema = new Schema.SchemaBuilder(txtTableName.getText().split("\\.")[0], null, null).build();
//        Table tempTable = new Table.TableBuilder(null, txtTableName.getText().split("\\.")[1], tempSchema, null).build();
//        List<Column> columns = new LinkedList<>();
//        for (PanelColumnTab c : columnControllers) {
//            if(c.isDeleted()) continue;
//            columns.add(c.getColumn(tempTable));
//        }
//        tempTable.setColumns(columns);
//        try {
//            DDLGenerator.createTable(tempTable, true);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }


}
