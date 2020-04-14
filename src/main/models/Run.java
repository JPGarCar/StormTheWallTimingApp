package models;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sun.istack.internal.NotNull;
import models.enums.Sitrep;
import models.exceptions.AddRunException;
import models.exceptions.CriticalErrorException;
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

    /**
     * Will calculate the final time the Team did in this Run. Called when the Team finishes the race for this Run.
     * Will set this Run to be undo and set it as done. It will then create a FinalTime with the associated Heat's
     * start time and the imputed end time.
     *
     * @param endTime   Calendar that represents the real time the Team finished this Run. To be used to calculate the
     *                  final time of this Run.
     * @throws CriticalErrorException   if the Heat associated to this Run is null.
     */
    public void calculateEndTime(@NotNull Calendar endTime) throws CriticalErrorException {
        canUndo = true;
        isDone = true;

        if (heat == null) {
            throw new CriticalErrorException("Error while trying to calculate end time. Run affected: " +
                    runNumber + ". The heat variable is null.");
        }

        finalTime = new FinalTime(heat.getStartTime(), endTime);
    }

    /**
     * Will disassociate this Run from both its Team and Heat, therefore it will set all connection to null so that
     * java can recycle this object as it is not needed any more.
     *
     * @throws CriticalErrorException   from removeRun() function from Heat.
     */
    public void selfDelete() throws CriticalErrorException {
        team.removeRun(runNumber);
        team = null;

        heat.removeRun(runNumber);

        heat = null;
        finalTime = null;
        sitrep = null;
        runNumber = null;
    }

    /**
     * Will move this Run from its previous Heat to the new Heat imputed. Will let the Heat classes do all the
     * job for us.
     *
     * @param newHeat Heat to move the Run to.
     * @throws CriticalErrorException   from removeRun().
     * @throws AddRunException from addRun().
     */
    public void moveRun(Heat newHeat) throws CriticalErrorException, AddRunException {
        heat.removeRun(runNumber);
        newHeat.addRun(this);
    }

}
