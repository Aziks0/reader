package com.aziks.reader.components;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Popup;

public abstract class CustomPopup extends Popup {
  protected final BorderPane borderPane;

  public CustomPopup() {
    super();
    super.setAutoHide(true);
    super.setAutoFix(true);

    this.borderPane = new BorderPane();
    this.borderPane.setStyle(
        "-fx-background-color: lightgray; -fx-background-radius: 6; -fx-border-color: black; -fx-border-radius: 6;");
    this.borderPane.setPadding(new Insets(10));
  }

  protected HBox createHeader(String headerText) {
    HBox hBox = new HBox();
    BorderPane.setMargin(hBox, new Insets(0, 0, 10, 0));

    Label label = new Label();
    label.setText(headerText);
    Font font = new Font("System Bold", 18);
    label.setFont(font);
    hBox.getChildren().add(label);

    return hBox;
  }
}
