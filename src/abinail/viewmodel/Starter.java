package abinail.viewmodel;

import abinail.model.*;
import abinail.viewmodel.UIChange;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Sergii on 19.02.2017.
 */
public class Starter {
    LinkContainer linkContainer;
    HtmlLoader htmlLoader;
    LinkExtractor linkExtractor;
    ImgExtractor imgExtractor;
    ImgLoader imgLoader;
    boolean started = false;
    boolean startedAll = false;

    UIChange uiChange;

    public Starter(UIChange uiChange) {
        this.uiChange = uiChange;
    }

    public void start(URL url, String dissalowParam) {
        linkContainer = new LinkContainer();
        linkContainer.NonUniqueEvent.addEventListner(uiChange.addNonUniqueHandler);
        linkContainer.UniqueEvent.addEventListner(uiChange.addUniqueHandler);

        htmlLoader = new HtmlLoader(linkContainer.getQueueOut(), 4);
        htmlLoader.linkProcessedEvent.addEventListner(uiChange.linkProcessedHandler);

        linkExtractor = new LinkExtractor(htmlLoader.getContentQueueOut());
        linkExtractor.setDisallowed(dissalowParam);
        linkExtractor.linkFoundEvent.addEventListner(uiChange.linkFoundHandler);

        linkContainer.setQueueIn(linkExtractor.getQueueOut());


        linkContainer.add(new Link(url));
        linkContainer.start();
        htmlLoader.start();
        linkExtractor.start();
        started = true;
    }

    public void startAll(URL url, String dissalowParam, String matches, File folderToSave) {
        start(url, dissalowParam);
        linkExtractor.enableQueuePassTrough();

        imgExtractor = new ImgExtractor(linkExtractor.getQueuePassThrough());
        imgExtractor.setAllowed(matches);
        imgExtractor.imgFoundEvent.addEventListner(uiChange.imgFoundHandler);
        imgExtractor.start();

        imgLoader = new ImgLoader(imgExtractor.getQueueOut(), folderToSave);
        imgLoader.imgLoadedEvent.addEventListner(uiChange.imgLoadedHandler);
        imgLoader.imgProcessedEvent.addEventListner(uiChange.imgProcessedHandler);
        imgLoader.start();
        startedAll = true;
    }

    public void stop() {
        if (started) {
            linkContainer.interrupt();
            linkExtractor.interrupt();
            htmlLoader.stop();
            started = false;
        }
    }

    public void stopAll() {
        stop();
        if (startedAll) {
            imgExtractor.interrupt();
            imgLoader.interrupt();
            startedAll = false;
        }
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
    }
}
