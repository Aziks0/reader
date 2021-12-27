package com.aziks.reader.utils;

import com.aziks.reader.I18n;
import javafx.scene.control.Alert;

public class ExceptionHandler extends Exception {
  public ExceptionHandler(String errorMessage) {
    super(errorMessage);
  }

  public static void alertUser(String errorMessage) {
    Alert alert = new Alert(Alert.AlertType.ERROR, errorMessage);
    alert.setTitle(I18n.getMessage("exception.encountered"));
    alert.showAndWait();
  }
}
