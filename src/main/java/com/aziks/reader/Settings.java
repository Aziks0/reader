package com.aziks.reader;

import java.util.Locale;
import java.util.prefs.Preferences;

public class Settings {
  private final String[] availableLanguages = {"EN", "FR"};
  private final Preferences userPreferences;

  public Settings() {
    this.userPreferences = Preferences.userNodeForPackage(getClass());
  }

  /** Set default settings for every setting not set */
  public void init() {
    // Set default language if settings does not exist
    if (this.userPreferences.get("language", null) == null) {
      // Use user language if translations are available for it
      String locale = Locale.getDefault().toString();
      for (String language : this.availableLanguages) {
        if (locale.contains(language)) {
          this.userPreferences.put("language", language);
          break;
        }
      }
      // Else, use english as default
      if (this.getLanguage() == null) this.userPreferences.put("language", "EN");
    }
  }

  /**
   * Get language settings
   *
   * @return The user language
   */
  public String getLanguage() {
    return this.userPreferences.get("language", null);
  }

  /**
   * Get the path to the library which was open when the user last exited the app
   *
   * @return A path to a library or null
   */
  public String getOpenedLibraryPath() {
    return this.userPreferences.get("lastLibraryPath", null);
  }
}
