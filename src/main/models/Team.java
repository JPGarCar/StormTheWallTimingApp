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

    /**
     * Associates a Team to a Heat by creating a Run and adding it to the runs list.
     *
     * <p>Due to the many to many connection this function also calls the Heat addRun() function to add the
     * created Run that associates this team with the Heat.</p>
     *
     * @param heat  to be associated to this Team with a Run
     * @throws AddRunException if this team is already associated with the heat. A Heat can not have a
     * Team associated twice.
     */
    public void addRunFromHeat(Heat heat) throws AddRunException {
        Run run = new Run(heat, this);

        if (!runs.containsKey(run.getRunNumber())) {
            runs.put(run.getRunNumber(), run);
            try {
                heat.addRun(run);
            } catch (AddRunException e) {
                // do nothing as we expect this because of the many to many connection
            }
        }
        else {
            throw new AddRunException("Team affected: " + teamName + ", with team number: " + teamNumber +
                    ". Could not add the team to the heat: " + heat.getHeatNumber() + ".");
        }
    }

    /**
     * Associate this Team to a Heat with an already crated Run. Assumes the Run to be already associated with a Heat.
     *
     * TODO: this does not check if the Heat connected to the Run has already been associated to this Team with a different Run
     * TODO: we need to check that some way. Probably add functionality to the RunNumber class.
     *
     * @param run   to be used to associate this Team with the Heat connected to the Run.
     * @throws AddRunException if this Team is already connected to this Run. A Team can not have duplicate Run(s).
     */
    public void addRun(Run run) throws AddRunException {
        if (!runs.containsKey(run.getRunNumber())) {
            runs.put(run.getRunNumber(), run);
        } else {
            throw new AddRunException("Team affected: " + teamName + ", with team number: " + teamNumber +
                    ". Could not add the run: " + run.getRunNumber() +
                    " because it is already connected to this Team.");
        }
    }

    /**
     * Will disassociate this team from a Run. Because a Run can not exist without a Team, the Run will call the
     * selfDelete() function to disappear. It will also remove the Run from the runs Map.
     *
     * @param runNumber RunNumber linked to the Run to be disassociated from this Team.
     * @throws CriticalErrorException if the RunNumber is not found in the runs list.
     */
    public void deleteRun(RunNumber runNumber) throws CriticalErrorException {
        if (runs.containsKey(runNumber)) {
            runs.get(runNumber).selfDelete();
        } else {
            throw new CriticalErrorException("Error while trying to remove run from team. Team affected: " + teamNumber +
                    ". Run that could not be removed: " + runNumber);
        }
    }

    /**
     * Disassociate a Run from this Team by removing the Run from the runs list.
     *
     * @param runNumber RunNumber linked to the Run to be disassociated.
     */
    public void removeRun(RunNumber runNumber) {
        runs.remove(runNumber);
    }

    /**
     * Will search through all the Run(s) in runs map find a Run with the imputed Heat number. If no Run is found with
     * the imputed Heat number then an exception is thrown.
     *
     * TODO: make this better by doing a RunNumber with data and using that to search.
     *
     * @param heatNumber    int to be used to find a Run associated to that Heat.
     * @return  The Run in the runs Map that has a Heat associated with the input Heat number.
     * @throws CriticalErrorException  if no Run is found in the Map with an associated Heat with the imputed Heat number.
     */
    public Run getRunByHeatNumber(int heatNumber) throws CriticalErrorException {
        for(Run run : runs.values()) {
            if (run.getRunNumber().getHeatNumber() == heatNumber) {
                return run;
            }
        }
        throw new CriticalErrorException("We could not find a Run with the Heat number: " + heatNumber +
                " in the Team: " + teamNumber + ".");
    }

}