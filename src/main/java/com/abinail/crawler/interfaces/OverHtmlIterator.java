package com.abinail.crawler.interfaces;

import com.abinail.crawler.models.util_classes.ContentTuple;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

public abstract class OverHtmlIterator implements Iterator<URL> {

  private URL url;
  protected Filter<String> filter;
  protected ContentTuple contentTuple;

  public abstract void setFilter(Filter<String> filter);

  public abstract void setContentTuple(ContentTuple contentTuple);

  protected abstract String getMatches();

  @Override
  public boolean hasNext() {
    if (url != null) {
      return true;
    }
    String resultStr;
    while (true) {
      resultStr = getMatches();
      if (resultStr != null) {
        if (filter != null && filter.test(resultStr)) {
          continue;
        } else {
          try {
            url = new URL(resultStr);
            return true;
          } catch (MalformedURLException e) {
            continue;
          }
        }
      } else {
        return false;
      }
    }
  }

  @Override
  public URL next() {
    URL next = url;
    url = null;
    return next;
  }
}
