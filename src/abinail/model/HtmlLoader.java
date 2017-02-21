package abinail.model;

import abinail.interfaces.Event;
import abinail.interfaces.Notifier;

import javax.lang.model.element.ElementVisitor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * Created by Sergii on 25.01.2017.
 */
public class HtmlLoader implements Runnable {
    private Queue<Link> linkQueueIn;
    private BlockingQueue<Content> contentQueueOut = new ArrayBlockingQueue<Content>(50);
    private Notifier linkProcessedEventNotifier = new Notifier();

    public final Event linkProcessedEvent = linkProcessedEventNotifier.getEvent();

    public HtmlLoader(Queue<Link> linkQueueIn) {
        this.linkQueueIn = linkQueueIn;
    }

    @Override
    public void run() {
        while (true) {
            Link link = linkQueueIn.poll();
            if (link != null) {
                URL url = link.url;
                try {
                    linkProcessedEventNotifier.raiseEvent(this, null);
                    StringBuilder sb = new StringBuilder(5000);
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                        reader.lines().forEach(s -> sb.append(s));
                    }
                    Content content = new Content(url, sb.toString());
                    contentQueueOut.put(content);
                    link.setOk(true);
                } catch (InterruptedException e) {
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public BlockingQueue<Content> getContentQueueOut() {
        return contentQueueOut;
    }
}
