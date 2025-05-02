package com.abelovagrupa.dbeeadmin.model.column;

public enum DataType {

    TINYINT,
    SMALLINT,
    MEDIUMINT,
    INT,
    BIGINT,
    // Fixed-Point types
    DECIMAL,
    NUMERIC,
    FLOAT,
    DOUBLE,

    // BIT type
    BIT,
    BINARY,
    VARBINARY,

    // DATE and TIME types
    DATE,
    TIME,
    DATETIME,
    TIMESTAMP,
    YEAR,

    // String data types
    CHAR,
    VARCHAR,
    TINYBLOB,
    BLOB,
    MEDIUMBLOB,
    LONGBLOB,
    TINYTEXT,
    TEXT,
    MEDIUMTEXT,
    LONGTEXT,
    ENUM,
    SET,
    JSON,

    // Geo-data type
    POINT,
    LINESTRING_2D,
    POLYGON_2D,
    LINESTRING_3D,
    POLYGON_3D,
    MULTIPOINT,
    GEOMETRY,
    GEOMETRY_COLLECTION,

    BOOLEAN;

    public static boolean hasVariableLength(DataType dataType){
        switch (dataType) {
            // Variable-length types
            case VARCHAR:
            case VARBINARY:
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB:
            case LONGBLOB:
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT:
            case LONGTEXT:
            case ENUM:
            case SET:
            case JSON:
            case GEOMETRY:
            case GEOMETRY_COLLECTION:
            case POINT:
            case LINESTRING_2D:
            case POLYGON_2D:
            case LINESTRING_3D:
            case POLYGON_3D:
            case MULTIPOINT:
                return true;

            // Fixed-length types
            case TINYINT:
            case SMALLINT:
            case MEDIUMINT:
            case INT:
            case BIGINT:
            case DECIMAL:
            case NUMERIC:
            case FLOAT:
            case DOUBLE:
            case BIT:
            case BINARY:
            case DATE:
            case TIME:
            case DATETIME:
            case TIMESTAMP:
            case YEAR:
            case CHAR:
            case BOOLEAN:
                return false;

            // Handle unexpected cases
            default:
                throw new IllegalArgumentException("Unknown SQL data type: " + dataType);
        }
    }

}
