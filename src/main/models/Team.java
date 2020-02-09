package models;

import com.fasterxml.jackson.annotation.*;
import models.enums.LeagueType;
import models.enums.Sitrep;
import models.enums.TeamType;

import java.util.ArrayList;
import java.util.Calendar;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property = "@UUID")
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

    @JsonManagedReference
    private ArrayList<TeamHeat> doneHeats;

    @JsonManagedReference
    private ArrayList<TeamHeat> remainingHeats;

    private int currentHeatID;

    public Team() {
        doneHeats = new ArrayList<>();
        remainingHeats = new ArrayList<>();
        heats = new ArrayList<>();
    }

    // CONSTRUCTOR
    public Team(TeamType teamType, LeagueType teamLeague, int teamNumber, String teamName) {
        this.teamType = teamType;
        this.teamLeague = teamLeague;
        this.teamNumber = teamNumber;
        this.teamName = teamName;
        this.sitRep = Sitrep.NONE;
        this.notes = "";
        currentHeatID = -1;

        heats = new ArrayList<>();
        doneHeats = new ArrayList<>();
        remainingHeats = new ArrayList<>();

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

    public ArrayList<Heat> getHeats() {
        return heats;
    }

    public ArrayList<TeamHeat> getRemainingHeats() {
        return remainingHeats;
    }

    public ArrayList<TeamHeat> getDoneHeats() {
        return doneHeats;
    }

    public void setTeamType(TeamType teamType) {
        this.teamType = teamType;
    }

    public void setHeats(ArrayList<Heat> heats) {
        this.heats = heats;
    }

    public void setCurrentHeatID(int currentHeatID) {
        this.currentHeatID = currentHeatID;
    }

    public void setDoneHeats(ArrayList<TeamHeat> doneHeats) {
        this.doneHeats = doneHeats;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setRemainingHeats(ArrayList<TeamHeat> remainingHeats) {
        this.remainingHeats = remainingHeats;
    }

    public void setTeamLeague(LeagueType teamLeague) {
        this.teamLeague = teamLeague;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
    }

    public int getCurrentHeatID() {
        return currentHeatID;
    }

    public void setCurrentHeatIDFromHeat(Heat currentHeatID) {
        this.currentHeatID = currentHeatID.getHeatNumber();
    }

    // EFFECTS: set the end time to the appropriate TeamHeat, depends on the heat number given.
    //          will also move the TeamHeat who got a final time to the done heat list
    public void markEndTime(Calendar endTime) {
        for (int i = 0; i < remainingHeats.size(); i++) {
            TeamHeat remainingHeat = remainingHeats.get(i);
            if (remainingHeat.getHeatID() == currentHeatID) {
                remainingHeat.calculateEndTime(endTime);
                doneHeats.add(remainingHeat);
                remainingHeats.remove(remainingHeat);
                i--;
            }
        }
    }

    // EFFECTS: add the heats in the input array to the heats array and the remaining heats queue
    public void addHeats(ArrayList<Heat> heats) {
        for (Heat heat : heats) {
            addHeat(heat);
        }
    }

    // Helper function: creates a TeamHeat out of a Heat
    private TeamHeat heatToTeamHeat(Heat heat) {
        return new TeamHeat(heat.getHeatNumber(), this);
    }

    // EFFECTS: add one heat to the heat array and remaining heat queue and add this team to the heat
    public void addHeat(Heat heat) {
        if (!heats.contains(heat)) {
            heats.add(heat);
            remainingHeats.add(heatToTeamHeat(heat));
            heat.addTeam(this);
        }
        else {
            // TODO add exception
        }
    }

    // EFFECTS: add notes to the note section
    public void addNotes(String notes) {
        if (this.notes.equals("")) {
            this.notes += notes;
        } else {
            this.notes += "\n" + notes;
        }
    }

    // EFFECTS: set the sitrep
    public void setSitRep(Sitrep sitRep) {
        this.sitRep = sitRep;
    }

    // EFFECTS: remove heat from this team in all three possible array lists and removes this team from heat
    public void removeHeat(Heat heat) {
        if (heats.contains(heat)) {
            heats.remove(heat);
            heat.removeTeam(this);
            for (int i = 0; i < remainingHeats.size(); i++) {
                TeamHeat teamHeat = remainingHeats.get(i);
                if (teamHeat.getHeatID() == heat.getHeatNumber()) {
                    remainingHeats.remove(teamHeat);
                    i--;
                }
            }
            for (int i = 0; i < doneHeats.size(); i++) {
                TeamHeat teamHeat = doneHeats.get(i);
                if (teamHeat.getHeatID() == heat.getHeatNumber()) {
                    remainingHeats.remove(teamHeat);
                    i--;
                }
            }
        }
    }
}