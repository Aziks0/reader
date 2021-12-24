package com.aziks.reader.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class Header extends VBox {
  public Header() throws IOException {
    FXMLLoader fxmlLoader =
        new FXMLLoader(getClass().getResource("/com/aziks/reader" + "/components/Header.fxml"));
    fxmlLoader.setRoot(this);
    fxmlLoader.setController(this);
    fxmlLoader.load();
  }
}
