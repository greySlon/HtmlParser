package com.abinail.crawler.models.events;

public class EndProcessEvent extends Event {

  public EndProcessEvent(Object sender) {
    super(sender);
  }

  @Override
  public String toString() {
    return "EndProcessEvent{}";
  }
}
