package com.abelovagrupa.dbeeadmin.services;

import com.abelovagrupa.dbeeadmin.model.schema.Schema;
import com.abelovagrupa.dbeeadmin.model.table.Table;

public class ProgramState {

    private static ProgramState instance = null;

    private Schema selectedSchema;
    private Table selectedTable;

    private ProgramState() {
        System.out.println("Initializing program state.");
    }

    public static ProgramState getInstance() {
        if(instance==null) {
            instance = new ProgramState();
        }
        return instance;
    }

    public void clearSelection() {
        this.selectedSchema = null;
        this.selectedTable = null;
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
