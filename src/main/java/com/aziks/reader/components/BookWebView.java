package com.aziks.reader.components;

import com.aziks.reader.Settings;
import javafx.scene.web.WebView;

public class BookWebView {
  private final WebView webView = new WebView();
  private String bodyText;

  public BookWebView() {
    webView.setContextMenuEnabled(false);
  }

  public void loadContent() {
    webView.getEngine().loadContent(createContent());
  }

  public WebView getWebView() {
    return webView;
  }

  public String getBodyText() {
    return (String) webView
            .getEngine()
            .executeScript(
                    """
                    (function getBody() {
                      return document.getElementsByTagName('pre')[0].innerHTML;
                    })();
                    """
            );
  }

  public void setBodyText(String bodyText) {
    this.bodyText = bodyText;
  }

  public String getSelectionNote() {
    return (String) webView
            .getEngine()
            .executeScript(
                    """
                    (function getSelectionNote() {
                      return selectedNote;
                    })();
                    """
            );
  }

  public void removeTagsSelection() {
    webView
            .getEngine()
            .executeScript(
                """
                (function removeTagsSelection() {
                    const range = window.getSelection().getRangeAt(0);
                  
                    let element;
                    let html = range.commonAncestorContainer.innerHTML;
                    if (html == null) element = range.commonAncestorContainer.parentElement;
                    else element = range.commonAncestorContainer;
                    
                    // If element contains a mark tag
                    if (/<\\/?(mark[^>]*)>/g.test(element.outerHTML)) {
                      selectedNote = null;
                    }
                  
                    // Remove mark, span, u and em tags
                    element.outerHTML = element.outerHTML.replace(/<\\/?(mark[^>]*|span[^>]*|u|em)>/g, '');
                  })();
                """
            );
  }

  public void keepSelected() {
    webView
            .getEngine()
            .executeScript(
                    """
                    (function keepSelected() {
                      selectedElementRangeKept = window.getSelection().getRangeAt(0);
                    })();
                    """
            );
  }

  public void makeSelectionNote(String note) {
    webView
            .getEngine()
            .executeScript(
                    """
                    (function makeSelectionNote() {
                      const html = '<mark onclick="select(this, `%s`)">' + selectedElementRangeKept + '</mark>';
                      selectedElementRangeKept.deleteContents();
    
                      const div = document.createElement('div');
                      div.innerHTML = html;
                      const fragment = document.createDocumentFragment();
                      fragment.appendChild(div.firstChild);
                      selectedElementRangeKept.insertNode(fragment);
                    })();
                    """.formatted(note)
            );
  }

  public void makeSelectionUnderline() {
    String htmlOpenTag = "<u>";
    String htmlCloseTag = "</u>";
    makeSelection(htmlOpenTag, htmlCloseTag);
  }

  public void makeSelectionItalic() {
    String htmlOpenTag = "<em>";
    String htmlCloseTag = "</em>";
    makeSelection(htmlOpenTag, htmlCloseTag);
  }

  public void makeSelectionBold() {
    String htmlOpenTag = "<span style=\"font-weight: bold;\">";
    String htmlCloseTag = "</span>";
    makeSelection(htmlOpenTag, htmlCloseTag);
  }

  private void makeSelection(String htmlOpenTag, String htmlCloseTag) {
    webView
        .getEngine()
        .executeScript(
                """
                (function makeSelection() {
                  const range = window.getSelection().getRangeAt(0);

                  const html = '%s' + range + '%S';
                  range.deleteContents();

                  const div = document.createElement('div');
                  div.innerHTML = html;
                  const fragment = document.createDocumentFragment();
                  fragment.appendChild(div.firstChild);
                  range.insertNode(fragment);
                })();
                """.formatted(htmlOpenTag, htmlCloseTag)
        );
  }

  private String createScript() {
    return """
            <script>
              let selectedElementRangeKept;
              let selectedElement;
              let selectedNote = null;
              
              function select(element, note) {
                if (selectedElement) selectedElement.classList.remove('selected');
                if (selectedElement === element) {
                  selectedElement = null;
                  selectedNote = null;
                  return;
                }
                
                element.classList.add('selected');
                selectedElement = element;
                selectedNote = note;
              }
            </script>
           """;
  }

  private String createStyle() {
    return """
            <style>
              body {
                background-color: %s;
                font-size: %spx;
                color: %s
              }
            
              mark {
                cursor: pointer;
                background-color: %s;
              }
            
              mark.selected {
                background-color: %s;
              }
            </style>
           """.formatted(
                    Settings.getBookBackgroundColor(),
                    Settings.getFontSize(),
                    Settings.getBookTextColor(),
                    Settings.getNoteColor(),
                    Settings.getSelectedNoteColor()
              );
  }

  private String createContent() {
    String html = "";

    html += createStyle();
    html += createScript();
    html += "<body><pre>";
    html += bodyText;
    html += "</pre></body>";

    return html;
  }
}
