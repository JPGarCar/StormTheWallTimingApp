package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import models.Day;
import java.io.IOException;
import java.util.ArrayList;

public class MainPageController extends UIController {

    private UIAppLogic controller;

    public MainPageController(UIAppLogic controller) {
        super(controller);
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
        if (checkData())
            return;

        ArrayList<String> dayStringList = new ArrayList<>();

        for (Day day : controller.getProgram().getProgramDays().values()) {
            dayStringList.add(day.getDayToRun());
        }

        // Choice dialog to choose what day to start
        ChoiceDialog dialog = new ChoiceDialog(dayStringList.get(0), dayStringList);
        dialog.setTitle("Choose day to start");
        dialog.setHeaderText("Choose day to start");
        dialog.setContentText("Please select what day you want to start timing for.");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        dialog.showAndWait();

        controller.setCurrentDay(controller.getProgram().getProgramDays().get(dialog.getSelectedItem()));


        loadNextPage("MainTiming.fxml", c -> new TimingPageController(controller));
    }

    @FXML
    private void ioDataButtonAction() {
        loadNextPage("dataIOPage.fxml", c -> new DataIOPageController(controller));
    }

    @FXML
    private void dataViewButtonAction() {
        if (checkData())
            return;

        loadNextPage("DataPage.fxml", c -> new DataPageController(controller));
    }

    // EFFECTS: if there is no data sends a warning to the user, and returns true to return
    private boolean checkData() {
        if (controller.getProgram() == null) {
            showAlert(Alert.AlertType.WARNING, "There is no data in the system, please connect" +
                    " to the data base, import local data or import an excel file.", "No data in system!");
            return true;
        } else {
            return false;
        }
    }

    // EFFECTS: do all the FXMLLoader stuff
    private void loadNextPage(String pageName, Callback<Class<?>, Object> fun ) {
        try {
            FXMLLoader root = new FXMLLoader(getClass().getResource(pageName));
            root.setControllerFactory(fun);
            startRaceButton.getScene().setRoot(root.load());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "There has been a critical error, please contact admin.",
                    "Critical Error");
            e.printStackTrace();
        }
    }


}
