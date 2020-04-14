package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

import java.io.IOException;

public class UIController {

    private UIAppLogic controller;

    public UIController(UIAppLogic controller) {
        this.controller = controller;
    }

    public UIAppLogic getController() {
        return controller;
    }

    /**
     * Helper function to show an alert to the user with the correct CSS file.
     *
     * <p>Depending on the type of Alert, the Alert will be shown or will be shown and the program will wait for
     * an action. At the moment only the AlertType.CONFIRMATION type of Alert will show and wait the Alert. All
     * other Alert will just show without a wait.</p>
     *
     * @param alertType The type of alert to be shown.
     * @param message   The message to be shown in the alert.
     * @param header    The title of the alert.
     * @return  The Alert to be shown in case its needed to know when the OK button is pushed.
     */
    protected Alert showAlert(Alert.AlertType alertType, String message, String header) {
        Alert alert = new Alert(alertType, message);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        alert.setHeaderText(header);
        alert.setTitle(header);

        if (alertType.equals(Alert.AlertType.CONFIRMATION))
            alert.showAndWait();
        else
            alert.show();

        return alert;
    }

    // EFFECTS: go back to main menu, requires a scene
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
