package com.abinail.model;

import com.abinail.interfaces.HtmlExtractor;
import com.abinail.filters.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

/**
 * Created by Sergii on 24.01.2017.
 */

public class LinkExtractor extends HtmlExtractor<Content,URL> {
    protected Consumer<Integer> upLinkProcessedHandler;
    public void setUpLinkProcessedHandler(Consumer<Integer> upLinkProcessedHandler) {
        this.upLinkProcessedHandler = upLinkProcessedHandler;
    }
    public LinkExtractor(BlockingQueue<Content> queueIn) {
        super(queueIn);
        htmlIterable = new HtmlLinkIterator();
        this.setDaemon(true);
    }

    @Override
    public void setDisallowed(String queryParamsToReplace) {
        htmlIterable.setDisalowed(queryParamsToReplace);
    }

    @Override
    public void extract(Content content) throws MalformedURLException, InterruptedException{
        htmlIterable.setIn(content);
        int count=0;
        for (URL url : htmlIterable) {
            queueOut.put(url);
            count++;
        }
        if (upLinkProcessedHandler != null) {
            upLinkProcessedHandler.accept(count);
        }
    }

    @Override
    public void run() {
        while (true) {
            if (isInterrupted()) return;
            try {
                Content content = queueIn.take();
                if(queuePassThrough!=null){
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
