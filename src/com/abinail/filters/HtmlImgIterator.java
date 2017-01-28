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
public class HtmlImgIterator implements HtmlIterable{
    private Pattern pattern = Pattern.compile("(?<=<img.{1,20}src[^\"]{1,4}\"\\s{0,3})[^\"]+");
    private Pattern kostili=Pattern.compile("/./|/../");

    private URL host;
    private URL baseUrl;

    private Matcher matcher;
    private Predicate<String>[] predicates;
    private String result;
    private BaseResolver baseResolver;

    public HtmlImgIterator() {
        this(null);
    }
    public HtmlImgIterator(Predicate<String>[] predicates) {
        this.predicates = predicates;
    }

    @Override
    public Iterator iterator() {
        return new Iterator() {
            @Override
            public boolean hasNext() {
                if (predicates == null) {
                    return getMatches();
                } else {
                    if (getMatches()) {
                        if (predicateTest(result))
                            return this.hasNext();
                        else
                            return true;
                    } else {
                        return false;
                    }
                }
            }

            @Override
            public String next() {
                return result;
            }
        };
    }
    @Override
    public void setIn(Content in) throws MalformedURLException{
        if(host==null){
            host=new URL(in.url.getProtocol(),in.url.getHost(),in.url.getPort(),"/");
            baseResolver=new BaseResolver();
        }
        matcher = pattern.matcher(in.content);
        baseUrl = baseResolver.getBaseUrl(in);
    }

    private boolean predicateTest(String s) {
        for (int i = 0; i < predicates.length; i++) {
            if (predicates[i].test(s))
                return true;
        }
        return false;
    }
    private boolean getMatches() {
        if (matcher.find()) {
            try {
                result = new URL(baseUrl,matcher.group()).toString();
                result=kostili.matcher(result).replaceAll("/");
                result=result.replace(" ", "%20");
            } catch (MalformedURLException e) {
                return getMatches();
            }
            return true;
        } else
            return false;
    }
    public static void main(String[] a) throws MalformedURLException{
/*        HtmlImgIterator filter = new HtmlImgIterator(new URL("http://abi.od.ua"), null);
        filter.setIn("Tutorial.\n" +
                "<bas e href=\"http://3dhostel.od.ua\">\n" +
                "<url><img src=\"http://abi.od.ua/photogallery.html\"></loc></url>\n" +
                "<url><img src=\"./sales.html\"></loc></url>\n" +
                "<url><loc>http://abi.od.ua/shellac.html</loc></url>\n" +
                "<url><loc>http://abi.");
        for (String s : filter) {
            System.out.println(s);
        }*/
    }
}
