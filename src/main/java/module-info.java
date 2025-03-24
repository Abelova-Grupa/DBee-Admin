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


    opens com.abelovagrupa.dbeeadmin to javafx.fxml;
    exports com.abelovagrupa.dbeeadmin;
    opens com.abelovagrupa.dbeeadmin.view to javafx.fxml;
    opens com.abelovagrupa.dbeeadmin.model.index to javafx.base;
}