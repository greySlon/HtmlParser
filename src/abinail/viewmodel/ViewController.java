package abinail.viewmodel;

import abinail.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

public class ViewController {
    @FXML
    CheckBox sitemapCheckBox;
    @FXML
    CheckBox loadCheckBox;
    @FXML
    HBox loadHbox;

    @FXML
    Button startButton;
    @FXML
    Button cancelButton;
    @FXML
    ComboBox protocolComboBox;
    @FXML
    TextField domainTextField;
    @FXML
    TextField paramTextField;
    @FXML
    ListView linkListView;
    @FXML
    ProgressBar linkProcessedBar;
    @FXML
    Label linkProcessedTextField;
    @FXML
    Label linkTotalTextField;

    @FXML
    TextField matchesTextField;
    @FXML
    Label imgLoadedLabel;
    @FXML
    Label imgLoadedSizeLabel;
    @FXML
    ProgressIndicator imgLoadedProgress;

    String host = null, protocol = null;
    private File folderToSave;
    private Stage stage;
    private UIChange uiChange;

    ObservableList<String> linkList = FXCollections.observableArrayList();
    private LinkContainer linkContainer;
    private HtmlLoader htmlLoader;
    private LinkExtractor linkExtractor;
    private ImgExtractor imgExtractor;
    private ImgLoader imgLoader;

    private boolean imgLoading;

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    void stopLinkProcessing() {
        if(htmlLoader!=null)htmlLoader.stop();
        if(linkExtractor!=null)linkExtractor.interrupt();
        if(linkContainer!=null) {
            linkContainer.interrupt();
            saveSitemap();
        }
    }

    void stopImgLoading() {
        if(imgExtractor!=null)imgExtractor.interrupt();
        if(imgLoader!=null)imgLoader.interrupt();
    }

    private void saveSitemap() {
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

    public void dispose() {
        stopLinkProcessing();
        if (imgLoading) {
            stopImgLoading();
        }
    }

    @FXML
    private void initialize() {
        uiChange = new UIChange(this);
        linkListView.setItems(linkList);
        protocolComboBox.setItems(FXCollections.observableArrayList(Arrays.asList("http", "https")));
        protocolComboBox.getSelectionModel().select(0);
    }

    @FXML
    private void saveButtonOnClick() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        folderToSave = directoryChooser.showDialog(stage);
    }

    @FXML
    private void sitemapMenuOnAction() {
        sitemapCheckBox.setSelected(true);
        loadCheckBox.setSelected(false);
        imgLoading = false;
        loadHbox.setDisable(true);
    }

    @FXML
    private void loadMenuOnAction() {
        loadCheckBox.setSelected(true);
        sitemapCheckBox.setSelected(false);
        imgLoading = true;
        loadHbox.setDisable(false);
    }

    @FXML
    public void handleHostInput() {
        host = domainTextField.getText();
        if (host != null && !"".equals(host)) {
            protocol = protocolComboBox.getValue().toString();
            startButton.setDisable(false);
        } else
            startButton.setDisable(true);
    }

    @FXML
    private void startButtonOnClick() throws MalformedURLException {
        uiChange.resetCounters();

        if (folderToSave == null) {
            new Alert(Alert.AlertType.ERROR, "There is no folder to save", ButtonType.OK).showAndWait();
            return;
        }

        linkContainer = new LinkContainer();
        linkContainer.setUpLinkTotalHandler(uiChange::upLinkTotalUnique);


        htmlLoader = new HtmlLoader(linkContainer.getQueueOut(), 4);

        linkExtractor = new LinkExtractor(htmlLoader.getContentQueueOut());
        linkExtractor.setDisallowed(paramTextField.getText());
        linkExtractor.setUpLinkProcessedHandler(uiChange::upLinkProcessed);

        linkContainer.setQueueIn(linkExtractor.getQueueOut());

        if (imgLoading) {
            linkExtractor.enableQueuePassTrough();

            imgExtractor = new ImgExtractor(linkExtractor.getQueuePassThrough());
            imgExtractor.setAllowed(matchesTextField.getText());
            imgExtractor.setUpImgFoundHandler(uiChange::upImgFound);
            imgExtractor.start();

            imgLoader = new ImgLoader(imgExtractor.getQueueOut(), folderToSave);
            imgLoader.setUiImgLoaded(uiChange::upImgLoaded);
            imgLoader.setUiImgProcessed(uiChange::upImgProcessed);
            imgLoader.start();
        }

        linkContainer.add(new Link(new URL(protocol, host, "/")));
        linkContainer.start();
        htmlLoader.start();
        linkExtractor.start();

        cancelButton.setDisable(false);
        startButton.setDisable(true);
    }

    @FXML
    public void cancelButtonOnClick() {
        cancelButton.setDisable(true);
        dispose();
        new Alert(Alert.AlertType.INFORMATION, "Canceled").showAndWait();
    }

}
