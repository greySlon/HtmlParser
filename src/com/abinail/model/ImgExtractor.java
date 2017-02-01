package com.abinail.model;

import com.abinail.filters.HtmlImgIterator;
import com.abinail.interfaces.ExtractorHtml;
import com.abinail.interfaces.GettingQueue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Sergii on 25.01.2017.
 */

public class ImgExtractor extends ExtractorHtml<Content, URL> {

    public ImgExtractor(GettingQueue<Content> contentQueueIn) {
        this.queueIn = (BlockingQueue<Content>) contentQueueIn.getQueue();
        this.htmlIterable = new HtmlImgIterator();
        this.setDaemon(true);
    }

    @Override
    public void setAllowed(String containString) {
        htmlIterable.setAllowed(containString);
    }

    @Override
    public void extract(Content content) throws MalformedURLException{
        htmlIterable.setIn(content);
        for (String s : htmlIterable) {
            try {
                URL imgUrl = new URL(s);
                queueOut.put(imgUrl);
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

    @Override
    public void run() {
        while (true) {
            if (isInterrupted()) return;
            try {
                Content content = queueIn.take();
                extract(content);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
}