package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;


public class main extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            FXMLLoader root = new FXMLLoader(getClass().getResource("/ui/MainSectionPages/IntroPage.fxml"));
            Scene scene = new Scene(root.load(), 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/ui/css/application.css").toExternalForm());
            primaryStage.setTitle("UBC Recreation Timing System");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "There has been an critical error. " +
                    "Please contact admin for assistance. " + e.getMessage());
            alert.setHeaderText("There has been an error!");
            alert.show();
            e.printStackTrace();
        }

    }

}
