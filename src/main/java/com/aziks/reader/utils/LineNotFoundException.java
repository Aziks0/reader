package com.aziks.reader.utils;

public class LineNotFoundException extends ExceptionHandler {
  public LineNotFoundException() {
    super("Line was not found");
  }
}
