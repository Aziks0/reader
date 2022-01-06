package com.aziks.reader.controllers;

import com.aziks.reader.I18n;
import com.aziks.reader.Settings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

abstract class Controller {
  protected void changeScene(Stage stage, String resourcePath) throws IOException {
    ResourceBundle bundle = I18n.getBundle();
    FXMLLoader fxmlLoader =
        new FXMLLoader(getClass().getResource("/com/aziks/reader" + resourcePath), bundle);
    Scene scene = new Scene((fxmlLoader.load()));

    if (Settings.getKeepWindowSize()) {
      stage.setWidth(Settings.getWindowWidth());
      stage.setHeight(Settings.getWindowHeight());
      stage.setMaximized(Settings.getWindowMaximized());
    }
    stage.setScene(scene);
    stage.show();
  }

  /**
   * Show a directory chooser dialog
   *
   * @param stage A window stage
   * @return The selected directory or null if no directory has been selected
   * @see javafx.stage.DirectoryChooser#showDialog
   */
  protected File getDirectory(Stage stage) {
    DirectoryChooser dirChooser = new DirectoryChooser();
    return dirChooser.showDialog(stage);
  }
}
