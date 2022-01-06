package com.aziks.reader.components;

import javafx.scene.control.TreeItem;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class ShelfItem extends TreeItem<String> {
  private String path;

  public ShelfItem() {
    this(null, null);
  }

  public ShelfItem(String name, String path) {
    super(name);

    this.path = path;

    FontIcon folderIcon = new FontIcon();
    folderIcon.setIconCode(Feather.FOLDER);
    super.setGraphic(folderIcon);
    super.setExpanded(true);
  }

  public String getPath() {
    return this.path;
  }

  public void setPath(String path) {
    this.path = path;
  }
}
