package com.aziks.reader;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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

  public void executeUpdate(String sql) throws SQLException {
    Statement statement = this.connection.createStatement();
    statement.executeUpdate(sql);
    statement.close();
  }

  public Connection getConnection() {
    return this.connection;
  }
}
