package abinail.viewmodel;


import abinail.model.*;
import com.odessa_flat.interfaces.LinkReader;
import com.odessa_flat.model.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Sergii on 19.02.2017.
 */
public class Starter {
    ExecutorService poolLink;
    ExecutorService poolImg;
    LinkContainer linkContainer;
    HtmlLoader htmlLoader;
    LinkExtractor linkExtractor;
    ImgExtractor imgExtractor;
    ImgLoader imgLoader;
    LinkReader xmlReader;
    boolean sitemapReady = false;

    UIChange uiChange;

    public Starter(UIChange uiChange) {
        this.uiChange = uiChange;
    }

    public boolean isSitemapReady() {
        return sitemapReady;
    }

    public void start(URL url, String dissalowParam, boolean imgLoading) {
        linkContainer = new LinkContainer();
        linkContainer.NonUniqueEvent.addEventListner(uiChange.addNonUniqueHandler);
        linkContainer.UniqueEvent.addEventListner(uiChange.addUniqueHandler);

        htmlLoader = new HtmlLoader(linkContainer.getQueueOut());
        htmlLoader.LinkProcessedEvent.addEventListner(uiChange.linkProcessedHandler);

        linkExtractor = new LinkExtractor.Builder(htmlLoader.getContentQueueOut())
                .enablePassThrough(imgLoading).setDisallowed(dissalowParam).build();
        linkExtractor.LinkFoundEvent.addEventListner(uiChange.linkFoundHandler);


        linkContainer.setQueueIn(linkExtractor.getQueueOut());

        linkContainer.add(new Link(url));
        uiChange.linkFoundHandler.eventHandler(this, 1);

        poolLink = Executors.newFixedThreadPool(6);
        poolLink.submit(linkExtractor);
        poolLink.submit(linkContainer);
        for (int i = 0; i < 4; i++) {
            poolLink.submit(htmlLoader);
        }
        sitemapReady = true;
    }

 /*   public void startFromSitemap() {
        xmlReader.UniqueEvent.addEventListner(uiChange.addUniqueHandler);
        xmlReader.LinkFoundEvent.addEventListner(uiChange.linkFoundHandler);

        htmlLoader = new HtmlLoader(xmlReader.getLinkQueue());
        htmlLoader.LinkProcessedEvent.addEventListner(uiChange.linkProcessedHandler);


        poolLink = Executors.newFixedThreadPool(5);
        poolLink.submit(xmlReader);
        for (int i = 0; i < 4; i++) {
            poolLink.submit(htmlLoader);
        }
    }*/

    public void startAll(URL url, String dissalowParam, String matches, File folderToSave) {
 /*       try {
            xmlReader = new SitemapReader(new URL(url, "sitemap.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (xmlReader == null) {*/
        start(url, dissalowParam, true);
        imgExtractor = new ImgExtractor.Builder(linkExtractor.getQueuePassThrough()).setAllowed(matches).build();
        /*} else {
            startFromSitemap();
            imgExtractor = new ImgExtractor.Builder(htmlLoader.getContentQueueOut()).setAllowed(matches).build();
        }*/

        imgExtractor.ImgFoundEvent.addEventListner(uiChange.imgFoundHandler);

        imgLoader = new ImgLoader(imgExtractor.getQueueOut(), folderToSave);
        imgLoader.ImgLoadedEvent.addEventListner(uiChange.imgLoadedHandler);
        imgLoader.ImgProcessedEvent.addEventListner(uiChange.imgProcessedHandler);

        poolImg = Executors.newFixedThreadPool(2);
        poolImg.submit(imgExtractor);
        poolImg.submit(imgLoader);

    }

    public void stop() {
        if (poolLink != null) poolLink.shutdownNow();
        poolLink = null;
        htmlLoader = null;
        linkExtractor = null;
    }

    public void stopAll() {
        stop();
        if (poolImg != null) poolImg.shutdownNow();
        xmlReader = null;
        imgExtractor = null;
        imgLoader = null;
    }

    public void saveSitemap(File folderToSave) {
        File file = new File(folderToSave, "sitemap.xml");
        try {
            StringBuilder sb = new StringBuilder(200);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            writer.write("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">");
            writer.newLine();
            for (Link link : linkContainer) {
                sb.setLength(0);
                if (link.isOk()) {

                    sb.append("<url><loc>").append(link.toString()).append("</loc></url>");

                    writer.write(sb.toString());
                    writer.newLine();
                }
            }
            writer.write("</urlset>");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sitemapReady = false;
    }
}
