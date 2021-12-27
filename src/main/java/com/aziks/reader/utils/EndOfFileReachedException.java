package com.aziks.reader.utils;

public class EndOfFileReachedException extends ExceptionHandler {
  public EndOfFileReachedException() {
    super("End of file has been reached too soon");
  }
}
