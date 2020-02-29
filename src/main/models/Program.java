package models;

import com.sun.istack.internal.NotNull;

import java.util.*;

/*
    General class for the program.
    Purpose: control list of all teams, builds teams and has list of all days to be run.
    Contains:
    - Team list of all teams
    - Wait list of teams
    - Day list of all days to be run

    Usage:
    - Used by edit heat page to control the teams that are in the wait list
    - Used by HBox to get team by team Number
    - Used by main timing controller to create heats
    - Used by main timing controller to get the days available

    Persistence:
    - This class is not in the database, it will get built every time the program starts using db data
    - Must keep all three map lists persistent with db
 */


public class Program {

// VARIABLES //

    private Map<String, Day> programDays;

    private Map<Integer, Team> allTeams;

    private Map<Integer, Team> waitList;

// CONSTRUCTORS //

    public Program() {
        programDays = new HashMap<>();
        allTeams = new HashMap<>();
        waitList = new HashMap<>();
    }

// GETTERS AND SETTERS //

    public Map<String, Day> getProgramDays() {
        return programDays;
    }

    public Map<Integer, Team> getAllTeams() {
        return allTeams;
    }


    public Map<Integer, Team> getWaitList() {
        return waitList;
    }

    public void setAllTeams(Map<Integer, Team> allTeams) {
        this.allTeams = allTeams;
    }

    public void setProgramDays(Map<String, Day> programDays) {
        this.programDays = programDays;
    }

    public void setWaitList(Map<Integer, Team> waitList) {
        this.waitList = waitList;
    }

// FUNCTIONS //

    // EFFECTS: add a day to the day list
    public void addDay(@NotNull Day day) {
        programDays.put(day.getDayToRun(), day);
    }

    // EFFECTS: add a team to the team list
    public void addTeam(@NotNull Team team) {
        allTeams.put(team.getTeamNumber(), team);
    }

    // EFFECTS: creates a team to use by the program
    public Team createTeam( @NotNull String poolName, @NotNull int teamNumber, @NotNull String teamName, @NotNull int teamID, String teamUnit) {
        Team team = new Team(poolName, teamNumber, teamName, teamID, teamUnit);
        addTeam(team);
        return team;
    }

    // EFFECTS: add a team to the waitList team list
    public void addTeamToWaitList(@NotNull Team team) {
        waitList.put(team.getTeamNumber(), team);
    }

    // EFFECTS: returns a team by its team number
    public Team getTeamByTeamNumber(@NotNull int teamNumber) {
        return allTeams.get(teamNumber);
    }

    // EFFECTS: remove a team from wait list by its team number
    public void removeTeamFromWaitList(@NotNull int teamNumber) {
        waitList.remove(teamNumber);
    }

//    // EFFECTS: creates a team to use by the program with an automatic number
//    public Team createTeamAutoNumber(@NotNull TeamType teamType, @NotNull LeagueType teamLeague, @NotNull String teamName) {
//        Team team = null;
//        for (int i = 0; i < allTeams.size() + 1; i++) {
//            if(allTeams.get(i) == null) {
//                team = createTeam(teamType, teamLeague, i, teamName);
//            }
//        }
//        return team;
//    }

    // EFFECTS: returns a day or builds a new one and returns that day
    public Day getProgramDayOrBuild(String programDay) {
        if (programDays.get(programDay) != null) {
            return programDays.get(programDay);
        } else {
            Day day = new Day(programDay, programDays.size());
            addDay(day);
            return day;
        }
    }


}
