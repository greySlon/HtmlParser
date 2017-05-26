package com.abinail.crawler.models.util_classes;

import java.net.URL;

public class ContentTuple {
    public URL url;
    public String content;

    public ContentTuple(URL url, String content) {
        this.url = url;
        this.content = content;
    }
}