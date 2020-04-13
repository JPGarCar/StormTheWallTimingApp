package models;

import com.fasterxml.jackson.annotation.*;
import com.sun.istack.internal.NotNull;
import models.exceptions.*;

import javax.persistence.*;
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
    - All the Runs that the team has - Map<Integer, Run>
    - Team unit that represents the unit of this team - String

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

    // Contains the Runs that this team will run
    @OneToMany
    private Map<RunNumber, Run> runs;

    private String teamUnit;

// CONSTRUCTORS //

    // DUMMY CONSTRUCTOR used by Jackson JSON
    public Team() {
        runs = new HashMap<>();
    }

    // CONSTRUCTOR
    public Team(@NotNull String poolName, @NotNull int teamNumber, @NotNull String teamName, @NotNull int teamID, @NotNull String teamUnit) {
        this.poolName = poolName;
        this.teamNumber = teamNumber;
        this.teamName = teamName;
        this.teamID = teamID;
        this.teamUnit = teamUnit;

        runs = new HashMap<>();
    }

// GETTERS AND SETTERS, used by Jackson JSON //


    public String getTeamUnit() {
        return teamUnit;
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

    public Map<RunNumber, Run> getRuns() {
        return runs;
    }

    public void setTeamID(@NotNull int teamID) {
        this.teamID = teamID;
    }

    public void setRuns(@NotNull Map<RunNumber, Run> runs) {
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

    // EFFECTS: add run to this team and to the heat
    public void addRunFromHeat(Heat heat) throws AddHeatException {
        Run run = new Run(heat, this);

        if (!runs.containsKey(run.getRunNumber())) {
            runs.put(run.getRunNumber(), run);
            try {
                heat.addRun(run);
            } catch (AddTeamException e) {
                // do nothing as we expect this because of the many to many connection
            }
        }
        else {
            throw new AddHeatException("Team affected: " + teamName + ", with team number: " + teamNumber +
                    ". Could not be added to run number: " + run.getRunNumber() + ".");
        }
    }

    //EFFECTS: adds a run to the run list
    public void addRun(Run run) throws AddHeatException {
        if (!runs.containsKey(run.getRunNumber())) {
            runs.put(run.getRunNumber(), run);
        } else {
            throw new AddHeatException("Team affected: " + teamName + ", with team number: " + teamNumber +
                    ". Could not be added to run number: " + run.getRunNumber() + "."); // TODO
        }
    }

    // EFFECTS: remove heat from this team thus delete the run
    public void deleteRun(RunNumber runNumber) throws NoHeatsException {
        if (runs.containsKey(runNumber)) {
            runs.get(runNumber).selfDelete();
        } else {
            throw new NoHeatsException("Error while trying to remove run from team. Team affected: " + teamNumber +
                    ". Run that could not be removed: " + runNumber);
        }
    }

    // EFFECTS: remove run from the list
    public void removeRun(RunNumber runNumber) {
        runs.remove(runNumber);
    }

    // EFFECTS: get teamHeat by its heat number from remainingHeats
    public Run getRunByHeatNumber(int heatNumber) throws NoRunFoundException {
        for(Run run : runs.values()) {
            if (run.getRunNumber().getHeatNumber() == heatNumber) {
                return run;
            }
        }
        throw new NoRunFoundException("Affected team: " + teamNumber + "Searching for run with heat: " + heatNumber);
    }

}