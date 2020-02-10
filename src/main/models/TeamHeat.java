package models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import models.exceptions.CouldNotCalculateFinalTimeExcpetion;
import models.exceptions.NoHeatsException;
import models.exceptions.NoTeamException;

import java.util.Calendar;

public class TeamHeat {

    // private fields
    private int heatID;
    private FinalTime finalTime;

    // private connections
    @JsonBackReference
    private Team team;

    // DUMMY CONSTRUCTOR for Jackson JSON
    public TeamHeat() {
    }

    // CONSTRUCTOR
    public TeamHeat(int heatID, Team team) {
        this.heatID = heatID;
        this.team = team;
    }

    // GETTERS AND SETTERS, used for Jackson JSON
    public FinalTime getFinalTime() {
        return finalTime;
    }

    public int getHeatID() {
        return heatID;
    }

    public Team getTeam() {
        return team;
    }

    public void setFinalTime(FinalTime finalTime) {
        this.finalTime = finalTime;
    }

    public void setHeatID(int heatID) {
        this.heatID = heatID;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    // EFFECTS: constructs a FinalTime with the heat's start and input end time
    public void calculateEndTime(Calendar endTime) throws NoHeatsException, CouldNotCalculateFinalTimeExcpetion, NoTeamException {
        Heat heat = getHeatFromTeam();
        if (heat == null) {
            throw new NoHeatsException();
        }

        finalTime = new FinalTime(heat.getStartTime(), endTime);
    }


    // EFFECTS: return the heat´s number associated with the TeamHeat
    private Heat getHeatFromTeam() throws NoTeamException {
        if (team == null) {
            throw new NoTeamException("heats");
        }
        for (Heat heat : team.getHeats()) {
            if (heat.getHeatNumber() == heatID) {
                return heat;
            }
        }
        return null;
    }

}
