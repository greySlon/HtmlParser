package abinail.model;

import abinail.filters.HtmlImgIterator;
import abinail.interfaces.HtmlExtractor;
import abinail.interfaces.HtmlIterable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

/**
 * Created by Sergii on 25.01.2017.
 */

public class ImgExtractor extends HtmlExtractor<Content, URL> {
    private HtmlIterable<URL> htmlIterable = new HtmlImgIterator();
    protected Consumer<Object> upImgFoundHandler;

    public ImgExtractor(BlockingQueue<Content> queueIn) {
        super(queueIn);
        super.setDaemon(true);
    }

    public void setUpImgFoundHandler(Consumer<Object> upImgFoundHandler) {
        this.upImgFoundHandler = upImgFoundHandler;
    }

    public void setAllowed(String containString) {
        htmlIterable.setAllowed(containString);
    }

    @Override
    public void extract(Content content) throws MalformedURLException, InterruptedException {
        htmlIterable.setIn(content);
        for (URL url : htmlIterable) {
            queueOut.put(url);
            if (upImgFoundHandler != null) {
                upImgFoundHandler.accept(null);
            }
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