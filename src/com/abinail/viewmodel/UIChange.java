package com.abinail.viewmodel;

import javafx.application.Platform;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Sergii on 27.01.2017.
 */
public class UIChange {
    private int linkTotal = 0;
    private int linkProcessed = 0;
    private int imgFound = 0;
    private int imgLoaded = 0;
    private int imgProcessed=0;
    private long imgLoadedSize = 0;
    private ViewController controller;
    private boolean linkProcessingStoped=false;

    public UIChange(ViewController controller) {
        this.controller = controller;
    }

    public void upLinkTotal(String url) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controller.linkList.add(url);
                controller.linkTotalTextField.setText(String.valueOf(++linkTotal));
                controller.linkProcessedBar.setProgress(linkProcessed / (double) linkTotal);
            }
        });
    }

    public void upLinkProcessed(Object o) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controller.linkProcessedTextField.setText(String.valueOf(++linkProcessed));
                controller.linkProcessedBar.setProgress(linkProcessed / (double) linkTotal);
                if (linkTotal == linkProcessed) {
                    controller.stopLinkProcessing();
                    linkProcessingStoped=true;
                }
            }
        });
    }

    public void upImgFound(Object o) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                imgFound++;
            }
        });
    }

    public  void upImgProcessed(Object o){
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        controller.imgLoadedProgress.setProgress(++imgProcessed/(double)imgFound);
                        if(linkProcessingStoped && imgProcessed==imgFound)
                            controller.stopImgLoading();
                    }
                });
    }

    public void upImgLoaded(long size) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                controller.imgLoadedLabel.setText(String.valueOf(++imgLoaded));
                imgLoadedSize += size;
                controller.imgLoadedSizeLabel.setText(String.format("%,d", imgLoadedSize));
            }
        });
    }

    public void resetCounters() {
        imgLoadedSize = linkTotal = linkProcessed = imgLoaded=imgFound=imgProcessed=0;
        controller.linkProcessedBar.setProgress(linkProcessed / (double) linkTotal);
        controller.imgLoadedProgress.setProgress(imgLoaded / (double) imgFound);
    }
}
