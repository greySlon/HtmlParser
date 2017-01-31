package com.abinail.model;

import com.abinail.filters.ContainStringFilter;
import com.abinail.filters.HtmlImgIterator;
import com.abinail.interfaces.GettingQueue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Created by Sergii on 25.01.2017.
 */
public class ImgExtractor extends Thread {
    private BlockingQueue<Content> contentQueueIn;
    private BlockingQueue<URL> urlQueueOut = new ArrayBlockingQueue<>(100);
    private Consumer<Object> uiImgFound;

    private HtmlImgIterator htmlImgIterator;

    public void setUiImgFound(Consumer<Object> uiImgFound) {
        this.uiImgFound = uiImgFound;
    }

    public ImgExtractor(GettingQueue<Content> contentQueueIn) {
        this.contentQueueIn = (BlockingQueue<Content>) contentQueueIn.getQueue();
        this.htmlImgIterator = new HtmlImgIterator();
        this.setDaemon(true);
    }

    public void setStringToMatch(String containString) {
        htmlImgIterator.setAllowed(containString);
    }

    public BlockingQueue<URL> getUrlQueueOut() {
        return urlQueueOut;
    }

    @Override
    public void run() {
        while (true) {
            if (isInterrupted()) return;
            try {
                Content content = contentQueueIn.take();
                htmlImgIterator.setIn(content);
                for (String s : htmlImgIterator) {
                    try {
                        URL imgUrl = new URL(s);
                        urlQueueOut.put(imgUrl);
                        if (uiImgFound != null)
                            uiImgFound.accept(null);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }
    }
}
