package abinail.model;

import abinail.interfaces.Event;
import abinail.interfaces.Notifier;
import abinail.interfaces.HtmlExtractor;
import abinail.filters.*;
import abinail.interfaces.HtmlIterable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Sergii on 24.01.2017.
 */

public class LinkExtractor extends HtmlExtractor<Content, URL> {
    private HtmlIterable<URL> htmlIterable = new HtmlLinkIterator();
    private Notifier<Integer> linkFoundEventNotifier = new Notifier();

    public final Event<Integer> linkFoundEvent = linkFoundEventNotifier.getEvent();

    public LinkExtractor(BlockingQueue<Content> queueIn) {
        super(queueIn);
    }

    public void setDisallowed(String queryParamsToReplace) {
        htmlIterable.setDisalowed(queryParamsToReplace);
    }

    @Override
    public void extract() throws InterruptedException {
        try {
            Content content = queueIn.take();
            if (queuePassThrough != null) {
                queuePassThrough.put(content);
            }
            htmlIterable.setIn(content);
            int count = 0;
            for (URL url : htmlIterable) {
                queueOut.put(url);
                count++;
            }
            linkFoundEventNotifier.raiseEvent(this, count);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
