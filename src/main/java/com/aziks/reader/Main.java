package com.aziks.reader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ResourceBundle;

public class Main extends Application {

  private static Settings settings;

  public static void main(String[] args) {
    settings = new Settings();
    settings.init();
    launch();
  }

  @Override
  public void start(Stage stage) throws IOException {
    // Get translations
    String language = settings.getLanguage();
    I18n i18n = new I18n(language);
    ResourceBundle bundle = i18n.getBundle();

    FXMLLoader fxmlLoader =
        new FXMLLoader(
            getClass().getResource("/com" + "/aziks/reader" + "/scenes/Welcome.fxml"), bundle);
    Scene scene = new Scene(fxmlLoader.load());
    stage.setTitle("Reader");
    stage.setScene(scene);
    stage.show();
  }
}
