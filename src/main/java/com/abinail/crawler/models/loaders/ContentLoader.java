package com.abinail.crawler.models.loaders;

import com.abinail.crawler.interfaces.Loader;
import com.abinail.crawler.models.util_classes.ContentTuple;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

@Component("contentLoader")
public class ContentLoader implements Loader<ContentTuple> {

  @Override
  public ContentTuple load(URL url) {
    try {
      URLConnection conn = url.openConnection();
      if (conn.getContentType().contains("html")) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(conn.getInputStream()))) {
          reader.lines().forEach(line -> sb.append(line));
        }
        return new ContentTuple(url, sb.toString());
      } else {
        throw new RuntimeException("ContentType isn't html");
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
