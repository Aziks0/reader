package com.aziks.reader;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {
  private static ResourceBundle bundle;

  /**
   * Initialize i18n to get translated messages
   *
   * @param language The language in which messages will be written. Must be a valid Locale language
   *     value.
   * @see java.util.Locale
   */
  public static void init(String language) {
    Locale locale = new Locale(language);
    bundle = ResourceBundle.getBundle("com.aziks.reader.Locales", locale);
  }

  /**
   * Get a translated message
   *
   * @param key The message key
   * @return A translated message
   */
  public static String getMessage(String key) {
    return bundle.getString(key);
  }

  public static ResourceBundle getBundle() {
    return bundle;
  }
}
