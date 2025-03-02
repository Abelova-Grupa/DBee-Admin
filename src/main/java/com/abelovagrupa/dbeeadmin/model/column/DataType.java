package com.abelovagrupa.dbeeadmin.model.column;

public enum DataType {

//    BIGINT(8),
//    BINARY(16),
//    BIT(1),
//    CHAR(10),
//    DATETIME(8),
//    DECIMAL(10),
//    DOUBLE(8),
//    FLOAT(8),
//    INT(4),
//    NUMERIC(10),
//    REAL(4),
//    SMALLINT(2),
//    TIME(6),
//    TINYINT(1),
//    VARBINARY(255),
//    VARCHAR(255),
//    YEAR(4),
//    // variable length
//    TIMESTAMP(100),
//    SET(100),
//    TEXT(100),
//    ENUM(100),
//    MEDIUMTEXT(100),
//    MEDIUMBLOB(100),
//    JSON(1000),
//    BLOB(1000),
//    LONGTEXT(1000),
//    LONGBLOB(1000),
//    GEOMETRY(1000),
//    MEDIUMINT(1000),
//    DATE(10);

    // PLAN THIS ENUM WILL ONLY HAVE PREDEFINED VALUES
    // DATATYPES WHOS SIZE IS DINAMICALLY DETERMINED IS GOING TO BE CALCUlATED IN A DIFFERENT WAY
    // Integer types
    TINYINT(1),
    SMALLINT(2),
    MEDIUMINT(3),
    INT(4),
    BIGINT(8),
    // Fixed-Point types
    DECIMAL(0),
    NUMERIC(0),
    FLOAT(4),
    DOUBLE(8),

    // BIT type
    BIT(8),

    // DATE and TIME types
    DATE(3),
    TIME(3),
    DATETIME(8),
    TIMESTAMP(4),
    YEAR(1),

    // String data types
    CHAR(0),
    VARCHAR(0),
    TINYBLOB(255),
    BLOB(65535),
    MEDIUMBLOB(16777215),
    LONGBLOB(4294967295L),
    TINYTEXT(255),
    TEXT(65535),
    MEDIUMTEXT(16777215),
    LONGTEXT(4294967295L),
    ENUM(0),
    SET(0),
    JSON(0),

    // Geo-data type
    POINT(32),
    LINESTRING_2D(16),
    POLYGON_2D(32),
    LINESTRING_3D(24),
    POLYGON_3D(40),
    MULTIPOINT(0),
    GEOMETRY_COLLECTION(0);

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

    private final double typeSize;

    DataType(double typeSize) {
        this.typeSize = typeSize;
    }

    public double getTypeSize() {
        return typeSize;
    }


}
