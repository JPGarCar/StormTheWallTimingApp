package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


public class main extends Application {

    private TimingController controller;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader root = new FXMLLoader(getClass().getResource("MainPage.fxml"));
        controller = new TimingController();
        root.setControllerFactory(c -> new MainPageController(controller));
        Scene scene = new Scene(root.load());
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        primaryStage.setTitle("Storm the Wall Timing App");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

}
