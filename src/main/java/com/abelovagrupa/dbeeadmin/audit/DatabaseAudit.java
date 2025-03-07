package com.abelovagrupa.dbeeadmin.audit;

import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.model.table.Table;

public class DatabaseAudit {
    private static DatabaseAudit instance = null;

    private Schema selectedSchema;
    private Table selectedTable;

    private DatabaseAudit() {}

    public static DatabaseAudit getInstance() {
        if(instance==null) {
            instance = new DatabaseAudit();
        }
        return instance;
    }

    public Schema getSelectedSchema() {
        return selectedSchema;
    }

    public void setSelectedSchema(Schema selectedSchema) {
        this.selectedSchema = selectedSchema;
    }

    public Table getSelectedTable() {
        return selectedTable;
    }

    public void setSelectedTable(Table selectedTable) {
        this.selectedTable = selectedTable;
    }

    @Override
    public String toString() {
        return "DatabaseAudit{" +
                "selectedSchema=" + selectedSchema +
                ", selectedTable=" + selectedTable +
                '}';
    }
}
