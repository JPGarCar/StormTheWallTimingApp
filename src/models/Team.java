package models;

import java.util.ArrayList;
import java.util.Calendar;

public class Team {

    // private variables
    private TeamType teamType;
    private Sitrep sitRep;
    private LeagueType teamLeague;
    private String notes;

    // private team vars
    private int teamNumber;
    private String teamName;

    // private connections
    private ArrayList<Heat> heats;

    // private time vars
    private Calendar startTime;
    private Calendar endTime;
    private FinalTime finalTime;

    // CONSTRUCTOR
    public Team(TeamType teamType, Sitrep sitrep, LeagueType teamLeague, String notes, int teamNumber, String teamName) {
        this.teamType = teamType;
        this.sitRep = sitrep;
        this.teamLeague = teamLeague;
        this.notes = notes;
        this.teamNumber = teamNumber;
        this.teamName = teamName;

        heats = new ArrayList<Heat>();
    }

    // GETTERS
    public TeamType getTeamType() {
        return teamType;
    }

    public int getTeamNumber() {
        return teamNumber;
    }

    public Sitrep getSitRep() {
        return sitRep;
    }

    public String getNotes() {
        return notes;
    }

    public String getTeamName() {
        return teamName;
    }

    public LeagueType getTeamLeague() {
        return teamLeague;
    }

    public FinalTime getFinalTime() {
        return finalTime;
    }

    public ArrayList<Heat> getHeats() {
        return heats;
    }

    //SETTERS
    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
        setFinalTime();
    }

    public void setHeats(ArrayList<Heat> heats) {
        this.heats = heats;
    }

    public void addHeat(Heat heat) {
        if (!heats.contains(heat)) {
            heats.add(heat);
        }
        else {
            // TODO add exception
        }
    }

    public void addNotes(String notes) {
        this.notes += "/n" + notes;
    }

    // MODIFIES: this
    // EFFECTS: calls a new FinalTime
    private void setFinalTime() {
        finalTime = new FinalTime(startTime, endTime);
    }

}