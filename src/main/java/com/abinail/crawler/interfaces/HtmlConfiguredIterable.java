package com.abinail.crawler.interfaces;

public interface HtmlConfiguredIterable<T> extends HtmlIterable<T> {
    void setFilter(Filter<String> filter);
}
