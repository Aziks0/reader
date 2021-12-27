module com.aziks.reader {
  requires javafx.controls;
  requires javafx.fxml;
  requires org.kordamp.ikonli.javafx;
  requires java.prefs;
  requires java.sql;
  requires java.net.http;

  opens com.aziks.reader to
      javafx.fxml;

  exports com.aziks.reader;
  exports com.aziks.reader.controllers;
  exports com.aziks.reader.utils;
  exports com.aziks.reader.components;
}
