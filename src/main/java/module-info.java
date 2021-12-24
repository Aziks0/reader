module com.aziks.reader {
    requires javafx.controls;
    requires javafx.fxml;
  requires java.prefs;

    requires org.kordamp.ikonli.javafx;

    opens com.aziks.reader to javafx.fxml;
    exports com.aziks.reader;
}