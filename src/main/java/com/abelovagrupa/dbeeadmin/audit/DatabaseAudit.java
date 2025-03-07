package com.abelovagrupa.dbeeadmin.audit;

public class DatabaseAudit {
    private static DatabaseAudit instance = null;

    private String selectedDatabase;
    private String selectedSchema;
    private String selectedTable;

    private DatabaseAudit() {}

    public static DatabaseAudit getInstance() {
        if(instance==null) {
            instance = new DatabaseAudit();
        }
        return instance;
    }

    public String getSelectedDatabase() {
        return selectedDatabase;
    }

    public void setSelectedDatabase(String selectedDatabase) {
        this.selectedDatabase = selectedDatabase;
    }

    public String getSelectedSchema() {
        return selectedSchema;
    }

    public void setSelectedSchema(String selectedSchema) {
        this.selectedSchema = selectedSchema;
    }

    public String getSelectedTable() {
        return selectedTable;
    }

    public void setSelectedTable(String selectedTable) {
        this.selectedTable = selectedTable;
    }

    @Override
    public String toString() {
        return "DatabaseAudit{" +
                "selectedDatabase='" + selectedDatabase + '\'' +
                ", selectedSchema='" + selectedSchema + '\'' +
                ", selectedTable='" + selectedTable + '\'' +
                '}';
    }
}
