package abinail;

import abinail.viewmodel.ViewController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getResource("viewmodel/View.fxml"));
        Parent root=loader.load();

        primaryStage.setTitle("Html Parser");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        ViewController controller=loader.getController();
        controller.setStage(primaryStage);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                controller.dispose();
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}