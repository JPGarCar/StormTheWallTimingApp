package models;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sun.istack.internal.NotNull;
import models.enums.Sitrep;
import models.exceptions.AddRunException;
import models.exceptions.CriticalErrorException;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.Calendar;

/**
 * <h3>Represents</h3> a {@link Team} participating in the race in a specific {@link Heat}. It is a way of
 * associating a Team to a Heat. Every Run must be unique, aka there can not be a Team twice in one Heat.
 * A Team and a Heat will generally have multiple Run(s) assocaited to them. The way we differentiate between
 * Run(s) its by the {@link RunNumber}, a unique identifier. Run(s) are used by the {@link ui.UIAppLogic} controller
 * to know what Run(s) are active, being staged, in wait list, paused, and finished.
 *
 * <p>It is important to note that when moving a Run from one Heat to another, it is important to use the
 * moveRun() function in this class because the RunNumber needs to be changed to account for the new Heat number.
 * The RunNumber linked to this Run must always stay up to date with the assocaited Heat's and Team's number.</p>
 *
 * <h3>Purpose:</h3> Controls the time it took the Team to finish the race and if they are in a specific status.
 *
 * <h3>Contains:</h3>
 *   - The heat number this run is going to run in - int
 *   - Id of this run, used by db - int - UNIQUE
 *   - The time it takes the team to finish the run - FinalTime
 *   - The situation of the team, if any, ex. DNS, DQ, etc - Sitrep
 *   - The team that is running this run - Team
 *   - If the run has been completed or not - boolean
 *   - Can the run be undo - boolean
 *   - A unique number to differentiate each run - RunNumber
 *
 * <h3>Usage:</h3>
 *   - Calculate the time it took the team to finish the race
 *   - Self destruct for when the Run is disassociated from both a Heat and Team
 *   - Move itself from one Heat to another Heat
 *
 * <h3>Persistence:</h3>
 *   - This class is an entity of the table name "run_table"
 *   - sitrep and finalTime will be changing during the program's life, must persist with db, all other vars
 *       should not change much or at all
 *   - Embedded class FinalTime as var finalTime in db
 *   - Many To One relationship with Team and Heat
 */

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property = "@UUID")
@Entity
@Table(name = "run_table")
public class Run {

// VARIABLES //

    /**
     * Contains the Heat this Run is associated to, aka the Heat in which the Team associated is participating in.
     */
    @ManyToOne
    private Heat heat;

    /**
     * Contains the Team associated to this Run, aka the Team to run the Heat associated.
     */
    @ManyToOne
    private Team team;

    /**
     * Contains the id of the run to be used by db, thus no need to instantiate in program - UNIQUE
     */
    @NaturalId
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    /**
     * Contains the time it took the Team to complete the Heat as a FinalTime. You can grab the minutes, seconds
     * and milliseconds from it.
     *
     * The value is embedded in the DB.
     */
    @Embedded
    private FinalTime finalTime;

    /**
     * Contains the RunNumber, a unique identifier for this Run to be used by the application. No other Run has the
     * same RunNumber. It is commonly used as a key in Map(s) where the Run is stored.
     *
     * The value is embedded in the DB.
     */
    @Embedded
    @Id
    private RunNumber runNumber;

    /**
     * Contains the Sitrep or situation the Team is in. Default is None.
     */
    private Sitrep sitrep;

    /**
     * Boolean to know if the Team has finished the race, aka the Run has been complete.
     */
    private boolean isDone;

    /**
     * Boolean to represent the possibility of being able to undo the Team's finish.
     */
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
     * job for us, however it will do something very important. It will update the RunNumber to contain the new
     * associated Heat number.
     *
     * @param newHeat Heat to move the Run to.
     * @throws CriticalErrorException   from removeRun().
     * @throws AddRunException from addRun().
     */
    public void moveRun(Heat newHeat) throws CriticalErrorException, AddRunException {
        heat.removeRun(runNumber);
        newHeat.addRun(this);
        runNumber = new RunNumber(team.getTeamNumber(), newHeat.getHeatNumber());
    }

}
