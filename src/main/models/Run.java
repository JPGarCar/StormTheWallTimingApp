package models;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sun.istack.internal.NotNull;
import models.enums.Sitrep;
import models.exceptions.CouldNotCalculateFinalTimeExcpetion;
import models.exceptions.NoHeatsException;
import models.exceptions.NoTeamException;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.Calendar;

/*
    Represents a specific team in a specific heat
    Purpose: Controls the end time and status of the team in this run
    Contains:
    - The heat number this run is going to run in - int
    - Id of this run, used by db - int - UNIQUE
    - The time it takes the team to finish the run - FinalTime
    - The situation of the team, if any, ex. DNS, DQ, etc - Sitrep
    - The team that is running this run - Team
    - If the run has been completed or not - boolean
    - Can the run be undo - boolean
    - A unique number to differentiate each run - RunNumber

    Usage:
    -

    Persistence:
    - This class is an entity of the table name "run_table"
    - sitrep and finalTime will be changing during the program's life, must persist with db, all other vars
        should not change much or at all
    - Embedded class FinalTime as var finalTime in db
    - Many To One relationship with Team
 */

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property = "@UUID")
@Entity
@Table(name = "run_table")
public class Run {

// VARIABLES //

    // Represents the heat number of the heat in which the team is running
    @Column()
    private Heat heat;

    // Contains the team that is running this run
    @ManyToOne
    private Team team;

    // Represents the id of the run to be used by db, thus no need to instantiate in program - UNIQUE
    @NaturalId
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    // Represents the time it took the team to complete the run, it is embedded to the db
    @Embedded
    private FinalTime finalTime;

    // Represents the run number, a unique number for each run to be used by the program
    @Embedded
    @Id
    private RunNumber runNumber;

    // Represents the situation of the team in this run, ex. DNS, DNF, DQ, etc
    private Sitrep sitrep;

    // Represents if the run has been completed
    private boolean isDone;

    // Represents the possibility to undo the run end time
    private boolean canUndo;

// CONSTRUCTORS //

    // DUMMY CONSTRUCTOR for Jackson JSON
    public Run() {
        isDone = false;
        canUndo = false;
    }

    // CONSTRUCTOR
    public Run(@NotNull Heat heat, @NotNull Team team) {
        this.heat = heat;
        this.team = team;
        this.sitrep = Sitrep.NONE;
        isDone = false;
        canUndo = false;
        runNumber = new RunNumber(team.getTeamNumber(), heat.getHeatNumber());
    }

// GETTERS AND SETTERS, used for Jackson JSON //

    public RunNumber getRunNumber() {
        return runNumber;
    }

    public boolean getCanUndo() {
        return canUndo;
    }

    public boolean getIsDone() {
        return isDone;
    }

    public FinalTime getFinalTime() {
        return finalTime;
    }

    public Heat getHeat() {
        return heat;
    }

    public Team getTeam() {
        return team;
    }

    public Sitrep getSitrep() {
        return sitrep;
    }

    public void setCanUndo(boolean canUndo) {
        this.canUndo = canUndo;
    }

    public void setIsDone(@NotNull boolean done) {
        isDone = done;
    }

    public void setFinalTime(@NotNull FinalTime finalTime) {
        this.finalTime = finalTime;
    }

    public void setHeat(@NotNull Heat heat) {
        this.heat = heat;
    }

    public void setTeam(@NotNull Team team) {
        this.team = team;
    }

    public void setSitrep(@NotNull Sitrep sitrep) {
        this.sitrep = sitrep;
    }

    public void setRunNumber(RunNumber runNumber) {
        this.runNumber = runNumber;
    }

// FUNCTIONS //

    // EFFECTS: constructs a FinalTime with the heat's start and input end time and sets canUndo and isDone to true
    public void calculateEndTime(@NotNull Calendar endTime) throws NoHeatsException, CouldNotCalculateFinalTimeExcpetion {
        canUndo = true;
        isDone = true;

        if (heat == null) {
            throw new NoHeatsException("Error while trying to calculate end time. Team affected: " +
                    team.getTeamNumber() + "Heat that was not found: " + heat.getHeatNumber());
        }

        finalTime = new FinalTime(heat.getStartTime(), endTime);
    }

    //EFFECTS: delete this run by nulling all associations and deleting itself from heat and team
    public void selfDelete() {
        team.removeRun(runNumber);
        team = null;
        try {
            heat.removeRun(runNumber);
        } catch (NoTeamException e) {
            // this should not happen TODO
        }
        heat = null;
        finalTime = null;
        sitrep = null;
        runNumber = null;

    }

}
