package com.aziks.reader.components;

import com.aziks.reader.GutScraper;
import com.aziks.reader.I18n;
import com.aziks.reader.Library.Book;
import com.aziks.reader.utils.EndOfFileReachedException;
import com.aziks.reader.utils.ExceptionHandler;
import com.aziks.reader.utils.HttpRequestUnsuccessful;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.aziks.reader.MainApp.library;
import static com.aziks.reader.controllers.RootController.findShelfItem;

public class PopupAddBook extends CustomPopupAdd {
  private final List<CheckBox> booksCheckBox = new ArrayList<>();
  private List<Book> books;
  private TextField searchBookTextField;

  public PopupAddBook(TreeView<String> treeView, String shelfPath) throws SQLException {
    super(treeView, shelfPath);

    super.borderPane.setPrefWidth(450);
    super.borderPane.setMinHeight(390);
    super.borderPane.setTop(createHeader());
    super.borderPane.setCenter(new Label("Aucune recherche effectuée"));
    super.borderPane.setBottom(super.createDefaultFooter(this::handleAddBookButtonAction));

    super.getContent().add(super.borderPane);
  }

  private VBox createHeader() {
    VBox vBox = new VBox();
    BorderPane.setMargin(vBox, new Insets(0, 0, 10, 0));

    Font font = new Font("System Bold", 18);
    Label label = new Label();
    label.setText(I18n.getMessage("root.popupAddBooks.addBooksLabel"));
    label.setFont(font);
    VBox.setMargin(label, new Insets(0, 0, 3, 0));
    vBox.getChildren().add(label);

    HBox hBox = new HBox();

    this.searchBookTextField = new TextField();
    this.searchBookTextField.setOnKeyReleased( // Search book when the user press enter
        new EventHandler<KeyEvent>() {
          @Override
          public void handle(KeyEvent event) {
            if (event.getCode() != KeyCode.ENTER) return;
            handleSearchButtonAction(null);
          }
        });
    HBox.setHgrow(this.searchBookTextField, Priority.ALWAYS);
    hBox.getChildren().add(this.searchBookTextField);

    FontIcon fontIcon = new FontIcon(Feather.SEARCH);
    Button button = new Button();
    button.setGraphic(fontIcon);
    button.setOnAction(this::handleSearchButtonAction);
    hBox.getChildren().add(button);

    vBox.getChildren().add(hBox);

    return vBox;
  }

  private GridPane createGridBooksSelection() {
    GridPane gridPane = new GridPane();
    gridPane.setGridLinesVisible(true);

    ColumnConstraints columnConstraints = new ColumnConstraints();
    columnConstraints.setHgrow(Priority.ALWAYS);
    gridPane.getColumnConstraints().add(columnConstraints);
    columnConstraints = new ColumnConstraints();
    columnConstraints.setPercentWidth(20);
    gridPane.getColumnConstraints().add(columnConstraints);
    columnConstraints = new ColumnConstraints();
    columnConstraints.setHgrow(Priority.NEVER);
    gridPane.getColumnConstraints().add(columnConstraints);

    RowConstraints rowConstraints = new RowConstraints();
    rowConstraints.setMinHeight(20);
    gridPane.getRowConstraints().add(rowConstraints);

    Label label = new Label();
    label.setText("Name");
    Font font = new Font("System Bold", 12);
    label.setFont(font);
    label.setPadding(new Insets(0, 0, 0, 5));
    GridPane.setConstraints(label, 0, 0);
    gridPane.getChildren().add(label);

    label = new Label();
    label.setText("Language");
    font = new Font("System Bold", 12);
    label.setFont(font);
    label.setPadding(new Insets(0, 0, 0, 5));
    GridPane.setConstraints(label, 1, 0);
    gridPane.getChildren().add(label);

    return gridPane;
  }

  private void handleSearchButtonAction(ActionEvent actionEvent) {
    String searchedBook = this.searchBookTextField.getText();

    if (searchedBook.isBlank()) {
      ExceptionHandler.alertUser(I18n.getMessage("exception.blankBookNameException"));
      return;
    }

    GutScraper scraper = new GutScraper();
    try {
      this.books = scraper.searchBooks(searchedBook);
    } catch (EndOfFileReachedException e) {
      e.printStackTrace();
      ExceptionHandler.alertUser(I18n.getMessage("exception.lineNotFoundException"));
      return;
    } catch (IOException e) {
      e.printStackTrace();
      ExceptionHandler.alertUser(I18n.getMessage("exception.IOException"));
      return;
    }

    if (this.books.isEmpty()) {
      super.borderPane.setCenter(new Label("Pas trouvé"));
      return;
    }

    GridPane gridPane = createGridBooksSelection();
    Book currentBook;
    CheckBox checkBox;
    Label title, language;
    for (int i = 0; i < this.books.size(); i++) {
      currentBook = this.books.get(i);

      title = new Label();
      title.setText(currentBook.title());
      GridPane.setConstraints(title, 0, i + 1);

      language = new Label();
      language.setText(currentBook.language());
      GridPane.setConstraints(language, 1, i + 1);

      checkBox = new CheckBox();
      GridPane.setMargin(checkBox, new Insets(0, 2, 1, 2));
      GridPane.setConstraints(checkBox, 2, i + 1);
      this.booksCheckBox.add(checkBox);

      RowConstraints rowConstraints = new RowConstraints();
      rowConstraints.setMinHeight(20);
      gridPane.getRowConstraints().add(rowConstraints);

      gridPane.getChildren().addAll(title, language, checkBox);
    }

    super.borderPane.setCenter(gridPane);
  }

  private void handleAddBookButtonAction(ActionEvent actionEvent) {
    String shelfPath = super.choiceBox.getValue();
    GutScraper scraper = new GutScraper();
    ShelfItem shelfItem;
    BookItem bookItem;
    Book book;
    String bookText;
    int i = -1;
    for (CheckBox checkBox : this.booksCheckBox) {
      i++;

      if (!checkBox.isSelected()) continue;

      book = this.books.get(i);
      bookItem = new BookItem(book.gutid(), book.title(), book.language(), false, shelfPath);

      try {
        if (library.isBookInDatabase(book.gutid())) {
          ExceptionHandler.alertUser(
              I18n.getMessage("exception.bookAlreadyInLibrary") + " " + book.title());
          continue;
        }
        bookText = scraper.downloadBook(book.gutid());
        library.addBookFile(book.gutid(), bookText);
        library.insertBook(bookItem);
      } catch (SQLException e) {
        e.printStackTrace();
        ExceptionHandler.alertUser(I18n.getMessage("exception.SQLInsertException"));
        return;
      } catch (IOException e) {
        e.printStackTrace();
        ExceptionHandler.alertUser(I18n.getMessage("exception.IOException"));
        return;
      } catch (HttpRequestUnsuccessful e) {
        ExceptionHandler.alertUser(I18n.getMessage("exception.httpRequestUnsuccessful"));
        e.printStackTrace();
        return;
      }

      shelfItem = findShelfItem(super.treeView, shelfPath);
      shelfItem.getChildren().add(bookItem);
    }

    super.hide();
  }
}
