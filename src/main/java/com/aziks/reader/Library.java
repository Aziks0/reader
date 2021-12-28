package com.aziks.reader;

import com.aziks.reader.utils.DirectoryNotCreatedException;

import java.io.File;
import java.nio.file.Path;
import java.sql.SQLException;
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

  /**
   * TODO write doc
   *
   * @param path The path to the library directory
   * @throws DirectoryNotCreatedException
   * @throws SQLException
   */
  public void createLibrary(Path path) throws DirectoryNotCreatedException, SQLException {
    // Create books directory
    File booksFile = path.resolve(booksDirectoryName).toFile();
    if (!booksFile.mkdir()) throw new DirectoryNotCreatedException();

    // Create library database
    String sqlInit =
        "CREATE TABLE IF NOT EXISTS books ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "title VARCHAR(255) NOT NULL"
            + ")";
    database.connectToDatabase(path);
    database.executeUpdate(sqlInit);
  }

  public void closeDatabase() throws SQLException {
    database.close();
  }

  public Path getPath() {
    return this._path;
  }

  public void setPath(Path path) {
    this._path = path;
  }

  public record Book(int id, String title, String language) {}
}
