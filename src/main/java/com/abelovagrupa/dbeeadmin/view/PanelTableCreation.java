package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.Main;
import com.abelovagrupa.dbeeadmin.controller.DatabaseInspector;

import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.foreignkey.ForeignKey;
import com.abelovagrupa.dbeeadmin.model.foreignkey.ForeignKeyColumns;
import com.abelovagrupa.dbeeadmin.model.index.Index;
import com.abelovagrupa.dbeeadmin.model.index.IndexedColumn;
import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.model.table.DBEngine;
import com.abelovagrupa.dbeeadmin.model.table.Table;
import com.abelovagrupa.dbeeadmin.services.DDLGenerator;
import com.abelovagrupa.dbeeadmin.services.ProgramState;
import com.abelovagrupa.dbeeadmin.services.QueryExecutor;
import com.abelovagrupa.dbeeadmin.util.AlertManager;
import com.abelovagrupa.dbeeadmin.util.DiffResult;
import com.abelovagrupa.dbeeadmin.util.ListDiff;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import kotlin.NotImplementedError;
import org.apache.logging.log4j.core.pattern.FormattingInfo;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class PanelTableCreation implements Initializable {

    @FXML
    ComboBox<String> cbSchema;

    @FXML
    ComboBox<DBEngine> cbEngine;

    @FXML
    TextField txtTableName;

    @FXML
    TabPane tableAttributeTabPane;

    @FXML
    Button applyBtn;

    @FXML
    Button revertBtn;

    Table currentTable;

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

    String applyQuery = "";

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

        // Filling comboBox with schema values
        cbSchema.getItems().addAll(DatabaseInspector.getInstance().getDatabaseNames());
        // Filling comboBox with engine values
        ObservableList<DBEngine> dbEngines = FXCollections.observableArrayList(DBEngine.values());
        cbEngine.getItems().addAll(dbEngines);
    }

    // TODO: Refactor current handler code
    public void handleTableChange(){
        // Recognise which tab is currently selected
        if(tableAttributeTabPane.getSelectionModel().getSelectedItem().equals(columnsTab)){
            applyQuery = "";
            List<Column> commitedColumnData = new LinkedList<>(columTabController.commitedColumnData);
            // Removing last empty row from list copy
            if(!commitedColumnData.isEmpty()) commitedColumnData.removeLast();

            DiffResult<Column> listDifferences = ListDiff.compareLists(commitedColumnData,columTabController.getTableColumns(),Column.columnAttributeComparator,Column.class);
            if(ListDiff.noDiff(listDifferences)) return;

            // Creating the table if it doesn't exist
            if(currentTable == null){
                // Table creation + column creation
                List<Column> columns = columTabController.getTableColumns();
                currentTable = createTable(columns);
                applyQuery += DDLGenerator.createTableCreationQuery(currentTable) +"\n";
                QueryExecutor.executeQuery(applyQuery,true);
                renderNewTable(currentTable);
                columTabController.commitedColumnData = new LinkedList<>(columTabController.columnsData)
                        .stream().map(Column::deepCopy).toList();
            }else{
                // Column deletion, addition, altering
                applyQuery += DDLGenerator.createTableAlterQuery(currentTable) + "\n";
                Optional<List<Column>> columnsToBeDeleted = Optional.ofNullable(dropTableColumns(listDifferences));
                Optional<List<Column>> columnsToBeAdded = Optional.ofNullable(addTableColumns(listDifferences));
                changeTableColumnsAttributes(listDifferences);
                QueryExecutor.executeQuery(applyQuery,true);
//                columnsToBeDeleted.ifPresent(this::renderColumnDeletion);
                columnsToBeAdded.ifPresent(this::renderNewColumns);
                columTabController.commitedColumnData = new LinkedList<>(columTabController.columnsData)
                        .stream().map(Column::deepCopy).toList();
            }

        }
        else if(tableAttributeTabPane.getSelectionModel().getSelectedItem().equals(indexTab)){
            if(currentTable == null) return;

            applyQuery = "";
            List<Index> commitedIndexData = new LinkedList<>(indexTabController.commitedIndexData);
            if(!commitedIndexData.isEmpty()) commitedIndexData.removeLast();

            DiffResult<Index> listDifferences = ListDiff.compareLists(commitedIndexData,indexTabController.getTableIndexes(),Index.indexAttributeComparator,Index.class);
            if(ListDiff.noDiff(listDifferences)) return;

            // Index deletion, addition, altering
            applyQuery += DDLGenerator.createTableAlterQuery(currentTable) + "\n";
            Optional<List<Index>> indexesToBeDeleted = Optional.ofNullable(dropTableIndexes(listDifferences));
            Optional<List<Index>> indexesToBeCreated = Optional.ofNullable(addTableIndexes(listDifferences));
            QueryExecutor.executeQuery(applyQuery,true);

            indexesToBeDeleted.ifPresent(this::renderIndexDeletion);
            indexesToBeCreated.ifPresent(this::renderNewIndexes);

            indexTabController.commitedIndexData = new LinkedList<>(indexTabController.indexData)
                    .stream().map(Index::deepCopy).toList();

        }else if(tableAttributeTabPane.getSelectionModel().getSelectedItem().equals(foreignKeyTab)){
            if(currentTable == null) return;

            applyQuery = "";
            List<ForeignKey> commitedForeignKeyData = new LinkedList<>(foreignKeyTabController.foreignKeyData);
            if(!commitedForeignKeyData.isEmpty()) commitedForeignKeyData.removeLast();

            DiffResult<ForeignKey> listDifferences = ListDiff.compareLists(commitedForeignKeyData,foreignKeyTabController.getTableForeignKeys(),ForeignKey.foreignKeyAttributeComparator,ForeignKey.class);
            if(ListDiff.noDiff(listDifferences)) return;

            // Foreign key deletion, addition, altering
            applyQuery += DDLGenerator.createTableAlterQuery(currentTable) + "\n";
            Optional<List<ForeignKey>> foreignKeysToBeDeleted = Optional.ofNullable(dropTableForeignKeys(listDifferences));
            Optional<List<ForeignKey>> foreignKeysToBeAdded = Optional.ofNullable(addTableForeignKeys(listDifferences));
            QueryExecutor.executeQuery(applyQuery,true);

            foreignKeysToBeDeleted.ifPresent(this::renderForeignKeyDeletion);
            foreignKeysToBeAdded.ifPresent(this::renderNewForeignKeys);

            foreignKeyTabController.commitedForeignKeyData = new LinkedList<>(foreignKeyTabController.foreignKeyData)
                    .stream().map(ForeignKey::deepCopy).toList();

        }
    }

    private void renderNewForeignKeys(@NotNull List<ForeignKey> foreignKeys) {
        TreeItem<String> tableTreeItemToChange =
                getBrowserController().getSchemaHashMap().get(currentTable.getSchema().getName()).
                        getSecond().getTableNodesHashMap().get(currentTable.getName()).getFirst();

        for(ForeignKey foreignKey : foreignKeys){
            TreeItem<String> newForeignKeyNode = getBrowserController().loadForeignKeyTreeItem(currentTable,foreignKey.getName());
            tableTreeItemToChange.getChildren().get(2).getChildren().add(newForeignKeyNode);
        }

    }

    private void renderForeignKeyDeletion(@NotNull List<ForeignKey> foreignKeys) {
        TreeItem<String> tableTreeItemToChange =
                getBrowserController().getSchemaHashMap().get(currentTable.getSchema().getName()).
                        getSecond().getTableNodesHashMap().get(currentTable.getName()).getFirst();

        List<TreeItem<String>> foreignKeyTreeItemsToDelete = getBrowserController().getSchemaHashMap().get(currentTable.getSchema().getName()).
                getSecond().getTableNodesHashMap().get(currentTable.getName()).getSecond().getForeignKeyNodesHashMap().values().stream().toList();

        // Think of a faster solution
        Set<String> deletedFKNames = new HashSet<>();
        for(ForeignKey foreignKey : foreignKeys) {
            deletedFKNames.add(foreignKey.getName());
        }

        Iterator<TreeItem<String>> iterator = foreignKeyTreeItemsToDelete.iterator();
        while(iterator.hasNext()){
            TreeItem<String> foreignKeyTreeItem = iterator.next();
            String name = foreignKeyTreeItem.getValue();

            if(!deletedFKNames.contains(name)){
                iterator.remove();
            }
        }

    }

    private List<ForeignKey> addTableForeignKeys(DiffResult<ForeignKey> fkDiff) {
        if(fkDiff.added.isEmpty()) return null;

        List<ForeignKey> foreignKeyToBeCreated = new LinkedList<>();
        for(int i = 0; i < fkDiff.added.size(); i++){
            ForeignKey foreignKey = fkDiff.added.get(i);
            foreignKey.setReferencingTable(currentTable);
            foreignKeyToBeCreated.add(foreignKey);
            applyQuery += DDLGenerator.createForeignKeyCreationQuery(foreignKey);
            if(i != fkDiff.added.size() - 1) applyQuery += ",\n";
            else applyQuery += ";\n";
        }

        return foreignKeyToBeCreated;
    }

    private List<ForeignKey> dropTableForeignKeys(DiffResult<ForeignKey> fkDiff) {
        if(fkDiff.removed.isEmpty()) return null;

        List<ForeignKey> foreignKeysToBeDeleted = new LinkedList<>();
        for(int i = 0; i < fkDiff.removed.size(); i++){
            ForeignKey foreignKey = fkDiff.removed.get(i);
            foreignKey.setReferencingTable(currentTable);
            foreignKeysToBeDeleted.add(foreignKey);
            applyQuery += DDLGenerator.createForeignKeyDropQuery(foreignKey);
            if(i != fkDiff.removed.size() - 1) applyQuery += ",\n";
            else applyQuery += ";\n";
        }
        return foreignKeysToBeDeleted;

    }

    private void renderIndexDeletion(@NotNull List<Index> indices) {
        TreeItem<String> tableTreeItemToChange =
                getBrowserController().getSchemaHashMap().get(currentTable.getSchema().getName()).
                        getSecond().getTableNodesHashMap().get(currentTable.getName()).getFirst();

        List<TreeItem<String>> indexTreeItemsToDelete = getBrowserController().getSchemaHashMap().get(currentTable.getSchema().getName()).
                getSecond().getTableNodesHashMap().get(currentTable.getName()).getSecond().getIndexNodesHashMap().values().stream().toList();

        // Think of a faster solution
        Set<String> deletedIndexNames = new HashSet<>();
        for(Index index : indices) {
            deletedIndexNames.add(index.getName());
        }

        Iterator<TreeItem<String>> iterator = indexTreeItemsToDelete.iterator();
        while(iterator.hasNext()){
            TreeItem<String> indexTreeItem = iterator.next();
            String name = indexTreeItem.getValue();

            if(!deletedIndexNames.contains(name)){
                iterator.remove();
            }
        }

    }

    private void renderNewIndexes(@NotNull List<Index> indices) {
        TreeItem<String> tableTreeItemToChange =
                getBrowserController().getSchemaHashMap().get(currentTable.getSchema().getName()).
                        getSecond().getTableNodesHashMap().get(currentTable.getName()).getFirst();

        for(Index index : indices){
            TreeItem<String> newIndexNode = getBrowserController().loadIndexTreeItem(currentTable,index.getName());
            tableTreeItemToChange.getChildren().get(1).getChildren().add(newIndexNode);
        }

    }

    private void renderNewColumns(List<Column> columnsToBeAdded) {
        TreeItem<String> tableTreeItemToChange =
                getBrowserController().getSchemaHashMap().get(currentTable.getSchema().getName()).
                        getSecond().getTableNodesHashMap().get(currentTable.getName()).getFirst();

        for(Column column : columnsToBeAdded){
            TreeItem<String> newColumnNode = getBrowserController().loadColumnTreeItem(currentTable,column.getName());
            tableTreeItemToChange.getChildren().getFirst().getChildren().add(newColumnNode);
        }
    }

    private void renderNewTable(Table currentTable) {
        TreeItem<String> newTableNode = getBrowserController().loadTableTreeItem(currentTable.getSchema(),currentTable.getName());

        TreeView<String> schemaViewToChange = getBrowserController()
                .getSchemaHashMap().get(currentTable.getSchema().getName()).getFirst();

        schemaViewToChange.getRoot().getChildren().getFirst().getChildren().add(newTableNode);
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
        DBEngine schemaEngine = cbEngine.getSelectionModel().getSelectedItem();
        if(schemaEngine == null){
            AlertManager.showErrorDialog("Error","Engine was not selected",null);
            return null;
        }

        Schema tableSchema = DatabaseInspector.getInstance().getDatabaseByName(schemaName);
        ProgramState.getInstance().setSelectedSchema(tableSchema);

        Table newTable = new Table();
        newTable.setName(tableName);
        newTable.setDbEngine(schemaEngine);
        tableSchema.getTables().add(newTable);
        newTable.setSchema(tableSchema);
        newTable.setColumns(columns);

        for(Column column : columns){
            column.setTable(newTable);
        }

//        try {
//            DDLGenerator.createTable(newTable,true);
//            Table createdTable = DatabaseInspector.getInstance().getTableByName(tableSchema,newTable.getName());
//            ProgramState.getInstance().setSelectedTable(createdTable);
//            TreeItem<String> newTableNode = getBrowserController().loadTableTreeItem(tableSchema,createdTable.getName());
//            // For now, it works
//            TreeView<String> treeViewToChange = getBrowserController().vboxBrowser.getChildren()
//                    .stream().filter(t -> t instanceof TreeView<?>)
//                    .map(t -> (TreeView<String>) t)
//                    .filter(t -> {
//                        return t.getRoot().getValue().equals(tableSchema.getName());
//                    })
//                    .findFirst().get();
//
////            TreeView<String> treeViewToChange = (TreeView<String>) getBrowserController().vboxBrowser.getChildren().stream().filter(
////                    t -> {
////                        TreeView<String> treeview = (TreeView<String>) t;
////                        if(treeview.getRoot().getValue().equals(tableSchema.getName())) return true;
////                        else return false;
////                    }).findFirst().get();
//            // Schema -> tableBranch("Tables") -> schema tables
//            treeViewToChange.getRoot().getChildren().getFirst().getChildren().add(newTableNode);
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
        return newTable;
    }

    private List<Column> dropTableColumns(DiffResult<Column> columnsDiff){
        if(columnsDiff.removed.isEmpty()) return null;

        List<Column> columnsToBeDeleted = new LinkedList<>();
        for(int i = 0; i < columnsDiff.removed.size(); i++) {
            Column column = columnsDiff.removed.get(i);
            column.setTable(currentTable);
            columnsToBeDeleted.add(column);
            applyQuery += DDLGenerator.createColumnDropQuery(column);
            if(i != columnsDiff.removed.size() -1) applyQuery += ",\n";
            else applyQuery += ";\n";
        }
        return columnsToBeDeleted;
    }

    private List<Column> addTableColumns(DiffResult<Column> columnsDiff){
        if(columnsDiff.added.isEmpty()) return null;

        List<Column> columnsToBeCreated = new LinkedList<>();
        for(int i = 0; i < columnsDiff.added.size(); i++){
            Column column = columnsDiff.added.get(i);
            column.setTable(currentTable);
            columnsToBeCreated.add(column);
            applyQuery += DDLGenerator.createColumnAdditionQuery(column);
            if(i != columnsDiff.added.size() - 1) applyQuery += ",\n";
            else applyQuery += ";\n";
        }

        return columnsToBeCreated;
    }

    private void changeTableColumnsAttributes(DiffResult<Column> columnsDiff){
        if(columnsDiff.changedAttributes.isEmpty()) return;
        for(Column column : columnsDiff.changedAttributes.keySet()){
            column.setTable(currentTable);
            applyQuery += DDLGenerator.createColumnAlterQuery(column);
        }
    }

    private List<Index> dropTableIndexes(DiffResult<Index> indexDiff) {
        if(indexDiff.removed.isEmpty()) return null;

        List<Index> indexesToBeDeleted = new LinkedList<>();
        for(int i = 0; i < indexDiff.removed.size(); i++){
            Index index = indexDiff.removed.get(i);
            index.setTable(currentTable);
            indexesToBeDeleted.add(index);
            applyQuery += DDLGenerator.createIndexDropQuery(index);
            if(i != indexDiff.removed.size() - 1) applyQuery += ",\n";
            else applyQuery += ";\n";
        }
        return indexesToBeDeleted;
    }

    private List<Index> addTableIndexes(DiffResult<Index> indexDiff){
        if(indexDiff.added.isEmpty()) return null;

        List<Index> indexesToBeCreated = new LinkedList<>();
        for(int i = 0; i < indexDiff.added.size(); i++){
            Index index = indexDiff.added.get(i);
            index.setTable(currentTable);
            indexesToBeCreated.add(index);
            applyQuery += DDLGenerator.createIndexCreationQuery(index);
            if(i != indexDiff.added.size() - 1) applyQuery += ",\n";
            else applyQuery += ";\n";
        }
        return indexesToBeCreated;
    }

    private void changeTableIndexAttributes(DiffResult<Index> indexDiff){
        throw new NotImplementedError();
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
