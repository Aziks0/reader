package com.aziks.reader.components;

import javafx.scene.control.TreeItem;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class BookItem extends TreeItem<String> {
  private final int gutid;
  private final String language;
  private final String shelfpath;
  private boolean read;

  public BookItem(int gutid, String title, String language, boolean read, String shelfpath) {
    super(title);

    this.gutid = gutid;
    this.language = language;
    this.read = read;
    this.shelfpath = shelfpath;

    FontIcon folderIcon = new FontIcon();
    folderIcon.setIconCode(read ? Feather.BOOK_OPEN : Feather.BOOK);
    super.setGraphic(folderIcon);
  }

  public int getGutId() {
    return this.gutid;
  }

  public String getLanguage() {
    return this.language;
  }

  public String getShelfPath() {
    return this.shelfpath;
  }

  public boolean isRead() {
    return this.read;
  }

  public void setIsRead(boolean read) {
    this.read = read;

    FontIcon folderIcon = new FontIcon();
    folderIcon.setIconCode(read ? Feather.BOOK_OPEN : Feather.BOOK);
    super.setGraphic(folderIcon);
  }
}
