package com.aziks.reader.controllers;

import com.aziks.reader.I18n;
import com.aziks.reader.Library.Book;
import com.aziks.reader.Library.Shelf;
import com.aziks.reader.MainApp;
import com.aziks.reader.components.*;
import com.aziks.reader.utils.ExceptionHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Duration;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;

import static com.aziks.reader.MainApp.library;

public class RootController extends Controller implements Initializable {
  @FXML public BorderPane bookPane;
  @FXML public BorderPane shelvesPane;
  @FXML public Button addBookButton;
  @FXML public Button removeBookButton;
  @FXML public Button addShelfButton;
  @FXML public Button removeShelfButton;
  @FXML public Button markAsReadButton;
  @FXML public Button settingsButton;

  private TreeView<String> treeView;
  private BookWebView bookView;
  private BookItem selectedBookItem;

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
  public void initialize(URL url, ResourceBundle resourceBundle) {
    this.bookView = new BookWebView();

    List<Shelf> shelves = null;

    try {
      library.connectToDatabase();
      shelves = library.getShelves();
    } catch (SQLException e) {
      e.printStackTrace();
      ExceptionHandler.alertUser(I18n.getMessage("exception.SQLException"));
      System.exit(1);
    }

    this.treeView = createShelvesTreeView(shelves);

    this.treeView.setShowRoot(false);
    this.treeView.setEditable(true);
    this.treeView.setCellFactory(
        new Callback<TreeView<String>, TreeCell<String>>() {
          @Override
          public TreeCell<String> call(TreeView<String> param) {
            return new ShelvesCellFactory();
          }
        });
    this.treeView
        .getSelectionModel()
        .selectedItemProperty()
        .addListener(
            new ChangeListener<TreeItem<String>>() {
              @Override
              public void changed(
                  ObservableValue<? extends TreeItem<String>> observable,
                  TreeItem<String> oldValue,
                  TreeItem<String> newValue) {
                if (newValue instanceof ShelfItem) {
                  removeBookButton.setDisable(true);
                  removeShelfButton.setDisable(false);
                  markAsReadButton.setDisable(true);

                  selectedBookItem = null;

                  Font font = new Font("System Bold", 32);
                  Label label = new Label();
                  label.setText(I18n.getMessage("root.noOpenBook"));
                  label.setFont(font);
                  BorderPane.setMargin(label, new Insets(0, 0, 25, 0));
                  bookPane.setCenter(label);
                } else if (newValue instanceof BookItem) {
                  removeBookButton.setDisable(false);
                  removeShelfButton.setDisable(true);
                  markAsReadButton.setDisable(false);

                  selectedBookItem = (BookItem) newValue;

                  FontIcon fontIcon =
                      new FontIcon((selectedBookItem.isRead() ? Feather.BOOK : Feather.BOOK_OPEN));
                  Tooltip tooltip =
                      new Tooltip(
                          I18n.getMessage(
                              selectedBookItem.isRead()
                                  ? "root.markBookAsUnreadButton.tooltip"
                                  : "root.markBookAsReadButton.tooltip"));
                  tooltip.setShowDelay(Duration.millis(500));
                  markAsReadButton.setGraphic(fontIcon);
                  markAsReadButton.setTooltip(tooltip);

                  showBook(selectedBookItem.getGutId());
                }
              }
            });
    shelvesPane.setCenter(this.treeView);
  }

  public void showBook(int gutid) {
    String bookText;
    try {
      bookText = library.getBookFileText(gutid);
    } catch (IOException e) {
      e.printStackTrace();
      ExceptionHandler.alertUser(
          I18n.getMessage("exception.IOException")
              + "\n"
              + I18n.getMessage("root.failedToOpenBook"));
      return;
    }

    this.bookView.setBodyText(bookText);
    this.bookView.loadContent();
    bookPane.setCenter(this.bookView.getWebView());
  }

  @FXML
  public void handleRemoveShelfButtonAction(ActionEvent actionEvent) {
    ShelfItem selectedShelfItem = (ShelfItem) this.treeView.getSelectionModel().getSelectedItem();
    try {
      library.deleteShelf(selectedShelfItem.getPath());
      library.deleteBooksFromShelfPath(selectedShelfItem.getPath());
    } catch (SQLException e) {
      e.printStackTrace();
      ExceptionHandler.alertUser(I18n.getMessage("exception.SQLRemoveShelfException"));

      return;
    }

    selectedShelfItem.getParent().getChildren().remove(selectedShelfItem);

    // Disable "remove buttons" if the library is empty
    if (this.treeView.getRoot().isLeaf()) {
      removeBookButton.setDisable(true);
      removeShelfButton.setDisable(true);
    }
  }

  @FXML
  public void handleAddShelfButtonAction(ActionEvent actionEvent) throws IOException {
    String path = "root";
    TreeItem<String> selectedItem = this.treeView.getSelectionModel().getSelectedItem();
    if (selectedItem instanceof ShelfItem) {
      path = ((ShelfItem) selectedItem).getPath();
    } else if (selectedItem instanceof BookItem) {
      path = ((BookItem) selectedItem).getShelfPath();
    }

    // The text field from the popup takes the focus when the popup is shown, but the "add shelf"
    // button also keeps his focus. When the user press enter to search a book, both of the controls
    // receive the event, so a new "blank" popup appears and the previous one hides itself. To avoid
    // this, we need to take the focus of the main window on something that doesn't accept keyboard
    // input.
    bookPane.requestFocus();
    PopupAddShelf popupAddShelf;
    try {
      popupAddShelf = new PopupAddShelf(this.treeView, path);
    } catch (SQLException e) {
      e.printStackTrace();
      ExceptionHandler.alertUser(I18n.getMessage("exception.SQLInsertException"));
      return;
    }

    Bounds bounds = addShelfButton.localToScreen(addShelfButton.getBoundsInLocal());
    popupAddShelf.setX(bounds.getMaxX() - addShelfButton.getWidth());
    popupAddShelf.setY(bounds.getMaxY() + 5);
    popupAddShelf.show(MainApp.getPrimaryStage());
  }

  @FXML
  public void handleAddBookAction(ActionEvent actionEvent) {
    String path = "root";
    TreeItem<String> selectedItem = this.treeView.getSelectionModel().getSelectedItem();
    if (selectedItem instanceof ShelfItem) {
      path = ((ShelfItem) selectedItem).getPath();
    } else if (selectedItem instanceof BookItem) {
      path = ((BookItem) selectedItem).getShelfPath();
    }

    // The text field from the popup takes the focus when the popup is shown, but the "add book"
    // button also keeps his focus. When the user press enter to search a book, both of the controls
    // receive the event, so a new "blank" popup appears and the previous one hides itself. To avoid
    // this, we need to take the focus of the main window on something that doesn't accept keyboard
    // input.
    bookPane.requestFocus();
    PopupAddBook popupAddBook;
    try {
      popupAddBook = new PopupAddBook(this.treeView, path);
    } catch (SQLException e) {
      e.printStackTrace();
      ExceptionHandler.alertUser(I18n.getMessage("exception.SQLInsertException"));
      return;
    }
    popupAddBook.show(MainApp.getPrimaryStage());
  }

  @FXML
  public void handleRemoveBookButton(ActionEvent actionEvent) {
    BookItem bookItem = (BookItem) this.treeView.getSelectionModel().getSelectedItem();
    try {
      library.deleteBook(bookItem.getGutId());
      library.deleteBookFile(bookItem.getGutId());
    } catch (SQLException e) {
      e.printStackTrace();
      ExceptionHandler.alertUser(I18n.getMessage("exception.SQLUpdateException"));
      return;
    } catch (IOException e) {
      e.printStackTrace();
      ExceptionHandler.alertUser(I18n.getMessage("exception.IOException"));
      return;
    }

    bookItem.getParent().getChildren().remove(bookItem);
  }

  @FXML
  public void handleMarkAsReadButtonAction(ActionEvent actionEvent) {
    BookItem selectedBookItem = (BookItem) this.treeView.getSelectionModel().getSelectedItem();

    try {
      library.updateBookIsRead(selectedBookItem.getGutId(), !selectedBookItem.isRead());
    } catch (SQLException e) {
      e.printStackTrace();
      ExceptionHandler.alertUser(I18n.getMessage("exception.SQLUpdateException"));
      return;
    }

    FontIcon fontIcon = new FontIcon(selectedBookItem.isRead() ? Feather.BOOK_OPEN : Feather.BOOK);
    Tooltip tooltip =
        new Tooltip(
            I18n.getMessage(
                selectedBookItem.isRead()
                    ? "root.markBookAsReadButton.tooltip"
                    : "root.markBookAsUnreadButton.tooltip"));
    tooltip.setShowDelay(Duration.millis(500));
    markAsReadButton.setGraphic(fontIcon);
    markAsReadButton.setTooltip(tooltip);

    selectedBookItem.setGraphic(fontIcon);
    selectedBookItem.setIsRead(!selectedBookItem.isRead());

    this.treeView.refresh();
  }

  @FXML
  public void handleSettingsButtonAction(ActionEvent actionEvent) {
    PopupSettings popupSettings = new PopupSettings();
    popupSettings.setOnHiding(this::handlePopupSettingsOnHidden);
    popupSettings.show(MainApp.getPrimaryStage());
  }

  private void handlePopupSettingsOnHidden(WindowEvent windowEvent) {
    this.bookView.loadContent();
  }

  /**
   * Put BookItems in their ShelfItem
   *
   * @param shelf The ShelfItem in which the BookItems will be placed
   */
  private void putBookItemsInShelf(ShelfItem shelf) {
    List<Book> books = null;
    try {
      books = library.getBooksFromShelfPath(shelf.getPath());
    } catch (SQLException e) {
      e.printStackTrace();
      ExceptionHandler.alertUser(I18n.getMessage("exception.SQLException"));
      System.exit(1);
    }

    if (books == null) return;

    ListIterator<Book> iterator = books.listIterator();

    BookItem bookItem;
    Book book;
    while (iterator.hasNext()) {
      book = iterator.next();
      bookItem =
          new BookItem(book.gutid(), book.title(), book.language(), book.read(), shelf.getPath());
      shelf.getChildren().add(bookItem);
    }
  }

  /**
   * Create a TreeView containing shelves
   *
   * @param shelves The Shelves to put in the TreeView
   * @return A TreeView containing shelves
   */
  private TreeView<String> createShelvesTreeView(List<Shelf> shelves) {
    ShelfItem rootItem = new ShelfItem("root", "root");
    putBookItemsInShelf(rootItem);

    if (shelves == null) return new TreeView<>(rootItem);

    ListIterator<Shelf> iterator = shelves.listIterator();

    ShelfItem currentItem = rootItem;
    String lastPath = "root";

    ShelfItem shelfItem;
    Shelf shelf;
    while (iterator.hasNext()) {
      shelf = iterator.next();

      shelfItem = new ShelfItem(shelf.name(), shelf.path());
      putBookItemsInShelf(shelfItem);

      // The new ShelfItem needs to be a child of the current one
      if (shelf.path().startsWith(lastPath)) {
        currentItem.getChildren().add(shelfItem);
        currentItem = shelfItem;
        lastPath = shelf.path();
        continue;
      }

      // The new ShelfItem is not a child of the current one, we need to find the right ShelfItem
      // parent
      String[] pathDir = shelf.path().split("/");
      String[] lastPathDir = lastPath.split("/");
      for (int i = lastPathDir.length - 1; i > 0; i--) {
        if (pathDir.length - 1 >= i && pathDir[i].equals(lastPathDir[i])) break;
        currentItem = (ShelfItem) currentItem.getParent();
      }

      currentItem.getChildren().add(shelfItem);
      currentItem = shelfItem;
      lastPath = shelf.path();
    }

    return new TreeView<>(rootItem);
  }

  public void handleMakeSelectionBoldButtonAction(ActionEvent actionEvent) {
    this.bookView.makeSelectionBold();
  }

  public void handleMakeSelectionItalicButtonAction(ActionEvent actionEvent) {
    this.bookView.makeSelectionItalic();
  }

  public void handleMakeSelectionUnderlineButtonAction(ActionEvent actionEvent) {
    this.bookView.makeSelectionUnderline();
  }

  public void handleSaveFileButtonAction(ActionEvent actionEvent) {
    if (selectedBookItem == null) return;

    String bodyText = this.bookView.getBodyText();

    try {
      library.addBookFile(selectedBookItem.getGutId(), bodyText);
    } catch (IOException e) {
      e.printStackTrace();
      ExceptionHandler.alertUser(
          I18n.getMessage("exception.IOException") + " " + I18n.getMessage("root.failedSaveBook"));
    }
  }

  public void handleRemoveTagsSelectionButtonAction(ActionEvent actionEvent) {
    this.bookView.removeTagsSelection();
  }

  public void handleAddNoteButtonAction(ActionEvent actionEvent) {
    this.bookView.keepSelected();

    PopupAddNote popupAddNote = new PopupAddNote(this.bookView);
    popupAddNote.show(MainApp.getPrimaryStage());

    popupAddNote.requestFocusOnTextField();
    // The text field from the popup takes the focus when the popup is shown, but the "add note"
    // button also keeps his focus. When the user press enter to search a book, both of the controls
    // receive the event, so a new "blank" popup appears and the previous one hides itself. To avoid
    // this, we need to take the focus of the main window on something that doesn't accept keyboard
    // input.
    bookPane.requestFocus();
  }

  public void handleShowNoteButtonAction(ActionEvent actionEvent) {
    String note = this.bookView.getSelectionNote();
    if (note == null) note = I18n.getMessage("root.noNoteSelected");

    PopupShowNote popupShowNote = new PopupShowNote(note);
    popupShowNote.show(MainApp.getPrimaryStage());
  }

  public void handleCloseLibraryButtonAction(ActionEvent event) {
    library.setPath(null);

    try {
      library.closeDatabase();
      changeScene(MainApp.getPrimaryStage(), "/scenes/Welcome.fxml");
    } catch (SQLException e) {
      e.printStackTrace();
      ExceptionHandler.alertUser(I18n.getMessage("exception.SQLException"));
      System.exit(1);
    } catch (IOException e) {
      e.printStackTrace();
      ExceptionHandler.alertUser(I18n.getMessage("exception.IOException"));
      System.exit(1);
    }
  }

  public void handleCloseButtonAction(ActionEvent actionEvent) {
    MainApp.getPrimaryStage().close();
  }

  public void handleAboutButtonAction(ActionEvent actionEvent) {
    PopupAbout popupAbout = new PopupAbout();
    popupAbout.show(MainApp.getPrimaryStage());
  }

  private static class ShelvesCellFactory extends TreeCell<String> {
    private TextField textField;

    public ShelvesCellFactory() {}

    @Override
    public void startEdit() {
      if (!(getTreeItem() instanceof ShelfItem)) return; // Allow edit for ShelfItem only

      super.startEdit();

      if (textField == null) createTextField();

      setText(null);
      setGraphic(textField);
      textField.selectAll();
    }

    @Override
    public void cancelEdit() {
      super.cancelEdit();
      if (textField != null) textField.setText(getString());
      setText(getItem());
      setGraphic(getTreeItem().getGraphic());
    }

    @Override
    protected void updateItem(String item, boolean empty) {
      super.updateItem(item, empty);

      if (empty) {
        setText(null);
        setGraphic(null);
        return;
      }

      if (isEditing()) {
        if (textField != null) textField.setText(getString());
        setText(null);
        setGraphic(textField);
        return;
      }

      setText(getString());
      setGraphic(getTreeItem().getGraphic());
    }

    /** Create a text field to allow the user to change the name of the ShelfItem */
    private void createTextField() {
      textField = new TextField(getString());
      textField.setOnKeyReleased(
          new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
              if (event.getCode()
                  == KeyCode.ENTER) { // Update the database and the TreeView with the new name
                // Don't allow empty/blank name
                if (textField.getText().isBlank()) {
                  cancelEdit();
                  return;
                }

                // Don't allow slashes in the name (it would break the path)
                if (textField.getText().contains("/")) {
                  ExceptionHandler.alertUser(
                      I18n.getMessage("exception.slashInShelfNameException"));
                  return;
                }

                // Create the new path
                ShelfItem shelfItem = (ShelfItem) getTreeItem();
                String newPath =
                    shelfItem
                        .getPath()
                        .replaceAll(shelfItem.getValue() + "\\/(?!.)", textField.getText());

                try {
                  library.updateShelf(textField.getText(), newPath, shelfItem.getPath());
                } catch (SQLException e) {
                  e.printStackTrace();
                  ExceptionHandler.alertUser(I18n.getMessage("exception.SQLUpdateException"));
                  cancelEdit();
                  return;
                }

                shelfItem.setPath(newPath);
                commitEdit(textField.getText());
              } else if (event.getCode() == KeyCode.ESCAPE) { // Cancel the renaming
                cancelEdit();
              }
            }
          });
    }

    private String getString() {
      return getItem() == null ? "" : getItem();
    }
  }
}
