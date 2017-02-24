package abinail.model;

import abinail.filters.HtmlImgIterator;
import abinail.interfaces.Event;
import abinail.interfaces.Notifier;
import abinail.interfaces.HtmlExtractor;
import abinail.interfaces.HtmlIterable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Sergii on 25.01.2017.
 */

public class ImgExtractor extends HtmlExtractor<Content, URL> {
    private HtmlIterable<URL> htmlIterable = new HtmlImgIterator();
    private Notifier<URL> imgFoundEventNotifier = new Notifier();

    public final Event<URL> imgFoundEvent = imgFoundEventNotifier.getEvent();

    public static class Builder {
        private BlockingQueue<Content> sourceQueue;
        private boolean enablePassThrough;
        private String allowed;

        public Builder(BlockingQueue<Content> sourceQueue) {
            this.sourceQueue = sourceQueue;
        }

        public Builder enablePassThrough(boolean enable) {
            this.enablePassThrough = enable;
            return this;
        }

        public Builder setAllowed(String allowed) {
            this.allowed = allowed;
            return this;
        }

        public ImgExtractor build() {
            return new ImgExtractor(this);
        }
    }

    private ImgExtractor(Builder builder) {
        super(builder.sourceQueue);
        htmlIterable.setAllowed(builder.allowed);
        enableQueuePassTrough(builder.enablePassThrough);
    }

    @Override
    protected void extract() throws InterruptedException {
        try {
            Content content = sourceQueue.take();
            htmlIterable.setIn(content);

            for (URL url : htmlIterable) {
                queueOut.put(url);
                imgFoundEventNotifier.raiseEvent(this, url);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}