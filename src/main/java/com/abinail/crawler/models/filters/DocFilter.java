package com.abinail.crawler.models.filters;

import com.abinail.crawler.interfaces.Filter;

import java.util.regex.Pattern;

public class DocFilter implements Filter<String> {

  private Pattern pattern = Pattern.compile(".pdf$|.xls$|.doc$", Pattern.CASE_INSENSITIVE);

  @Override
  public boolean test(String s) {
    return pattern.matcher(s).find();
  }
}
