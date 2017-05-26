package com.abinail.crawler.models.extractors;

import com.abinail.crawler.interfaces.Filter;
import com.abinail.crawler.interfaces.HtmlConfiguredIterable;
import com.abinail.crawler.interfaces.OverHtmlIterator;
import com.abinail.crawler.models.util_classes.ContentTuple;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Iterator;

import javax.annotation.Resource;

@Component("htmlLinkExtractor")
public class HtmlLinkExtractor implements HtmlConfiguredIterable<URL> {

  @Resource(name = "imgFilter")
  private Filter<String> filter;

  private ContentTuple contentTuple;

  @Lookup("linkIterator")
  public OverHtmlIterator getLinkIterator() {
    throw new RuntimeException("Lookup method");
  }

  @Override
  public void setFilter(Filter<String> filter) {
    this.filter = filter;
  }

  @Override
  public void iterateOver(ContentTuple contentTuple) {
    this.contentTuple = contentTuple;
  }

  @Override
  public Iterator<URL> iterator() {
    OverHtmlIterator linkIterator = getLinkIterator();
    linkIterator.setContentTuple(contentTuple);
    linkIterator.setFilter(filter);
    return linkIterator;
  }
}
