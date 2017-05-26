package com.abinail.crawler.models.events;

import java.net.URL;

public class LinkProcessedEvent extends Event {

  private URL urlProcessed;

  public LinkProcessedEvent(Object sender, URL urlProcessed) {
    super(sender);
    this.urlProcessed = urlProcessed;
  }

  public URL getUrlProcessed() {
    return urlProcessed;
  }

  @Override
  public String toString() {
    return "LinkProcessedEvent{" +
        "urlProcessed=" + urlProcessed +
        '}';
  }
}
