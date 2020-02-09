package models;

import org.codehaus.jackson.annotate.JsonBackReference;

import java.util.Calendar;

public class TeamHeat {

    private int heatID;

    @JsonBackReference
    private Team team;

    private FinalTime finalTime;

    public TeamHeat() {

    }

    public TeamHeat(int heatID, Team team) {
        this.heatID = heatID;
        this.team = team;
    }

    public void setEndTime(Calendar endTime) {
        finalTime = new FinalTime(getHeatFromTeam().getStartTime(), endTime);
    }

    public FinalTime getFinalTime() {
        return finalTime;
    }

    // EFFECTS: return the heat´s number associated with the TeamHeat
    private Heat getHeatFromTeam() {
        for (Heat heat : team.getHeats()) {
            if (heat.getHeatNumber() == heatID) {
                return heat;
            }
        }
        return null;
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
}
