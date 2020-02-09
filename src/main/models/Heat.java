package models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import models.enums.LeagueType;
import models.enums.TeamType;
import org.codehaus.jackson.annotate.JsonBackReference;

import java.util.ArrayList;
import java.util.Calendar;

//@JsonIdentityInfo(generator = ObjectIdGenerators.UUIDGenerator.class, property = "@UUID")
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
    @JsonManagedReference
    private ArrayList<Team> teams;

    @JsonBackReference
    private Day dayToRace;

    public Heat() {

    }

    public Heat(Calendar timeToStart, LeagueType leagueType, TeamType teamType, int heatNumber, Day dayToRace) {
        this.timeToStart = timeToStart;
        this.leagueType = leagueType;
        this.teamType = teamType;
        this.heatNumber = heatNumber;
        this.hasStarted = false;

        teams = new ArrayList<>();
        setDay(dayToRace);
    }

    // GETTERS AND SETTERS
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

    public void markStartTimeStarted(Calendar startTime) {
        this.startTime = startTime;
        hasStarted = true;
        for (Team team : teams) {
            team.setCurrentHeatIDFromHeat(this);
        }
    }

    public boolean isHasStarted() {
        return hasStarted;
    }

    public void setDayToRace(Day dayToRace) {
        if (this.dayToRace == null) {
            setDay(dayToRace);
        }
    }

    // EFFECTS: return the time to start as a string
    public String timeToStartString() {
        return timeToStart.get(Calendar.HOUR_OF_DAY) + ":" + timeToStart.get(Calendar.MINUTE);
    }

    // EFFECTS: add a team to the heat and add this heat to the team
    public void addTeam(Team team) {
        if (!teams.contains(team)) {
            teams.add(team);
            team.addHeat(this);
        }
    }

    // EFFECTS: add all the teams from a list of teams
    public void addTeams(ArrayList<Team> teams) {
        for (Team team : teams) {
            addTeam(team);
        }
    }

    // EFFECTS: remove a team from this heat and this heat from the team
    public void removeTeam(Team team) {
        if (teams.contains(team)) {
            teams.remove(team);
            team.removeHeat(this);
        }
    }

    // EFFECTS: add a day to the heat and this heat to that day
    private void setDay(Day day) {
        this.dayToRace = day;
        day.addHeat(this);
    }


}