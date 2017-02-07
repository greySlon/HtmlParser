package com.abinail.model;

import com.abinail.interfaces.GettingQueue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * Created by Sergii on 25.01.2017.
 */
public class HtmlLoader implements Runnable {
    private Queue<Link> linkQueueIn;
    private int nThreads;
    private ExecutorService exec;
    private BlockingQueue<Content> contentQueueOut = new ArrayBlockingQueue<Content>(50);

    public HtmlLoader(Queue<Link> linkQueueIn, int nThreads) {
        this.linkQueueIn = linkQueueIn;
        this.nThreads = (nThreads > 0) ? nThreads : 1;
        this.exec = Executors.newFixedThreadPool(nThreads);
    }

    @Override
    public void run() {
        Link link = linkQueueIn.poll();
        if (link != null) {
            URL url = link.url;
            try {
                StringBuilder sb = new StringBuilder(5000);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    reader.lines().forEach(s -> sb.append(s));
                }
                Content content = new Content(url, sb.toString());
                contentQueueOut.put(content);
                link.setOk(true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        exec.execute(this);
    }

    public void start() {
        for (int i = 0; i < nThreads; i++) {
            exec.execute(this);
        }
    }

    public void stop() {
        exec.shutdownNow();
    }

    public BlockingQueue<Content> getContentQueueOut() {
        return contentQueueOut;
    }
}
