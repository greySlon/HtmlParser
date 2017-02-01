package com.abinail.filters;

import com.abinail.interfaces.HtmlIterable;
import com.abinail.model.Content;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sergii on 24.01.2017.
 */
public class HtmlImgIterator implements HtmlIterable<URL> {
    private Pattern pattern = Pattern.compile("(?<=<img.{1,20}src[^\"]{1,4}\"\\s{0,3})[^\"]+");
    private Pattern kostili = Pattern.compile("/./|/../");

    private URL host;
    private URL baseUrl;

    private Matcher matcher;
    private Predicate<String> filter;
    private String resultStr;
    private URL resultUrl;
    private BaseResolver baseResolver;

    public HtmlImgIterator() {
    }

    public HtmlImgIterator(Predicate<String> filter) {
        this.filter = filter;
    }

    @Override
    public void setAllowed(String allowed) {
        if (filter != null) {
            this.filter.and(new ContainStringFilter(allowed).negate());
        } else {
            this.filter = new ContainStringFilter(allowed).negate();
        }
    }

    @Override
    public Iterator iterator() {
        return new Iterator() {
            @Override
            public boolean hasNext() {
                if (filter == null) {
                    return getMatches();
                } else {
                    return getMatches() && (!filter.test(resultStr) || this.hasNext());
                }
            }

            @Override
            public URL next() {
                return resultUrl;
            }
        };
    }

    @Override
    public void setIn(Content in) throws MalformedURLException {
        if (host == null) {
            host = new URL(in.url.getProtocol(), in.url.getHost(), in.url.getPort(), "/");
            baseResolver = new BaseResolver();
        }
        matcher = pattern.matcher(in.content);
        baseUrl = baseResolver.getBaseUrl(in);
    }

    private boolean getMatches() {
        if (matcher.find()) {
            try {
                resultStr = new URL(baseUrl, matcher.group()).toString();
                resultStr = kostili.matcher(resultStr).replaceAll("/");
                resultStr = resultStr.replace(" ", "%20");
                resultUrl=new URL(resultStr);
            } catch (MalformedURLException e) {
                return getMatches();
            }
            return true;
        } else
            return false;
    }
}
