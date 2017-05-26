package com.abinail.crawler.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

@Component("paramReplacer")
public class QueryParamReplacer {

  private List<Pattern> patternList = new LinkedList<>();

  @Autowired
  public QueryParamReplacer(QueryParamsProvider queryParamsProvider) {
    String[] queryParam = queryParamsProvider.getQueryParams();
    for (String param : queryParam) {
      if (!param.isEmpty()) {
        String patternString = "(?<=\\?)" + param + "=[^\\s\\&]*(\\&){0,1}";
        patternList.add(Pattern.compile(patternString, Pattern.CASE_INSENSITIVE));
        patternString = "(?<=\\?)" + param + "$";
        patternList.add(Pattern.compile(patternString, Pattern.CASE_INSENSITIVE));
        patternString = "(?<=\\?)" + param + "\\&";
        patternList.add(Pattern.compile(patternString, Pattern.CASE_INSENSITIVE));
        //
        patternString = "\\&" + param + "=[^\\s\\&]*";
        patternList.add(Pattern.compile(patternString, Pattern.CASE_INSENSITIVE));
        patternString = "\\&" + param + "$";
        patternList.add(Pattern.compile(patternString, Pattern.CASE_INSENSITIVE));
        patternString = "\\&" + param + "(?=\\&)";
        patternList.add(Pattern.compile(patternString, Pattern.CASE_INSENSITIVE));
      }
    }
  }

  public String removeParam(String s) {
    for (Pattern pattern : patternList) {
      s = pattern.matcher(s).replaceAll("");
    }
    return s;
  }
}
