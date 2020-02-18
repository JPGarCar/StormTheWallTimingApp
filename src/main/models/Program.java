package models;

import com.sun.istack.internal.NotNull;
import models.enums.LeagueType;
import models.enums.TeamType;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class Program {

    // private vars
    private ArrayList<Day> programDays;
    private Dictionary<Integer, Team> allTeams;

    public Program() {
        programDays = new ArrayList<>();
        allTeams = new Hashtable<>();
    }

    // GETTERS AND SETTERS
    public ArrayList<Day> getProgramDays() {
        return programDays;
    }

    public Dictionary<Integer, Team> getAllTeams() {
        return allTeams;
    }

    public void setAllTeams(Dictionary<Integer, Team> allTeams) {
        this.allTeams = allTeams;
    }

    public void setProgramDays(ArrayList<Day> programDays) {
        this.programDays = programDays;
    }

    // EFFECTS: add a day to the day list
    public void addDay(Day day) {
        programDays.add(day);
    }

    // EFFECTS: add a team to the team list
    public void addTeam(Team team) {
        allTeams.put(team.getTeamNumber(), team);
    }

    // EFFECTS: creates a team to use by the program
    public Team createTeam(@NotNull TeamType teamType, @NotNull LeagueType teamLeague, @NotNull int teamNumber, @NotNull String teamName) {
        Team team = new Team(teamType, teamLeague, teamNumber, teamName);
        addTeam(team);
        return team;
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


}
