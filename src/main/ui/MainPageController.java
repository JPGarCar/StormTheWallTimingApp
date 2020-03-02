package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.Day;
import java.io.IOException;
import java.util.ArrayList;

public class MainPageController {

    private TimingController controller;

    public MainPageController(TimingController controller) {
        this.controller = controller;
    }

    @FXML
    protected void initialize() {
        mainImageCenter.setImage(new Image(getClass().getResource("/resources/stormTheWall_Logo.png").toExternalForm()));
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
    private ImageView mainImageCenter;

    @FXML
    private void startRaceButtonAction() {
        if (checkData()) {
            return;
        }

        ArrayList<String> dayStringList = new ArrayList<>();

        for (Day day : controller.getProgram().getProgramDays().values()) {
            dayStringList.add(day.getDayToRun());
        }
        ChoiceDialog dialog = new ChoiceDialog(dayStringList.get(0), dayStringList);
        dialog.setTitle("Choose day to start");
        dialog.setContentText("Please select what day you want to start timing for.");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
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
        if (checkData()) {
            return;
        }

        try {
            FXMLLoader root = new FXMLLoader(getClass().getResource("DataPage.fxml"));
            root.setControllerFactory(c -> new DataPageController(controller));
            dataWindowButton.getScene().setRoot(root.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // EFFECTS: if there is no data sends a warning to the user, and returns true to return
    private boolean checkData() {
        if (controller.getProgram() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "There is no data in the system, please connect" +
                    " to the data base, import local data or import an excel file.");
            alert.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            alert.show();
            return true;
        } else {
            return false;
        }
    }


}
