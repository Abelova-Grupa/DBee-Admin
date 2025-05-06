package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.Main;
import com.abelovagrupa.dbeeadmin.controller.DatabaseInspector;

import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.foreignkey.ForeignKey;
import com.abelovagrupa.dbeeadmin.model.foreignkey.ForeignKeyColumns;
import com.abelovagrupa.dbeeadmin.model.index.Index;
import com.abelovagrupa.dbeeadmin.model.index.IndexType;
import com.abelovagrupa.dbeeadmin.model.index.IndexedColumn;
import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.model.table.DBEngine;
import com.abelovagrupa.dbeeadmin.model.table.Table;
import com.abelovagrupa.dbeeadmin.services.DDLGenerator;
import com.abelovagrupa.dbeeadmin.services.ProgramState;
import com.abelovagrupa.dbeeadmin.services.QueryExecutor;
import com.abelovagrupa.dbeeadmin.util.AlertManager;
import com.abelovagrupa.dbeeadmin.util.DiffResult;
import com.abelovagrupa.dbeeadmin.util.StructDiff;
import com.abelovagrupa.dbeeadmin.util.Pair;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
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
    PanelColumnTab columnTabController;

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

            columnTabController = loader.getController();
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
                    for (Column column : columnTabController.columnsData){
                        if(columnTabController.emptyProperties(column)) continue;
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
                    if(columnTabController.columnsData.size() > foreignKeyTabController.foreignKeyColumnsData.size()
                            && currentTable != null && currentTable.getSchema() != null){
                        // If a table and schema are selected fill comboBox and referencing columns
                        foreignKeyTabController.foreignKeyColumnsData.clear();

                        List<String> schemaTablesNames = DatabaseInspector.getInstance().getTableNames(currentTable.getSchema());
                        foreignKeyTabController.cbReferencedTable.clear();
                        for(String schemaTableName : schemaTablesNames){
                            foreignKeyTabController.cbReferencedTable.add(currentTable.getSchema().getName()+"."+schemaTableName);
                        }

                        for(int i=0; i < currentTable.getColumns().size();i++){
                            Column column = currentTable.getColumns().get(i);
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
            List<Column> commitedColumnData = new LinkedList<>(columnTabController.commitedColumnData);
            // Removing last empty row from list copy
            if(!commitedColumnData.isEmpty() && columnTabController.emptyProperties(commitedColumnData.getLast())){
                commitedColumnData.removeLast();
            }

            List<Column> columnData = new LinkedList<>(columnTabController.getTableColumns());
            Integer lastPairId = columnTabController.columnPairs.isEmpty() ? 0 : Collections.max(columnTabController.columnPairs.keySet());
            for(Column column : columnData){
                if(!columnTabController.columnId.containsKey(column)) {
                    columnTabController.columnPairs.put(++lastPairId,Pair.of(null,column));
                    columnTabController.columnId.put(column,lastPairId);
                }
            }


            DiffResult<Column> listDifferences = StructDiff.comparePairs(columnTabController.columnPairs,Column.columnAttributeComparator,Column.class);
            if(StructDiff.noDiff(listDifferences)) return;

            // Creating the table if it doesn't exist
            if(currentTable == null){
                // Table creation + column creation
                List<Column> columns = columnTabController.getTableColumns();
                currentTable = createTable(columns);
                applyQuery += DDLGenerator.createTableCreationQuery(currentTable) +"\n";
                QueryExecutor.executeQuery(applyQuery,true);
                renderNewTable(currentTable);
                columnTabController.commitedColumnData = new LinkedList<>(columnTabController.columnsData)
                        .stream().map(Column::deepCopy).toList();
            }else{
                handleTableColumnChange(listDifferences);
            }

        }
        else if(tableAttributeTabPane.getSelectionModel().getSelectedItem().equals(indexTab)){
            if(currentTable == null) return;

            applyQuery = "";
            List<Index> commitedIndexData = new LinkedList<>(indexTabController.commitedIndexData);
            commitedIndexData.removeIf(s -> s.getName().equals("PRIMARY") && s.getType().equals(IndexType.PRIMARY));
            if(!commitedIndexData.isEmpty() && indexTabController.emptyProperties(commitedIndexData.getLast())) commitedIndexData.removeLast();

            List<Index> indexData = indexTabController.getTableIndexes();
            // commitedIndexData and indexData are the same length
            Integer lastPairId = indexTabController.indexPairs.isEmpty() ? 0 : Collections.max(indexTabController.indexPairs.keySet());
//            for(int i = 0; i < Math.min(commitedIndexData.size(),indexData.size()); i++){
//                indexTabController.indexPairs.put(++lastPairId, Pair.of(commitedIndexData.get(i),indexData.get(i)));
//            }
            for(Index index : indexData){
                if(!indexTabController.indexIds.containsKey(index)){
                    indexTabController.indexPairs.put(++lastPairId,Pair.of(null,index));
                    indexTabController.indexIds.put(index,lastPairId);
                }
            }

            DiffResult<Index> listDifferences = StructDiff.comparePairs(indexTabController.indexPairs,Index.indexAttributeComparator,Index.class);
            if(StructDiff.noDiff(listDifferences)) return;

            // Index deletion, addition, altering
            handleTableIndexChange(listDifferences);

        }else if(tableAttributeTabPane.getSelectionModel().getSelectedItem().equals(foreignKeyTab)){
            if(currentTable == null) return;

            applyQuery = "";
            List<ForeignKey> commitedForeignKeyData = new LinkedList<>(foreignKeyTabController.commitedForeignKeyData);
            if(!commitedForeignKeyData.isEmpty() && foreignKeyTabController.emptyProperties(commitedForeignKeyData.getLast())) commitedForeignKeyData.removeLast();

            DiffResult<ForeignKey> listDifferences = StructDiff.comparePairs(foreignKeyTabController.fkPairs,ForeignKey.foreignKeyAttributeComparator,ForeignKey.class);
            if(StructDiff.noDiff(listDifferences)) return;

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

    private void handleTableColumnChange(DiffResult<Column> listDifferences) {
        // Column deletion, addition, altering
        applyQuery += DDLGenerator.createTableAlterQuery(currentTable) + "\n";
        Optional<List<Column>> columnsToBeDeleted = Optional.ofNullable(dropTableColumns(listDifferences));
        Optional<List<Column>> columnsToBeAdded = Optional.ofNullable(addTableColumns(listDifferences));
        changeTableColumnsAttributes(listDifferences);

        applyQuery += ";";
        QueryExecutor.executeQuery(applyQuery,true);

        columnsToBeDeleted.ifPresent(this::renderColumnDeletion);
        columnsToBeAdded.ifPresent(this::renderNewColumns);
        columnTabController.commitedColumnData = new LinkedList<>(columnTabController.columnsData)
                .stream().map(Column::deepCopy).toList();
    }

    private void handleTableIndexChange(DiffResult<Index> listDifferences) {
        applyQuery += DDLGenerator.createTableAlterQuery(currentTable) + "\n";
        Optional<List<Index>> indexesToBeDeleted = Optional.ofNullable(dropTableIndexes(listDifferences));
        Optional<List<Index>> indexesToBeCreated = Optional.ofNullable(addTableIndexes(listDifferences));
        changeTableIndexAttributes(listDifferences);
        QueryExecutor.executeQuery(applyQuery,true);

        indexesToBeDeleted.ifPresent(this::renderIndexDeletion);
        indexesToBeCreated.ifPresent(this::renderNewIndexes);

        indexTabController.commitedIndexData = new LinkedList<>(indexTabController.indexData)
                .stream().map(Index::deepCopy).toList();
    }

    private void renderColumnDeletion(@NotNull List<Column> columns) {
        TreeItem<String> tableTreeItemToChange =
                getBrowserController().getSchemaHashMap().get(currentTable.getSchema().getName()).
                        getSecond().getTableNodesHashMap().get(currentTable.getName()).getFirst();
        if(tableTreeItemToChange == null) return;

        List<TreeItem<String>> columnTreeItemsToDelete = getBrowserController().getSchemaHashMap().get(currentTable.getSchema().getName()).
                getSecond().getTableNodesHashMap().get(currentTable.getName()).getSecond().getColumnNodesHashMap().values().stream().toList();

        // Think of a faster solution
        Set<String> deletedColumnNames = new HashSet<>();
        for(Column column : columns) {
            deletedColumnNames.add(column.getName());
        }

        Iterator<TreeItem<String>> iterator = columnTreeItemsToDelete.iterator();
        while(iterator.hasNext()){
            TreeItem<String> columnKeyTreeItem = iterator.next();
            String name = columnKeyTreeItem.getValue();

            if(!deletedColumnNames.contains(name)){
                iterator.remove();
            }
        }

    }

    private void renderNewForeignKeys(@NotNull List<ForeignKey> foreignKeys) {
        TreeItem<String> tableTreeItemToChange =
                getBrowserController().getSchemaHashMap().get(currentTable.getSchema().getName()).
                        getSecond().getTableNodesHashMap().get(currentTable.getName()).getFirst();
        if(tableTreeItemToChange == null) return;

        for(ForeignKey foreignKey : foreignKeys){
            TreeItem<String> newForeignKeyNode = getBrowserController().loadForeignKeyTreeItem(currentTable,foreignKey.getName());
            tableTreeItemToChange.getChildren().get(2).getChildren().add(newForeignKeyNode);
        }

    }

    private void renderForeignKeyDeletion(@NotNull List<ForeignKey> foreignKeys) {
        TreeItem<String> tableTreeItemToChange =
                getBrowserController().getSchemaHashMap().get(currentTable.getSchema().getName()).
                        getSecond().getTableNodesHashMap().get(currentTable.getName()).getFirst();
        if(tableTreeItemToChange == null) return;

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
            foreignKey.setReferencingSchema(currentTable.getSchema());
            foreignKeyToBeCreated.add(foreignKey);
            applyQuery += DDLGenerator.createForeignKeyCreationQuery(foreignKey);
            if(i != fkDiff.added.size() - 1) applyQuery += ",\n";
            else applyQuery += "\n";
        }

        return foreignKeyToBeCreated;
    }

    private List<ForeignKey> dropTableForeignKeys(DiffResult<ForeignKey> fkDiff) {
        if(fkDiff.removed.isEmpty()) return null;

        List<ForeignKey> foreignKeysToBeDeleted = new LinkedList<>();
        for(int i = 0; i < fkDiff.removed.size(); i++){
            ForeignKey foreignKey = fkDiff.removed.get(i);
            foreignKey.setReferencingTable(currentTable);
            foreignKey.setReferencingSchema(currentTable.getSchema());
            foreignKeysToBeDeleted.add(foreignKey);
            applyQuery += DDLGenerator.createForeignKeyDropQuery(foreignKey);
            if(i != fkDiff.removed.size() - 1) applyQuery += ",\n";
            else applyQuery += "\n";
        }
        return foreignKeysToBeDeleted;

    }

    private void renderIndexDeletion(@NotNull List<Index> indices) {
        TreeItem<String> tableTreeItemToChange =
                getBrowserController().getSchemaHashMap().get(currentTable.getSchema().getName()).
                        getSecond().getTableNodesHashMap().get(currentTable.getName()).getFirst();
        if(tableTreeItemToChange == null) return;

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
        if(tableTreeItemToChange == null) return;

        for(Index index : indices){
            TreeItem<String> newIndexNode = getBrowserController().loadIndexTreeItem(currentTable,index.getName());
            tableTreeItemToChange.getChildren().get(1).getChildren().add(newIndexNode);
        }

    }

    private void renderNewColumns(List<Column> columnsToBeAdded) {
        TreeItem<String> tableTreeItemToChange =
                getBrowserController().getSchemaHashMap().get(currentTable.getSchema().getName()).
                        getSecond().getTableNodesHashMap().get(currentTable.getName()).getFirst();
        if(tableTreeItemToChange == null) return;

        for(Column column : columnsToBeAdded){
            TreeItem<String> newColumnNode = getBrowserController().loadColumnTreeItem(currentTable,column.getName());
            tableTreeItemToChange.getChildren().getFirst().getChildren().add(newColumnNode);
        }
    }

    private void renderNewTable(Table currentTable) {
        TreeItem<String> newTableNode = getBrowserController().loadTableTreeItem(currentTable.getSchema(),currentTable.getName());
        if(newTableNode == null) return;

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
            else applyQuery += "\n";
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
            else applyQuery += "\n";
        }

        return columnsToBeCreated;
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
            else applyQuery += "\n";
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
            else applyQuery += "\n";
        }
        return indexesToBeCreated;
    }

    private void changeTableColumnsAttributes(DiffResult<Column> columnsDiff){
        if(columnsDiff.changedAttributes.isEmpty()) return;
        for(Column column : columnsDiff.changedAttributes.keySet()){
            column.setTable(currentTable);
            // Name is changed, create a rename query
            if(columnsDiff.changedAttributes.get(column).get("name").length != 0 && columnsDiff.changedAttributes.get(column).size() == 1){
                String oldName = (String) columnsDiff.changedAttributes.get(column).get("name")[0];
                String newName = (String) columnsDiff.changedAttributes.get(column).get("name")[1];
                applyQuery += DDLGenerator.createColumnRenameQuery(column,oldName,newName);
                applyQuery += "\n";
            }
            else if(columnsDiff.changedAttributes.get(column).get("name").length != 0 && columnsDiff.changedAttributes.get(column).size() > 1){
                String oldName = (String) columnsDiff.changedAttributes.get(column).get("name")[0];
                String newName = (String) columnsDiff.changedAttributes.get(column).get("name")[1];
                applyQuery += DDLGenerator.createColumnRenameAndAlterQuery(oldName,newName,column);
                applyQuery += "\n";
            }
            else{
                applyQuery += DDLGenerator.createColumnAlterQuery(column);
            }
        }
    }
    
    private void changeTableIndexAttributes(DiffResult<Index> indexDiff){
        if(indexDiff.changedAttributes.isEmpty()) return;

        for(Index index : indexDiff.changedAttributes.keySet()){

        }

        for(Index index : indexDiff.changedAttributes.keySet()){
            index.setTable(currentTable);
            Integer id = indexTabController.indexIds.get(index);
            Index committedIndex = indexTabController.indexPairs.get(id).getFirst();

            DiffResult<IndexedColumn> indexColumnDiff = StructDiff.compareLists(committedIndex.getIndexedColumns(),index.getIndexedColumns(),IndexedColumn.indexedColumnAttributeComparator,IndexedColumn.class);
            // Name is changed, create a rename query
            if(indexDiff.changedAttributes.get(index).get("name").length != 0 && indexDiff.changedAttributes.get(index).size() == 1 && StructDiff.noDiff(indexColumnDiff)){
                String oldName = (String) indexDiff.changedAttributes.get(index).get("name")[0];
                String newName = (String) indexDiff.changedAttributes.get(index).get("name")[1];
                applyQuery += DDLGenerator.createIndexRenameQuery(index,oldName,newName);
                applyQuery += "\n";
            }
            else{
                applyQuery += DDLGenerator.createIndexAlterQuery(index);
            }
        }
    }
    
}
