package com.abinail.crawler.models.events;

public abstract class Event {

  protected Object sender;

  public Event(Object sender) {
    this.sender = sender;
  }

  public Object getSender() {
    return sender;
  }
}
