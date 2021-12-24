package com.aziks.reader.controllers;

import com.aziks.reader.I18n;
import com.aziks.reader.Settings;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

abstract class Controller {
  protected static final Settings settings;
  protected static final I18n i18n;

  static {
    settings = new Settings();
    String language = settings.getLanguage();
    i18n = new I18n(language);
  }

  /**
   * Get the window stage from an ActionEvent
   *
   * @param event An ActionEvent
   * @return The window stage
   */
  protected Stage getStage(ActionEvent event) {
    Node node = (Node) event.getSource();
    return (Stage) node.getScene().getWindow();
  }

  protected File getDirectory(ActionEvent event) {
    Stage currentStage = getStage(event);
    DirectoryChooser dirChooser = new DirectoryChooser();
    return dirChooser.showDialog(currentStage);
  }
}
