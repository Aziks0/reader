package com.aziks.reader.components;

import com.aziks.reader.I18n;
import com.aziks.reader.utils.ExceptionHandler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.sql.SQLException;

import static com.aziks.reader.MainApp.library;
import static com.aziks.reader.controllers.RootController.findShelfItem;

public class PopupAddShelf extends CustomPopupAdd {
  private TextField shelfNameTextField;

  public PopupAddShelf(TreeView<String> treeView, String shelfPath) throws SQLException {
    super(treeView, shelfPath);

    super.borderPane.setCenter(createBody());
    super.borderPane.setBottom(super.createDefaultFooter(this::handleAddShelfButtonAction));

    super.getContent().add(super.borderPane);
  }

  private VBox createBody() {
    VBox vBox = new VBox();

    Font font = new Font(18);
    Label label = new Label();
    label.setText(I18n.getMessage("root.popupAddShelf.addShelfLabel"));
    label.setFont(font);
    VBox.setMargin(label, new Insets(0, 0, 3, 0));
    vBox.getChildren().add(label);

    label = new Label();
    label.setText(I18n.getMessage("root.popupAddShelf.shelfNameLabel"));
    vBox.getChildren().add(label);

    this.shelfNameTextField = new TextField();
    this.shelfNameTextField
        .setOnKeyReleased( // Add shelf when the user press enter in the text field
            new EventHandler<KeyEvent>() {
              @Override
              public void handle(KeyEvent event) {
                if (event.getCode() != KeyCode.ENTER) return;
                handleAddShelfButtonAction(null);
              }
            });
    vBox.getChildren().add(this.shelfNameTextField);

    return vBox;
  }

  private void handleAddShelfButtonAction(ActionEvent actionEvent) {
    String shelfName = shelfNameTextField.getText();

    if (shelfName.isBlank()) {
      ExceptionHandler.alertUser(I18n.getMessage("exception.blankShelfNameException"));
      return;
    }

    if (shelfName.contains("/")) {
      ExceptionHandler.alertUser(I18n.getMessage("exception.slashInShelfNameException"));
      return;
    }

    String newShelfPath = super.choiceBox.getValue() + "/" + shelfName;

    try {
      if (library.isShelfInDatabase(newShelfPath)) {
        ExceptionHandler.alertUser(I18n.getMessage("exception.shelfAlreadyExistException"));
        return;
      }

      library.insertShelf(shelfName, newShelfPath);
    } catch (SQLException e) {
      e.printStackTrace();
      ExceptionHandler.alertUser(I18n.getMessage("exception.SQLInsertException"));
      return;
    }

    ShelfItem newShelfItem = new ShelfItem(shelfName, newShelfPath);
    ShelfItem parentShelfItem = findShelfItem(super.treeView, super.choiceBox.getValue());

    parentShelfItem.getChildren().add(newShelfItem);
    super.hide();
  }
}
