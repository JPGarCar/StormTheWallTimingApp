package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MainPageController {

    @FXML
    private Button startRaceButton;

    @FXML
    private Button dataWindowButton;

    @FXML
    private Button infoButton;

    @FXML
    private Button inputDataButton;

    @FXML
    private void startRaceButtonAction() {
        try {
            startRaceButton.getScene().setRoot(FXMLLoader.load(getClass().getResource("MainTiming.fxml")));
        } catch (IOException e) {
            System.out.println("Error on startRaceActionButton");
            e.printStackTrace();
        }
    }


}
