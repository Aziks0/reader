package com.aziks.reader;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {
  private ResourceBundle bundle;

  /**
   * Create an i18n instance to get translated messages
   *
   * @param language The language in which messages will be written. Must be a valid Locale language
   *     value.
   * @see java.util.Locale
   */
  public I18n(String language) {
    Locale locale = new Locale(language);
    bundle = ResourceBundle.getBundle("com.aziks.reader.Locales", locale);
  }

  /**
   * Get a translated message
   *
   * @param key The message key
   * @return A translated message
   */
  public String getMessage(String key) {
    return bundle.getString(key);
  }

  public ResourceBundle getBundle() {
    return bundle;
  }
}
