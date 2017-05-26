package com.abinail.crawler.models;

import com.abinail.crawler.models.util_classes.ContentTuple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("baseResolver")
public class BaseResolver {

  DataTag dataTag;
  private Pattern pattern = Pattern.compile("<base[^>]+>");
//    private Pattern pattern = Pattern.compile("(?<=<base.{1,20}href[^\"]{1,4}\"\\s{0,3})[^\"]+");

  @Autowired
  public BaseResolver(DataTag dataTag) {
    this.dataTag = dataTag;
  }

  public URL getBaseUrl(ContentTuple in) {
    String content = in.content;
    URL baseUrl = in.url;

    Matcher matcher = pattern.matcher(content);
    if (matcher.find()) {
      String s = matcher.group();
      s = dataTag.getAttribute(s, "href");
      try {
        baseUrl = new URL(baseUrl, s);
      } catch (MalformedURLException e1) {
      }
    }
    return baseUrl;
  }
}
