package com.aziks.reader;

import com.aziks.reader.utils.ExceptionHandler;
import com.aziks.reader.utils.HttpRequestUnsuccessful;
import com.aziks.reader.utils.LineNotFoundException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class Main extends Application {
  public static Library library = new Library();
  private static Stage primaryStage;

  public static void main(String[] args) {
    Settings.init();
    I18n.init(Settings.getLanguage());

    // Store the GUTINDEX.ALL file on first run or if an update is available
    GutScraper scraper = new GutScraper();
    try {
      if (Settings.getIsFirstRun()
          || (Settings.getUpdateOnStartup() && scraper.isUpdateAvailable())) {
        scraper.storeGutenbergIndex();
      }
    } catch (IOException e) {
      e.printStackTrace();
      ExceptionHandler.alertUser(I18n.getMessage("exception.IOException"));
    } catch (HttpRequestUnsuccessful e) {
      e.printStackTrace();
      ExceptionHandler.alertUser(I18n.getMessage("exception.httpRequestUnsuccessful"));
    } catch (LineNotFoundException e) {
      e.printStackTrace();
      ExceptionHandler.alertUser(I18n.getMessage("exception.lineNotFoundException"));
    }

    Settings.setIsFirstRun(false);

    launch();
  }

  public static Stage getPrimaryStage() {
    return primaryStage;
  }

  @Override
  public void start(Stage stage) throws IOException {
    primaryStage = stage;
    // Get translations
    ResourceBundle bundle = I18n.getBundle();

    String openedLibraryPath = Settings.getOpenedLibraryPath();
    FXMLLoader fxmlLoader;
    if (openedLibraryPath != null && Library.isLibrary(new File(openedLibraryPath))) {
      library.setPath(Path.of(openedLibraryPath));
      fxmlLoader =
          new FXMLLoader(getClass().getResource("/com/aziks/reader" + "/scenes/Root.fxml"), bundle);
    } else {
      fxmlLoader =
          new FXMLLoader(
              getClass().getResource("/com/aziks/reader" + "/scenes/Welcome.fxml"), bundle);
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
    library.closeDatabase();

    if (Settings.getKeepWindowSize()) {
      Settings.setWindowWidth(primaryStage.getWidth());
      Settings.setWindowHeight(primaryStage.getHeight());
      Settings.setWindowMaximized(primaryStage.isMaximized());
    }

    Path libraryPath = library.getPath();
    if (libraryPath == null) {
      Settings.setOpenedLibraryPath(null);
    } else {
      Settings.setOpenedLibraryPath(libraryPath.toString());
    }
  }
}
