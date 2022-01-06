package com.aziks.reader;

import com.aziks.reader.Library.Book;
import com.aziks.reader.utils.EndOfFileReachedException;
import com.aziks.reader.utils.HttpRequestUnsuccessful;
import com.aziks.reader.utils.LineNotFoundException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GutScraper {
  private final String gutIndexAllUrl;
  private final Path pathToGutIndexAll;
  private final Fetcher fetcher;

  public GutScraper() {
    this.fetcher = new Fetcher();
    this.gutIndexAllUrl = "https://www.gutenberg.org/dirs/GUTINDEX.ALL";

    this.pathToGutIndexAll = Path.of(Settings.getReaderPath().toString(), "GUTINDEX.ALL.txt");
  }

  public void storeGutenbergIndex() throws IOException, HttpRequestUnsuccessful {
    URL url = new URL(gutIndexAllUrl);
    String gutIndex = this.fetcher.fetch(url);

    Files.deleteIfExists(this.pathToGutIndexAll);
    File gutIndexFile = this.pathToGutIndexAll.toFile();

    FileWriter fileWriter = new FileWriter(gutIndexFile);
    fileWriter.write(gutIndex);
    fileWriter.close();
  }

  public List<Book> searchBooks(String query) throws IOException, EndOfFileReachedException {
    List<Book> books = new ArrayList<>();
    Scanner scanner = getGutIndexAllScanner();
    int max = Settings.getMaxBooksToSearch();

    skipIntro(scanner); // Skip preamble

    Book book;
    String line;
    while (scanner.hasNext()) {
      if (books.size() == max) break;

      line = scanner.nextLine();

      line = line.replaceAll("\u00A0", " "); // Replace non-spaces char by spaces

      // No need to continue after this line because there are no more books to find
      if (line.trim().contains("<==End of GUTINDEX.ALL==>")) break;

      if (line.isBlank()) continue; // Skip empty lines

      // Skip new year intro
      if (line.trim().startsWith("===")) {
        skipIntro(scanner);
        continue;
      }

      // A new book might have been reached
      book = parseBook(scanner, line);
      if (book == null) continue;

      String lowerQuery = query.toLowerCase();
      String lowerBookTitle = book.title().toLowerCase();
      if (lowerBookTitle.contains(lowerQuery)) books.add(book);
    }

    scanner.close();
    return books;
  }

  public boolean isUpdateAvailable()
      throws IOException, LineNotFoundException, HttpRequestUnsuccessful {
    String localDate = getLocalLastUpdateDate();
    String upstreamDate = getUpstreamLastUpdateDate();
    return !localDate.equals(upstreamDate);
  }

  public String downloadBook(int gutid) throws IOException, HttpRequestUnsuccessful {
    // There are 2 possible url for txt files. If the first one doesn't work, we need to try with
    // the second one.
    // Possible URLs:
    // https://www.gutenberg.org/cache/epub/{GUTID}/pg{GUTID}.txt
    // https://www.gutenberg.org/files/{GUTID}/{GUTID}-0.txt
    String bookUrl = "https://www.gutenberg.org/cache/epub/" + gutid + "/pg" + gutid + ".txt";
    URL url = new URL(bookUrl);

    String bookText;
    try {
      bookText = this.fetcher.fetch(url); // Try with the first url
    } catch (HttpRequestUnsuccessful e) {
      e.printStackTrace();

      // Try with the second url if the first one was unsuccessful
      bookUrl = "https://www.gutenberg.org/files/" + gutid + "/" + gutid + "-0.txt";
      url = new URL(bookUrl);
      bookText = this.fetcher.fetch(url);
    }

    return bookText;
  }

  private Scanner getGutIndexAllScanner() throws IOException {
    File gutIndexAll = this.pathToGutIndexAll.toFile();
    return new Scanner(gutIndexAll, StandardCharsets.UTF_8);
  }

  private String getUpstreamLastUpdateDate()
      throws IOException, HttpRequestUnsuccessful, LineNotFoundException {
    String lineToFind = "Updated to";

    URL url = new URL(gutIndexAllUrl);
    BufferedReader inputStream = this.fetcher.fetchPartial(url);

    // We need to find the line "Updated to {Month} {Day}, {Year}"
    String line = null;
    while ((line = inputStream.readLine()) != null) {
      line = line.replaceAll("\u00A0", " "); // Replace non-spaces char by spaces
      line = line.trim();

      if (line.startsWith(lineToFind)) break;
    }

    if (line == null) throw new LineNotFoundException();

    inputStream.close();

    return line.substring(lineToFind.length() + 1); // Returns "{Month} {Day}, {Year}"
  }

  private String getLocalLastUpdateDate() throws IOException, LineNotFoundException {
    String lineToFind = "Updated to";

    Scanner scanner = getGutIndexAllScanner();

    // We need to find the line "Updated to {Month} {Day}, {Year}"
    String line = "";
    while (scanner.hasNext()) {
      line = scanner.nextLine();

      line = line.replaceAll("\u00A0", " "); // Replace non-spaces char by spaces
      line = line.trim();

      if (line.startsWith(lineToFind)) break;
    }

    if (!scanner.hasNext()) throw new LineNotFoundException();

    scanner.close();

    return line.substring(lineToFind.length() + 1); // Returns "{Month} {Day}, {Year}"
  }

  private void skipIntro(Scanner scanner) throws EndOfFileReachedException {
    String lineToReach = "TITLE and AUTHOR";
    String line;
    while (scanner.hasNext()) {
      line = scanner.nextLine();
      line = line.replaceAll("\u00A0", " "); // Replace non-spaces char by spaces
      if (line.contains(lineToReach)) return;
    }

    throw new EndOfFileReachedException();
  }

  private Book parseBook(Scanner scanner, String line) throws EndOfFileReachedException {
    // Example of book in gutindex:
    // "Jeremias, by Stefan Zweig                                     40564
    //   [Subtitle: Eine dramatische Dichtung in neun Bildern]
    //   [Language: German]"

    // Need to trim the line to avoid having spaces after the id (yes, it does happen)
    line = line.trim();

    // Parse the line to get the book's id
    String idString = line.substring(line.lastIndexOf(' ') + 1);

    // Some books have the letter B or the letter C has the last char id, there is no need to keep
    // them
    if (idString.endsWith("B") || idString.endsWith("C"))
      idString = idString.substring(0, idString.length() - 1);

    // Skip lines that are not books by checking if there is an id
    int id;
    try {
      id = Integer.parseInt(idString);
    } catch (NumberFormatException e) {
      //      e.printStackTrace();
      return null;
    }

    // Parse the line to get the (beginning) of the book's title
    StringBuilder title = new StringBuilder();
    title.append(line.substring(0, line.lastIndexOf(' ')).trim());

    // Find possible language and/or the rest of the title (titles can be on multiple lines)
    boolean tagsReached = false;
    String trimmedLine;
    String language = null;
    while (scanner.hasNext()) {
      line = scanner.nextLine();

      line = line.replaceAll("\u00A0", " "); // Replace non-spaces char by spaces
      trimmedLine = line.trim();

      // End of book description reached because books are separated by a blank line
      // (except a few because the file is poorly formatted)
      if (line.isBlank()) break;

      // Parse the line if a language has been found
      // Example of language line:
      // "[Language: German]"
      // We need to remove everything except "German"
      if (trimmedLine.startsWith("[Language")) {
        language = trimmedLine.replaceAll("\\[[^\\s]*\\s*", ""); // removes "[Language: "
        language = language.substring(0, language.length() - 1); // removes "]"
        // Language tag is always after the title, so we can now insert the book because we found
        // every info we need
        break;
      }

      // Skip tags
      if (trimmedLine.startsWith("[")) {
        tagsReached = true;
        continue;
      }

      // Tags are always after titles, if a tag has been reached, it means the entire title has been
      // parsed there are only tags left
      if (tagsReached) continue;

      // Lines are supposed to start with a single space, I think it's better to stop the current
      // parsing if we found a line without a space
      if (!line.startsWith(" ")) break;

      // Append the rest of the tile
      title.append(" ");
      title.append(trimmedLine);
    }

    if (!scanner.hasNext()) throw new EndOfFileReachedException();

    return new Book(id, title.toString(), language, false);
  }
}
