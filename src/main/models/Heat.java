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
    - Expected time to start - Calendar
    - Category of the heat - String
    - Heat ID (used by db and access) - UNIQUE - int
    - Heat Number (used by participants and this program) - UNIQUE - int
    - Boolean that represents if the heat has started - boolean
    - The actual time the heat started - Calendar
    - TreeMap of all the teams that are assigned to this heat, key is the team's number - TreeMap<Integer, Team>
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
    private Calendar timeToStart;

    private String category;

    // Represents the heat id, used by db and access - UNIQUE
    @Id
    private int heatID;

    // Represents the heat number, usually starts at 1 and goes up as needed - UNIQUE
    private int heatNumber;

    // Boolean used to know if the heat has started to run
    private boolean hasStarted;

    // Represents the actual time the heat started as a Calendar
    private Calendar actualStartTime;

    // All the teams that are running in this heat
    @ManyToMany
    private Map<Integer,Team> teams;

    // A back reference to the day this heat is running in
    @ManyToOne
    @JsonBackReference
    private Day dayToRace;

// CONSTRUCTORS //

    // DUMMY CONSTRUCTOR used by Jackson JSON
    public Heat() {
        teams = new TreeMap<>();
        this.hasStarted = false;
    }

    // CONSTRUCTOR
    public Heat(@NotNull Calendar timeToStart, @NotNull String category,
                @NotNull int heatNumber, @NotNull Day dayToRace, @NotNull int heatID) {
        this.timeToStart = timeToStart;
        this.category = category;
        this.heatNumber = heatNumber;
        this.heatID = heatID;

        this.hasStarted = false;
        teams = new TreeMap<>();

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

    public Calendar getActualStartTime() {
        return actualStartTime;
    }

    public Map<Integer, Team> getTeams() {
        return teams;
    }

    public Calendar getTimeToStart() {
        return timeToStart;
    }

    public boolean isHasStarted() {
        return hasStarted;
    }

    public void setHeatID(@NotNull int heatID) {
        this.heatID = heatID;
    }

    public void setActualStartTime(@NotNull Calendar actualStartTime) {
        this.actualStartTime = actualStartTime;
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

    public void setTeams(@NotNull Map<Integer, Team> teams) {
        this.teams = teams;
    }

    public void setTimeToStart(@NotNull Calendar timeToStart) {
        this.timeToStart = timeToStart;
    }

// FUNCTIONS //

    // MODIFIES: startTime, hasStarted, teams(children)
    // EFFECTS: sets the heat's startTime, marks hasStarted to true, and sets this heat's team's current heat to this
    public void markActualStartTime(@NotNull Calendar startTime) {
        this.actualStartTime = startTime;
        hasStarted = true;
        for (Team team : teams.values()) {
            team.markCurrentRun(heatNumber);
        }
    }

    // EFFECTS: sets the given day as the day this heat will race, will delete itself from the day it was in
    public void setDayToRace(@NotNull Day day) {
        if (dayToRace != null) {
            dayToRace.removeHeatByHeatNumber(heatNumber);
        }
        try {
            day.addHeat(this);
        } catch (AddHeatRuntimeException e) {
            // nothing because we expect this due to one to one connection
        }
        this.dayToRace = day;
    }

    // EFFECTS: return the time to start as a string
    public String timeToStartString() {
        // used to set the time string to two values
        DecimalFormat decimalFormat = new DecimalFormat("00");
        return decimalFormat.format(timeToStart.get(Calendar.HOUR_OF_DAY)) + ":" + decimalFormat.format(timeToStart.get(Calendar.MINUTE));
    }

    // EFFECTS: add a team to the heat and add this heat to the team
    public void addTeam(@NotNull Team team) throws AddTeamException {
        if (!teams.containsKey(team.getTeamNumber())) {
            teams.put(team.getTeamNumber(), team);
            try {
                team.addHeat(this);
            } catch (AddHeatRuntimeException e) {
                // do nothing as we expect this to happen because of the many to many connection
            }
        } else {
            throw new AddTeamException("Heat affected: " + heatNumber + ". Team number that tried to get added: " + team.getTeamNumber());
        }
    }

    // EFFECTS: remove a team from this heat and this heat from the team by team number
    public Team removeTeam(@NotNull int teamNumber) throws NoTeamException {
        if (teams.containsKey(teamNumber)) {
            Team team = teams.remove(teamNumber);
            try {
                team.removeHeat(heatNumber);
            } catch (NoHeatsException e) {
                // do nothing as we expect this to happen because of the many to many connection
            }
            return team;
        } else {
            throw new NoTeamException("Could not remove a team. Heat affected: " + heatNumber +
                    ". Team number that go tried to remove: " + teamNumber);
        }
    }

    // EFFECTS: restart the heat by deleting start time and changing hasStarted
    public void undoHeatStart() throws CanNotUndoHeatException {
        if (hasStarted) {
            hasStarted = false;
            actualStartTime = null;
            for (Team team : teams.values()) {
                team.markCurrentRun(-1);
            }
        } else {
            throw new CanNotUndoHeatException("Affected heat number: " + heatNumber);
        }
    }

    // EFFECTS: return a team from this heat by its team number
    public Team teamFromHeatByTeamNumber(@NotNull int teamNumber) {
        return teams.get(teamNumber);
    }

    // EFFECTS: return only those heats with teamHeats that don't have DNS, those that do get a 0 time
    public ArrayList<Team> teamsThatWillRun() throws NoRunFoundException, CouldNotCalculateFinalTimeExcpetion, NoHeatsException {
        ArrayList<Team> runnableTeams = new ArrayList<>();
        for (Team team : teams.values()) {
            if (team.getRunByHeatNumber(heatNumber).getSitrep() != Sitrep.DNS) {
                runnableTeams.add(team);
            } else {
                team.getRunByHeatNumber(heatNumber).calculateEndTime(actualStartTime);
            }
        }
        return runnableTeams;
    }

    // EFFECTS: return the actual start time as a string, if it has not started return empty string
    public String actualStartTimeString() {
        if (!hasStarted) {
            return "";
        }
        // used to format the time string to two values
        DecimalFormat formatter = new DecimalFormat("00");
        return formatter.format(actualStartTime.get(Calendar.HOUR_OF_DAY)) + ":" + formatter.format(actualStartTime.get(Calendar.MINUTE));
    }

}