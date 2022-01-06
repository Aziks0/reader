package com.aziks.reader.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatcher {
  private final Pattern pattern;
  private final String exception;

  public RegexMatcher(String regex) {
    this(regex, null);
  }

  public RegexMatcher(String regex, String exception) {
    this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    this.exception = exception;
  }

  public boolean matches(String text) {
    Matcher matcher = this.pattern.matcher(text);
    if (matcher.matches()) return true;
    if (this.exception != null) yell();
    return false;
  }

  private void yell() {
    ExceptionHandler.alertUser(this.exception);
  }
}
