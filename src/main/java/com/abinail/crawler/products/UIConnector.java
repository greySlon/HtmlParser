package com.abinail.crawler.products;

import com.abinail.crawler.interfaces.MessageProvider;
import com.abinail.crawler.models.HostProvider;

import org.springframework.stereotype.Component;

import java.net.URL;

import javax.annotation.Resource;

@Component("uiConnector")
public class UIConnector {

  @Resource(name = "hostProvider")
  private HostProvider hostProvider;
  @Resource(name = "messageProvider")
  private MessageProvider paramsProvider;

  public HostProvider getHostProvider() {
    return hostProvider;
  }

  public MessageProvider getParamsProvider() {
    return paramsProvider;
  }

  public void setHost(URL host) {
    hostProvider.setHost(host);
  }

  public void setIgnorParams(String ignorParams) {
    paramsProvider.setMessage(ignorParams);
  }
}
