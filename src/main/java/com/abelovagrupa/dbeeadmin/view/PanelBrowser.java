package com.abelovagrupa.dbeeadmin.view;

import com.abelovagrupa.dbeeadmin.Main;
import com.abelovagrupa.dbeeadmin.controller.DatabaseInspector;
import com.abelovagrupa.dbeeadmin.model.column.Column;
import com.abelovagrupa.dbeeadmin.model.foreignkey.ForeignKey;
import com.abelovagrupa.dbeeadmin.model.index.Index;
import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.model.table.Table;
import com.abelovagrupa.dbeeadmin.model.view.View;
import com.abelovagrupa.dbeeadmin.services.ProgramState;
import com.abelovagrupa.dbeeadmin.services.QueryExecutor;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

    private List<PanelSchemaTree> schemaControllers;

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

    public List<PanelSchemaTree> getSchemaControllers() {
        return schemaControllers;
    }

    public void setSchemaControllers(List<PanelSchemaTree> schemaControllers) {
        this.schemaControllers = schemaControllers;
    }

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TODO: WRITE COMMENTS FROM THIS METHOD
        schemaControllers = new LinkedList<>();
        schemaViews = FXCollections.observableArrayList();
        List<String> schemaNames = DatabaseInspector.getInstance().getDatabaseNames();
        for (String schemaName : schemaNames) {
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


    }

    public TreeView<String> loadSchemaView(FXMLLoader loader, String schemaName) {
        final int TREE_CELL_HEIGHT = 30;
        try {
            TreeView<String> schemaView = loader.load();
            schemaView.setPrefHeight(TREE_CELL_HEIGHT);
            // Adding schema controller to the list
            schemaControllers.add(loader.getController());

            // Creating a root node with its first children tables, views, stored procedures and functions
            TreeItem<String> schemaNode = new TreeItem<>(schemaName, new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database.png").toExternalForm())));
            schemaView.setRoot(schemaNode);
            TreeItem<String> tableBranch = new TreeItem<>("Tables", new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));
            TreeItem<String> viewBranch = new TreeItem<>("Views", new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));
            TreeItem<String> procedureBranch = new TreeItem<>("Stored Procedures", new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));
            TreeItem<String> functionBranch = new TreeItem<>("Functions", new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-management.png").toExternalForm())));

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
            ChangeListener<Boolean> viewBranchListener = new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean odlValue, Boolean newValue) {
                    if(newValue) {
                        Schema schema = DatabaseInspector.getInstance().getDatabaseByName(schemaName);
                        List<String> viewNames = DatabaseInspector.getInstance().getViewNames(schema);
                        viewBranch.getChildren().remove(tableDummyNode);
                        for(var v : viewNames) {
                            viewBranch.getChildren().add(new TreeItem<>(v, new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/view.png").toExternalForm()))));
                        }
                    }
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
                            TreeItem<String> tableNode = new TreeItem<>(tableName, new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/database-table.png").toExternalForm())));
                            TreeItem<String> columnBranch = new TreeItem<>("Columns", new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/columns.png").toExternalForm())));
                            TreeItem<String> columnDummy = new TreeItem<>("Column Dummy");
                            columnBranch.getChildren().add(columnDummy);

                            // Same logic applies to column expansion listener
                            ChangeListener<Boolean> columnBranchListener = new ChangeListener<Boolean>() {
                                @Override
                                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                    if (newValue) {
                                        columnBranch.getChildren().remove(columnDummy);
                                        Table table = DatabaseInspector.getInstance().getTableByName(schema, tableName);
                                        for (Column column : table.getColumns()) {
                                            // Adding key icon to primary key columns. Aesthetic :)
                                            String columnName = column.isPrimaryKey() ? column.getName() + " (\uD83D\uDD11)" : column.getName();
                                            TreeItem<String> columnNode = new TreeItem<>(columnName);
                                            columnBranch.getChildren().add(columnNode);
                                        }
                                        // Removing column listener
                                        columnBranch.expandedProperty().removeListener(this);
                                    }
                                }
                            };
                            columnBranch.expandedProperty().addListener(columnBranchListener);

                            // Adding index branch with dummy child
                            TreeItem<String> indexBranch = new TreeItem<>("Indexes", new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/indexes.png").toExternalForm())));
                            TreeItem<String> indexDummy = new TreeItem<>("Index dummy");
                            indexBranch.getChildren().add(indexDummy);

                            // Same logic applies for index expansion listener
                            ChangeListener<Boolean> indexBranchListener = new ChangeListener<Boolean>() {
                                @Override
                                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                    if (newValue) {
                                        indexBranch.getChildren().remove(indexDummy);
                                        Table table = DatabaseInspector.getInstance().getTableByName(schema, tableName);
                                        List<Index> indexes = DatabaseInspector.getInstance().getIndexes(schema, table);
                                        for (Index index : indexes) {
                                            TreeItem<String> indexNode = new TreeItem<>(index.getName());
                                            indexBranch.getChildren().add(indexNode);
                                        }
                                        // Removing index listener after first usage
                                        indexBranch.expandedProperty().removeListener(this);
                                    }
                                }
                            };
                            indexBranch.expandedProperty().addListener(indexBranchListener);

                            // Adding foreign key branch with dummy child
                            TreeItem<String> foreignKeyBranch = new TreeItem<>("Foreign Keys", new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/foreignkeys.png").toExternalForm())));
                            TreeItem<String> foreignKeyDummy = new TreeItem<>("ForeignKeyDummy");
                            foreignKeyBranch.getChildren().add(foreignKeyDummy);

                            // Same logic as before for this listener
                            ChangeListener<Boolean> foreignKeyListener = new ChangeListener<Boolean>() {
                                @Override
                                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                                    if (newValue) {
                                        foreignKeyBranch.getChildren().remove(foreignKeyDummy);
                                        Table table = DatabaseInspector.getInstance().getTableByName(schema, tableName);
                                        List<ForeignKey> foreignKeys = DatabaseInspector.getInstance().getForeignKeys(schema, table);
                                        for (ForeignKey foreignKey : foreignKeys) {
                                            TreeItem<String> foreignKeyNode = new TreeItem<>(foreignKey.getName());
                                            foreignKeyBranch.getChildren().add(foreignKeyNode);
                                        }
                                        // Removing listener after first usage
                                        foreignKeyBranch.expandedProperty().removeListener(this);
                                    }
                                }
                            };
                            foreignKeyBranch.expandedProperty().addListener(foreignKeyListener);

                            // Not implemented yet
                            TreeItem<String> triggersBranch = new TreeItem<>("Triggers", new ImageView(new Image(getClass().getResource("/com/abelovagrupa/dbeeadmin/images/triggers.png").toExternalForm())));

                            tableNode.getChildren().addAll(columnBranch, indexBranch, foreignKeyBranch, triggersBranch);
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
                    // Fetch schema name
                    selectedSchemaName = schemaView.getRoot().getValue();
                    // Display active schema in info panel and set in programState
                    Schema selectedSchema = DatabaseInspector.getInstance().getDatabaseByName(selectedSchemaName);
                    infoController.setSelected(selectedSchema);
                    ProgramState.getInstance().setSelectedSchema(selectedSchema);

                    if(event.getClickCount() == 2){

                    }
                }

                Optional<TreeItem<String>> selectedItemOptional = Optional.ofNullable(schemaView.getSelectionModel().getSelectedItem());
                Schema schema = DatabaseInspector.getInstance().getDatabaseByName(schemaName);

                if (selectedItemOptional.isPresent()) {
                    TreeItem<String> selectedItem = selectedItemOptional.get();
                    Optional<Table> selectedTableOptional = schema.getTables().stream().filter(s -> s.getName().equals(selectedItem.getValue())).findFirst();
                    if (selectedTableOptional.isPresent()) {
                        Table selectedTable = selectedTableOptional.get();
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

                            if(event.getButton() == MouseButton.SECONDARY) {
                                // Table context menu
                                ContextMenu contextMenu = new ContextMenu();
                                MenuItem viewTable = new MenuItem("View Data (SELECT *)");
                                MenuItem editTable = new MenuItem("Edit Table");
                                MenuItem deleteTable = new MenuItem("Delete Table");
                                MenuItem addColumn = new MenuItem("Add Column");
                                MenuItem generateTableSQL = new MenuItem("Generate Table SQL");

                                viewTable.setOnAction(tblClick -> displaySelectedTable(selectedTable));
//                                editTable.setOnAction(tblClick -> System.out.println("Editing table..."));
//                                deleteTable.setOnAction(tblClick -> System.out.println("Deleting table..."));
//                                addColumn.setOnAction(tblClick -> System.out.println("Adding column to table..."));
//                                generateTableSQL.setOnAction(tblClick -> System.out.println("Generating SQL for table..."));

                                contextMenu.getItems().addAll(viewTable, editTable, deleteTable, addColumn, generateTableSQL);
                                contextMenu.show((Node) event.getSource(), event.getScreenX(), event.getScreenY());
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
                            }
                        }


                        if (selectedItem.getParent().getValue().equals("Indexes") && getTreeItemDepth(selectedItem) == 5) {
                            // TODO: make it efficient, for now it works
                            String indexName = selectedItem.getValue();
                            Table table = DatabaseInspector.getInstance().getTableByName(schema, selectedItem.getParent().getParent().getValue());
                            Index selectedIndex = DatabaseInspector.getInstance().getIndexByName(schema, table, indexName);
                            if (selectedIndex != null) {
                                infoController.setSelected(selectedIndex);
                            }

                        }

                        if (selectedItem.getParent().getValue().equals("Foreign Keys")) {
                            String foreignKeyName = selectedItem.getValue();
                            Table table = DatabaseInspector.getInstance().getTableByName(schema, selectedItem.getParent().getParent().getValue());
                            ForeignKey selectedForeignKey = DatabaseInspector.getInstance().getForeignKeyByName(schema, table, foreignKeyName);
                            if (selectedForeignKey != null) {
                                // TODO: Foreign Keys
                                infoController.setSelected(selectedForeignKey);
                            }

                        }
                    } catch (NullPointerException e) {
                        logger.warn("No parent value for selected item...");
                    }
                }
            });

            return schemaView;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
                if(searchCharacterCount(firstSchemaName,text) > searchCharacterCount(secondSchemaName,text)){
                    return -1;
                }else if (searchCharacterCount(firstSchemaName,text) < searchCharacterCount(secondSchemaName,text)){
                    return 1;
                }else return 0;
            }
        };
        // Sorting tree views
        ObservableList<TreeView<String>> sortedSchemaViews = schemaViews;
        FXCollections.sort(sortedSchemaViews, comparator::compare);

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
