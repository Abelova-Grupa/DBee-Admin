module com.abelovagrupa.dbeeadmin {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.abelovagrupa.dbeeadmin to javafx.fxml;
    exports com.abelovagrupa.dbeeadmin;
}