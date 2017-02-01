package com.abinail.model;

import com.abinail.interfaces.ExtractorHtml;
import com.abinail.interfaces.GettingQueue;
import com.abinail.filters.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Sergii on 24.01.2017.
 */

public class LinkExtractor extends ExtractorHtml<Content,Content> {
    private Set<Link> linkSetOut;

    public LinkExtractor(GettingQueue<Content> gettingQueueIn, Set<Link> linkSetOut) {
        this.queueIn = ((BlockingQueue<Content>) gettingQueueIn.getQueue());
        this.linkSetOut = linkSetOut;
        htmlIterable = new HtmlLinkIterator();
        this.setDaemon(true);
    }

    @Override
    public void setDisallowed(String queryParamsToReplace) {
        htmlIterable.setDisalowed(queryParamsToReplace);
    }

    @Override
    public void extract(Content content) throws MalformedURLException{
        htmlIterable.setIn(content);
        for (String s : htmlIterable) {
            URL url = new URL(s);
            Link link = new Link(url);
            linkSetOut.add(link);
        }
    }

    @Override
    public void run() {
        while (true) {
            if (isInterrupted()) return;
            try {
                Content content = queueIn.take();
                if (queueOut != null)
                    queueOut.put(content);
                extract(content);
                if (processEventHandler != null)
                    processEventHandler.accept(null);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
