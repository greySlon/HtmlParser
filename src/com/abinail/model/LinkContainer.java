package com.abinail.model;

import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;

/**
 * Created by Sergii on 25.01.2017.
 */
public class LinkContainer extends ConcurrentSkipListSet<Link> {
    private ConcurrentLinkedQueue<Link> linkQueueOut=new ConcurrentLinkedQueue<>();
    private Consumer<String> uiConsumer;

    public void setUiConsumer(Consumer<String> uiConsumer) {
        this.uiConsumer = uiConsumer;
    }

    @Override
    public boolean add(Link link) {
        if (super.add(link)) {
            if(uiConsumer!=null)
                uiConsumer.accept(link.url.getFile());
            linkQueueOut.add(link);
            return true;
        }else
            return false;
    }

    public ConcurrentLinkedQueue<Link> getLinkQueueOut() {
        return linkQueueOut;
    }
}
