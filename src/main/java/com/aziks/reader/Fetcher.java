package com.aziks.reader;

import com.aziks.reader.utils.HttpRequestUnsuccessful;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Fetcher {
  public BufferedReader fetchPartial(URL url) throws IOException, HttpRequestUnsuccessful {
    HttpURLConnection connection = null;
    connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Content-Type", "application/text");

    if (connection.getResponseCode() != 200) throw new HttpRequestUnsuccessful();

    return new BufferedReader(new InputStreamReader(connection.getInputStream()));
  }

  public String fetch(URL url) throws IOException, HttpRequestUnsuccessful {
    BufferedReader inputStream = fetchPartial(url);

    String line;
    StringBuilder body = new StringBuilder();
    while ((line = inputStream.readLine()) != null) {
      body.append(line);
      body.append('\n');
    }

    inputStream.close();

    return body.toString();
  }
}
