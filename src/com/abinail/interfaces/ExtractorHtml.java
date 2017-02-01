package com.abinail.interfaces;

import com.abinail.model.Content;

import java.net.MalformedURLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

/**
 * Created by Sergii on 01.02.2017.
 */
abstract public class ExtractorHtml<T, U> extends Thread {
    protected BlockingQueue<T> queueIn;
    protected BlockingQueue<U> queueOut;
    protected HtmlIterable htmlIterable;
    protected Consumer<Object> processEventHandler;

    abstract public void extract(T t) throws MalformedURLException;

    public void setAllowed(String allowed) {
    }

    public void setDisallowed(String disallowed) {
    }

    public void setProcessEventHandler(Consumer<Object> processEventHandler) {
        this.processEventHandler = processEventHandler;
    }

    public void enableQueueOut() {
        this.queueOut = new ArrayBlockingQueue<U>(100);
    }

    public BlockingQueue<U> getQueueOut() {
        if (queueOut != null) {
            return queueOut;
        } else {
            throw new RuntimeException("No queue created");
        }
    }
}
