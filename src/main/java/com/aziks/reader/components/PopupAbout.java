package com.aziks.reader.components;

import com.aziks.reader.I18n;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class PopupAbout extends CustomPopup {
  public PopupAbout() {
    super();
    super.borderPane.setTop(super.createHeader(I18n.getMessage("popupAbout.aboutLabel")));
    super.borderPane.setCenter(createAcknowledgements());
    super.getContent().add(super.borderPane);
  }

  private GridPane createAcknowledgements() {
    GridPane gridPane = new GridPane();
    gridPane.setHgap(6);
    gridPane.setVgap(6);

    ColumnConstraints columnConstraints = new ColumnConstraints();
    columnConstraints.setHgrow(Priority.NEVER);
    gridPane.getColumnConstraints().add(columnConstraints);
    columnConstraints = new ColumnConstraints();
    columnConstraints.setHgrow(Priority.ALWAYS);
    gridPane.getColumnConstraints().add(columnConstraints);

    Label label = new Label();
    label.setText("Reader");
    label.setWrapText(true);
    GridPane.setConstraints(label, 1, 1);
    gridPane.getChildren().add(label);

    Hyperlink hyperlink = new Hyperlink();
    hyperlink.setText("https://github.com/Aziks0/reader");
    GridPane.setConstraints(hyperlink, 2, 1);
    gridPane.getChildren().add(hyperlink);

    label = new Label();
    label.setText("Project Gutenberg");
    label.setWrapText(true);
    GridPane.setConstraints(label, 1, 2);
    gridPane.getChildren().add(label);

    hyperlink = new Hyperlink();
    hyperlink.setText("https://www.gutenberg.org");
    GridPane.setConstraints(hyperlink, 2, 2);
    gridPane.getChildren().add(hyperlink);

    label = new Label();
    label.setText("javaFX");
    label.setWrapText(true);
    GridPane.setConstraints(label, 1, 3);
    gridPane.getChildren().add(label);

    hyperlink = new Hyperlink();
    hyperlink.setText("https://github.com/openjdk/jfx");
    GridPane.setConstraints(hyperlink, 2, 3);
    gridPane.getChildren().add(hyperlink);

    label = new Label();
    label.setText("ikonli");
    label.setWrapText(true);
    GridPane.setConstraints(label, 1, 4);
    gridPane.getChildren().add(label);

    hyperlink = new Hyperlink();
    hyperlink.setText("https://github.com/kordamp/ikonli/");
    GridPane.setConstraints(hyperlink, 2, 4);
    gridPane.getChildren().add(hyperlink);

    label = new Label();
    label.setText("feather");
    label.setWrapText(true);
    GridPane.setConstraints(label, 1, 5);
    gridPane.getChildren().add(label);

    hyperlink = new Hyperlink();
    hyperlink.setText("https://github.com/feathericons/feather");
    GridPane.setConstraints(hyperlink, 2, 5);
    gridPane.getChildren().add(hyperlink);

    return gridPane;
  }
}
