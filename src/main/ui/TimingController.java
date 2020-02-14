package ui;


import models.Heat;
import models.Team;


import java.util.ArrayList;

public class TimingController {

    // private vars
    private Heat stagedHeat;
    private ArrayList<Team> runningTeams;
    private ArrayList<Team> finishedTeams;

    private mainTimingController uiController;


    // DUMMY CONSTRUCTOR for Jackson JSON
    public TimingController(mainTimingController uiController) {
        runningTeams = new ArrayList<>();
        finishedTeams = new ArrayList<>();
        this.uiController = uiController;
    }

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
        uiController.updateFinishedTeamList();
    }

    public void removeRunningTeam(Team team) {
        runningTeams.remove(team);
        uiController.updateRunningTeamList();
    }

    public void removeFinishedTeam(Team team) {
        finishedTeams.remove(team);
        uiController.updateFinishedTeamList();
    }

    public void addRunningTeam(Team team) {
        runningTeams.add(team);
        uiController.updateRunningTeamList();
    }

    public void addRunningTeams(ArrayList<Team> teamArrayList) {
        for (Team team : teamArrayList) {
            addRunningTeam(team);
        }
    }
}