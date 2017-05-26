package com.abinail.crawler.models.extractors;

import com.abinail.crawler.interfaces.Filter;
import com.abinail.crawler.interfaces.HtmlConfiguredIterable;
import com.abinail.crawler.interfaces.OverHtmlIterator;
import com.abinail.crawler.models.util_classes.ContentTuple;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Iterator;

@Component("htmlImageExtractor")
public class HtmlImageExtractor implements HtmlConfiguredIterable<URL> {

  private Filter<String> filter;
  private ContentTuple in;

  @Override
  public void setFilter(Filter<String> filter) {
    this.filter = filter;
  }

  @Override
  public void iterateOver(ContentTuple contentTuple) {
    this.in = contentTuple;
  }

  @Override
  public Iterator<URL> iterator() {
//    return new ImgIterator(in.contentTuple, filter);
    OverHtmlIterator imgIterator = getImgIterator();
    imgIterator.setFilter(filter);
    imgIterator.setContentTuple(in);
    return getImgIterator();
  }

  @Lookup("imgIterator")
  private OverHtmlIterator getImgIterator() {
    throw new RuntimeException("Lookup method");
  }
}