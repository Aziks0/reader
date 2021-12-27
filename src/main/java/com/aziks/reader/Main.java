package com.aziks.reader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

public class Main extends Application {
  public static Library library = new Library();
  private static Stage app;

  public static void main(String[] args) {
    Settings.init();
    I18n.init(Settings.getLanguage());

    launch();
  }

  @Override
  public void start(Stage stage) throws IOException {
    app = stage;
    // Get translations
    ResourceBundle bundle = I18n.getBundle();

    FXMLLoader fxmlLoader;
    if (Settings.getOpenedLibraryPath() == null) {
      fxmlLoader =
          new FXMLLoader(
              getClass().getResource("/com/aziks/reader" + "/scenes/Welcome.fxml"), bundle);
    } else {
      fxmlLoader =
          new FXMLLoader(getClass().getResource("/com/aziks/reader" + "/scenes/Root.fxml"), bundle);
    }

    Scene scene = new Scene(fxmlLoader.load());
    stage.setTitle("Reader");
    if (Settings.getKeepWindowSize()) {
      stage.setWidth(Settings.getWindowWidth());
      stage.setHeight(Settings.getWindowHeight());
      stage.setMaximized(Settings.getWindowMaximized());
    }
    stage.setScene(scene);
    stage.show();
  }

  @Override
  public void stop() throws Exception {
    if (Settings.getKeepWindowSize()) {
      Settings.setWindowWidth(app.getWidth());
      Settings.setWindowHeight(app.getHeight());
      Settings.setWindowMaximized(app.isMaximized());
    }
  }
}
