package abinail.model;

import abinail.interfaces.Event;

import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by Sergii on 25.01.2017.
 */
public class LinkContainer extends ConcurrentSkipListSet<Link> {
    private BlockingQueue<URL> queueIn;
    private ConcurrentLinkedQueue<Link> queueOut = new ConcurrentLinkedQueue<>();
    private Thread selfThread;

    public final Event<String> addUniqueEvent = new Event<>();
    public final Event addNonUniqueEvent = new Event();

    public void setQueueIn(BlockingQueue<URL> queueIn) {
        this.queueIn = queueIn;
    }

    public void interrupt() {
        selfThread.interrupt();
    }

    public void start() {
        selfThread = new Thread(() -> {
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
            addUniqueEvent.fireEvent(this, link.url.getFile());
            queueOut.add(link);
            return true;
        } else {
            addNonUniqueEvent.fireEvent(this, null);
            return false;
        }
    }

    public ConcurrentLinkedQueue<Link> getQueueOut() {
        return queueOut;
    }
}
