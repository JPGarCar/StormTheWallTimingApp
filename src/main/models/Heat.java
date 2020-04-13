package models;

import com.fasterxml.jackson.annotation.*;
import com.sun.istack.internal.NotNull;
import models.enums.Sitrep;
import models.exceptions.*;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.util.*;

/*
    Represents a regular heat that will run during the event
    Purpose: Control the teams that are running in this heat, when it starts and what kind of teams are running
    Contains:
    - Scheduled time to start - Calendar
    - Category of the heat - String
    - Heat ID (used by db and access) - UNIQUE - int
    - Heat Number (used by participants and this program) - UNIQUE - int
    - Boolean that represents if the heat has started - boolean
    - The actual time the heat started - Calendar
    - TreeMap of all the teams that are assigned to this heat, key is the team's number - LinkedHashMap<Integer, Team>
    - Day this heat is assigned to and running on - Day

    Usage:
    -

    Persistence:
    - Class is an entity in the table name "heat_table"
    - Actual start time, the teams that will run and weather or not heat started will change during the life of program,
        all other should not change much or at all
    - Back reference, Many To One relationship with Day
    - Many to Many relationship with Team
 */


@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property = "@UUID")
@Entity
@Table(name = "heat_table")
public class Heat {

// VARIABLES //

    // Represents the time this heat should start as a Calendar
    private Calendar scheduledTime;

    private String category;

    // Represents the heat id, used by db and access - UNIQUE
    @Id
    private int heatID;

    // Represents the heat number, usually starts at 1 and goes up as needed - UNIQUE
    private int heatNumber;

    // Boolean used to know if the heat has started to run
    private boolean hasStarted;

    // Represents the actual time the heat started as a Calendar
    private Calendar startTime;

    // All the teams that are running in this heat
    @ManyToMany
    private Map<RunNumber, Run> runs;

    // A back reference to the day this heat is running in
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

    // MODIFIES: startTime, hasStarted, teams(children)
    // EFFECTS: sets the heat's startTime, marks hasStarted to true
    public void markStartTime(@NotNull Calendar startTime) {
        this.startTime = startTime;
        hasStarted = true;
    }

    // EFFECTS: sets the given day as the day this heat will race, will delete itself from the day it was in
    public void setDayToRace(@NotNull Day day) {
        if (dayToRace != null) {
            dayToRace.removeHeatByHeatNumber(heatNumber);
        }
        try {
            day.addHeat(this);
        } catch (AddHeatException e) {
            // nothing because we expect this due to one to one connection
        }
        this.dayToRace = day;
    }

    // EFFECTS: return the time to start as a string
    public String scheduledTimeString() {
        // used to set the time string to two values
        DecimalFormat decimalFormat = new DecimalFormat("00");
        return decimalFormat.format(scheduledTime.get(Calendar.HOUR_OF_DAY)) + ":" + decimalFormat.format(scheduledTime.get(Calendar.MINUTE));
    }

    // EFFECTS: add a run to the heat and add this heat to the run from a team
    public void addRunFromTeam(@NotNull Team team) throws AddTeamException {
        Run run = new Run(this, team);

        if (!runs.containsKey(run.getRunNumber())) {
            runs.put(run.getRunNumber(), run);
            try {
                run.getTeam().addRun(run);
            } catch (AddHeatException e) {
                // we expect this
            }
        } else {
            throw new AddTeamException("Heat affected: " + heatNumber + ". RunNumber that tried to get added: " + run.getRunNumber());
        }
    }

    //EFFECTS: adds a run to the run list
    public void addRun(Run run) throws AddTeamException {
        if (!runs.containsKey(run.getRunNumber())) {
            runs.put(run.getRunNumber(), run);
        } else {
            throw new AddTeamException("Heat affected: " + heatNumber + ". RunNumber that tried to get added: " + run.getRunNumber()); // TODO
        }
    }

    // EFFECTS: remove a run from this heat and this heat from the run by RunNumber
    public Run removeRun(@NotNull RunNumber runNumber) throws NoTeamException {
        if (runs.containsKey(runNumber)) {
            Run run = runs.remove(runNumber);
            run.setHeat(null);
            return run;
        } else {
            throw new NoTeamException("Could not remove a run. Heat affected: " + heatNumber +
                    ". RunNumber that go tried to remove: " + runNumber);
        }
    }

    // EFFECTS: restart the heat by deleting start time and changing hasStarted
    public void undoHeatStart() throws CanNotUndoHeatException {
        if (hasStarted) {
            hasStarted = false;
            startTime = null;
        } else {
            throw new CanNotUndoHeatException("Affected heat number: " + heatNumber);
        }
    }

    // EFFECTS: return only Run without a DNS, if have DNS endTime == startTime for totalTime of 0
    public ArrayList<Run> listOfRunsWithoutDNS() throws NoHeatsException {
        ArrayList<Run> runnableTeams = new ArrayList<>();
        for (Run run : runs.values()) {
            if (run.getSitrep() != Sitrep.DNS) {
                runnableTeams.add(run);
            } else {
                try {
                    run.calculateEndTime(startTime);
                } catch (CouldNotCalculateFinalTimeExcpetion couldNotCalculateFinalTimeExcpetion) {
                    // Nothing to be done as this is technically impossible
                }
            }
        }
        return runnableTeams;
    }

    // EFFECTS: return the actual start time as a string, if it has not started return empty string
    public String startTimeString() {
        if (!hasStarted) {
            return "";
        }
        // used to format the time string to two values
        DecimalFormat formatter = new DecimalFormat("00");
        return formatter.format(startTime.get(Calendar.HOUR_OF_DAY)) + ":" + formatter.format(startTime.get(Calendar.MINUTE));
    }

}