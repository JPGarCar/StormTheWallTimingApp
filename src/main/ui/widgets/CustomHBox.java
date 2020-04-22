package ui.widgets;

import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import models.Run;
import models.enums.Sitrep;
import models.exceptions.*;
import ui.UIAppLogic;

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
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/ui/css/application.css").toExternalForm());
        alert.setHeaderText(header);
        alert.setTitle(header);

        if (alertType.equals(Alert.AlertType.CONFIRMATION))
            alert.showAndWait();
        else
            alert.show();

        return alert;
    }


}
