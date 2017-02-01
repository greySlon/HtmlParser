package com.abinail.model;

import com.abinail.interfaces.GettingQueue;

import java.io.*;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

/**
 * Created by Sergii on 25.01.2017.
 */
public class ImgLoader extends Thread {
    private BlockingQueue<URL> urlQueueIn;
    private File folder;

    private Consumer<Long> uiImgLoaded;
    private Consumer<Object> uiImgProcessed;

    private StringBuilder sb = new StringBuilder(300);

    public void setUiImgLoaded(Consumer<Long> uiImgLoaded) {
        this.uiImgLoaded = uiImgLoaded;
    }

    public void setUiImgProcessed(Consumer<Object> uiImgProcessed) {
        this.uiImgProcessed = uiImgProcessed;
    }

    public ImgLoader(BlockingQueue<URL> queueIn, File folder) {
        this.urlQueueIn = queueIn;
        this.folder = folder;
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            if (isInterrupted()) return;
            long fileSize = 0;
            try {
                URL url = urlQueueIn.take();
                String startName = url.toString();
                String newName = sb.append(startName.hashCode())
                        .append(startName.substring(startName.length() - 4, startName.length())).toString();
                sb.setLength(0);
                File file = new File(folder, newName);
                if (!file.exists()) {

                    BufferedInputStream bis = new BufferedInputStream(url.openStream());
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

                    int b = -1;
                    while ((b = bis.read()) != -1) {
                        bos.write(b);
                        fileSize++;
                    }
                    bis.close();
                    bos.close();
                    if (uiImgLoaded != null)
                        uiImgLoaded.accept(fileSize);
                }
                if (uiImgProcessed != null)
                    uiImgProcessed.accept(null);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
