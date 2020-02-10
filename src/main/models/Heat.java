package models;

import com.fasterxml.jackson.annotation.*;
import models.enums.LeagueType;
import models.enums.TeamType;
import models.exceptions.AddHeatException;
import models.exceptions.AddTeamException;
import models.exceptions.NoHeatsException;
import models.exceptions.NoTeamException;

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
    public Heat(Calendar timeToStart, LeagueType leagueType, TeamType teamType, int heatNumber, Day dayToRace) throws AddHeatException {
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

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

    public void setHeatNumber(int heatNumber) {
        this.heatNumber = heatNumber;
    }

    public void setLeagueType(LeagueType leagueType) {
        this.leagueType = leagueType;
    }

    public void setTeams(ArrayList<Team> teams) {
        this.teams = teams;
    }

    public void setTeamType(TeamType teamType) {
        this.teamType = teamType;
    }

    public void setTimeToStart(Calendar timeToStart) {
        this.timeToStart = timeToStart;
    }

    // MODIFIES: startTime, hasStarted, teams(children)
    // EFFECTS: sets the heat's startTime, marks hasStarted to true, and sets this heat's team's current heat to this
    public void markStartTimeStarted(Calendar startTime) {
        this.startTime = startTime;
        hasStarted = true;
        for (Team team : teams) {
            team.setCurrentHeatIDFromHeat(this);
        }
    }

    // EFFECTS: returns value of hasStarted
    public boolean isHasStarted() {
        return hasStarted;
    }

    public void setDayToRace(Day day) {
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

}