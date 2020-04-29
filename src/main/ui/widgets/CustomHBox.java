package ui.widgets;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import models.Run;
import models.enums.Sitrep;
import models.exceptions.*;
import ui.UIAppLogic;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Calendar;

/**
 * This is the parent custom HBox used in the program. All HBoxes used are its children. It holds the Run each HBox
 * represents and it has three helper functions, updateStatus, endTeam and showAlert.
 *
 * <p>HBoxes are used to populate lists in the timer UI page. I decided to use HBoxes because it is the easiest
 * way of putting different kinds of data in them and then just list them in a graphical list.</p>
 *
 * <p>Looked into the idea of using what the DataPage uses, TreeTables but at the moment I dont have the time
 * to look into it further and if needed do the changes.</p>
 */
public abstract class CustomHBox extends HBox implements CustomHBoxFactory {

// VARIABLES //

    Run run;

    final String CSSAPPRESOURCE = "/ui/css/application.css";

// CONSTRUCTOR //

    CustomHBox(double spacing, Run run) {
        super(spacing);
        this.run = run;
    }

// FUNCTIONS //

    /**
     * Helper function to change the status of a Run. It had more usefulness before, now it is only here in case more
     * changes occur to how status works and we don't need to go into every HBox.
     *
     * @param sitrep    The new situation we will assign to the Run.
     */
    protected void updateStatus(String sitrep) {
        run.setSitrep(Sitrep.valueOf(sitrep));
    }

    /**
     * Helper function to pause a Run and then finish the Run. It calls the controller's endRun() function. It was more
     * useful in the past but now it is only here in case more changes occur to make our life easier.
     *
     * @param controller    UIAppLogic controller to be called endRun() on.
     * @throws CriticalErrorException   From endRun();
     */
    protected void pauseRun(UIAppLogic controller) throws CriticalErrorException {
        controller.endRun(run.getRunNumber());
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


}
