package com.abinail.model;

import com.abinail.filters.HtmlImgIterator;
import com.abinail.interfaces.GettingQueue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by Sergii on 25.01.2017.
 */
public class ImgExtractor extends Thread{
    private BlockingQueue<Content> contentQueueIn;
    private BlockingQueue<URL> urlQueueOut=new ArrayBlockingQueue<URL>(100);
    private Consumer<Object> uiImgFound;

    private HtmlImgIterator htmlImgIterator;

    public void setUiImgFound(Consumer<Object> uiImgFound) {
        this.uiImgFound = uiImgFound;
    }

    public ImgExtractor(GettingQueue<Content> contentQueueIn, Predicate<String>[] predicates) {
        this.contentQueueIn = ((BlockingQueue<Content>) contentQueueIn.getQueue());
        if(predicates==null)
            this.htmlImgIterator=new HtmlImgIterator();
        else
            this.htmlImgIterator=new HtmlImgIterator(predicates);
        this.setDaemon(true);

    }
    public ImgExtractor(GettingQueue<Content> contentQueueIn) {
        this(contentQueueIn, null);
    }

    public BlockingQueue<URL> getUrlQueueOut() {
        return urlQueueOut;
    }

    @Override
    public void run() {
        while (true){
            try {
                Content content=contentQueueIn.take();
                htmlImgIterator.setIn(content);
                for (String s : htmlImgIterator) {
                    try {
                        URL imgUrl=new URL(s);
                        urlQueueOut.put(imgUrl);
                        if(uiImgFound !=null)
                            uiImgFound.accept(null);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }catch (MalformedURLException e){
                e.printStackTrace();
            }

        }
    }
}
