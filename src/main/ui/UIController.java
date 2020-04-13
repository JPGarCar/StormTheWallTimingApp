package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.io.IOException;

public class UIController {

    private TimingController controller;

    public UIController(TimingController controller) {
        this.controller = controller;
    }

    public TimingController getController() {
        return controller;
    }

    // EFFECTS: shows an alert
    protected Alert showAlert(Alert.AlertType alertType, String message, String header) {
        Alert alert = new Alert(alertType, message);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        alert.setHeaderText(header);

        if (alertType.equals(Alert.AlertType.CONFIRMATION))
            alert.showAndWait();
        else
            alert.show();

        return alert;
    }

    // EFFECTS: go back to main menu, uses a label to get scene
    protected void backToMainMenu(Scene scene) {
        try {
            controller.saveData();
            FXMLLoader root = new FXMLLoader(getClass().getResource("MainPage.fxml"));
            root.setControllerFactory(c -> new MainPageController(controller));
            scene.setRoot(root.load());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Please contact an admin if the problem persists. Error: "
                    + e.getMessage(), "There has been an error while trying to go back to the main menu.");
        }
    }



}
