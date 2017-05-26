package com.abinail.crawler.models.extractors;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.abinail.crawler.interfaces.Filter;
import com.abinail.crawler.interfaces.OverHtmlIterator;
import com.abinail.crawler.models.BaseResolver;
import com.abinail.crawler.models.DataTag;
import com.abinail.crawler.models.UrlNormalizer;
import com.abinail.crawler.models.util_classes.ContentTuple;
import javax.annotation.Resource;


@Component("imgIterator")
@Scope("prototype")
@Lazy
public class ImgIterator extends OverHtmlIterator {

  @Resource(name = "dataTag")
  private DataTag dataTag;
  @Resource(name = "baseResolver")
  private BaseResolver baseResolver;
  @Resource(name = "urlNormalizer")
  private UrlNormalizer urlNormalizer;

  private Pattern pattern = Pattern.compile("<a[^>]+>|<img[^>]+>");
  private URL baseUrl;
  private Matcher matcher;

  @Override
  public void setContentTuple(ContentTuple contentTuple) {
    this.contentTuple = contentTuple;
    baseUrl = baseResolver.getBaseUrl(contentTuple);
    matcher = pattern.matcher(contentTuple.content);
  }

  @Override
  public void setFilter(Filter<String> filter) {
    super.filter = filter;
  }

  @Override
  protected String getMatches() {
    while (true) {
      if (matcher.find()) {
        try {
          String headTag = matcher.group();
          String s = dataTag.getAttribute(headTag, "src");
          if (s == null || s.isEmpty()) {
            s = dataTag.getAttribute(headTag, "href");
          }
          if (s == null || s.isEmpty()) {
            continue;
          }
          return urlNormalizer.normalizeUrl(new URL(baseUrl, s));

        } catch (MalformedURLException e) {
          continue;
        }
      } else {
        return null;
      }
    }
  }
}
