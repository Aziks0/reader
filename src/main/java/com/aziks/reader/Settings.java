package com.aziks.reader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.prefs.Preferences;

public class Settings {
  private static final String[] availableLanguages = {"EN", "FR"};
  private static final Preferences userPreferences = Preferences.userNodeForPackage(Settings.class);
  private static Path homeReader;

  /** Set default settings for every setting not set */
  public static void init() {
    // Set default language if setting does not exist
    if (userPreferences.get("language", null) == null) {
      // Use user language if translations are available for it
      String locale = Locale.getDefault().toString();
      for (String language : availableLanguages) {
        if (locale.contains(language)) {
          userPreferences.put("language", language);
          break;
        }
      }
      // Else, use english as default
      if (getLanguage() == null) userPreferences.put("language", "EN");
    }
  }

  /**
   * Get language settings
   *
   * @return The user language
   */
  public static String getLanguage() {
    return userPreferences.get("language", "EN");
  }

  public static void setLanguage(String language) {
    userPreferences.put("language", language);
  }

  /**
   * Get the path to the library which was open when the user last exited the app
   *
   * @return A path to a library or null
   */
  public static String getOpenedLibraryPath() {
    return userPreferences.get("lastLibraryPath", null);
  }

  public static void setOpenedLibraryPath(String path) {
    userPreferences.put("lastLibraryPath", path);
  }

  public static boolean getKeepWindowSize() {
    return userPreferences.getBoolean("keepWindowSize", false);
  }

  public static double getWindowWidth() {
    return userPreferences.getDouble("windowWidth", 1000);
  }

  public static void setWindowWidth(double width) {
    userPreferences.putDouble("windowWidth", width);
  }

  public static double getWindowHeight() {
    return userPreferences.getDouble("windowHeight", 800);
  }

  public static void setWindowHeight(double height) {
    userPreferences.putDouble("windowHeight", height);
  }

  public static boolean getWindowMaximized() {
    return userPreferences.getBoolean("windowMaximized", false);
  }

  public static void setWindowMaximized(boolean fullscreen) {
    userPreferences.putBoolean("windowMaximized", fullscreen);
  }
}
