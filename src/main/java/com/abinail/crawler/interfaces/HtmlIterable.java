package com.abinail.crawler.interfaces;


import com.abinail.crawler.models.util_classes.ContentTuple;

public interface HtmlIterable<T> extends Iterable<T> {
    void iterateOver(ContentTuple in);
}