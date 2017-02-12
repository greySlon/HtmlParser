package abinail.model;

import abinail.interfaces.HtmlExtractor;
import abinail.filters.*;
import abinail.interfaces.HtmlIterable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

/**
 * Created by Sergii on 24.01.2017.
 */

public class LinkExtractor extends HtmlExtractor<Content, URL> {

    private HtmlIterable<URL> htmlIterable = new HtmlLinkIterator();
    protected Consumer<Integer> linkFoundHandler;

    public LinkExtractor(BlockingQueue<Content> queueIn) {
        super(queueIn);
        super.setDaemon(true);
    }

    public void setLinkFoundHandler(Consumer<Integer> linkFoundHandler) {
        this.linkFoundHandler = linkFoundHandler;
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
        if (linkFoundHandler != null) {
            linkFoundHandler.accept(count);
        }
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
