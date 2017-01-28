package com.abinail.model;

import com.abinail.interfaces.GettingQueue;
import com.abinail.interfaces.HtmlIterable;
import com.abinail.filters.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by Sergii on 24.01.2017.
 */
public class LinkExtractor extends Thread {
    private Set<Link> linkSetOut;
    private BlockingQueue<Content> contentQueueIn;
    private BlockingQueue<Content> contentQueueOut;
    private HtmlIterable htmlIterable;

    private Predicate<String>[] filters = (Predicate<String>[]) Arrays.asList(new DocFilter(), new ImgFilter()).toArray();

    private Consumer<Object> uiLinkProcessed;

    public void setUiLinkProcessed(Consumer<Object> uiLinkProcessed) {
        this.uiLinkProcessed = uiLinkProcessed;
    }

    public LinkExtractor(
            GettingQueue<Content> gettingQueueIn,
            Set<Link> linkSetOut,
            String queryParamsToReplace) {
        this(gettingQueueIn, linkSetOut, queryParamsToReplace, false);
    }

    public LinkExtractor(
            GettingQueue<Content> gettingQueueIn,
            Set<Link> linkSetOut,
            String queryParamsToReplace,
            boolean contentOueueOut) {
        this.contentQueueIn = ((BlockingQueue<Content>) gettingQueueIn.getQueue());
        this.linkSetOut = linkSetOut;
        if (queryParamsToReplace == null || queryParamsToReplace.isEmpty())
            htmlIterable = new HtmlLinkIterator(filters);
        else
            htmlIterable = new HtmlLinkIterator(filters, queryParamsToReplace);
        if (contentOueueOut)
            this.contentQueueOut = new ArrayBlockingQueue<Content>(100);
        this.setDaemon(true);
    }

    public BlockingQueue<Content> getContentQueueOut() {
        if (contentQueueOut != null)
            return contentQueueOut;
        else
            throw new RuntimeException("No queue created");
    }

    @Override
    public void run() {
        while (true) {
            if (isInterrupted()) return;
            try {
                Content content = contentQueueIn.take();
                if (contentQueueOut != null)
                    contentQueueOut.put(content);
                htmlIterable.setIn(content);
                for (String s : htmlIterable) {
                    URL url = new URL(s);
                    Link link = new Link(url);
                    linkSetOut.add(link);
                }
                if (uiLinkProcessed != null)
                    uiLinkProcessed.accept(null);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
