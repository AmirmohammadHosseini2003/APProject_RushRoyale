module com.example.rushroyalegame {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.jsobject;
    requires org.json;


    opens com.example.rushroyalegame to javafx.fxml;
    exports com.example.rushroyalegame;
}