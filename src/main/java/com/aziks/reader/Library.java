package com.aziks.reader;

import com.aziks.reader.components.BookItem;
import com.aziks.reader.utils.DirectoryNotCreatedException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

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
    database.connectToDatabase(path);

    String sqlInit =
        "CREATE TABLE IF NOT EXISTS shelves ("
            + "path VARCHAR(400) PRIMARY KEY,"
            + "name VARCHAR(100) NOT NULL"
            + ")";
    database.executeUpdate(sqlInit);

    sqlInit = "INSERT INTO shelves (path, name) VALUES ('root', 'root')";
    database.executeUpdate(sqlInit);

    sqlInit =
        "CREATE TABLE IF NOT EXISTS books ("
            + "gutid INTEGER PRIMARY KEY,"
            + "shelfpath VARCHAR(400) DEFAULT 'root' REFERENCES shelves(path) ON DELETE SET DEFAULT ON UPDATE CASCADE,"
            + "title VARCHAR(255) NOT NULL,"
            + "language VARCHAR(30),"
            + "read INTEGER DEFAULT 0"
            + ")";
    database.executeUpdate(sqlInit);
  }

  public void addBookFile(int gutid, String bookText) throws IOException {
    Path bookPath = getBookPath(gutid);
    Files.deleteIfExists(bookPath);
    File bookFile = bookPath.toFile();

    FileWriter fileWriter = new FileWriter(bookFile, StandardCharsets.UTF_8);
    fileWriter.write(bookText);
    fileWriter.close();
  }

  public void deleteBookFile(int gutid) throws IOException {
    Path bookPath = getBookPath(gutid);
    Files.delete(bookPath);
  }

  public String getBookFileText(int gutid) throws IOException {
    File bookFile = getBookPath(gutid).toFile();
    Scanner scanner = new Scanner(bookFile, StandardCharsets.UTF_8);

    StringBuilder bookText = new StringBuilder();
    while (scanner.hasNext()) {
      bookText.append(scanner.nextLine());
      bookText.append("\n");
    }

    scanner.close();
    return bookText.toString();
  }

  public void insertBook(BookItem bookItem) throws SQLException {
    String sqlUpdate =
        "INSERT INTO books (shelfpath, gutid, title, language, read) VALUES (?, ?, ?, ?, ?)";
    PreparedStatement statement = database.getConnection().prepareStatement(sqlUpdate);
    statement.setString(1, bookItem.getShelfPath());
    statement.setInt(2, bookItem.getGutId());
    statement.setString(3, bookItem.getValue());
    statement.setString(4, bookItem.getLanguage());
    statement.setInt(5, 0);
    statement.executeUpdate();
    statement.close();
  }

  public void deleteBook(int gutid) throws SQLException {
    String sqlUpdate = "DELETE FROM books WHERE gutid = ?";
    PreparedStatement statement = database.getConnection().prepareStatement(sqlUpdate);
    statement.setInt(1, gutid);
    statement.executeUpdate();
    statement.close();
  }

  public void updateBookPath(int gutid, String shelfPath) throws SQLException {
    String sqlUpdate = "UPDATE books SET shelfpath = ? WHERE gutid = ?";
    PreparedStatement statement = database.getConnection().prepareStatement(sqlUpdate);
    statement.setString(1, shelfPath);
    statement.setInt(2, gutid);
    statement.executeUpdate();
    statement.close();
  }

  public void updateBookIsRead(int gutid, boolean isRead) throws SQLException {
    String sqlUpdate = "UPDATE books SET read = ? WHERE gutid = ?";
    PreparedStatement statement = database.getConnection().prepareStatement(sqlUpdate);
    statement.setInt(1, isRead ? 1 : 0);
    statement.setInt(2, gutid);
    statement.executeUpdate();
    statement.close();
  }

  public boolean isBookInDatabase(int gutid) throws SQLException {
    String sqlQuery = "SELECT * FROM books WHERE gutid = ?";
    PreparedStatement statement = database.getConnection().prepareStatement(sqlQuery);
    statement.setInt(1, gutid);
    ResultSet resultSet = statement.executeQuery();

    boolean isShelfInDB = resultSet.next();
    statement.close();
    return isShelfInDB;
  }

  public boolean isShelfInDatabase(String path) throws SQLException {
    String sqlQuery = "SELECT * FROM shelves WHERE path = ?";
    PreparedStatement statement = database.getConnection().prepareStatement(sqlQuery);
    statement.setString(1, path);
    ResultSet resultSet = statement.executeQuery();

    boolean isShelfInDB = resultSet.next();
    statement.close();
    return isShelfInDB;
  }

  public void insertShelf(String name, String path) throws SQLException {
    String sqlUpdate = "INSERT INTO shelves (path, name) VALUES (?, ?)";
    PreparedStatement statement = database.getConnection().prepareStatement(sqlUpdate);
    statement.setString(1, path);
    statement.setString(2, name);
    statement.executeUpdate();
    statement.close();
  }

  public List<Shelf> getShelves() throws SQLException {
    String sqlQuery = "SELECT * FROM shelves ORDER BY path";
    ResultSet resultSet = database.executeQuery(sqlQuery);

    resultSet.next(); // Skip root row
    if (!resultSet.next())
      return null; // Empty result set (there is only the root shelf in the table)

    List<Shelf> shelves = new ArrayList<>();
    Shelf shelf;
    do {
      shelf = new Shelf(resultSet.getString(2), resultSet.getString(1));
      shelves.add(shelf);
    } while (resultSet.next());

    resultSet.close();
    return shelves;
  }

  public List<Book> getBooksFromShelfPath(String shelfPath) throws SQLException {
    String sqlQuery = "SELECT * FROM books WHERE shelfpath = ?";
    PreparedStatement statement = database.getConnection().prepareStatement(sqlQuery);
    statement.setString(1, shelfPath);
    ResultSet resultSet = statement.executeQuery();

    if (!resultSet.next()) return null; // Empty result set

    List<Book> books = new ArrayList<>();
    Book book;
    do {
      book =
          new Book(
              resultSet.getInt("gutid"),
              resultSet.getString("title"),
              resultSet.getString("language"),
              resultSet.getInt("read") == 1);
      books.add(book);
    } while (resultSet.next());

    statement.close();

    return books;
  }

  public void updateShelf(String name, String newPath, String oldPath) throws SQLException {
    String sqlUpdate = "UPDATE shelves SET name = ?, path = ? WHERE path = ?";
    PreparedStatement statement = database.getConnection().prepareStatement(sqlUpdate);
    statement.setString(1, name);
    statement.setString(2, newPath);
    statement.setString(3, oldPath);
    statement.executeUpdate();
    statement.close();
  }

  public void deleteShelf(String path) throws SQLException {
    String childrenPath = path + "%";
    String sqlUpdate = "DELETE FROM shelves WHERE path = ? OR path LIKE ?";
    PreparedStatement statement = database.getConnection().prepareStatement(sqlUpdate);
    statement.setString(1, path);
    statement.setString(2, childrenPath);
    statement.executeUpdate();
    statement.close();
  }

  public void deleteBooksFromShelfPath(String shelfPath) throws SQLException {
    String childrenShelfPath = shelfPath + "%";
    String sqlUpdate = "DELETE FROM books WHERE shelfpath = ? OR shelfpath LIKE ?";
    PreparedStatement statement = database.getConnection().prepareStatement(sqlUpdate);
    statement.setString(1, shelfPath);
    statement.setString(2, childrenShelfPath);
    statement.executeUpdate();
    statement.close();
  }

  public void connectToDatabase() throws SQLException {
    database.connectToDatabase(this._path);
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

  private Path getBookPath(int gutid) {
    return Path.of(_path.toString(), booksDirectoryName, Integer.toString(gutid) + ".txt");
  }

  public record Book(int gutid, String title, String language, boolean read) {}

  public record Shelf(String name, String path) {}
}
