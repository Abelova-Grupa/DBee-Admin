package com.abelovagrupa.dbeeadmin.model.view;

import com.abelovagrupa.dbeeadmin.model.schema.Schema;

public class View {
    private String name;
    private String definition;
    private Schema schema;

    public View(Schema schema, String name, String definition) {
        this.name = name;
        this.definition = definition;
        this.schema = schema;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    @Override
    public String toString() {
        return name;
    }
}
