package models;

import models.enums.LeagueType;
import models.enums.TeamType;

import java.util.ArrayList;
import java.util.Calendar;

public class Heat {

    // private vars
    private Calendar timeToStart;
    private LeagueType leagueType;
    private TeamType teamType;
    private int heatNumber;

    // private time vars
    private Calendar startTime;

    // private connections
    private ArrayList<Team> teams;
    private Day dayToRace;

    public Heat(Calendar timeToStart, LeagueType leagueType, TeamType teamType, int heatNumber, Day dayToRace) {
        this.timeToStart = timeToStart;
        this.leagueType = leagueType;
        this.teamType = teamType;
        this.heatNumber = heatNumber;

        teams = new ArrayList<>();
        this.dayToRace = dayToRace;
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

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    // EFFECTS: return the time to start as a string
    public String getTimeToStartString() {
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



}