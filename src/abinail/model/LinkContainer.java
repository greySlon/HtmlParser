package abinail.model;

import com.odessa_flat.interfaces.Event;
import com.odessa_flat.interfaces.Notifier;
import com.odessa_flat.model.Link;

import java.net.URL;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Sergii on 25.01.2017.
 */
public class LinkContainer extends TreeSet<Link> implements Runnable {
    private BlockingQueue<URL> queueIn;
    private ConcurrentLinkedQueue<Link> queueOut = new ConcurrentLinkedQueue<>();

    private Notifier<String> uniqueEventNotifier = new Notifier<>();
    private Notifier nonUniqueEventNotifier = new Notifier();

    public final Event<String> UniqueEvent = uniqueEventNotifier.getEvent();
    public final Event NonUniqueEvent = nonUniqueEventNotifier.getEvent();

    public void setQueueIn(BlockingQueue<URL> queueIn) {
        this.queueIn = queueIn;
    }

    @Override
    public boolean add(Link link) {
        if (super.add(link)) {
            uniqueEventNotifier.raiseEvent(this, link.url.getFile());
            queueOut.add(link);
            return true;
        } else {
            nonUniqueEventNotifier.raiseEvent(this, null);
            return false;
        }
    }

    public ConcurrentLinkedQueue<Link> getQueueOut() {
        return queueOut;
    }

    @Override
    public void run() {
        while (true) {
            try {
                URL url = queueIn.take();
                add(new Link(url));
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
