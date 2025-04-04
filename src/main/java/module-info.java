module com.abelovagrupa.dbeeadmin {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires java.sql;
    requires java.dotenv;
    requires java.desktop;
    requires annotations;
    requires log4j.api;


    opens com.abelovagrupa.dbeeadmin to javafx.fxml;
    exports com.abelovagrupa.dbeeadmin;
    opens com.abelovagrupa.dbeeadmin.view to javafx.fxml;
    opens com.abelovagrupa.dbeeadmin.model.index to javafx.base;
    opens com.abelovagrupa.dbeeadmin.model.column to javafx.base;
    opens com.abelovagrupa.dbeeadmin.model.foreignkey to javafx.base;

}