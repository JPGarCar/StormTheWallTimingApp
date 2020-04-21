package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import java.io.IOException;

/**
 * <h3>Represents</h3> a general UI Controller. It contains the key components of a Controller like resource variables
 * and other common helper function.
 *
 * <h3>Purpose:</h3> Have common vars and helper functions in one parent class.
 *
 * <h3>Contains:</h3>
 *   - CSSHBOXRESOURCE, the resource path to the hBoxInList.css file - String
 *   - CSSAPPRESOURCE, the resource path to the application.css file - String
 *
 * <h3>Usage:</h3>
 *   - showAlert, a helper function to show UI alerts
 *
 */
public class UIController {

// VARIABLES //

    private UIAppLogic controller;

    /**
     * String resource path to the hBoxInList.css file.
     */
    final String CSSHBOXRESOURCE = "/ui/css/hBoxInList.css";

    /**
     * String resource path to the application.css file.
     */
    final String CSSAPPRESOURCE = "/ui/css/application.css";

// GETTERS AND SETTERS

    public UIController(UIAppLogic controller) {
        this.controller = controller;
    }

    public UIAppLogic getController() {
        return controller;
    }

// FUNCTIONS //

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
        alert.getDialogPane().getStylesheets().add(getClass().getResource(CSSAPPRESOURCE).toExternalForm());
        alert.setHeaderText(header);
        alert.setTitle(header);

        if (alertType.equals(Alert.AlertType.CONFIRMATION))
            alert.showAndWait();
        else
            alert.show();

        return alert;
    }

    /**
     * Helper function to go back to the main menu.
     *
     * @param scene The Scene to be used to set the Root .fxml page. You can get it from any item in the screen.
     */
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
