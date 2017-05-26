package com.abinail.crawler.models.extractors;

import com.abinail.crawler.interfaces.Filter;
import com.abinail.crawler.interfaces.OverHtmlIterator;
import com.abinail.crawler.models.BaseResolver;
import com.abinail.crawler.models.DataTag;
import com.abinail.crawler.models.QueryParamReplacer;
import com.abinail.crawler.models.UrlNormalizer;
import com.abinail.crawler.models.util_classes.ContentTuple;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

/**
 * Created by Sergii on 15.05.2017.
 */
@Component("linkIterator")
@Scope("prototype")
@Lazy
public class LinkIterator extends OverHtmlIterator {

  @Resource(name = "dataTag")
  private DataTag dataTag;
  @Resource(name = "urlNormalizer")
  private UrlNormalizer urlNormalizer;
  @Resource(name = "paramReplacer")
  private QueryParamReplacer replacer;
  @Resource(name = "baseResolver")
  private BaseResolver baseResolver;

  private Pattern pattern = Pattern.compile("<a[^>]+>");
  private URL baseUrl;
  private Matcher matcher;
/*
  public void setReplacer(QueryParamReplacer replacer) {
    this.replacer = replacer;
  }

  public void setBaseResolver(BaseResolver baseResolver) {
    this.baseResolver = baseResolver;
  }*/

  @Override
  public void setFilter(Filter<String> filter) {
    super.filter = filter;
  }

  @Override
  public void setContentTuple(ContentTuple contentTuple) {
    super.contentTuple = contentTuple;
    baseUrl = baseResolver.getBaseUrl(contentTuple);
    matcher = pattern.matcher(contentTuple.content);
  }

  @Override
  protected String getMatches() {
    while (true) {
      if (matcher.find()) {
        try {
          String resultStr;
          String href = dataTag.getAttribute(matcher.group(), "href");
          URL url = new URL(baseUrl, href);
          if (!url.getHost().equals(baseUrl.getHost())) {
            throw new MalformedURLException();
          }

          resultStr = urlNormalizer.normalizeUrl(url);
          if (replacer != null) {
            resultStr = replacer.removeParam(resultStr);
          }
          return resultStr;
        } catch (MalformedURLException e) {
          continue;
        }
      } else {
        return null;
      }
    }
  }
}
