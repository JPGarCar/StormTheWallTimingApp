package ui.widgets;

import javafx.scene.layout.HBox;
import models.Team;
import models.enums.Sitrep;
import models.exceptions.*;
import ui.TimingController;

public abstract class CustomHBox extends HBox {


    CustomHBox(double spacing) {
        super(spacing);
    }

    // EFFECTS: update the status of a specific team private does all the work, three publics for different situations
    private void updateStatus(int teamNumber, String sitrep, TimingController timingController, int heatNumber, boolean fromRemaining, boolean staged) {
        Team team = timingController.getProgram().getTeamByTeamNumber(teamNumber);
        try {
            if (fromRemaining && staged) {
                team.getRunByHeatNumber(timingController.getStagedHeat().getHeatNumber()).setSitrep(Sitrep.valueOf(sitrep));
            } else if (fromRemaining) {
                team.getCurrentRun().setSitrep(Sitrep.valueOf(sitrep));
            } else {
                team.getRunByHeatNumber(heatNumber).setSitrep(Sitrep.valueOf(sitrep));
            }
        } catch (NoRunFoundException e) {
            e.printStackTrace();
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
