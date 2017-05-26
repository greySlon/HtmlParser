package com.abinail.crawler.products;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import com.abinail.crawler.interfaces.HtmlIterable;
import com.abinail.crawler.interfaces.Loader;
import com.abinail.crawler.models.HostProvider;
import com.abinail.crawler.models.events.EndProcessEvent;
import com.abinail.crawler.models.events.Event;
import com.abinail.crawler.models.events.LinkProcessedEvent;
import com.abinail.crawler.models.events.NewLinkFoundEvent;
import com.abinail.crawler.models.util_classes.ContentTuple;
import com.abinail.crawler.models.util_classes.Link;
import javax.annotation.Resource;

@Component("crawler")
public class WebSiteCrawler {

  @Resource(name = "messageQueue")
  private BlockingQueue<Event> eventBlockingQueue;
  @Resource(name = "contentLoader")
  private Loader<ContentTuple> contentLoader;
  @Resource(name = "htmlLinkExtractor")
  private HtmlIterable<URL> htmlIterable;
  @Value("#{uiConnector.hostProvider}")
  private HostProvider hostProvider;
  private Set<Link> linkSet;
  private Thread crawlerThread;


  public void start() {
    linkSet = new HashSet<>();
    URL urlStartPoint = hostProvider.getHost();

    crawlerThread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          linkSet.add(new Link(urlStartPoint));
          eventBlockingQueue.put(new NewLinkFoundEvent(this, urlStartPoint));
          Object[] links;
          do {
            links = linkSet.stream().filter(link -> !link.isOk).toArray();
            for (Object link : links) {
              if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
              } else {
                findToCrawl(((Link) link));
              }
            }
          } while (links.length > 0);
        } catch (InterruptedException e) {
          eventBlockingQueue.clear();
          System.out.println("Crawler interrupted");
        } finally {
          eventBlockingQueue.offer(new EndProcessEvent(this));
        }
      }
    });
    crawlerThread.start();
  }

  public void interrupt() {
    crawlerThread.interrupt();
  }

  private void findToCrawl(Link link) throws InterruptedException {
    ContentTuple content = null;
    try {
      content = contentLoader.load(link.url);
      htmlIterable.iterateOver(content);
      for (URL url : htmlIterable) {
        if (linkSet.add(new Link(url))) {
          eventBlockingQueue.put(new NewLinkFoundEvent(this, url));
        }
      }
      link.isOk = true;
      eventBlockingQueue.put(new LinkProcessedEvent(this, link.url));
    } catch (RuntimeException e) {
      System.out.println(e.getMessage());
    }
  }
}
