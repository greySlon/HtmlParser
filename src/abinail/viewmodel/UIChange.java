package abinail.viewmodel;

import abinail.interfaces.Event;
import abinail.interfaces.Listner;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Created by Sergii on 27.01.2017.
 */
public class UIChange {
    private int linkUnique = 0;
    private int linkNonUnique = 0;
    private int linkTotal = 0;
    private int linkProcessed = 0;

    private int imgFound = 0;
    private int imgLoaded = 0;
    private int imgProcessed = 0;
    private long imgLoadedSize = 0;
    private ViewController controller;
    private boolean linkProcessingStoped = false;

    UIChange(ViewController controller) {
        this.controller = controller;
    }

    void resetCounters() {
        imgLoadedSize = linkUnique = linkProcessed = imgLoaded = imgFound = imgProcessed = linkNonUnique = 0;
        linkTotal = 1;
        linkProcessingStoped = false;
        controller.linkProcessedBar.setProgress(linkProcessed / (double) linkUnique);
        controller.imgLoadedProgress.setProgress(imgLoaded / (double) imgFound);
        controller.linkList.clear();
    }

    final Listner addNonUniqueHandler = (sender, arg) -> {
        Platform.runLater(() -> {
            linkNonUnique++;
            checkStop();
        });
    };
    final Listner<String> addUniqueHandler = (sender, stringUrl) -> {
        Platform.runLater(() -> {
            controller.linkList.add(stringUrl);
            controller.linkTotalTextField.setText(String.valueOf(++linkUnique));
            controller.linkProcessedBar.setProgress(linkProcessed / (double) linkUnique);
        });
    };

    final Listner<Integer> linkFoundHandler = (sender, count) -> {
        Platform.runLater(() -> {
            linkTotal += count;
            checkStop();
        });
    };

    final Listner linkProcessedHandler = (sender, arg) -> {
        Platform.runLater(() -> {
            controller.linkProcessedTextField.setText(String.valueOf(++linkProcessed));
            controller.linkProcessedBar.setProgress(linkProcessed / (double) linkUnique);
        });
    };

    final Listner imgFoundHandler = (sender, url) -> Platform.runLater(() -> imgFound++);

    final Listner imgProcessedHandler = (sender, arg) -> {
        Platform.runLater(() -> {
            controller.imgLoadedProgress.setProgress(++imgProcessed / (double) imgFound);
            chekStopImgProc();
        });
    };

    final Listner<Long> imgLoadedHandler = (sender, size) -> {
        Platform.runLater(() -> {
            controller.imgLoadedLabel.setText(String.valueOf(++imgLoaded));
            imgLoadedSize += size;
            controller.imgLoadedSizeLabel.setText(String.format("%,d", imgLoadedSize));
        });
    };

    private void checkStop() {
        if (linkUnique == linkProcessed && linkTotal == (linkUnique + linkNonUnique)) {
            controller.stopLinkProcessing();
            linkProcessingStoped = true;
            new Alert(Alert.AlertType.INFORMATION, "Processing links has done ").show();
            chekStopImgProc();
        }
    }

    private void chekStopImgProc() {
        if (controller.isImgLoading() && linkProcessingStoped && imgProcessed == imgFound) {
            controller.stopImgLoading();
            new Alert(Alert.AlertType.INFORMATION, "Loading images has done").show();
        }
    }
}
