package com.abinail.crawler.models;

import com.abinail.crawler.interfaces.MessageProvider;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("messageProvider")
@Scope("prototype")
public class MessageProviderImpl implements MessageProvider {

  private String msg;

  public MessageProviderImpl() {
    System.out.println("MessageProviderImpl created");
  }

  @Override
  public void setMessage(String message) {
    this.msg = message;
  }

  @Override
  public String getMessage() {
    return msg;
  }
}
