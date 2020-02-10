package ui;

import models.Heat;
import models.Team;

import java.util.ArrayList;

public class MainPageController {

    // private vars
    private Heat stagedHeat;
    private ArrayList<Team> runningTeams = new ArrayList<>();
    private ArrayList<Team> finishedTeams = new ArrayList<>();

    // DUMMY CONSTRUCTOR for Jackson JSON
    public MainPageController() {}

    // GETTERS AND SETTERS, used by Jackson JSON
    public ArrayList<Team> getFinishedTeams() {
        return finishedTeams;
    }

    public ArrayList<Team> getRunningTeams() {
        return runningTeams;
    }

    public Heat getStagedHeat() {
        return stagedHeat;
    }

    public void setRunningTeams(ArrayList<Team> runningTeams) {
        this.runningTeams = runningTeams;
    }

    public void setFinishedTeams(ArrayList<Team> finishedTeams) {
        this.finishedTeams = finishedTeams;
    }

    public void setStagedHeat(Heat stagedHeat) {
        this.stagedHeat = stagedHeat;
    }

    public void addFinishedTeam(Team team) {
        finishedTeams.add(team);
    }
}