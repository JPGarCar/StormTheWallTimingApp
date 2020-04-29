package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import ui.MainSectionPages.MainPageController;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;

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
    final public String CSSHBOXRESOURCE = "/ui/css/hBoxInList.css";

    /**
     * String resource path to the application.css file.
     */
    final public String CSSAPPRESOURCE = "/ui/css/application.css";

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
     * Helper function to show an alert to the user with the correct CSS file, plus a button to print the stack trace
     * to a file.
     *
     * <p>Depending on the type of Alert, the Alert will be shown or will be shown and the program will wait for
     * an action. At the moment only the AlertType.CONFIRMATION type of Alert will show and wait the Alert. All
     * other Alert will just show without a wait.</p>
     *
     * <p>An extra button to print the stack trace to a file in case admin needs it for debugging. For this
     * we don't use the default Alert text, we build a vertical box with the text as a Label and then the button.</p>
     *
     * @param alertType The type of alert to be shown.
     * @param message   The message to be shown in the alert.
     * @param header    The title of the alert.
     * @param e         Exception to be printed to file if needed.
     * @return  The Alert to be shown in case its needed to know when the OK button is pushed.
     * @helper stackTraceToFile
     */
    protected Alert showAlert(Alert.AlertType alertType, String message, String header, Exception e) {
        // Alert stuff
        Alert alert = new Alert(alertType);
        alert.getDialogPane().getStylesheets().add(getClass().getResource(CSSAPPRESOURCE).toExternalForm());
        alert.setHeaderText(header);
        alert.setTitle(header);

        // Button to create stack trace file
        Button button = new Button();
        button.setText("Create Stack Trace File");
        button.setOnAction(click -> {
            try {
                stackTraceToFile(e);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        // VBox to put the message and button together
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setStyle("-fx-background-color: white");

        // Where the message will live
        Label label  = new Label(message);
        label.setFont(new Font(18));

        // Add all the objects
        vBox.getChildren().addAll(label, button);
        alert.getDialogPane().setContent(vBox);

        // show alert
        if (alertType.equals(Alert.AlertType.CONFIRMATION))
            alert.showAndWait();
        else
            alert.show();

        return alert;
    }

    /**
     * Writes the stack trace of an exception to a file with file name, stackTrace.txt
     *
     * @param e Exception from which to grab stack trace to write to file.
     * @throws IOException  If there is an error with the file stackTrace.txt.
     */
    private void stackTraceToFile(Exception e) throws IOException {
        PrintStream printWriter = new PrintStream(new File(System.getProperty("user.dir") + "/logFiles/" +
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "-" + Calendar.getInstance().get(Calendar.MONTH) +
                "-" + Calendar.getInstance().get(Calendar.YEAR) + "_stackTrace.log"));
        e.printStackTrace(printWriter);
        printWriter.close();
    }

    /**
     * Helper function to go back to the main menu.
     *
     * @param scene The Scene to be used to set the Root .fxml page. You can get it from any item in the screen.
     */
    protected void backToMainMenu(Scene scene) {
        try {
            controller.saveData();
            FXMLLoader root = new FXMLLoader(getClass().getResource("/ui/MainSectionPages/MainPage.fxml"));
            root.setControllerFactory(c -> new MainPageController(controller));
            scene.setRoot(root.load());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Please contact an admin if the problem persists. Error: "
                    + e.getMessage(), "There has been an error while trying to go back to the main menu.", e);
        }
    }



}
