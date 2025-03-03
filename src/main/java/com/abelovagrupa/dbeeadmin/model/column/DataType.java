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
    GEOMETRY_COLLECTION;

}
