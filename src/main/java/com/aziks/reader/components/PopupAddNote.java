package com.aziks.reader.components;

import com.aziks.reader.I18n;
import com.aziks.reader.utils.ExceptionHandler;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class PopupAddNote extends CustomPopup {
  private final TextArea textArea;
  private final BookWebView webView;

  public PopupAddNote(BookWebView webView) {
    super();
    this.webView = webView;

    super.borderPane.setTop(super.createHeader(I18n.getMessage("root.popupAddNote.addNoteLabel")));
    super.borderPane.setBottom(createFooter());

    this.textArea = new TextArea();
    this.textArea.prefHeight(200);
    this.textArea.prefWidth(400);
    super.borderPane.setCenter(textArea);
    super.getContent().add(super.borderPane);
  }

  public void requestFocusOnTextField() {
    this.textArea.requestFocus();
  }

  private HBox createFooter() {
    HBox hBox = new HBox();
    BorderPane.setMargin(hBox, new Insets(10, 0, 0, 0));

    Pane pane = new Pane();
    HBox.setHgrow(pane, Priority.ALWAYS);
    hBox.getChildren().add(pane);

    Button button = new Button();
    button.setText(I18n.getMessage("customPopupAdd.defaultFooter.cancelButton"));
    button.setOnAction(this::handleCancelButtonAction);
    HBox.setMargin(button, new Insets(0, 4, 0, 0));
    hBox.getChildren().add(button);

    button = new Button();
    button.setText(I18n.getMessage("customPopupAdd.defaultFooter.addButton"));
    button.setOnAction(this::handleAddButtonAction);
    HBox.setMargin(button, new Insets(0, 4, 0, 0));
    hBox.getChildren().add(button);

    return hBox;
  }

  private void handleCancelButtonAction(ActionEvent event) {
    super.hide();
  }

  private void handleAddButtonAction(ActionEvent event) {
    if (this.textArea.getText().isBlank()) {
      ExceptionHandler.alertUser(I18n.getMessage("root.popupAddNote.blankNoteException"));
      return;
    }

    this.webView.makeSelectionNote(this.textArea.getText());
    super.hide();
  }
}
