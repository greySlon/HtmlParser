package abinail.model;

import abinail.filters.HtmlImgIterator;
import abinail.interfaces.Event;
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

    public final Event<URL> imgFoundEvent = new Event();

    public ImgExtractor(BlockingQueue<Content> queueIn) {
        super(queueIn);
        super.setDaemon(true);
    }

    public void setAllowed(String containString) {
        htmlIterable.setAllowed(containString);
    }

    @Override
    public void extract(Content content) throws MalformedURLException, InterruptedException {
        htmlIterable.setIn(content);
        for (URL url : htmlIterable) {
            queueOut.put(url);
            imgFoundEvent.fireEvent(this, url);
        }
    }

    @Override
    public void run() {
        while (true) {
            if (isInterrupted()) return;
            try {
                Content content = queueIn.take();
                extract(content);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
}