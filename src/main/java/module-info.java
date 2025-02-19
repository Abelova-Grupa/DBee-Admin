module com.abelovagrupa.dbeeadmin {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.fxmisc.richtext;
    requires java.sql;
    requires java.dotenv;

    opens com.abelovagrupa.dbeeadmin to javafx.fxml;
    exports com.abelovagrupa.dbeeadmin;
    opens com.abelovagrupa.dbeeadmin.view to javafx.fxml;
}