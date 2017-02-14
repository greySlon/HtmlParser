package abinail.model;

import abinail.interfaces.Event;
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

    public final Event<Integer> linkFoundEvent = new Event();

    public LinkExtractor(BlockingQueue<Content> queueIn) {
        super(queueIn);
        super.setDaemon(true);
    }

    public void setDisallowed(String queryParamsToReplace) {
        htmlIterable.setDisalowed(queryParamsToReplace);
    }

    @Override
    public void extract(Content content) throws MalformedURLException, InterruptedException {
        htmlIterable.setIn(content);
        int count = 0;
        for (URL url : htmlIterable) {
            queueOut.put(url);
            count++;
        }
        linkFoundEvent.fireEvent(this, count);
    }

    @Override
    public void run() {
        while (true) {
            if (isInterrupted()) return;
            try {
                Content content = queueIn.take();
                if (queuePassThrough != null) {
                    queuePassThrough.put(content);
                }
                extract(content);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
