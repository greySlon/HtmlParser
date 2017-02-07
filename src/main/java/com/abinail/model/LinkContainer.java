package com.abinail.model;

import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;

/**
 * Created by Sergii on 25.01.2017.
 */
public class LinkContainer extends ConcurrentSkipListSet<Link> {
    private BlockingQueue<URL> queueIn;
    private ConcurrentLinkedQueue<Link> queueOut = new ConcurrentLinkedQueue<>();

    private Consumer<String> upLinkTotalHandler;
    private Thread selfThread;


    public void setUpLinkTotalHandler(Consumer<String> upLinkTotalHandler) {
        this.upLinkTotalHandler = upLinkTotalHandler;
    }

    public void setQueueIn(BlockingQueue<URL> queueIn) {
        this.queueIn = queueIn;
    }

    public void interrupt(){
        selfThread.interrupt();
    }
    public void start() {
        selfThread = new Thread(()-> {
                while (true) {
                    try {
                        URL url = queueIn.take();
                        add(new Link(url));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
        });
        selfThread.setDaemon(true);
        selfThread.start();
    }
    @Override
    public boolean add(Link link) {
        if (super.add(link)) {
            if(upLinkTotalHandler !=null)
                upLinkTotalHandler.accept(link.url.getFile());
            queueOut.add(link);
            return true;
        }else {
            if(upLinkTotalHandler !=null)
                upLinkTotalHandler.accept(null);
            return false;
        }
    }

    public ConcurrentLinkedQueue<Link> getQueueOut() {
        return queueOut;
    }
}
