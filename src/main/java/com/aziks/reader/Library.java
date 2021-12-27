package com.aziks.reader;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Library {
  private static final String booksDirectoryName = "books";
  private static final Database database = new Database("reader.db");
  private Path _path;

  /**
   * Check if a directory is a Reader library
   *
   * @param path A path to a directory
   * @return True if the directory is a Reader library, false otherwise
   */
  public static boolean isLibrary(File path) {
    String[] _files = path.list();
    if (_files == null) return false;

    List<String> files = Arrays.asList(_files);
    return files.contains(database.getDatabaseFileName()) && files.contains(booksDirectoryName);
  }

  public Path getPath() {
    return this._path;
  }

  public void setPath(Path path) {
    this._path = path;
  }
}
