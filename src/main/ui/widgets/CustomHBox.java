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
    private void updateStatus(int teamID, String sitrep, TimingController timingController, boolean fromRemaining, boolean staged) {
        Team team = timingController.getProgram().getTeamByTeamNumber(teamID);
        try {
            if (fromRemaining && staged) {
                team.getTeamHeatByHeatNumberFromRemaining(timingController.getStagedHeat().getHeatNumber()).setSitrep(Sitrep.valueOf(sitrep));
            } else if (fromRemaining) {
                team.getTeamHeatByHeatNumberFromRemaining(team.getCurrentHeatID()).setSitrep(Sitrep.valueOf(sitrep));
            } else {
                team.getTeamHeatByHeatNumberFromDone(team.getCurrentHeatID()).setSitrep(Sitrep.valueOf(sitrep));
            }
        } catch (NoTeamHeatException e) {
            e.printStackTrace();
        }
    }
    public void updateStatusForStaged(int teamID, String sitrep, TimingController timingController) {
        updateStatus(teamID, sitrep, timingController, true, true);
    }
    public void updateStatusForRunning(int teamID, String sitrep, TimingController timingController) {
        updateStatus(teamID, sitrep, timingController, true, false);
    }
    public void updateStatusForFinished(int teamID, String sitrep, TimingController timingController) {
        updateStatus(teamID, sitrep, timingController, false, false);
    }

    // EFFECTS: take team out of the running team list, set its final time and show in finished team list
    // ASSUME: id is part of a current team running
    public void endTeam(int id, TimingController controller) throws NoHeatsException, CouldNotCalculateFinalTimeExcpetion, NoCurrentHeatIDException, NoTeamException, NoRemainingHeatsException {
        controller.endTeam(id);
    }


}
