package abinail.viewmodel;

import abinail.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
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
    private boolean imgLoading;

    ObservableList<String> linkList = FXCollections.observableArrayList();

    private Starter starter;


    public boolean isImgLoading() {
        return imgLoading;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    void stopLinkProcessing() {
        starter.stop();
        starter.saveSitemap(folderToSave);
    }

    void stopImgLoading() {
        starter.stopAll();
    }

    public void dispose() {
        if (starter != null) {
            starter.stopAll();
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
        if (folderToSave == null) {
            new Alert(Alert.AlertType.ERROR, "There is no folder to save", ButtonType.OK).showAndWait();
            return;
        }
        uiChange.resetCounters();
        URL url = new URL(protocol, host, "/");

        starter = new Starter(uiChange);
        if (imgLoading) {
            starter.startAll(url, paramTextField.getText(), matchesTextField.getText(), folderToSave);
        } else {
            starter.start(url, paramTextField.getText());
        }

        cancelButton.setDisable(false);
        startButton.setDisable(true);
    }

    @FXML
    public void cancelButtonOnClick() {
        cancelButton.setDisable(true);
        dispose();
        new Alert(Alert.AlertType.INFORMATION, "Canceled").showAndWait();
    }

    @FXML
    private void saveButtonOnClick() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        folderToSave = directoryChooser.showDialog(stage);
    }

}
