package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;


import java.io.IOException;

public class MainPageController {

    private TimingController controller;

    public MainPageController(TimingController controller) {
        this.controller = controller;
    }

    @FXML
    private Button startRaceButton;

    @FXML
    private Button dataWindowButton;

    @FXML
    private Button infoButton;

    @FXML
    private Button ioDataButton;

    @FXML
    private void startRaceButtonAction() {
        try {
            FXMLLoader root = new FXMLLoader(getClass().getResource("MainTiming.fxml"));
            root.setControllerFactory(c -> new mainTimingController(controller));
            startRaceButton.getScene().setRoot(root.load());
        } catch (IOException e) {
            System.out.println("Error on startRaceActionButton");
            e.printStackTrace();
        }
    }

    @FXML
    public void ioDataButtonAction() {
        try {
            FXMLLoader root = new FXMLLoader(getClass().getResource("dataIOPage.fxml"));
            root.setControllerFactory(c -> new DataIOPageController(controller));
            ioDataButton.getScene().setRoot(root.load());
        } catch (IOException e) {
            System.out.println("Error on ioDataActionButton");
            e.printStackTrace();
        }
    }

    @FXML
    public void dataViewButtonAction() {
        try {
            FXMLLoader root = new FXMLLoader(getClass().getResource("DataPage.fxml"));
            root.setControllerFactory(c -> new DataPageController(controller));
            dataWindowButton.getScene().setRoot(root.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
