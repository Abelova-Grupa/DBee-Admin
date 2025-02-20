package com.abelovagrupa.dbeeadmin.services;

import com.abelovagrupa.dbeeadmin.model.Schema;

public class ProgramState {

    private static ProgramState instance;
    private Schema currentSchema;

    private ProgramState() {}

    public static ProgramState getInstance() {
        if(instance == null) instance = new ProgramState();
        return instance;
    }

    public Schema getCurrentSchema() {
        return currentSchema;
    }

    public void setCurrentSchema(Schema currentSchema) {
        this.currentSchema = currentSchema;
    }

}
