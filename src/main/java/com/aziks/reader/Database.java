package com.aziks.reader;

import java.nio.file.Path;
import java.sql.*;

public class Database {
  private final String databaseFileName;
  private Connection connection = null;

  public Database(String databaseFileName) {
    this.databaseFileName = databaseFileName;
  }

  public String getDatabaseFileName() {
    return this.databaseFileName;
  }

  public void connectToDatabase(Path path) throws SQLException {
    this.connection = DriverManager.getConnection("jdbc:sqlite:" + path.resolve(databaseFileName));
  }

  public void executeUpdate(String update) throws SQLException {
    Statement statement = this.connection.createStatement();
    statement.executeUpdate(update);
    statement.close();
  }

  public void close() throws SQLException {
    if (this.connection == null) return;

    this.connection.close();
    this.connection = null;
  }

  public Connection getConnection() {
    return this.connection;
  }
}
