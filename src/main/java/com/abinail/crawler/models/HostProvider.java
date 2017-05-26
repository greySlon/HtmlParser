package com.abinail.crawler.models;

import org.springframework.stereotype.Component;

import java.net.URL;

@Component("hostProvider")
public class HostProvider {

  private URL host;

  public void setHost(URL host) {
    this.host = host;
  }

  public URL getHost() {
    return host;
  }
}
