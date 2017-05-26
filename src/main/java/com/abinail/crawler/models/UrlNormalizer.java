package com.abinail.crawler.models;

import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.regex.Pattern;

@Component("urlNormalizer")
public class UrlNormalizer {

  private Pattern ampPattern = Pattern.compile("&amp;");
  private Pattern hashPattern = Pattern.compile("#.*");
  private Pattern kostili = Pattern.compile("/\\./|/\\.\\./");
  private Pattern qPattern = Pattern.compile("/&");

  public String normalizeUrl(URL url) {
    String resultStr = url.toString();
    resultStr = ampPattern.matcher(resultStr).replaceAll("&");
    resultStr = hashPattern.matcher(resultStr).replaceAll("");
    resultStr = qPattern.matcher(resultStr).replaceAll("?");
    resultStr = kostili.matcher(resultStr).replaceAll("/");
    resultStr = resultStr.replace(" ", "%20");

    return resultStr;
  }
}
