package com.aziks.reader.controllers;

import com.aziks.reader.I18n;
import com.aziks.reader.Library;
import com.aziks.reader.MainApp;
import com.aziks.reader.components.PopupAbout;
import com.aziks.reader.components.PopupSettings;
import com.aziks.reader.utils.DirectoryNotCreatedException;
import com.aziks.reader.utils.ExceptionHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static com.aziks.reader.MainApp.library;

public class WelcomeController extends Controller {
  @FXML
  public void handleCreateLibraryButtonAction(ActionEvent event) throws IOException {
    File directory = getDirectory(MainApp.getPrimaryStage());
    if (directory == null) return;

    // Show an error if the directory is not empty
    if (directory.list().length != 0) {
      Alert alert =
          new Alert(Alert.AlertType.ERROR, I18n.getMessage("welcome.alertDirectoryNotEmpty"));
      alert.showAndWait();
      return;
    }

    try {
      library.createLibrary(directory.toPath());
    } catch (DirectoryNotCreatedException ex) {
      ex.printStackTrace();
      ExceptionHandler.alertUser(I18n.getMessage("exception.directoryNotCreated"));
      return;
    } catch (SQLException ex) {
      ex.printStackTrace();
      ExceptionHandler.alertUser(I18n.getMessage("exception.databaseInitFailed"));
      return;
    }

    library.setPath(directory.toPath());
    changeScene(MainApp.getPrimaryStage(), "/scenes/Root.fxml");
  }

  @FXML
  public void handleOpenLibraryButtonAction(ActionEvent event) throws IOException {
    File directory = getDirectory(MainApp.getPrimaryStage());
    if (directory == null) return;

    if (!Library.isLibrary(directory)) {
      Alert alert = new Alert(Alert.AlertType.ERROR, I18n.getMessage("welcome.alertNotALibrary"));
      alert.showAndWait();
      return;
    }

    library.setPath(directory.toPath());
    changeScene(MainApp.getPrimaryStage(), "/scenes/Root.fxml");
  }

  public void handleCloseButtonAction(ActionEvent actionEvent) {
    MainApp.getPrimaryStage().close();
  }

  public void handleSettingsButtonAction(ActionEvent actionEvent) {
    PopupSettings popupSettings = new PopupSettings();
    popupSettings.show(MainApp.getPrimaryStage());
  }

  public void handleAboutButtonAction(ActionEvent actionEvent) {
    PopupAbout popupAbout = new PopupAbout();
    popupAbout.show(MainApp.getPrimaryStage());
  }
}
