package abinail.interfaces;

import java.net.MalformedURLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Sergii on 01.02.2017.
 */
abstract public class HtmlExtractor<T, U> extends Thread {
    protected BlockingQueue<T> queueIn;
    protected BlockingQueue<U> queueOut;
    protected BlockingQueue<T> queuePassThrough;
    protected HtmlIterable<U> htmlIterable;

    public HtmlExtractor(BlockingQueue<T> queueIn) {
        this.queueIn = queueIn;
        queueOut=new ArrayBlockingQueue<U>(100);
    }

    abstract public void extract(T t) throws MalformedURLException, InterruptedException;

    public void setAllowed(String allowed) {
    }

    public void setDisallowed(String disallowed) {
    }

    public void enableQueuePassTrough() {
        this.queuePassThrough = new ArrayBlockingQueue<T>(100);
    }

    public BlockingQueue<U> getQueueOut() {
        if (queueOut != null) {
            return queueOut;
        } else {
            throw new RuntimeException("No queue created");
        }
    }

    public BlockingQueue<T> getQueuePassThrough() {
        return queuePassThrough;
    }
}
