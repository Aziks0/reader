package com.aziks.reader.controllers;

import com.aziks.reader.components.ShelfItem;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.ResourceBundle;

public class RootController extends Controller implements Initializable {
  /**
   * Find a ShelfItem from a TreeView using a ShelfItem path
   *
   * @param treeView The TreeView to search in
   * @param path The path of the ShelfItem to find
   * @return The ShelfItem at {@code path}
   */
  public static ShelfItem findShelfItem(TreeView<String> treeView, String path) {
    String[] pathDir = path.split("/");
    if (pathDir.length == 1) return (ShelfItem) treeView.getRoot();

    TreeItem<String> currentItem = treeView.getRoot();
    for (int i = 1; i < pathDir.length; i++) {
      for (TreeItem<String> child : currentItem.getChildren()) {
        if (!(child instanceof ShelfItem)) continue;
        if (((ShelfItem) child).getPath().endsWith("/" + pathDir[i])) {
          currentItem = child;
          break;
        }
      }
      if (((ShelfItem) currentItem).getPath().equals(path)) break;
    }

    return (ShelfItem) currentItem;
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {}
}
