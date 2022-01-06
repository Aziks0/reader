package com.aziks.reader.components;

import com.aziks.reader.I18n;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class PopupShowNote extends CustomPopup {
  public PopupShowNote(String note) {
    super();

    TextArea textArea = new TextArea();
    textArea.setText(note);
    textArea.setEditable(false);
    textArea.prefHeight(200);
    textArea.prefWidth(400);
    super.borderPane.setCenter(textArea);
    super.borderPane.setBottom(createFooter());
    super.getContent().add(super.borderPane);
  }

  private HBox createFooter() {
    HBox hBox = new HBox();
    BorderPane.setMargin(hBox, new Insets(10, 0, 0, 0));

    Pane pane = new Pane();
    HBox.setHgrow(pane, Priority.ALWAYS);
    hBox.getChildren().add(pane);

    Button button = new Button();
    button.setText(I18n.getMessage("root.popupShowNote.closeButton"));
    button.setOnAction(this::handleHideButton);
    hBox.getChildren().add(button);

    return hBox;
  }

  private void handleHideButton(ActionEvent actionEvent) {
    super.hide();
  }
}
