package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import models.Day;
import java.io.IOException;
import java.util.ArrayList;

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
        if (controller.getProgram() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "There is no data in the system, please connect" +
                    " to the data base, import local data or import an excel file.");
            alert.show();
            return;
        }

        ArrayList<String> dayStringList = new ArrayList<>();

        for (Day day : controller.getProgram().getProgramDays().values()) {
            dayStringList.add(day.getDayToRun());
        }
        ChoiceDialog dialog = new ChoiceDialog(dayStringList.get(0), dayStringList);
        dialog.setTitle("Choose day to start");
        dialog.setContentText("Please select what day you want to start timing for.");
        dialog.showAndWait();

        controller.setCurrentDay(controller.getProgram().getProgramDays().get(dialog.getSelectedItem()));

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
