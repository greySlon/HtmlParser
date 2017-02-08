package abinail.filters;

import abinail.interfaces.HtmlIterable;
import abinail.model.Content;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sergii on 24.01.2017.
 */
public class HtmlLinkIterator implements HtmlIterable<URL> {
    private Pattern pattern = Pattern.compile("(?<=<a.{1,20}href[^\"]{1,4}\"\\s{0,3})[^\"]+");
    private Pattern ampPattern = Pattern.compile("&amp;");
    private Pattern hashPattern = Pattern.compile("#.*");
    private Pattern kostili = Pattern.compile("/\\./|/\\.\\./");
    private Pattern qPattern = Pattern.compile("/&");

    private URL baseUrl;

    private Matcher matcher;
    private Predicate<String> filter = new DocFilter().or(new ImgFilter()).negate();
    private String resultStr;
    private URL resultUrl;
    private BaseResolver baseResolver;
    private QueryParamReplacer queryParamReplacer;

    @Override
    public void setDisalowed(String disallowed) {
        if (disallowed != null && !disallowed.isEmpty()) {
            this.queryParamReplacer = new QueryParamReplacer(disallowed);
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
                    return getMatches() && (filter.test(resultStr) || this.hasNext());
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
        if (baseResolver == null) {
            baseResolver = new BaseResolver();
        }
        matcher = pattern.matcher(in.content);
        baseUrl = baseResolver.getBaseUrl(in);
    }

    private boolean getMatches() {
        if (matcher.find()) {
            try {
                String href = matcher.group();
                URL url = new URL(baseUrl, href);
                if (!url.getHost().equals(baseUrl.getHost())) throw new MalformedURLException();

                resultStr = url.toString();
                resultStr = ampPattern.matcher(resultStr).replaceAll("&");
                resultStr = hashPattern.matcher(resultStr).replaceAll("");
                resultStr = qPattern.matcher(resultStr).replaceAll("?");
                resultStr = kostili.matcher(resultStr).replaceAll("/");
                if (queryParamReplacer != null) {
                    resultStr = queryParamReplacer.removeParam(resultStr);
                }
                resultUrl=new URL(resultStr);
            } catch (MalformedURLException e) {
                return getMatches();
            }
            return true;
        } else
            return false;
    }
}
