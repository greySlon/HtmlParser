package com.abinail.crawler.models.loaders;

import com.abinail.crawler.interfaces.Loader;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("imageLoader")
public class ImageLoader implements Loader<Object> {

  private File folder;
  private Pattern pattern = Pattern.compile("(?<=\\/)[^\\/]+?$", Pattern.CASE_INSENSITIVE);
  private StringBuilder sb = new StringBuilder(300);

  public ImageLoader(File folder) {
    this.folder = folder;
  }

  @Override
  public Object load(URL url) {
    try {
      String name = url.getFile();
      name = getNewName(name);

      Files.copy(url.openStream(), new File(folder, name).toPath());
      return name;
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  private String getNewName(String startName) throws IOException {
    sb.setLength(0);
    Matcher matcher = pattern.matcher(startName);
    String fName = null;
    if (matcher.find()) {
      fName = matcher.group();
    } else {
      throw new IOException();
    }
    return sb.append(fName.substring(0, fName.length() - 4))
        .append("(").append(startName.hashCode()).append(")")
        .append(fName.substring(fName.length() - 4, fName.length())).toString();
  }
}
