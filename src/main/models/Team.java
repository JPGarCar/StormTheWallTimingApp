package models;

import com.fasterxml.jackson.annotation.*;
import com.sun.istack.internal.NotNull;
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
    - Pool Name - String
    - Team id used by db and access - UNIQUE - int
    - Team number used by participants and the program - UNIQUE - int
    - Team´s name - String
    - All the heats this team is running in - Map<Integer, Heat>
    - All the Runs that the team has - Map<Integer, Run>
    - Current run if the team is running, null otherwise - Run

    Usage:
    -

    Persistence:
    - This class is an entity in the table name "team_table"
    - currentRun, heats and runs will be changing during the program's life they
        must be persist with db, all other vars will not change much or at all
    - Many To Many relation with Heat, it is mapped by Heat
    - One To Many with Run
 */


@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property = "@UUID")
@Entity
@Table(name = "team_table")
public class Team {

// VARIABLES //

    private String poolName;

    // Represents team id used by db and access - UNIQUE
    @Id
    private int teamID;

    // Represents the team number they use during the race and in this program - UNIQUE
    private int teamNumber;

    private String teamName;

    // Contains all the heats this team is in, will be mapped by the heats table in db
    @ManyToMany(mappedBy = "heat_table")
    private Map<Integer, Heat> heats;

    // Contains the TeamHeats that have not finished
    @OneToMany
    private Map<Integer, Run> runs;

    // Contains the current run the team is running
    private Run currentRun;

    private String teamUnit;

// CONSTRUCTORS //

    // DUMMY CONSTRUCTOR used by Jackson JSON
    public Team() {
        runs = new HashMap<>();
        heats = new HashMap<>();
    }

    // CONSTRUCTOR
    public Team(@NotNull String poolName, @NotNull int teamNumber, @NotNull String teamName, @NotNull int teamID, @NotNull String teamUnit) {
        this.poolName = poolName;
        this.teamNumber = teamNumber;
        this.teamName = teamName;
        this.teamID = teamID;
        this.teamUnit = teamUnit;

        runs = new HashMap<>();
        heats = new HashMap<>();

    }

// GETTERS AND SETTERS, used by Jackson JSON //


    public String getTeamUnit() {
        return teamUnit;
    }

    public Run getCurrentRun() {
        return currentRun;
    }

    public int getTeamID() {
        return teamID;
    }

    public int getTeamNumber() {
        return teamNumber;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getPoolName() {
        return poolName;
    }

    public Map<Integer, Heat> getHeats() {
        return heats;
    }

    public Map<Integer, Run> getRuns() {
        return runs;
    }

    public void setCurrentRun(@NotNull Run currentRun) {
        this.currentRun = currentRun;
    }

    public void setTeamID(@NotNull int teamID) {
        this.teamID = teamID;
    }

    public void setHeats(@NotNull Map<Integer, Heat> heats) {
        this.heats = heats;
    }

    public void setRuns(@NotNull Map<Integer, Run> runs) {
        this.runs = runs;
    }

    public void setPoolName(@NotNull String poolName) {
        this.poolName = poolName;
    }

    public void setTeamName(@NotNull String teamName) {
        this.teamName = teamName;
    }

    public void setTeamNumber(@NotNull int teamNumber) {
        this.teamNumber = teamNumber;
    }

    public void setTeamUnit(String teamUnit) {
        this.teamUnit = teamUnit;
    }

    // FUNCTIONS //

    // EFFECTS: set the end time to the appropriate TeamHeat, depends on the heat number given.
    //          will also move the TeamHeat who got a final time to the done heat list
    public void markEndTime(@NotNull Calendar endTime) throws NoHeatsException, NoCurrentHeatIDException, CouldNotCalculateFinalTimeExcpetion, NoRemainingHeatsException {
        if (runs.size() == 0 || currentRun == null) {
            throw new NoRemainingHeatsException();
        }
        currentRun.calculateEndTime(endTime);
    }

    // EFFECTS: add the heats in the input array to the heats array and the remaining heats queue
    public void addHeats(ArrayList<Heat> heats) throws AddHeatException {
        for (Heat heat : heats) {
            addHeat(heat);
        }
    }

    // EFFECTS: set the current run that this team is running
    public void markCurrentRun(int heatNumber) {
        if (heatNumber == -1) {
            currentRun = null;
        } else {
            setCurrentRun(runs.get(heatNumber));
        }
    }

    // Helper function: creates a TeamHeat out of a Heat
    private Run heatToTeamHeat(@NotNull Heat heat) {
        return new Run(heat.getHeatNumber(), this);
    }

    // EFFECTS: add one heat to the heat array and remaining heat queue and add this team to the heat
    public void addHeat(Heat heat) throws AddHeatRuntimeException {
        if (!heats.containsKey(heat.getHeatNumber())) {
            heats.put(heat.getHeatNumber(), heat);
            Run run = heatToTeamHeat(heat);
            runs.put(run.getHeatNumber(), run);
            try {
                heat.addTeam(this);
            } catch (AddTeamException e) {
                // do nothing as we expect this because of the many to many connection
            }
        }
        else {
            throw new AddHeatRuntimeException("Team affected: " + teamName + ", with team number: " + teamNumber +
                    ". Could not be added to heat number: " + heat.getHeatNumber() + ".");
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
            runs.remove(heatNumber);
        } else {
            throw new NoHeatsException();
        }
    }

    // EFFECTS: undo the end time stuff
    public void undoEndTimeMark() {
        currentRun.setCanUndo(true);
    }

    // EFFECTS: get teamHeat by its heat number from remainingHeats
    public Run getRunByHeatNumber(int heatNumber) throws NoTeamHeatException {
        Run run = runs.get(heatNumber);
        if (run == null) {
            throw new NoTeamHeatException();
        }
        return run;
    }

}