package abinail.interfaces;

import java.net.MalformedURLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Sergii on 01.02.2017.
 */
abstract public class HtmlExtractor<T, U> extends Thread {
    protected BlockingQueue<T> queueIn;
    protected BlockingQueue<U> queueOut = new ArrayBlockingQueue<U>(100);
    protected BlockingQueue<T> queuePassThrough;

    public HtmlExtractor(BlockingQueue<T> queueIn) {
        this.queueIn = queueIn;
        setDaemon(true);
    }

    abstract public void extract(T t) throws MalformedURLException, InterruptedException;

    public void enableQueuePassTrough() {
        this.queuePassThrough = new ArrayBlockingQueue<T>(100);
    }

    public BlockingQueue<U> getQueueOut() {
        return queueOut;
    }

    public BlockingQueue<T> getQueuePassThrough() {
        if (queuePassThrough != null) {
            return queuePassThrough;
        } else {
            throw new RuntimeException("No queue created");
        }
    }
}
