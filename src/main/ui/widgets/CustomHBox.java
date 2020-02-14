package ui.widgets;

import javafx.collections.FXCollections;
import javafx.scene.layout.HBox;
import models.Team;
import models.enums.Sitrep;
import models.exceptions.CouldNotCalculateFinalTimeExcpetion;
import models.exceptions.NoCurrentHeatIDException;
import models.exceptions.NoHeatsException;
import models.exceptions.NoTeamException;
import ui.TimingController;

import java.util.ArrayList;
import java.util.Calendar;

public abstract class CustomHBox extends HBox {


    CustomHBox(double spacing) {
        super(spacing);
    }

    // EFFECTS: update the status of a specific team
    public void updateStatus(int id, String sitrep, TimingController timingController) {
        for (Team team : timingController.getStagedHeat().getTeams()) {
            if (team.getTeamNumber() == id) {
                team.setSitRep(Sitrep.valueOf(sitrep));
            }
        }
    }

    // EFFECTS: take team out of the running team list, set its final time and show in finished team list
    // ASSUME: id is part of a current team running
    public void endTeam(int id, TimingController controller) throws NoHeatsException, CouldNotCalculateFinalTimeExcpetion, NoCurrentHeatIDException, NoTeamException {
        for (Team team : controller.getRunningTeams()) {
            if (team.getTeamNumber() == id) {
                team.markEndTime(Calendar.getInstance());
                controller.addFinishedTeam(team);
                controller.removeRunningTeam(team);
                break;
            }
        }

    }


}
