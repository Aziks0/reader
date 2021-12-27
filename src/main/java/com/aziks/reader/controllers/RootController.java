package com.aziks.reader.controllers;

import com.aziks.reader.Main;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class RootController extends Controller implements Initializable {
  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    System.out.println(Main.library.getPath());
  }
}
