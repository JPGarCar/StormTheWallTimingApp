package ui.widgets;

import javafx.scene.control.Alert;
import javafx.scene.layout.HBox;
import models.Run;
import models.Team;
import models.enums.Sitrep;
import models.exceptions.*;
import ui.TimingController;

public abstract class CustomHBox extends HBox {

// CONSTRUCTOR //

    CustomHBox(double spacing) {
        super(spacing);
    }

// FUNCTIONS //

    // EFFECTS: update the status of a specific team private does all the work, three public for different situations
    private void updateStatus(int teamNumber, String sitrep, TimingController timingController, int heatNumber, boolean fromRemaining, boolean staged) {
        Team team = timingController.getProgram().getTeamByTeamNumber(teamNumber);
        try {
            Run run;
            if (fromRemaining && staged) {
                run = team.getRunByHeatNumber(timingController.getStagedHeat().getHeatNumber());
            } else if (fromRemaining) {
                run = team.getCurrentRun();
            } else {
                run = team.getRunByHeatNumber(heatNumber);
            }

            run.setSitrep(Sitrep.valueOf(sitrep));
        } catch (NoRunFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "If the error persists please " +
                    "contact an admin. Error: " + e.getMessage());
            alert.setHeaderText("There has been an error while trying to update the status");
            alert.getDialogPane().getStylesheets().add(timingController.getClass().getResource("application.css").toExternalForm());
            alert.show();
        }
    }
    public void updateStatusForStaged(int teamID, String sitrep, TimingController timingController) {
        updateStatus(teamID, sitrep, timingController,0, true, true);
    }
    public void updateStatusForRunning(int teamID, String sitrep, TimingController timingController) {
        updateStatus(teamID, sitrep, timingController,0, true, false);
    }
    public void updateStatusForFinished(int teamID, String sitrep, TimingController timingController, int heatNumber) {
        updateStatus(teamID, sitrep, timingController, heatNumber, false, false);
    }

    // EFFECTS: take team out of the running team list, set its final time and show in finished team list
    // ASSUME: id is part of a current team running
    public void endTeam(int teamNumber, int heatNumber, TimingController controller) throws NoHeatsException, CouldNotCalculateFinalTimeExcpetion {
        controller.endRun(teamNumber, heatNumber);
    }


}
