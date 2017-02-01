package com.abinail.viewmodel;

import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Created by Sergii on 27.01.2017.
 */
public class UIChange {
    private int linkUnique = 0;
    private int linkNonUnique=0;
    private int linkTotal=0;
    private int linkProcessed = 0;

    private int imgFound = 0;
    private int imgLoaded = 0;
    private int imgProcessed = 0;
    private long imgLoadedSize = 0;
    private ViewController controller;
    private boolean linkProcessingStoped = false;

    public UIChange(ViewController controller) {
        this.controller = controller;
    }

    public void upLinkTotalUnique(String url) {
        Platform.runLater(() -> {
            if(url!=null) {
                controller.linkList.add(url);
                controller.linkTotalTextField.setText(String.valueOf(++linkUnique));
                controller.linkProcessedBar.setProgress(linkProcessed / (double) linkUnique);
            }else{
                linkNonUnique++;
//                System.out.println("container");
                checkStop();
            }
        });
    }

    public void upLinkProcessed(int count) {
        Platform.runLater(() -> {
            linkTotal+= count;
            controller.linkProcessedTextField.setText(String.valueOf(++linkProcessed));
            controller.linkProcessedBar.setProgress(linkProcessed / (double) linkUnique);
//            System.out.println("extractor");
            checkStop();
        });
    }

    private void checkStop() {
//        System.out.println(String.format("linkProcessed %d linkTotal %d linkUnique %d linkNonUnique %d",
//                linkProcessed, linkTotal, linkUnique,  linkNonUnique));
        if (linkUnique == linkProcessed && linkTotal==(linkUnique+linkNonUnique)) {
            controller.stopLinkProcessing();
            linkProcessingStoped = true;
            if (!controller.isImgLoading())
                new Alert(Alert.AlertType.INFORMATION, "Done").showAndWait();
        }
    }

    public void upImgFound(Object o) {
        Platform.runLater(() -> imgFound++);
    }

    public void upImgProcessed(Object o) {
        Platform.runLater(() -> {
            controller.imgLoadedProgress.setProgress(++imgProcessed / (double) imgFound);
            if (linkProcessingStoped && imgProcessed == imgFound) {
                controller.stopImgLoading();
                new Alert(Alert.AlertType.INFORMATION, "Done").showAndWait();
            }
        });
    }

    public void upImgLoaded(long size) {
        Platform.runLater(() -> {
            controller.imgLoadedLabel.setText(String.valueOf(++imgLoaded));
            imgLoadedSize += size;
            controller.imgLoadedSizeLabel.setText(String.format("%,d", imgLoadedSize));
        });
    }

    public void resetCounters() {
        imgLoadedSize = linkUnique = linkProcessed = imgLoaded = imgFound = imgProcessed = linkNonUnique=0;
        linkTotal=1;
        controller.linkProcessedBar.setProgress(linkProcessed / (double) linkUnique);
        controller.imgLoadedProgress.setProgress(imgLoaded / (double) imgFound);
        controller.linkList.clear();
    }
}
