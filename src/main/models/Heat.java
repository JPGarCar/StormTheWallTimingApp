package models;

import com.fasterxml.jackson.annotation.*;
import com.sun.istack.internal.NotNull;
import models.enums.Sitrep;
import models.exceptions.*;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 *   Represents a regular heat that will run during the event. Each Heat will have associated x number of Run(s) that
 *   represent an association to x Team(s). Each Run must be unique and can not repeat within one Heat. RunNumber(s) are
 *   used to differentiate Run(s) from each other.
 *
 *   Purpose: Control the teams that are running in this heat, when it starts and what kind of teams are running
 *
 *   Contains:
 *   - Scheduled time to start - Calendar
 *   - Category of the heat - String
 *   - Heat ID (used by db and access) - UNIQUE - int
 *   - Heat Number (used by participants and this program) - UNIQUE - int
 *   - Boolean that represents if the heat has started - boolean
 *   - The actual time the heat started - Calendar
 *   - TreeMap of all the Run(s) that are associated to this Heat, key is the RunNumber of
 *      every Run - LinkedHashMap<Integer, Team>
 *   - Day this heat is assigned to and running on - Day
 *
 *   Usage:
 *   - add and remove Run(s) associated to this Heat by using either a Run, RunNumber or Team
 *   - start the Heat
 *   - undo the Heat's start
 *   - send non DNS Run(s) to UI and set endTime to DNS Run(s)
 *   - Start time and scheduled time as String(s)
 *
 *   Persistence:
 *   - Class is an entity in the table name "heat_table"
 *   - Actual start time, the teams that will run and weather or not heat started will change during the life of program,
 *       all other should not change much or at all
 *   - Back reference, Many To One relationship with Day
 *   - Many to Many relationship with Team
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property = "@UUID")
@Entity
@Table(name = "heat_table")
public class Heat {

// VARIABLES //

    /**
     * Represents the time this heat is scheduled to start at. This is only for informational purposes and will
     * not be used for anything else. This time might not be accurate or represent in any way the time the Heat started.
     * It only contains the time, not the day, month, year, or any other information. Only hour and minute of day.
     */
    private Calendar scheduledTime;

    /**
     * Contains the category of the Heat, for informational purposes only.
     */
    private String category;

    /**
     * Contains the heat id, used by db and access. - UNIQUE
     */
    @Id
    private int heatID;

    /**
     * Contains the heat number, needs to start at a certain number and increment by one without
     * jumping any numbers. This is so that the program works properly. - UNIQUE
     */
    private int heatNumber;

    /**
     * Boolean used to know if the heat has started.
     */
    private boolean hasStarted;

    /**
     * Contains the Calendar time the Heat started.
     */
    private Calendar startTime;

    /**
     * Contains all the Run(s) associated to this Heat. The Run's RunNumber is used as a key.
     */
    @ManyToMany
    private Map<RunNumber, Run> runs;

    /**
     * A back reference to the day this heat is associated to, aka the Day this Heat is starting.
     */
    @ManyToOne
    @JsonBackReference
    private Day dayToRace;

// CONSTRUCTORS //

    // DUMMY CONSTRUCTOR used by Jackson JSON
    public Heat() {
        runs = new LinkedHashMap<RunNumber, Run>();
        this.hasStarted = false;
    }

    // CONSTRUCTOR
    public Heat(@NotNull Calendar scheduledTime, @NotNull String category,
                @NotNull int heatNumber, @NotNull Day dayToRace, @NotNull int heatID) {
        this.scheduledTime = scheduledTime;
        this.category = category;
        this.heatNumber = heatNumber;
        this.heatID = heatID;

        this.hasStarted = false;
        runs = new LinkedHashMap<RunNumber, Run>();

        setDayToRace(dayToRace);
    }

// GETTERS AND SETTERS, used by Jackson JSON //


    public int getHeatID() {
        return heatID;
    }

    public int getHeatNumber() {
        return heatNumber;
    }

    public String getCategory() {
        return category;
    }

    public Day getDayToRace() {
        return dayToRace;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public Map<RunNumber, Run> getRuns() {
        return runs;
    }

    public Calendar getScheduledTime() {
        return scheduledTime;
    }

    public boolean isHasStarted() {
        return hasStarted;
    }

    public void setHeatID(@NotNull int heatID) {
        this.heatID = heatID;
    }

    public void setStartTime(@NotNull Calendar startTime) {
        this.startTime = startTime;
    }

    public void setHasStarted(@NotNull boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

    public void setHeatNumber(@NotNull int heatNumber) {
        this.heatNumber = heatNumber;
    }

    public void setCategory(@NotNull String category) {
        this.category = category;
    }

    public void setRuns(@NotNull Map<RunNumber, Run> runs) {
        this.runs = runs;
    }

    public void setScheduledTime(@NotNull Calendar scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

// FUNCTIONS //

    /**
     * Will officially start the Heat by setting the startTime and mark hasStarted to true.
     *
     * @param startTime Calendar representing the Heat's start time.
     */
    public void markStartTime(@NotNull Calendar startTime) {
        this.startTime = startTime;
        hasStarted = true;
    }

    /**
     * Will associate this Heat with a new Day by setting the given day as the day this heat will race.
     * Will delete itself from the day it was in, if any.
     *
     * @param day   Day this Heat will be associated to.
     */
    public void setDayToRace(Day day) {
        if (dayToRace != null) {
            dayToRace.removeHeatByHeatNumber(heatNumber);
        }
        day.addHeat(this);
        this.dayToRace = day;
    }

    /**
     * Associates a Team to this Heat by building a new Run to use as the medium of association. This new Run will
     * have this Heat and the imputed Team as its Heat and Team, and thus will add the Run to the Team as well.
     *
     * @param team  Team to be associated to this Heat and to be added the new Run.
     * @throws AddRunException  If the Team is already associated to this Heat.
     */
    public void addRunFromTeam(@NotNull Team team) throws AddRunException {
        Run run = new Run(this, team);

        if (!runs.containsKey(run.getRunNumber())) {
            runs.put(run.getRunNumber(), run);
            try {
                run.getTeam().addRun(run);
            } catch (AddRunException e) {
                // we expect this
            }
        } else {
            throw new AddRunException("Heat affected: " + heatNumber + ". RunNumber that tried to get added: " + run.getRunNumber());
        }
    }

    /**
     * Associate a Run to this Heat by adding it to the runs Map with the Run's RunNumber as a key and setting
     * the Run's heat variable to point to this Heat.
     *
     * @param run   Run to be associated to this Heat.
     * @throws AddRunException  If this Heat is already associated to a Run with the same RunNumber.
     */
    public void addRun(Run run) throws AddRunException {
        if (!runs.containsKey(run.getRunNumber())) {
            runs.put(run.getRunNumber(), run);
            run.setHeat(this);
        } else {
            throw new AddRunException("Heat affected: " + heatNumber + ". RunNumber that tried to get added: " + run.getRunNumber());
        }
    }

    /**
     * Disassociate a Run from this Heat by searching for the Run in the runs Map with the linked RunNumber. It will
     * remove the Run from the runs list and set the Run's heat to null.
     *
     * @param runNumber RunNumber to be used to find the Run to be disassociated.
     * @return  The Run that was disassociated.
     * @throws CriticalErrorException  If no Run is disassociated, aka no Run is found with the RunNumber imputed.
     */
    public Run removeRun(@NotNull RunNumber runNumber) throws CriticalErrorException {
        if (runs.containsKey(runNumber)) {
            Run run = runs.remove(runNumber);
            run.setHeat(null);
            return run;
        } else {
            throw new CriticalErrorException("Could not remove a run. Heat affected: " + heatNumber +
                    ". RunNumber that go tried to remove: " + runNumber);
        }
    }

    /**
     * Used when the user wants to Undo the start of this Heat. It will restart the Heat by setting the startTime to
     * null and hasStated to false.
     *
     * @throws CanNotUndoHeatException  if the Heat has not started yet.
     */
    public void undoHeatStart() throws CanNotUndoHeatException {
        if (hasStarted) {
            hasStarted = false;
            startTime = null;
        } else {
            throw new CanNotUndoHeatException("Affected heat number: " + heatNumber);
        }
    }

    /**
     * Will go over the Map of Run(s) and select only the Run(s) that do not have a status of "DNS"
     * For those Run(s) that have DNS status, they will not go active and will be set an end time equal
     * to its start time to have a 0 final time, representing the Team not running.
     *
     * @return  ArrayList of Run(s) that are associated with this Heat and are not DNS status
     */
    public ArrayList<Run> listOfRunsWithoutDNS() {
        ArrayList<Run> runnableTeams = new ArrayList<>();
        for (Run run : runs.values()) {
            if (run.getSitrep() != Sitrep.DNS) {
                runnableTeams.add(run);
            } else {
                try {
                    run.calculateEndTime(startTime);
                } catch (CriticalErrorException criticalErrorException) {
                    // Nothing to be done as this is technically impossible
                }
            }
        }
        return runnableTeams;
    }

    /**
     * DecimalFormat is used to show the single digit numbers with a leading 0 because when talking about time,
     * 1 != 01, the 1 would be taken as 10.
     *
     * @return  Will return the start time of the Heat as a String, if the Heat has not started then an empty string
     *              is returned
     */
    public String startTimeString() {
        if (!hasStarted) {
            return "";
        }
        // used to format the time string to two values
        DecimalFormat formatter = new DecimalFormat("00");
        return formatter.format(startTime.get(Calendar.HOUR_OF_DAY)) + ":" + formatter.format(startTime.get(Calendar.MINUTE));
    }

    /**
     * DecimalFormat is used to show the single digit numbers with a leading 0 because when talking about time,
     * 1 != 01, the 1 would be taken as 10.
     *
     * @return  Will return the scheduled time of the Heat as a String
     */
    public String scheduledTimeString() {
        // used to set the time string to two values
        DecimalFormat decimalFormat = new DecimalFormat("00");
        return decimalFormat.format(scheduledTime.get(Calendar.HOUR_OF_DAY)) + ":" + decimalFormat.format(scheduledTime.get(Calendar.MINUTE));
    }

}