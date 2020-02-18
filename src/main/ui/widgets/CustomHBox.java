package ui.widgets;

import javafx.collections.FXCollections;
import javafx.scene.layout.HBox;
import models.Team;
import models.TeamHeat;
import models.enums.Sitrep;
import models.exceptions.*;
import ui.TimingController;

import java.util.ArrayList;
import java.util.Calendar;

public abstract class CustomHBox extends HBox {


    CustomHBox(double spacing) {
        super(spacing);
    }

    // EFFECTS: update the status of a specific team, only use for team in stage heat list
    public void updateStatus(int teamID, String sitrep, TimingController timingController) {
        for (Team team : timingController.getStagedHeat().getTeams()) {
            if (team.getTeamNumber() == teamID) {
                team.getTeamHeatByHeatID(timingController.getStagedHeat().getHeatNumber()).setSitrep(Sitrep.valueOf(sitrep));
            }
        }
    }

    // EFFECTS: take team out of the running team list, set its final time and show in finished team list
    // ASSUME: id is part of a current team running
    public void endTeam(int id, TimingController controller) throws NoHeatsException, CouldNotCalculateFinalTimeExcpetion, NoCurrentHeatIDException, NoTeamException, NoRemainingHeatsException {
        for (Team team : controller.getRunningTeams()) {
            if (team.getTeamNumber() == id) {
                team.markEndTime(Calendar.getInstance());
                controller.addFinishedTeam(team);
                controller.removeRunningTeamWithUpdate(team);
                break;
            }
        }

    }


}
