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
public class HtmlLinkIterator implements HtmlIterable{
    private Pattern pattern = Pattern.compile("(?<=<a.{1,20}href[^\"]{1,4}\"\\s{0,3})[^\"]+");
    private Pattern ampPattern = Pattern.compile("&amp;");
    private Pattern hashPattern = Pattern.compile("#.*");
    private Pattern kostili=Pattern.compile("/\\./|/\\.\\./");
    private Pattern qPattern=Pattern.compile("/&");
//    private Pattern hashPattern=Pattern.compile("#.*");

    private URL host;
    private URL baseUrl;

    private Matcher matcher;
    private Predicate<String>[] predicates;
    private String result;
    private BaseResolver baseResolver;
    private QueryParamReplacer queryParamReplacer;

    public HtmlLinkIterator(Predicate<String>[] predicates){
        this.predicates=predicates;
    }
    public HtmlLinkIterator(Predicate<String>[] predicates, String queryParamsToReplace) {
        this(predicates);
        queryParamReplacer=new QueryParamReplacer(queryParamsToReplace);
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
                        return !predicateTest(result)||this.hasNext();
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
//        System.err.println("*urlContent*"+in.url.toString());
        if(baseResolver==null){
            host=new URL(in.url.getProtocol(),in.url.getHost(),in.url.getPort(),"/");
            baseResolver=new BaseResolver();
        }
        matcher = pattern.matcher(in.content);
        baseUrl=baseResolver.getBaseUrl(in);
//        System.err.println("*base*"+baseUrl.toString());
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
                String href=matcher.group();//3dhostel.od.ua
                URL url=new URL(baseUrl,href);
                if(!url.getHost().equals(baseUrl.getHost())) throw new MalformedURLException();

                result = url.toString();
//                System.err.println("*find1*"+result);
                result = ampPattern.matcher(result).replaceAll("&");
                result= hashPattern.matcher(result).replaceAll("");
                result=qPattern.matcher(result).replaceAll("?");
                result=kostili.matcher(result).replaceAll("/");
                if (queryParamReplacer != null)
                    result = queryParamReplacer.removeParam(result);
//                System.err.println("*find2*"+result);
            } catch (MalformedURLException e) {
                return getMatches();
            }
            return true;
        } else
            return false;
    }
    public static void main(String[]a) throws  MalformedURLException{
        URL base=new URL("http://qwe.com/f1/");
        System.out.println(new URL(base,"./dfdf?c=f#rg").getFile());
        System.out.println(new URL(base,"./dfdf?c=f#rg").toString());
    }
}
