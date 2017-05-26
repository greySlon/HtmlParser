package com.abinail.crawler.models.util_classes;

import java.net.URL;
import java.util.Objects;

public class Link implements Comparable<Link> {
    public final URL url;
    public boolean isOk;

    public Link(URL url) {
        this.url = url;
    }

    @Override
    public int compareTo(Link o) {
        return toString().compareTo(o.toString());
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }

    @Override
    public String toString() {
        return url.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass())
            return false;
        Link link = ((Link) obj);
        return Objects.equals(url, link.url);
    }
}

