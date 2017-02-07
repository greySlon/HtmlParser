package com.abinail.interfaces;

import com.abinail.model.Content;

import java.net.MalformedURLException;

/**
 * Created by Sergii on 24.01.2017.
 */
public interface HtmlIterable<T> extends Iterable<T> {
    void setIn(Content source) throws MalformedURLException;
    default void setAllowed(String allowed){}
    default void setDisalowed(String disallowed){}
}
