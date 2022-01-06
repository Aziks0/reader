package com.aziks.reader.components;

import com.aziks.reader.I18n;
import com.aziks.reader.Library.Shelf;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TreeView;
import javafx.scene.layout.*;

import java.sql.SQLException;
import java.util.List;

import static com.aziks.reader.MainApp.library;

public abstract class CustomPopupAdd extends CustomPopup {
  protected final TreeView<String> treeView;
  protected final String defaultShelfPath;
  protected final ChoiceBox<String> choiceBox;

  public CustomPopupAdd(TreeView<String> treeView, String defaultShelfPath) throws SQLException {
    super();

    this.treeView = treeView;
    this.defaultShelfPath = defaultShelfPath;
    this.choiceBox = createShelvesPathChoiceBox();
  }

  protected VBox createDefaultFooter(EventHandler<ActionEvent> handleAddButtonAction) {
    VBox vBox = new VBox();
    BorderPane.setMargin(vBox, new Insets(5, 0, 0, 0));

    Label label = new Label();
    label.setText(I18n.getMessage("customPopupAdd.defaultFooter.shelfPathLabel"));
    vBox.getChildren().add(label);
    this.choiceBox.setMaxWidth(1000);
    vBox.getChildren().add(this.choiceBox);

    HBox hBox = new HBox();
    VBox.setMargin(hBox, new Insets(10, 0, 0, 0));

    Pane pane = new Pane();
    HBox.setHgrow(pane, Priority.ALWAYS);
    hBox.getChildren().add(pane);

    Button button = new Button();
    button.setText(I18n.getMessage("customPopupAdd.defaultFooter.cancelButton"));
    HBox.setMargin(button, new Insets(0, 4, 0, 0));
    button.setOnAction(this::handleCancelButtonAction);
    hBox.getChildren().add(button);

    button = new Button();
    button.setText(I18n.getMessage("customPopupAdd.defaultFooter.addButton"));
    button.setOnAction(handleAddButtonAction);
    hBox.getChildren().add(button);

    vBox.getChildren().add(hBox);

    return vBox;
  }

  private ChoiceBox<String> createShelvesPathChoiceBox() throws SQLException {
    ChoiceBox<String> choiceBox = new ChoiceBox<>();
    choiceBox.getItems().add("root");
    choiceBox.setValue("root");
    List<Shelf> shelves = library.getShelves();

    if (shelves == null) return choiceBox;

    for (Shelf shelf : shelves) {
      choiceBox.getItems().add(shelf.path());
    }

    choiceBox.setValue(this.defaultShelfPath); // Set the selected path as default
    return choiceBox;
  }

  private void handleCancelButtonAction(ActionEvent actionEvent) {
    super.hide();
  }
}
