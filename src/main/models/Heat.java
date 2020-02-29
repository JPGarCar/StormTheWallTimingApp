package models;

import com.fasterxml.jackson.annotation.*;
import com.sun.istack.internal.NotNull;
import models.enums.Sitrep;
import models.exceptions.*;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/*
    Represents a regular heat that will run during the event
    Purpose: Control the teams that are running in this heat, when it starts and what kind of teams are running
    Contains:
    - Expected time to start
    - Category - String
    - Team type
    - Heat ID (used by db and access) - UNIQUE
    - Heat Number (used by participants and this program) - UNIQUE
    - If heat has started
    - Actual start time
    - Teams to run this heat
    - Day this heat is running in

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
        teams = new HashMap<>();
    }

    // CONSTRUCTOR
    public Heat(@NotNull Calendar timeToStart, @NotNull String category,
                @NotNull int heatNumber, @NotNull Day dayToRace, @NotNull int heatID) {
        this.timeToStart = timeToStart;
        this.category = category;
        this.heatNumber = heatNumber;
        this.heatID = heatID;
        this.hasStarted = false;

        teams = new HashMap<>();
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

    // EFFECTS: returns value of hasStarted
    public boolean isHasStarted() {
        return hasStarted;
    }

    public void setDayToRace(@NotNull Day day) {
        try {
            day.addHeat(this);
        } catch (AddHeatException e) {
            // nothing because we expect this due to one to one connection
        }
        this.dayToRace = day;
    }

    // EFFECTS: return the time to start as a string
    public String timeToStartString() {
        DecimalFormat decimalFormat = new DecimalFormat("00");
        return decimalFormat.format(timeToStart.get(Calendar.HOUR_OF_DAY)) + ":" + decimalFormat.format(timeToStart.get(Calendar.MINUTE));
    }

    // EFFECTS: add a team to the heat and add this heat to the team
    public void addTeam(@NotNull Team team) throws AddTeamException {
        if (!teams.containsKey(team.getTeamNumber())) {
            teams.put(team.getTeamNumber(), team);
            try {
                team.addHeat(this);
            } catch (AddHeatException e) {
                // do nothing as we expect this to happen because of the many to many connection
            }
        } else {
            throw new AddTeamException();
        }
    }

    // EFFECTS: add all the teams from a list of teams
    public void addTeams(@NotNull ArrayList<Team> teams) throws AddTeamException {
        for (Team team : teams) {
            addTeam(team);
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
            throw new NoTeamException();
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
            throw new CanNotUndoHeatException();
        }
    }

    // EFFECTS: return a team from this heat by its team number
    public Team getTeamFromHeatByTeamNumber(@NotNull int teamNumber) {
        return teams.get(teamNumber);
    }

    // EFFECTS: return only those heats with teamHeats that don't have DNS
    public ArrayList<Team> getTeamsThatWillRun() {
        ArrayList<Team> runnableTeams = new ArrayList<>();
        for (Team team : teams.values()) {
            try {
                if (team.getRunByHeatNumber(heatNumber).getSitrep() != Sitrep.DNS) {
                    runnableTeams.add(team);
                }
            } catch (NoTeamHeatException e) {
                e.printStackTrace();
            }
        }
        return runnableTeams;
    }

    // EFFECTS: return the actual start time as a string
    public String getActualStartTimeString() {
        if (actualStartTime == null) {
            return "";
        }
        DecimalFormat formatter = new DecimalFormat("00");
        return formatter.format(actualStartTime.get(Calendar.HOUR_OF_DAY)) + ":" + formatter.format(actualStartTime.get(Calendar.MINUTE));
    }

    // EFFECTS: return the intended start time as a string
    public String getStartTimeString() {
        if (timeToStart == null) {
            return "";
        }
        DecimalFormat formatter = new DecimalFormat("00");
        return formatter.format(timeToStart.get(Calendar.HOUR_OF_DAY)) + ":" + formatter.format(timeToStart.get(Calendar.MINUTE));
    }

}