package models;

import com.fasterxml.jackson.annotation.*;
import com.sun.istack.internal.NotNull;
import models.enums.LeagueType;
import models.enums.TeamType;
import models.exceptions.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/*
    Represents a team that will run one or more heats during the event
    Purpose: Control the heats the team is running in, their times and their team information
    Contains:
    - Team Type - TeamType
    - League Type - LeagueType
    - The id of the heat in which they are currently running, -1 if they are not running at the moment - int
    - Possibility of undoing the start of the heat - boolean
    - Team id used by db and access - UNIQUE - int
    - Team number used by participants and the program - UNIQUE - int
    - Team´s name - String
    - All the heats this team is running in - Map<Integer, Heat>
    - All the TeamHeats that the team finished - Map<Integer, TeamHeat>
    - All the TeamHeats that the team has not finished - Map<Integer, TeamHeat>

    Usage:
    -

    Persistence:
    - This class is an entity in the table name "team_table"
    - currentHeatID, heats, doneHeats and remainingHeats will be changing during the program's life they
        must be persist with db, all other vars will not change much or at all
    - Many To Many relation with Heat, it is mapped by Heat
    - One To Many with TeamHeat x2
 */


@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property = "@UUID")
@Entity
@Table(name = "team_table")
public class Team {

// VARIABLES //

    private TeamType teamType;

    private LeagueType teamLeague;

    // Represents the ID of the heat in which this team is currently running, -1 if they are not running at the moment
    private int currentHeatID;

    // Represents the possibility of undoing the team's end time // TODO move this to TeamHeat
    private boolean possibleUndo;

    // Represents team id used by db and access - UNIQUE
    @Id
    private int teamID;

    // Represents the team number they use during the race and in this program - UNIQUE
    private int teamNumber;

    private String teamName;

    // Contains all the heats this team is in, will be mapped by the heats table in db
    @ManyToMany(mappedBy = "heat_table")
    private Map<Integer, Heat> heats;

    // Contains the TeamHeats that have finished
    @JsonManagedReference
    @OneToMany
    private Map<Integer, Run> doneHeats;

    // Contains the TeamHeats that have not finished
    @JsonManagedReference
    @OneToMany
    private Map<Integer, Run> remainingHeats;

// CONSTRUCTORS //

    // DUMMY CONSTRUCTOR used by Jackson JSON
    public Team() {
        doneHeats = new HashMap<>();
        remainingHeats = new HashMap<>();
        heats = new HashMap<>();
    }

    // CONSTRUCTOR
    public Team(@NotNull TeamType teamType, @NotNull LeagueType teamLeague, @NotNull int teamNumber, @NotNull String teamName, @NotNull int teamID) {
        this.teamType = teamType;
        this.teamLeague = teamLeague;
        this.teamNumber = teamNumber;
        this.teamName = teamName;
        this.teamID = teamID;
        currentHeatID = -1;

        doneHeats = new HashMap<>();
        remainingHeats = new HashMap<>();
        heats = new HashMap<>();

    }

// GETTERS AND SETTERS, used by Jackson JSON //

    public int getTeamID() {
        return teamID;
    }

    public boolean getPossibleUndo() {
        return possibleUndo;
    }

    public TeamType getTeamType() {
        return teamType;
    }

    public int getTeamNumber() {
        return teamNumber;
    }

    public String getTeamName() {
        return teamName;
    }

    public LeagueType getTeamLeague() {
        return teamLeague;
    }

    public Map<Integer, Heat> getHeats() {
        return heats;
    }

    public Map<Integer, Run> getRemainingHeats() {
        return remainingHeats;
    }

    public Map<Integer, Run> getDoneHeats() {
        return doneHeats;
    }

    public int getCurrentHeatID() {
        return currentHeatID;
    }

    public void setTeamID(@NotNull int teamID) {
        this.teamID = teamID;
    }

    public void setPossibleUndo(@NotNull boolean possibleUndo) {
        this.possibleUndo = possibleUndo;
    }

    public void setTeamType(@NotNull TeamType teamType) {
        this.teamType = teamType;
    }

    public void setHeats(@NotNull Map<Integer, Heat> heats) {
        this.heats = heats;
    }

    public void setCurrentHeatID(@NotNull int currentHeatID) {
        this.currentHeatID = currentHeatID;
    }

    public void setDoneHeats(@NotNull Map<Integer, Run> doneHeats) {
        this.doneHeats = doneHeats;
    }

    public void setRemainingHeats(@NotNull Map<Integer, Run> remainingHeats) {
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

// FUNCTIONS //

    // EFFECTS: set the end time to the appropriate TeamHeat, depends on the heat number given.
    //          will also move the TeamHeat who got a final time to the done heat list
    public void markEndTime(@NotNull Calendar endTime) throws NoHeatsException, NoCurrentHeatIDException, CouldNotCalculateFinalTimeExcpetion, NoRemainingHeatsException {
        if (remainingHeats.size() == 0) {
            throw new NoRemainingHeatsException();
        } else if (currentHeatID == -1) {
            throw new NoCurrentHeatIDException();
        }
        Run remainingHeat = remainingHeats.remove(currentHeatID);
        remainingHeat.calculateEndTime(endTime);
        setPossibleUndo(true);
        doneHeats.put(remainingHeat.getHeatNumber(), remainingHeat);
    }

    // EFFECTS: add the heats in the input array to the heats array and the remaining heats queue
    public void addHeats(ArrayList<Heat> heats) throws AddHeatException {
        for (Heat heat : heats) {
            addHeat(heat);
        }
    }

    // Helper function: creates a TeamHeat out of a Heat
    private Run heatToTeamHeat(@NotNull Heat heat) {
        return new Run(heat.getHeatNumber(), this);
    }

    // EFFECTS: add one heat to the heat array and remaining heat queue and add this team to the heat
    public void addHeat(Heat heat) throws AddHeatException {
        if (!heats.containsKey(heat.getHeatNumber())) {
            heats.put(heat.getHeatNumber(), heat);
            Run run = heatToTeamHeat(heat);
            remainingHeats.put(run.getHeatNumber(), run);
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

    // EFFECTS: remove heat from this team in all three possible array lists and removes this team from heat
    public void removeHeat(int heatNumber) throws NoHeatsException {
        if (heats.containsKey(heatNumber)) {
            try {
                heats.remove(heatNumber).removeTeam(teamNumber);
            } catch (NoTeamException e) {
                // do nothing as we expect this here due to many to many connection
            }
            remainingHeats.remove(heatNumber);
            doneHeats.remove(heatNumber);
        } else {
            throw new NoHeatsException();
        }
    }

    // EFFECTS: undo the end time stuff
    public void undoEndTimeMark() {
        Run run = doneHeats.remove(currentHeatID);
        remainingHeats.put(run.getHeatNumber(), run);
        setPossibleUndo(false);
    }

    // EFFECTS: get teamHeat by its heat number from remainingHeats
    public Run getTeamHeatByHeatNumberFromRemaining(int heatNumber) throws NoTeamHeatException {
        Run run = remainingHeats.get(heatNumber);
        if (run == null) {
            throw new NoTeamHeatException();
        }
        return run;
    }

    // EFFECTS: get teamHeat by heat number from doneHeats
    public Run getTeamHeatByHeatNumberFromDone(int heatNumber) throws NoTeamHeatException {
        Run run = doneHeats.get(heatNumber);
        if (run == null) {
            throw new NoTeamHeatException();
        }
        return run;
    }

}