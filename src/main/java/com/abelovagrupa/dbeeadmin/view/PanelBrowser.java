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
import com.abelovagrupa.dbeeadmin.model.trigger.Trigger;
import com.abelovagrupa.dbeeadmin.services.ProgramState;
import com.abelovagrupa.dbeeadmin.services.QueryExecutor;
import com.abelovagrupa.dbeeadmin.services.QueryProcessor;
import com.abelovagrupa.dbeeadmin.util.Pair;
import com.abelovagrupa.dbeeadmin.view.schemaview.PanelSchemaTree;
import com.abelovagrupa.dbeeadmin.view.schemaview.PanelTableTree;
import com.abelovagrupa.dbeeadmin.view.schemaview.tableView.PanelForeignKeyTree;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class PanelBrowser implements Initializable {

    public static final Logger logger = LogManager.getRootLogger();

    private final int TREE_CELL_HEIGHT = 30;

    private PanelMain mainController;

    private HashMap<String, Pair<TreeView<String>,PanelSchemaTree>> schemaHashMap;

    private ObservableList<TreeView<String>> schemaViews;

    private PanelInfoNew infoController;

    private String selectedSchemaName;

    @FXML
    VBox vboxBrowser;

    @FXML
    TextField searchObjects;

    @FXML
    ScrollPane browserScrollPane;

    @FXML
    BorderPane browserHeader;
    private ContextMenu contextMenu;

    public PanelMain getMainController() {
        return mainController;
    }

    public void setMainController(PanelMain mainController) {
        this.mainController = mainController;
    }

    public PanelInfoNew getInfoController() {
        return infoController;
    }

    public void setInfoController(PanelInfoNew infoController) {
        this.infoController = infoController;
    }

    public TextField getSearchObjects() {
        return searchObjects;
    }

    public void setSearchObjects(TextField searchObjects) {
        this.searchObjects = searchObjects;
    }

    public HashMap<String, Pair<TreeView<String>, PanelSchemaTree>> getSchemaHashMap() {
        return schemaHashMap;
    }

    public void setSchemaHashMap(HashMap<String, Pair<TreeView<String>, PanelSchemaTree>> schemaHashMap) {
        this.schemaHashMap = schemaHashMap;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // OPTIMIZATION: Checking time to initialize browser
        long startTime = System.nanoTime();
        schemaHashMap = new HashMap<>();
        schemaViews = FXCollections.observableArrayList();
        List<String> schemaNames = DatabaseInspector.getInstance().getDatabaseNames();
        for (String schemaName : schemaNames) {
            // OPTIMIZATION: Don't load pref and info; Browser loading time cut by 30.05%
            if(schemaName.equals("performance_schema") || schemaName.equals("information_schema"))
                continue;
            // Loading each schema treeView
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("panelSchemaTree.fxml"));
            TreeView<String> schemaView = loadSchemaView(loader,schemaName);
            vboxBrowser.getChildren().add(schemaView);
        }

        for (var child : vboxBrowser.getChildren()) {
            VBox.setVgrow(child, Priority.NEVER);
        }

        searchObjects.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                searchObjects.setText("");
            } else searchObjects.setText("Search Objects");
        });

        searchObjects.setOnKeyPressed(s -> {
            if(!s.getCode().equals(KeyCode.ENTER)) return;

            if(!(searchObjects.getText().isEmpty() || searchObjects.getText().equals("Search Objects"))){
                sortAndFilterSchemas(searchObjects.getText());
            }else {
                sortAndFilterSchemas(null);
            }
        });

        // OPTIMIZATION: Read browser loading time and print it!
        long endTime = System.nanoTime();
        long duration = endTime - startTime; // in nanoseconds
        logger.info("Browser initialization time: {} ns", duration);
    }

    public TreeView<String> loadSchemaView(FXMLLoader loader, String schemaName) {
        final int TREE_CELL_HEIGHT = 30;
        try {
            TreeView<String> schemaView = loader.load();
            schemaView.setPrefHeight(TREE_CELL_HEIGHT);
            // Adding schemaview reference and schema controller to hashmap
            schemaHashMap.put(schemaName,Pair.of(schemaView,new PanelSchemaTree()));

            // Creating a root node with its first children tables, views, stored procedures and functions
            TreeItem<String> schemaNode = new TreeItem<>(schemaName, new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database.png")).toExternalForm())));
            schemaView.setRoot(schemaNode);
            TreeItem<String> tableBranch = new TreeItem<>("Tables", new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png")).toExternalForm())));
            TreeItem<String> viewBranch = new TreeItem<>("Views", new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png")).toExternalForm())));
            TreeItem<String> procedureBranch = new TreeItem<>("Stored Procedures", new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png")).toExternalForm())));
            TreeItem<String> functionBranch = new TreeItem<>("Functions", new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png")).toExternalForm())));

            // Creating an initial tableDummy so that the tableBranch can be expandable
            TreeItem<String> tableDummyNode = new TreeItem<>("Dummy table");
            tableBranch.getChildren().add(tableDummyNode);
            viewBranch.getChildren().add(tableDummyNode);

            schemaNode.getChildren().addAll(tableBranch, viewBranch, procedureBranch, functionBranch);
            // Adding expanding and collapsing listeners to the treeview so that the height of it would change
            schemaView.getRoot().addEventHandler(TreeItem.branchExpandedEvent(), event -> {
                Platform.runLater(() -> schemaView.setPrefHeight(schemaView.getExpandedItemCount() * TREE_CELL_HEIGHT));
            });

            schemaView.getRoot().addEventHandler(TreeItem.branchCollapsedEvent(), event -> {
                Platform.runLater(() -> schemaView.setPrefHeight(schemaView.getExpandedItemCount() * TREE_CELL_HEIGHT));
            });

            schemaViews.add(schemaView);

            // If "Views" branch is expanded, its children are loaded and dummy node is removed.
            // OPTIMIZATION: This will execute as a Task (on a separate thread)
            ChangeListener<Boolean> viewBranchListener = new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean odlValue, Boolean newValue) {
                    Task<List<TreeItem<String>>> loadViewsTask = new Task<>() {
                        @Override
                        protected List<TreeItem<String>> call() {
                            Schema schema = DatabaseInspector.getInstance().getDatabaseByName(schemaName);
                            List<String> viewNames = DatabaseInspector.getInstance().getViewNames(schema);

                            List<TreeItem<String>> viewItems = new ArrayList<>();
                            for (String viewName : viewNames) {
                                ImageView icon = new ImageView(
                                    new Image(Objects.requireNonNull(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/view.png")).toExternalForm())
                                );
                                viewItems.add(new TreeItem<>(viewName, icon));
                            }
                            return viewItems;
                        }
                    };

                    loadViewsTask.setOnSucceeded(workerStateEvent -> {
                        List<TreeItem<String>> viewItems = loadViewsTask.getValue();
                        viewBranch.getChildren().remove(tableDummyNode);
                        viewBranch.getChildren().addAll(viewItems);
                    });

                    new Thread(loadViewsTask).start();
                    viewBranch.expandedProperty().removeListener(this);
                }
            };

            // If the "Tables" branch of schema expands its children are being loaded
            // in this case table name nodes and the dummy node is being removed
            ChangeListener<Boolean> tableBranchListener = new ChangeListener<>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (newValue) {
                        Schema schema = DatabaseInspector.getInstance().getDatabaseByName(schemaName);
                        List<String> tableNames = DatabaseInspector.getInstance().getTableNames(schema);
                        tableBranch.getChildren().remove(tableDummyNode);
                        for (String tableName : tableNames) {
                            // Adding "Columns" branch for each table which has a dummy branch
                            TreeItem<String> tableNode = loadTableTreeItem(schema,tableName);
                            tableBranch.getChildren().add(tableNode);
                        }
                        // Removing the table branch listener because its first level children are loaded
                        tableBranch.expandedProperty().removeListener(this);
                    }
                }
            };

            // Register table branch listener
            tableBranch.expandedProperty().addListener(tableBranchListener);

            // Register view branch listener
            viewBranch.expandedProperty().addListener(viewBranchListener);

            // TODO: REFACTOR
            schemaView.setOnMouseClicked(event -> {
                // Selecting up active schema
                if(event.getButton().equals(MouseButton.PRIMARY)){
                    if(contextMenu != null && contextMenu.isShowing()){
                        contextMenu.hide();
                        return;
                    }
                    // Fetch schema name
                    selectedSchemaName = schemaView.getRoot().getValue();
                    // Display active schema in info panel and set in programState
                    Schema selectedSchema = DatabaseInspector.getInstance().getDatabaseByName(selectedSchemaName);
                    infoController.setSelected(selectedSchema);
                    ProgramState.getInstance().setSelectedSchema(selectedSchema);
//                    if(event.getClickCount() == 2){
//
//                    }
                }


                Optional<TreeItem<String>> selectedItemOptional = Optional.ofNullable(schemaView.getSelectionModel().getSelectedItem());
                Schema schema = DatabaseInspector.getInstance().getDatabaseByName(schemaName);

                if (selectedItemOptional.isPresent()) {
                    TreeItem<String> selectedItem = selectedItemOptional.get();
                    Optional<Table> selectedTableOptional = schema.getTables().stream().filter(s -> s.getName().equals(selectedItem.getValue())).findFirst();
                    if (selectedTableOptional.isPresent()) {
                        Table selectedTable = selectedTableOptional.get();
                        selectedTable.setIndexes(DatabaseInspector.getInstance().getIndexes(schema,selectedTable));
                        selectedTable.setForeignKeys(DatabaseInspector.getInstance().getForeignKeys(schema,selectedTable));

                        // If table is and selected
                        if (getTreeItemDepth(selectedItem) == 3 && (isChildOf(selectedItem, tableBranch))) {

                            // Display table in info panel
                            infoController.setSelected(selectedTable);

                            // Set selected table in ProgramState
                            ProgramState.getInstance().setSelectedTable(selectedTable);

                            // Do SELECT on double click
                            if(event.getClickCount() == 2) {
                                displaySelectedTable(selectedTable);
                            }

                            // Right click creates context menu while primary click closes it
                            if(event.getButton() == MouseButton.PRIMARY){
                                if(contextMenu != null && contextMenu.isShowing()){
                                    contextMenu.hide();
                                    return;
                                }
                            }
                            if(event.getButton() == MouseButton.SECONDARY) {
                                // Table context menu
                                contextMenu = new ContextMenu();
                                MenuItem viewTable = new MenuItem("View Data (SELECT *)");
                                MenuItem alterTable = new MenuItem("Edit Table");
                                MenuItem deleteTable = new MenuItem("Delete Table");
                                MenuItem addColumn = new MenuItem("Add Column");
                                MenuItem generateTableSQL = new MenuItem("Generate Table SQL");

                                viewTable.setOnAction(tblClick -> displaySelectedTable(selectedTable));
                                alterTable.setOnAction(tblClick -> alterSelectedTable(selectedTable));
                                deleteTable.setOnAction(tblClick -> deleteSelectedTable(selectedTable));
//                                addColumn.setOnAction(tblClick -> System.out.println("Adding column to table..."));
//                                generateTableSQL.setOnAction(tblClick -> System.out.println("Generating SQL for table..."));

                                contextMenu.getItems().addAll(viewTable, alterTable, deleteTable, addColumn, generateTableSQL);
                                contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());

                                event.consume();
                            }

                            if (getTreeItemDepth(selectedItem) == 3 && (isChildOf(selectedItem, viewBranch))) {

                            }


                        }
                    }
//                        // If view branch is selected
//                        if (getTreeItemDepth(selectedItem) == 3 && (isChildOf(selectedItem, viewBranch))) {
//
//                            // FIXME: Find a way to store views instead of querying for them each time.
//                            // Get selected view (non optimal solution)
//                            Optional<View> selectedView = DatabaseInspector.getInstance().getViews(schema).stream().filter(view -> view.getName().equals(selectedItem.getValue())).findFirst();
//                            if(selectedView.isPresent()) {
//                                // Display table in info panel
//                                infoController.setSelected(selectedView.get());
//
//                                // Do SELECT on double click
//                                if(event.getClickCount() == 2) {
//                                    try {
//                                        mainController.resultsController.printResultSetToTable(
//                                            QueryExecutor.executeQuery(
//                                                "SELECT * FROM " + selectedView.get().getName()
//                                            ).getFirst()
//                                        );
//                                    } catch (SQLException e) {
//                                        throw new RuntimeException(e);
//                                    }
//                                }
//                            }
//
//                        }

                    try {
                        if (selectedItem.getParent().getValue().equals("Columns") && getTreeItemDepth(selectedItem) == 5) {
                            // TODO: make it efficient, for now it works
                            // Removes key icon from name (after " ") for searching
                            String columnName = selectedItem.getValue().split(" ")[0];
                            Table table = DatabaseInspector.getInstance().getTableByName(schema, selectedItem.getParent().getParent().getValue());
                            Column selectedColumn = DatabaseInspector.getInstance().getColumnByName(table, columnName);
                            if (selectedColumn != null) {
                                // Display column in info panel
                                infoController.setSelected(selectedColumn);

                                // Context menu
                                if(event.getButton() == MouseButton.PRIMARY){
                                    if(contextMenu != null && contextMenu.isShowing())
                                        contextMenu.hide();
                                }
                                if(event.getButton() == MouseButton.SECONDARY) {
                                    contextMenu = new ContextMenu();
                                    MenuItem edit = new MenuItem("Edit column");
                                    MenuItem delete = new MenuItem("Delete column");

                                    // TODO: Implement column CM
                                    edit.setOnAction(tblClick -> System.out.println("Editing..."));
                                    delete.setOnAction(tblClick -> deleteSelectedColumn(selectedColumn));

                                    contextMenu.getItems().addAll(edit, delete);
                                    contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
                                    event.consume();
                                }
                            }
                        }


                        if (selectedItem.getParent().getValue().equals("Indexes") && getTreeItemDepth(selectedItem) == 5) {
                            // TODO: make it efficient, for now it works
                            String indexName = selectedItem.getValue();
                            Table table = DatabaseInspector.getInstance().getTableByName(schema, selectedItem.getParent().getParent().getValue());
                            Index selectedIndex = DatabaseInspector.getInstance().getIndexByName(schema, table, indexName);
                            if (selectedIndex != null) {
                                infoController.setSelected(selectedIndex);

                                // Context menu
                                if(event.getButton() == MouseButton.PRIMARY){
                                    if(contextMenu != null && contextMenu.isShowing())
                                        contextMenu.hide();
                                }
                                if(event.getButton() == MouseButton.SECONDARY) {
                                    contextMenu = new ContextMenu();
                                    MenuItem edit = new MenuItem("Edit index");
                                    MenuItem delete = new MenuItem("Delete index");

                                    // TODO: Implement index CM
                                    edit.setOnAction(tblClick -> System.out.println("Editing..."));
                                    delete.setOnAction(tblClick -> deleteSelectedIndex(selectedIndex));

                                    contextMenu.getItems().addAll(edit, delete);
                                    contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
                                    event.consume();
                                }

                            }

                        }

                        if (selectedItem.getParent().getValue().equals("Foreign Keys")) {
                            String foreignKeyName = selectedItem.getValue();
                            Table table = DatabaseInspector.getInstance().getTableByName(schema, selectedItem.getParent().getParent().getValue());
                            ForeignKey selectedForeignKey = DatabaseInspector.getInstance().getForeignKeyByName(schema, table, foreignKeyName);
                            if (selectedForeignKey != null) {
                                // TODO: Foreign Keys
                                infoController.setSelected(selectedForeignKey);

                                // Context menu
                                if(event.getButton() == MouseButton.PRIMARY){
                                    if(contextMenu != null && contextMenu.isShowing())
                                        contextMenu.hide();
                                }
                                if(event.getButton() == MouseButton.SECONDARY) {
                                    contextMenu = new ContextMenu();
                                    MenuItem edit = new MenuItem("Edit foreign key");
                                    MenuItem delete = new MenuItem("Delete foreign key");

                                    // TODO: Implement FK CM
                                    edit.setOnAction(tblClick -> System.out.println("Editing..."));
                                    delete.setOnAction(tblClick -> deleteSelectedForeignKey(selectedForeignKey));

                                    contextMenu.getItems().addAll(edit, delete);
                                    contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
                                    event.consume();
                                }
                            }

                        }
                        if (selectedItem.getParent().getValue().equals("Triggers")) {
                            String triggerName = selectedItem.getValue();
                            Table table = DatabaseInspector.getInstance().getTableByName(schema, selectedItem.getParent().getParent().getValue());
                            Trigger selectedTrigger = DatabaseInspector.getInstance().getTriggerByName(table, triggerName);
                            if(selectedTrigger != null) {
                                infoController.setSelected(selectedTrigger);

                                // Context menu
                                if(event.getButton() == MouseButton.PRIMARY){
                                    if(contextMenu != null && contextMenu.isShowing())
                                        contextMenu.hide();
                                }
                                if(event.getButton() == MouseButton.SECONDARY) {
                                    contextMenu = new ContextMenu();
                                    MenuItem edit = new MenuItem("Edit trigger");
                                    MenuItem delete = new MenuItem("Delete trigger");

                                    // TODO: Implement Trigger CM
                                    edit.setOnAction(tblClick -> System.out.println("Editing..."));
                                    delete.setOnAction(tblClick -> System.out.println("Deleting..."));

                                    contextMenu.getItems().addAll(edit, delete);
                                    contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
                                    event.consume();
                                }
                            }

                        }
                    } catch (NullPointerException e) {
                        logger.warn("No parent value for selected item...");
                    }
                }
                if(!event.isConsumed() && event.getButton() == MouseButton.SECONDARY) {
                    // Fetch schema name
                    selectedSchemaName = schemaView.getRoot().getValue();
                    // Display active schema in info panel and set in programState
                    Schema selectedSchema = DatabaseInspector.getInstance().getDatabaseByName(selectedSchemaName);
                    infoController.setSelected(selectedSchema);
                    ProgramState.getInstance().setSelectedSchema(selectedSchema);

                    // Table context menu
                    contextMenu = new ContextMenu();
                    MenuItem alterSchema = new MenuItem("Alter Schema");
                    MenuItem deleteSchema = new MenuItem("Delete Schema");

//                                editTable.setOnAction(tblClick -> System.out.println("Editing table..."));
                    deleteSchema.setOnAction(tblClick -> deleteSelectedSchema(selectedSchema));
//                                addColumn.setOnAction(tblClick -> System.out.println("Adding column to table..."));
//                                generateTableSQL.setOnAction(tblClick -> System.out.println("Generating SQL for table..."));

                    contextMenu.getItems().addAll(alterSchema, deleteSchema);
                    contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
                }
            });
            return schemaView;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void alterSelectedTable(Table selectedTable) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("panelTableCreation.fxml"));
            BorderPane tableTabContent = null;
            tableTabContent = fxmlLoader.load();
            PanelTableCreation tableCreationController = fxmlLoader.getController();
            tableCreationController.setBrowserController(this);
            PanelColumnTab columnController = tableCreationController.columnTabController;

            // Loading column data
            tableCreationController.currentTable = selectedTable;
            columnController.commitedColumnData = new LinkedList<>(selectedTable.getColumns())
                    .stream().map(column -> {
                        column = Column.deepCopy(column);
                        return column;
                    }).toList();

            List<Column> columnData = new LinkedList<>(selectedTable.getColumns());
            columnData.add(new Column());
            columnController.columnsData = FXCollections.observableArrayList(columnData);
            columnController.columnTable.setItems(tableCreationController.columnTabController.columnsData);

            //Putting columns from both lists into a hashmap of pairs that we will later compare
            for(int i = 0; i < columnController.commitedColumnData.size(); i++){
                Column commitedColumn = columnController.commitedColumnData.get(i);
                Column column = columnController.columnsData.get(i);
                Integer lastId = columnController.columnPairs.isEmpty() ? 0 : Collections.max(columnController.columnPairs.keySet());

                columnController.columnPairs.put(++lastId,Pair.of(commitedColumn,column));
                columnController.commitedColumnId.put(column,lastId);
                columnController.columnId.put(column,lastId);
            }

            tableCreationController.txtTableName.setText(selectedTable.getName());
            tableCreationController.txtTableName.setEditable(false);

            tableCreationController.cbEngine.setValue(selectedTable.getDbEngine());
            tableCreationController.cbEngine.setDisable(true);

            tableCreationController.cbSchema.setValue(selectedTable.getSchema().getName());
            tableCreationController.cbSchema.setDisable(true);

            // Loading index data
            PanelIndexTab indexController = tableCreationController.indexTabController;
            indexController.commitedIndexData = new LinkedList<>(selectedTable.getIndexes())
                    .stream().map(index -> {
                        Index indexCopy = Index.deepCopy(index);
                        List<IndexedColumn> indexColCopies = indexCopy.getIndexedColumns().stream().map(IndexedColumn::deepCopy).toList();
                        indexCopy.setIndexedColumns(indexColCopies);
                        for (IndexedColumn ic : indexCopy.getIndexedColumns()){
                            ic.checkedColumnProperty().set(true);
                        }
                        return indexCopy;
                    }).toList();

            List<Index> indexData = new LinkedList<>(selectedTable.getIndexes()
                    .stream().map(index -> {
                        for (IndexedColumn ic : index.getIndexedColumns()){
                            ic.checkedColumnProperty().set(true);
                        }
                        return index;
                    }).toList());
            indexData.add(new Index());
            indexController.indexData = FXCollections.observableArrayList(indexData);
            indexController.indexTable.setItems(tableCreationController.indexTabController.indexData);

            for(int i = 0; i < indexController.commitedIndexData.size(); i++){
                Index commitedIndex = indexController.commitedIndexData.get(i);
                Index index = indexController.indexData.get(i);
                Integer lastId = indexController.indexPairs.isEmpty() ? 0 : Collections.max(indexController.indexPairs.keySet());
                indexController.indexPairs.put(++lastId,Pair.of(commitedIndex,index));
                indexController.commitedIndexIds.put(commitedIndex,lastId);
                indexController.indexIds.put(index,lastId);
            }

            // Loading foreign key data
            if(tableCreationController.tabLoaded[2]){
                PanelFKTab foreignKeyController = tableCreationController.foreignKeyTabController;
                foreignKeyController.commitedForeignKeyData = new LinkedList<>(selectedTable.getForeignKeys().stream().map(
                        foreignKey -> {
                            ForeignKey fkCopy = ForeignKey.deepCopy(foreignKey);
                            fkCopy.getReferencingColumns().clear();
                            fkCopy.getReferencedColumns().clear();
                            List<ForeignKeyColumns> fkColumnCopies = fkCopy.getColumnPairs().stream().map(fkColumn ->{
                                ForeignKeyColumns fkColumnCopy = ForeignKeyColumns.deepCopy(fkColumn);
                                fkCopy.getReferencingColumns().add(fkColumnCopy.getFirst());
                                fkCopy.getReferencedColumns().add(fkColumnCopy.getSecond());
                                return fkColumnCopy;
                            }).toList();
                            fkCopy.setColumnPairs(fkColumnCopies);
                            return fkCopy;
                        }
                ).toList());

                List<ForeignKey> foreignKeyData = new LinkedList<>(selectedTable.getForeignKeys()
                        .stream().map(foreignKey -> {
                            foreignKey.setReferencedTableProperty(foreignKey.getReferencedSchema().getName()+"."+foreignKey.getReferencedTable().getName());
                            for(ForeignKeyColumns fkColumns : foreignKey.getColumnPairs()){
                                fkColumns.setCheckedColumnProperty(true);
                            }
                            return foreignKey;
                        }).toList());
                foreignKeyData.add(new ForeignKey());
                foreignKeyController.foreignKeyData = FXCollections.observableArrayList(foreignKeyData);
                foreignKeyController.foreignKeyTable.setItems(foreignKeyController.foreignKeyData);

            }else{
                tableCreationController.committedForeignKeyData = new LinkedList<>(selectedTable.getForeignKeys().stream().map(
                        foreignKey -> {
                            ForeignKey fkCopy = ForeignKey.deepCopy(foreignKey);
                            fkCopy.getReferencingColumns().clear();
                            fkCopy.getReferencedColumns().clear();
                            List<ForeignKeyColumns> fkColumnCopies = fkCopy.getColumnPairs().stream().map(fkColumn ->{
                                ForeignKeyColumns fkColumnCopy = ForeignKeyColumns.deepCopy(fkColumn);
                                fkCopy.getReferencingColumns().add(fkColumnCopy.getFirst());
                                fkCopy.getReferencedColumns().add(fkColumnCopy.getSecond());
                                return fkColumnCopy;
                            }).toList();
                            fkCopy.setColumnPairs(fkColumnCopies);
                            return fkCopy;
                        }
                ).toList());

                List<ForeignKey> foreignKeyData = new LinkedList<>(selectedTable.getForeignKeys()
                        .stream().map(foreignKey -> {
                            foreignKey.setReferencedTableProperty(foreignKey.getReferencedSchema().getName()+"."+foreignKey.getReferencedTable().getName());

                            for(ForeignKeyColumns fkColumns : foreignKey.getColumnPairs()){
                                fkColumns.setCheckedColumnProperty(true);
                            }
                            return foreignKey;
                        }).toList());
                foreignKeyData.add(new ForeignKey());
                tableCreationController.foreignKeyData = FXCollections.observableArrayList(foreignKeyData);

            }

            // Creating tab
            Tab tableTab = new Tab("New Table");
            tableTab.setContent(tableTabContent);
            mainController.editorController.editorTabs.getTabs().add(tableTab);
            mainController.editorController.editorTabs.getSelectionModel().select(tableTab);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TreeItem<String> loadTableTreeItem(Schema schema, String tableName) {
        if(schemaHashMap.get(schema.getName()).getSecond().getTableNodesHashMap().containsKey(tableName)) return null;
        TreeItem<String> tableNode = new TreeItem<>(tableName, new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-table.png")).toExternalForm())));
        TreeItem<String> columnBranch = new TreeItem<>("Columns", new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/columns.png")).toExternalForm())));
        TreeItem<String> columnDummy = new TreeItem<>("Loading columns...");
        columnBranch.getChildren().add(columnDummy);

        // Adding tableNode to tableNodes hashmap in schemaTreeController to make finding it later faster
        schemaHashMap.get(schema.getName()).getSecond().
                getTableNodesHashMap().put(tableName, Pair.of(tableNode,new PanelTableTree()));

        // Same logic applies to column expansion listener
        // OPTIMIZATION: This will execute as a Task (on a separate thread)
        ChangeListener<Boolean> columnBranchListener = new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    Task<List<TreeItem<String>>> loadColumnsTask = new Task<>() {
                        @Override
                        protected List<TreeItem<String>> call() {
                            Table table = DatabaseInspector.getInstance().getTableByName(schema, tableName);
                            List<TreeItem<String>> columnNodes = new ArrayList<>();

                            for (Column column : table.getColumns()) {
                                String columnName = column.isPrimaryKey()
                                        ? column.getName() + " (\uD83D\uDD11)"
                                        : column.getName();
                                TreeItem<String> columnNode = loadColumnTreeItem(table,columnName);
                                columnBranch.getChildren().add(columnNode);
                            }
                            return columnNodes;
                        }


                    };

                    loadColumnsTask.setOnSucceeded(workerStateEvent -> {
                        List<TreeItem<String>> columnNodes = loadColumnsTask.getValue();
                        columnBranch.getChildren().remove(columnDummy);
                        columnBranch.getChildren().addAll(columnNodes);
                        columnBranch.expandedProperty().removeListener(this);
                    });

                    loadColumnsTask.setOnFailed(workerStateEvent -> {
                        Throwable error = loadColumnsTask.getException();
                        logger.error(error.getMessage());
                    });
                    new Thread(loadColumnsTask).start();
                }
            }
        };
        columnBranch.expandedProperty().addListener(columnBranchListener);

        // Adding index branch with dummy child
        TreeItem<String> indexBranch = new TreeItem<>("Indexes", new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/indexes.png")).toExternalForm())));
        TreeItem<String> indexDummy = new TreeItem<>("Loading indexes...");
        indexBranch.getChildren().add(indexDummy);

        // Same logic applies for index expansion listener
        // OPTIMIZATION: This will execute as a Task (on a separate thread)
        ChangeListener<Boolean> indexBranchListener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    Task<List<TreeItem<String>>> loadIndexesTask = new Task<>() {
                        @Override
                        protected List<TreeItem<String>> call() {
                            Table table = DatabaseInspector.getInstance().getTableByName(schema, tableName);
                            List<Index> indexes = DatabaseInspector.getInstance().getIndexes(schema, table);

                            List<TreeItem<String>> indexNodes = new ArrayList<>();
                            for (Index index : indexes) {
                                TreeItem<String> indexNode = loadIndexTreeItem(table,index.getName());
                                indexBranch.getChildren().add(indexNode);
                            }
                            return indexNodes;
                        }
                    };

                    loadIndexesTask.setOnSucceeded(workerStateEvent -> {
                        List<TreeItem<String>> indexNodes = loadIndexesTask.getValue();
                        indexBranch.getChildren().remove(indexDummy);
                        indexBranch.getChildren().addAll(indexNodes);
                        indexBranch.expandedProperty().removeListener(this);
                    });

                    loadIndexesTask.setOnFailed(workerStateEvent -> {
                        Throwable error = loadIndexesTask.getException();
                        logger.error(error.getMessage());
                    });

                    new Thread(loadIndexesTask).start();
                }
            }
        };
        indexBranch.expandedProperty().addListener(indexBranchListener);

        // Adding foreign key branch with dummy child
        TreeItem<String> foreignKeyBranch = new TreeItem<>("Foreign Keys", new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/foreignkeys.png")).toExternalForm())));
        TreeItem<String> foreignKeyDummy = new TreeItem<>("Loading foreign keys...");
        foreignKeyBranch.getChildren().add(foreignKeyDummy);

        // Same logic as before for this listener
        ChangeListener<Boolean> foreignKeyListener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    Task<List<TreeItem<String>>> loadForeignKeysTask = new Task<>() {
                        @Override
                        protected List<TreeItem<String>> call() {
                            Table table = DatabaseInspector.getInstance().getTableByName(schema, tableName);
                            List<ForeignKey> foreignKeys = DatabaseInspector.getInstance().getForeignKeys(schema, table);

                            List<TreeItem<String>> foreignKeyNodes = new ArrayList<>();
                            for (ForeignKey fk : foreignKeys) {
                                TreeItem<String> foreignKeyNode = new TreeItem<>(fk.getName());
                                foreignKeyNodes.add(foreignKeyNode);
                                schemaHashMap.get(schema.getName()).getSecond().
                                        getTableNodesHashMap().get(tableName).getSecond()
                                        .getForeignKeyNodesHashMap().put(fk.getName(),foreignKeyNode);
                            }
                            return foreignKeyNodes;
                        }
                    };

                    loadForeignKeysTask.setOnSucceeded(event -> {
                        foreignKeyBranch.getChildren().setAll(loadForeignKeysTask.getValue());
                        foreignKeyBranch.expandedProperty().removeListener(this);
                    });

                    loadForeignKeysTask.setOnFailed(event -> {
                        logger.error(loadForeignKeysTask.getException().getMessage());
                    });

                    new Thread(loadForeignKeysTask).start();
                }
            }
        };
        foreignKeyBranch.expandedProperty().addListener(foreignKeyListener);

        // Not implemented yet

        TreeItem<String> triggersBranch = new TreeItem<>("Triggers", new ImageView(new Image(Objects.requireNonNull(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/triggers.png")).toExternalForm())));
        TreeItem<String> triggerDummy = new TreeItem<>("Loading triggers...");
        triggersBranch.getChildren().add(triggerDummy);

        // Create change listener to trigger list
        ChangeListener<Boolean> triggerListener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    Task<List<TreeItem<String>>> loadTriggersTask = new Task<>() {
                        @Override
                        protected List<TreeItem<String>> call() {
                            Table table = DatabaseInspector.getInstance().getTableByName(schema, tableName);
                            List<Trigger> triggers = DatabaseInspector.getInstance().getTriggers(table);

                            List<TreeItem<String>> triggerNodes = new ArrayList<>();
                            for (Trigger trigger : triggers) {
                                TreeItem<String> triggerNode = new TreeItem<>(trigger.getName());
                                triggerNodes.add(triggerNode);
                                schemaHashMap.get(schema.getName()).getSecond()
                                    .getTableNodesHashMap().get(tableName).getSecond()
                                    .getTriggerNodesHashMap()
                                    .put(trigger.getName(), triggerNode);
                            }
                            return triggerNodes;
                        }
                    };

                    loadTriggersTask.setOnSucceeded(event -> {
                        triggersBranch.getChildren().setAll(loadTriggersTask.getValue());
                        triggersBranch.expandedProperty().removeListener(this);
                    });

                    loadTriggersTask.setOnFailed(event -> {
                        logger.error(loadTriggersTask.getException().getMessage());
                    });

                    new Thread(loadTriggersTask).start();
                }
            }
        };

        triggersBranch.expandedProperty().addListener(triggerListener);


        tableNode.getChildren().addAll(columnBranch, indexBranch, foreignKeyBranch, triggersBranch);

        return tableNode;
    }

    public TreeItem<String> loadColumnTreeItem(Table table, String columnName) {
        if(schemaHashMap.get(table.getSchema().getName()).getSecond()
                .getTableNodesHashMap().get(table.getName()).getSecond()
                .getColumnNodesHashMap().containsKey(columnName)) return null;
        TreeItem<String> columnNode = new TreeItem<>(columnName);

        schemaHashMap.get(table.getSchema().getName()).getSecond()
                .getTableNodesHashMap().get(table.getName()).getSecond()
                .getColumnNodesHashMap().put(columnName,columnNode);

        return columnNode;
    }

    public TreeItem<String> loadIndexTreeItem(Table table, String indexName) {
        if(schemaHashMap.get(table.getSchema().getName()).getSecond()
                .getTableNodesHashMap().get(table.getName()).getSecond()
                .getIndexNodesHashMap().containsKey(indexName)) return null;
        TreeItem<String> indexNode = new TreeItem<>(indexName);

        schemaHashMap.get(table.getSchema().getName()).getSecond()
                .getTableNodesHashMap().get(table.getName()).getSecond()
                .getIndexNodesHashMap().put(indexName,indexNode);

        return indexNode;
    }

    public TreeItem<String> loadForeignKeyTreeItem(Table table, String foreignKeyName) {
        if(schemaHashMap.get(table.getSchema().getName()).getSecond()
                .getTableNodesHashMap().get(table.getName()).getSecond()
                .getForeignKeyNodesHashMap().containsKey(foreignKeyName)) return null;
        TreeItem<String> foreignKeyNode = new TreeItem<>(foreignKeyName);

        schemaHashMap.get(table.getSchema().getName()).getSecond()
                .getTableNodesHashMap().get(table.getName()).getSecond()
                .getForeignKeyNodesHashMap().put(foreignKeyName,foreignKeyNode);

        return foreignKeyNode;
    }

    private void displaySelectedTable(Table selectedTable) {
        try {
            mainController.resultsController.printResultSetToTable(
                    QueryExecutor.executeQuery(
                            "SELECT * FROM " + selectedSchemaName + "." + selectedTable.getName()
                    ).getFirst()
            );
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void deleteSelectedSchema(Schema selectedSchema) {
        TreeView<String> selectedSchemaView = schemaHashMap.get(selectedSchema.getName()).getFirst();
        // TODO: Drop here
        try {
            QueryProcessor.dropSchema(selectedSchema, true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        schemaViews.remove(selectedSchemaView);
        vboxBrowser.getChildren().remove(selectedSchemaView);

    }

    private void deleteSelectedTable(Table selectedTable){
            TreeItem<String> selectedTableNode = schemaHashMap.get(selectedTable.getSchema().getName()).getSecond()
                    .getTableNodesHashMap().get(selectedTable.getName()).getFirst();

            QueryProcessor.dropTable(selectedTable,false);
            TreeItem<String> tableNodeParent = selectedTableNode.getParent();
            tableNodeParent.getChildren().remove(selectedTableNode);

    }

    private void deleteSelectedColumn(Column selectedColumn) {
            TreeItem<String> selectedColumnNode = schemaHashMap.get(selectedColumn.getTable().getSchema().getName()).getSecond()
                    .getTableNodesHashMap().get(selectedColumn.getTable().getName()).getSecond().
                    getColumnNodesHashMap().get(selectedColumn.getName());
                QueryProcessor.dropColumn(selectedColumn,false);

            TreeItem<String> columnNodeParent = selectedColumnNode.getParent();
            columnNodeParent.getChildren().remove(selectedColumnNode);
    }

    private void deleteSelectedIndex(Index selectedIndex) {
            TreeItem<String> selectedIndexNode = schemaHashMap.get(selectedIndex.getTable().getSchema().getName()).getSecond()
                    .getTableNodesHashMap().get(selectedIndex.getTable().getName()).getSecond().
                    getIndexNodesHashMap().get(selectedIndex.getName());
            QueryProcessor.dropIndex(selectedIndex,false);

            TreeItem<String> indexNodeParent = selectedIndexNode.getParent();
            indexNodeParent.getChildren().remove(selectedIndexNode);
    }

    private void deleteSelectedForeignKey(ForeignKey selectedForeignKey) {
            TreeItem<String> selectedForeignKeyNode = schemaHashMap.get(selectedForeignKey.getReferencingTable().getSchema().getName()).getSecond()
                    .getTableNodesHashMap().get(selectedForeignKey.getReferencingTable().getName()).getSecond().
                    getForeignKeyNodesHashMap().get(selectedForeignKey.getName());
            QueryProcessor.dropForeignKey(selectedForeignKey,false);
            TreeItem<String> foreignKeyNodeParent = selectedForeignKeyNode.getParent();
            foreignKeyNodeParent.getChildren().remove(selectedForeignKeyNode);
    }

    private void sortAndFilterSchemas(String text) {
        if(text == null){
            ObservableList<TreeView<String>> sortedSchemaViews = schemaViews;
            FXCollections.sort(sortedSchemaViews, Comparator.comparing(view -> {
                if(view != null){
                    return ((TreeView<String>) view).getRoot().getValue();
                }
                return "";
            }));
            for(TreeView<String> schemaView : schemaViews){
                schemaView.setVisible(true);
            }
            vboxBrowser.getChildren().setAll(sortedSchemaViews);
            return;
        }
        // If the search query is not empty
        Comparator<TreeView<String>> comparator = new Comparator<TreeView<String>>() {
            @Override
            public int compare(TreeView<String> o1, TreeView<String> o2) {
                String firstSchemaName = o1.getRoot().getValue();
                String secondSchemaName = o2.getRoot().getValue();
                return Integer.compare(searchCharacterCount(secondSchemaName, text), searchCharacterCount(firstSchemaName, text));
            }
        };
        // Sorting tree views
        ObservableList<TreeView<String>> sortedSchemaViews = schemaViews;
        FXCollections.sort(sortedSchemaViews, comparator);

        //Filtering tree views
        for (TreeView<String> schemaView : sortedSchemaViews){
            if(!schemaView.getRoot().getValue().startsWith(searchObjects.getText())){
                schemaView.setVisible(false);
            }
        }
        vboxBrowser.getChildren().setAll(sortedSchemaViews);
    }

    private int searchCharacterCount(String text,String searchText){
        int length = Math.min(text.length(),searchText.length());
        int count = 0;
        for(int i = 0; i < length; i++){
            if(text.charAt(i) == searchText.charAt(i)) count++;
            else break;
        }
        return count;
    }

    public static boolean isChildOf(TreeItem<?> item, TreeItem<?> potentialParent) {

        if (item == null || potentialParent == null)
            return false;

        TreeItem<?> parent = item.getParent();

        if (parent == potentialParent) {
            return true;
        }

        while (parent != null) {
            if (parent == potentialParent)
                return true;
            parent = parent.getParent();
        }
        return false;
    }

    public static int getTreeItemDepth(TreeItem<?> item) {
        int depth = 0;
        TreeItem<?> current = item;

        while (current != null) {
            depth++;
            current = current.getParent();
        }

        return depth;
    }

    public double getBrowserHeight() {
        double browserHeight = 0;
        for (TreeView<String> schemaView : schemaViews) {
            browserHeight += TREE_CELL_HEIGHT;
            if (schemaView.getRoot().isExpanded()) {
                for (TreeItem<String> schemaItem : schemaView.getRoot().getChildren()) {
                    browserHeight += getItemHeight(schemaItem);
                }
            }
        }
        return browserHeight;
    }

    public double getItemHeight(TreeItem<String> treeItem) {
        double itemHeight = 0;
        itemHeight += TREE_CELL_HEIGHT;

        if (treeItem.isExpanded()) {
            for (TreeItem<String> child : treeItem.getChildren()) {
                itemHeight += getItemHeight(child);
            }
        }

        return itemHeight;
    }
}
