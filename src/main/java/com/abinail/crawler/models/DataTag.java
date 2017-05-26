package com.abinail.crawler.models;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("dataTag")
@Lazy()
public class DataTag {

  public DataTag() {
    System.out.println("************DATATAG*************");
  }

  private Pattern spacePattern = Pattern.compile("[\\s]*=[\\s]*");
  private Map<String, Pattern> patternMap = new ConcurrentHashMap<>();

  public String getAttribute(String headTag, String attribute) {
    headTag = spacePattern.matcher(headTag).replaceAll("=");

    Matcher matcher = compilePattern(attribute).matcher(headTag);
    if (matcher.find()) {
      return matcher.group().trim();
    }
    if (headTag.contains(attribute)) {
      return "";
    } else {
      return null;
    }
  }

  private Pattern compilePattern(String s) {
    if (patternMap.containsKey(s)) {
      return patternMap.get(s);
    } else {
      Pattern pattern = Pattern.compile("(?<=" + s + "=\")[^\"]+");
      patternMap.put(s, pattern);
      return pattern;
    }
  }
}
