package models;

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

        teams = new ArrayList<Team>();
        this.dayToRace = dayToRace;
    }

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

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public String getTimeToStartString() {
        return timeToStart.get(Calendar.HOUR_OF_DAY) + ":" + timeToStart.get(Calendar.MINUTE);
    }


}