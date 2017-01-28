package com.abinail.interfaces;

import com.abinail.model.Content;

import java.net.MalformedURLException;

/**
 * Created by Sergii on 24.01.2017.
 */
public interface HtmlIterable extends Iterable<String> {
    void setIn(Content source) throws MalformedURLException;
}
