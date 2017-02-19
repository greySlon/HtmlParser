package abinail.model;

import abinail.interfaces.Event;
import abinail.interfaces.Notifier;
import com.sun.istack.internal.NotNull;

import java.io.*;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sergii on 25.01.2017.
 */
public class ImgLoader extends Thread {
    private BlockingQueue<URL> urlQueueIn;
    private File folder;
    private StringBuilder sb = new StringBuilder(300);

    private Notifier<Long> imgLoadedEventNotifier = new Notifier<>();
    private Notifier imgProcessedEventNotifier = new Notifier();

    public final Event<Long> imgLoadedEvent = imgLoadedEventNotifier.getEvent();
    public final Event imgProcessedEvent = imgProcessedEventNotifier.getEvent();

    public ImgLoader(BlockingQueue<URL> queueIn, File folder) {
        this.urlQueueIn = queueIn;
        this.folder = folder;
    }

    private Pattern pattern = Pattern.compile("(?<=\\/)[^\\/]+?$", Pattern.CASE_INSENSITIVE);

    @Override
    public void run() {
        while (true) {
            if (isInterrupted()) return;
            try {
                URL url = urlQueueIn.take();
                String newName = getNewName(url.getPath());
                saveFile(url, newName);
            } catch (InterruptedException e) {
                System.err.println(getClass().getName() + " INTERRUPTED");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getNewName(String startName) throws IOException {
        sb.setLength(0);
        Matcher matcher = pattern.matcher(startName);
        String fName = null;
        if (matcher.find()) {
            fName = matcher.group();
        } else {
            imgProcessedEventNotifier.raiseEvent(this, null);
            throw new IOException();
        }
        return sb.append(fName.substring(0, fName.length() - 4)).append("(")
                .append(startName.hashCode()).append(")")
                .append(fName.substring(fName.length() - 4, fName.length())).toString();
    }

    private void saveFile(URL url, String name) throws IOException {
        long fileSize = 0;
        File file = new File(folder, name);
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
            imgLoadedEventNotifier.raiseEvent(this, fileSize);
        }
        imgProcessedEventNotifier.raiseEvent(this, null);
    }
}
