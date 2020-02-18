package models;

import com.fasterxml.jackson.annotation.*;
import com.sun.istack.internal.NotNull;
import models.enums.LeagueType;
import models.enums.Sitrep;
import models.enums.TeamType;
import models.exceptions.*;

import java.util.ArrayList;
import java.util.Calendar;

@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property = "@UUID")
public class Heat {

    // private vars
    private Calendar timeToStart;
    private LeagueType leagueType;
    private TeamType teamType;
    private int heatNumber;
    private boolean hasStarted;

    // private time vars
    private Calendar startTime;

    // private connections
    private ArrayList<Team> teams;

    @JsonBackReference
    private Day dayToRace;

    // DUMMY CONSTRUCTOR used by Jackson JSON
    public Heat() {
        teams = new ArrayList<>();
    }

    // CONSTRUCTOR
    public Heat(@NotNull Calendar timeToStart, @NotNull LeagueType leagueType, @NotNull TeamType teamType, @NotNull int heatNumber,@NotNull Day dayToRace) throws AddHeatException {
        this.timeToStart = timeToStart;
        this.leagueType = leagueType;
        this.teamType = teamType;
        this.heatNumber = heatNumber;
        this.hasStarted = false;

        teams = new ArrayList<>();
        setDayToRace(dayToRace);
    }

    // GETTERS AND SETTERS, used by Jackson JSON
    public int getHeatNumber() {
        return heatNumber;
    }

    public LeagueType getLeagueType() {
        return leagueType;
    }

    public TeamType getTeamType() {
        return teamType;
    }

    public Day getDayToRace() {
        return dayToRace;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public Calendar getTimeToStart() {
        return timeToStart;
    }

    public void setStartTime(@NotNull Calendar startTime) {
        this.startTime = startTime;
    }

    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

    public void setHeatNumber(@NotNull int heatNumber) {
        this.heatNumber = heatNumber;
    }

    public void setLeagueType(@NotNull LeagueType leagueType) {
        this.leagueType = leagueType;
    }

    public void setTeams(@NotNull ArrayList<Team> teams) {
        this.teams = teams;
    }

    public void setTeamType(@NotNull TeamType teamType) {
        this.teamType = teamType;
    }

    public void setTimeToStart(@NotNull Calendar timeToStart) {
        this.timeToStart = timeToStart;
    }

    // MODIFIES: startTime, hasStarted, teams(children)
    // EFFECTS: sets the heat's startTime, marks hasStarted to true, and sets this heat's team's current heat to this
    public void markStartTimeStarted(@NotNull Calendar startTime) {
        this.startTime = startTime;
        hasStarted = true;
        for (Team team : teams) {
            team.setCurrentHeatID(heatNumber);
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
        return timeToStart.get(Calendar.HOUR_OF_DAY) + ":" + timeToStart.get(Calendar.MINUTE);
    }

    // EFFECTS: add a team to the heat and add this heat to the team
    public void addTeam(Team team) throws AddTeamException {
        if (!teams.contains(team)) {
            teams.add(team);
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
    public void addTeams(ArrayList<Team> teams) throws AddTeamException {
        for (Team team : teams) {
            addTeam(team);
        }
    }

    // EFFECTS: remove a team from this heat and this heat from the team
    public void removeTeam(Team team) throws NoTeamException {
        if (teams.contains(team)) {
            teams.remove(team);
            try {
                team.removeHeat(this);
            } catch (NoHeatsException e) {
                // do nothing as we expect this to happen because of the many to many connection
            }
        } else {
            throw new NoTeamException();
        }
    }

    // EFFECTS: restart the heat by deleting start time and changing hasStarted
    public void undoHeatStart() throws CanNotUndoHeatException {
        if (hasStarted) {
            hasStarted = false;
            startTime = null;
            for (Team team : teams) {
                team.setCurrentHeatID(-1);
            }
        } else {
            throw new CanNotUndoHeatException();
        }
    }

    // get a team from this heat from its id
    public Team getTeamFromHeatByID(int teamID) {
        for (Team team : teams) {
            if (team.getTeamNumber() == teamID) {
                return team;
            }
        }

        return null; // TODO send exception
    }

    // EFFECTS: return only those heats with teamHeats that don't have DNS
    public ArrayList<Team> getTeamsThatWillRun() {
        ArrayList<Team> runnableTeams = new ArrayList<>();
        for (Team team : teams) {
            if (team.getTeamHeatByHeatID(heatNumber).getSitrep() != Sitrep.DNS) {
                runnableTeams.add(team);
            }
        }
        return runnableTeams;
    }

}