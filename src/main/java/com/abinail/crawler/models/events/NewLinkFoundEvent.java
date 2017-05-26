package com.abinail.crawler.models.events;

import java.net.URL;

public class NewLinkFoundEvent extends Event {

  private URL url;

  public NewLinkFoundEvent(Object sender, URL url) {
    super(sender);
    this.url = url;
  }

  public URL getUrl() {
    return url;
  }

  @Override
  public String toString() {
    return "NewLinkFoundEvent{" +
        "url=" + url +
        '}';
  }
}
