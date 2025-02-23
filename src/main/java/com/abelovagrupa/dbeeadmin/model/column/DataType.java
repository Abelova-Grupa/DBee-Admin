package com.abelovagrupa.dbeeadmin.model.column;

public enum DataType {

    BIGINT(8),
    BINARY(16),
    BIT(1),
    CHAR(10),
    DATETIME(8),
    DECIMAL(10),
    DOUBLE(8),
    FLOAT(8),
    INT(4),
    NUMERIC(10),
    REAL(4),
    SMALLINT(2),
    TIME(6),
    TINYINT(1),
    VARBINARY(255),
    VARCHAR(255),
    YEAR(4);

    // FIXME: No enum constant com.abelovagrupa.dbeeadmin.model.column.DataType.DATE
    // FIXME: No enum constant com.abelovagrupa.dbeeadmin.model.column.DataType.MEDIUMTEXT
    // FIXME: No enum constant com.abelovagrupa.dbeeadmin.model.column.DataType.TEXT
    // FIXME: No enum constant com.abelovagrupa.dbeeadmin.model.column.DataType.TINYTEXT
    // FIXME: No enum constant com.abelovagrupa.dbeeadmin.model.column.DataType.BLOB
    // FIXME: No enum constant com.abelovagrupa.dbeeadmin.model.column.DataType.LONGBLOB
    // FIXME: No enum constant com.abelovagrupa.dbeeadmin.model.column.DataType.SET
    // FIXME: No enum constant com.abelovagrupa.dbeeadmin.model.column.DataType.ENUM
    // FIXME: No enum constant com.abelovagrupa.dbeeadmin.model.column.DataType.TIMESTAMP
    // FIXME: No enum constant com.abelovagrupa.dbeeadmin.model.column.DataType.LONGTEXT

    private final int typeSize;

    DataType(int typeSize) {
        this.typeSize = typeSize;
    }

    public int getTypeSize() {
        return typeSize;
    }


}
