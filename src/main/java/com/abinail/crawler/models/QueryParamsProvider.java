package com.abinail.crawler.models;

import com.abinail.crawler.interfaces.MessageProvider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("queryParamsProvider")
@Scope(scopeName = "singleton")
public class QueryParamsProvider {

  @Value("#{uiConnector.paramsProvider}")
  private MessageProvider rawParamsProvider;

  public void setRawParamsProvider(MessageProvider rawParamsProvider) {
    this.rawParamsProvider = rawParamsProvider;
  }

  public String[] getQueryParams() {
    String queryParams = rawParamsProvider.getMessage();
    if (queryParams != null && !queryParams.isEmpty()) {
      return queryParams.split(" ");
    }
    return new String[0];
  }
}
