package com.aziks.reader.components;

import com.aziks.reader.I18n;
import com.aziks.reader.Settings;
import com.aziks.reader.utils.RegexMatcher;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class PopupSettings extends CustomPopup {
  private final ChoiceBox<String> languageChoiceBox = new ChoiceBox<>();
  private final CheckBox updateOnStartCheckBox = new CheckBox();
  private final CheckBox keepWindowSize = new CheckBox();
  private final Spinner<Integer> fontSizeSpinner = new Spinner<>();
  private final TextField bookTextColor = new TextField();
  private final TextField bookBackgroundColor = new TextField();
  private final TextField noteHighlightColor = new TextField();
  private final TextField selectedNoteHighlightColor = new TextField();
  private final Spinner<Integer> maxBookSearchSpinner = new Spinner<>();

  public PopupSettings() {
    super();
    super.borderPane.setTop(
        super.createHeader(I18n.getMessage("root.popupSettings.settingsLabel")));
    super.borderPane.setBottom(createFooter());
    super.borderPane.setCenter(createBody());
    super.getContent().add(super.borderPane);
  }

  private GridPane createBody() {
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
    label.setText(I18n.getMessage("root.popupSettings.languageLabel"));
    label.setWrapText(true);
    GridPane.setConstraints(label, 1, 1);
    gridPane.getChildren().add(label);

    label = new Label();
    label.setText(I18n.getMessage("root.popupSettings.updateOnStartup"));
    label.setWrapText(true);
    GridPane.setConstraints(label, 1, 2);
    gridPane.getChildren().add(label);

    label = new Label();
    label.setText(I18n.getMessage("root.popupSettings.keepWindowSize"));
    label.setWrapText(true);
    GridPane.setConstraints(label, 1, 3);
    gridPane.getChildren().add(label);

    label = new Label();
    label.setText(I18n.getMessage("root.popupSettings.bookFontSize"));
    label.setWrapText(true);
    GridPane.setConstraints(label, 1, 4);
    gridPane.getChildren().add(label);

    label = new Label();
    label.setText(I18n.getMessage("root.popupSettings.bookTextColor"));
    label.setWrapText(true);
    GridPane.setConstraints(label, 1, 5);
    gridPane.getChildren().add(label);

    label = new Label();
    label.setText(I18n.getMessage("root.popupSettings.bookBackgroundColor"));
    label.setWrapText(true);
    GridPane.setConstraints(label, 1, 6);
    gridPane.getChildren().add(label);

    label = new Label();
    label.setText(I18n.getMessage("root.popupSettings.noteColor"));
    label.setWrapText(true);
    GridPane.setConstraints(label, 1, 7);
    gridPane.getChildren().add(label);

    label = new Label();
    label.setText(I18n.getMessage("root.popupSettings.selectedNoteColor"));
    label.setWrapText(true);
    GridPane.setConstraints(label, 1, 8);
    gridPane.getChildren().add(label);

    label = new Label();
    label.setText(I18n.getMessage("root.popupSettings.maxBookSearch"));
    label.setWrapText(true);
    GridPane.setConstraints(label, 1, 9);
    gridPane.getChildren().add(label);

    String[] availableLanguages = Settings.getAvailableLanguages();
    for (String language : availableLanguages) {
      languageChoiceBox.getItems().add(language);
    }
    languageChoiceBox.setValue(Settings.getLanguage());
    GridPane.setConstraints(languageChoiceBox, 2, 1);
    gridPane.getChildren().add(languageChoiceBox);

    updateOnStartCheckBox.setSelected(Settings.getUpdateOnStartup());
    GridPane.setConstraints(updateOnStartCheckBox, 2, 2);
    gridPane.getChildren().add(updateOnStartCheckBox);

    keepWindowSize.setSelected(Settings.getKeepWindowSize());
    GridPane.setConstraints(keepWindowSize, 2, 3);
    gridPane.getChildren().add(keepWindowSize);

    SpinnerValueFactory<Integer> spinnerValueFactory =
        new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 30);
    spinnerValueFactory.setValue(Settings.getFontSize());
    fontSizeSpinner.setValueFactory(spinnerValueFactory);
    GridPane.setConstraints(fontSizeSpinner, 2, 4);
    gridPane.getChildren().add(fontSizeSpinner);

    bookTextColor.setText(Settings.getBookTextColor());
    GridPane.setConstraints(bookTextColor, 2, 5);
    gridPane.getChildren().add(bookTextColor);

    bookBackgroundColor.setText(Settings.getBookBackgroundColor());
    GridPane.setConstraints(bookBackgroundColor, 2, 6);
    gridPane.getChildren().add(bookBackgroundColor);

    noteHighlightColor.setText(Settings.getNoteColor());
    GridPane.setConstraints(noteHighlightColor, 2, 7);
    gridPane.getChildren().add(noteHighlightColor);

    selectedNoteHighlightColor.setText(Settings.getSelectedNoteColor());
    GridPane.setConstraints(selectedNoteHighlightColor, 2, 8);
    gridPane.getChildren().add(selectedNoteHighlightColor);

    spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20);
    spinnerValueFactory.setValue(Settings.getMaxBooksToSearch());
    maxBookSearchSpinner.setValueFactory(spinnerValueFactory);
    GridPane.setConstraints(maxBookSearchSpinner, 2, 9);
    gridPane.getChildren().add(maxBookSearchSpinner);

    return gridPane;
  }

  private HBox createFooter() {
    HBox hBox = new HBox();
    BorderPane.setMargin(hBox, new Insets(10, 0, 0, 0));

    Pane pane = new Pane();
    HBox.setHgrow(pane, Priority.ALWAYS);
    hBox.getChildren().add(pane);

    Button button = new Button();
    button.setText(I18n.getMessage("root.popupSettings.cancelButton"));
    HBox.setMargin(button, new Insets(0, 4, 0, 0));
    button.setOnAction(this::handleCancelButtonAction);
    hBox.getChildren().add(button);

    button = new Button();
    button.setText(I18n.getMessage("root.popupSettings.applyButton"));
    button.setOnAction(this::handleApplyButton);
    hBox.getChildren().add(button);

    return hBox;
  }

  private void handleApplyButton(ActionEvent actionEvent) {
    RegexMatcher hexaMatcher =
        new RegexMatcher("#[0-9a-f]{6}", I18n.getMessage("exception.colorNotHexadecimal"));
    if (!hexaMatcher.matches(bookTextColor.getText())) return;
    if (!hexaMatcher.matches(bookBackgroundColor.getText())) return;
    if (!hexaMatcher.matches(noteHighlightColor.getText())) return;
    if (!hexaMatcher.matches(selectedNoteHighlightColor.getText())) return;

    boolean restartNeeded = !languageChoiceBox.getValue().equals(Settings.getLanguage());

    Settings.setLanguage(languageChoiceBox.getValue());
    Settings.setUpdateOnStartup(updateOnStartCheckBox.isSelected());
    Settings.setKeepWindowSize(keepWindowSize.isSelected());
    Settings.setFontSize(fontSizeSpinner.getValue());
    Settings.setMaxBooksToSearch(maxBookSearchSpinner.getValue());
    Settings.setBookTextColor(bookTextColor.getText());
    Settings.setBookBackgroundColor(bookBackgroundColor.getText());
    Settings.setNoteColor(noteHighlightColor.getText());
    Settings.setSelectedNoteColor(selectedNoteHighlightColor.getText());

    super.hide();

    if (restartNeeded) {
      Alert alert =
          new Alert(
              Alert.AlertType.INFORMATION, I18n.getMessage("root.popupSettings.restartNeeded"));
      alert.showAndWait();
    }
  }

  private void handleCancelButtonAction(ActionEvent event) {
    super.hide();
  }
}
