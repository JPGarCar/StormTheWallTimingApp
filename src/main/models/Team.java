package models;

import com.fasterxml.jackson.annotation.*;
import com.sun.istack.internal.NotNull;
import models.enums.LeagueType;
import models.enums.Sitrep;
import models.enums.TeamType;
import models.exceptions.*;

import java.util.ArrayList;
import java.util.Calendar;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property = "@UUID")
public class Team {

    // private variables
    private TeamType teamType;
    private LeagueType teamLeague;
    private String notes;
    private int currentHeatID;
    private boolean possibleUndo;

    // private team vars
    private int teamNumber;
    private String teamName;

    // private connections
    private ArrayList<Heat> heats;

    @JsonManagedReference
    private ArrayList<TeamHeat> doneHeats;

    @JsonManagedReference
    private ArrayList<TeamHeat> remainingHeats;

    // DUMMY CONSTRUCTOR used by Jackson JSON
    public Team() {
        doneHeats = new ArrayList<>();
        remainingHeats = new ArrayList<>();
        heats = new ArrayList<>();
    }

    // CONSTRUCTOR
    public Team(@NotNull TeamType teamType, @NotNull LeagueType teamLeague, @NotNull int teamNumber, @NotNull String teamName) {
        this.teamType = teamType;
        this.teamLeague = teamLeague;
        this.teamNumber = teamNumber;
        this.teamName = teamName;
        this.notes = "";
        currentHeatID = -1;

        heats = new ArrayList<>();
        doneHeats = new ArrayList<>();
        remainingHeats = new ArrayList<>();

    }

    // GETTERS AND SETTERS, used by Jackson JSON
    public boolean getPossibleUndo() {
        return possibleUndo;
    }

    public void setPossibleUndo(@NotNull boolean possibleUndo) {
        this.possibleUndo = possibleUndo;
    }

    public TeamType getTeamType() {
        return teamType;
    }

    public int getTeamNumber() {
        return teamNumber;
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

    public void setTeamType(@NotNull TeamType teamType) {
        this.teamType = teamType;
    }

    public void setHeats(@NotNull ArrayList<Heat> heats) {
        this.heats = heats;
    }

    public void setCurrentHeatID(@NotNull int currentHeatID) {
        this.currentHeatID = currentHeatID;
    }

    public void setDoneHeats(@NotNull ArrayList<TeamHeat> doneHeats) {
        this.doneHeats = doneHeats;
    }

    public void setNotes(@NotNull String notes) {
        this.notes = notes;
    }

    public void setRemainingHeats(@NotNull ArrayList<TeamHeat> remainingHeats) {
        this.remainingHeats = remainingHeats;
    }

    public void setTeamLeague(@NotNull LeagueType teamLeague) {
        this.teamLeague = teamLeague;
    }

    public void setTeamName(@NotNull String teamName) {
        this.teamName = teamName;
    }

    public void setTeamNumber(@NotNull int teamNumber) {
        this.teamNumber = teamNumber;
    }

    public int getCurrentHeatID() {
        return currentHeatID;
    }


    // EFFECTS: set the end time to the appropriate TeamHeat, depends on the heat number given.
    //          will also move the TeamHeat who got a final time to the done heat list
    public void markEndTime(@NotNull Calendar endTime) throws NoHeatsException, NoCurrentHeatIDException, CouldNotCalculateFinalTimeExcpetion, NoRemainingHeatsException {
        if (remainingHeats.size() == 0) {
            throw new NoRemainingHeatsException();
        } else if (currentHeatID == -1) {
            throw new NoCurrentHeatIDException();
        }
        for (int i = 0; i < remainingHeats.size(); i++) {
            TeamHeat remainingHeat = remainingHeats.get(i);
            if (remainingHeat.getHeatID() == currentHeatID) {
                remainingHeat.calculateEndTime(endTime);
                doneHeats.add(remainingHeat);
                remainingHeats.remove(remainingHeat);
                setPossibleUndo(true);
                i--;
            }
        }
    }

    // EFFECTS: add the heats in the input array to the heats array and the remaining heats queue
    public void addHeats(ArrayList<Heat> heats) throws AddHeatException {
        for (Heat heat : heats) {
            addHeat(heat);
        }
    }

    // Helper function: creates a TeamHeat out of a Heat
    private TeamHeat heatToTeamHeat(@NotNull Heat heat) {
        return new TeamHeat(heat.getHeatNumber(), this);
    }

    // EFFECTS: add one heat to the heat array and remaining heat queue and add this team to the heat
    public void addHeat(Heat heat) throws AddHeatException {
        if (!heats.contains(heat)) {
            heats.add(heat);
            remainingHeats.add(heatToTeamHeat(heat));
            try {
                heat.addTeam(this);
            } catch (AddTeamException e) {
                // do nothing as we expect this because of the many to many connection
            }
        }
        else {
            throw new AddHeatException("because this team already has a heat with that ID.");
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

    // EFFECTS: remove heat from this team in all three possible array lists and removes this team from heat
    public void removeHeat(Heat heat) throws NoHeatsException {
        if (heats.contains(heat)) {
            heats.remove(heat);
            try {
                heat.removeTeam(teamNumber);
            } catch (NoTeamException e) {
                // do nothing as we expect this here due to many to many connection
            }
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
        } else {
            throw new NoHeatsException();
        }
    }

    // EFFECTS: undo the end time stuff
    public void undoEndTimeMark() {
        for (int i = 0; i < doneHeats.size(); i++) {
            TeamHeat teamHeat = doneHeats.get(i);
            if (teamHeat.getHeatID() == currentHeatID) {
                remainingHeats.add(teamHeat);
                doneHeats.remove(teamHeat);
                setPossibleUndo(false);
                i--;
            }
        }
    }

    // EFFECTS: get teamHeat by heatID from remainingHeats
    public TeamHeat getTeamHeatByHeatIDFromRemaining(int heatID) throws NoTeamHeatException {
        for (TeamHeat teamHeat : remainingHeats) {
            if (teamHeat.getHeatID() == heatID) {
                return teamHeat;
            }
        }
        throw new NoTeamHeatException();
    }

    // EFFECTS: get teamHeat by heatID from doneHeats
    public TeamHeat getTeamHeatByHeatIDFromDone(int heatID) throws NoTeamHeatException {
        for (TeamHeat teamHeat : doneHeats) {
            if (teamHeat.getHeatID() == heatID) {
                return teamHeat;
            }
        }
        throw new NoTeamHeatException();
    }

}