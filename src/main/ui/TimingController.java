package ui;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.internal.NotNull;
import javafx.application.Platform;
import models.Heat;
import models.Team;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class TimingController {

    // private vars
    private Heat stagedHeat;
    private ArrayList<Team> runningTeams;
    private ArrayList<Team> finishedTeams;
    private ArrayList<Team> finalFinishedTeams;

    @JsonIgnore
    private mainTimingController uiController;


    // DUMMY CONSTRUCTOR for Jackson JSON
    public TimingController(mainTimingController uiController) {
        runningTeams = new ArrayList<>();
        finishedTeams = new ArrayList<>();
        finalFinishedTeams = new ArrayList<>();
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

    public ArrayList<Team> getFinalFinishedTeams() {
        return finalFinishedTeams;
    }

    public mainTimingController getUiController() {
        return uiController;
    }

    public void setFinalFinishedTeams(@NotNull ArrayList<Team> finalFinishedTeams) {
        this.finalFinishedTeams = finalFinishedTeams;
    }

    public void setUiController(@NotNull mainTimingController uiController) {
        this.uiController = uiController;
    }

    public void addFinishedTeam(Team team) {
        finishedTeams.add(team);
        uiController.updateFinishedTeamList();
        Timer t = new Timer();
        t.schedule( new TimerTask() {
            @Override
            public void run() {
                if (team.getPossibleUndo()) {
                    team.setPossibleUndo(false);
                    Platform.runLater(() -> addFinalFinishedTeam(team));
                    Platform.runLater(() -> removeFinishedTeamWithUpdate(team));
                    t.cancel();
                }
            }
        },
        10000);
    }

    public void removeRunningTeam(Team team) {
        runningTeams.remove(team);
    }

    public void removeRunningTeamWithUpdate(Team team) {
        removeRunningTeam(team);
        uiController.updateRunningTeamList();
    }

    public void addRunningTeam(Team team) {
        runningTeams.add(team);
    }

    public void addRunningTeamWithUpdate(Team team) {
        addRunningTeam(team);
        uiController.addToRunningTeamListToTop(team);
    }

    public void addRunningTeams(ArrayList<Team> teamArrayList) {
        for (Team team : teamArrayList) {
            addRunningTeam(team);
        }
        uiController.updateRunningTeamList();
    }

    public void removeFinishedTeam(Team team) {
        finishedTeams.remove(team);
    }

    public void removeFinishedTeamWithUpdate(Team team) {
        removeFinishedTeam(team);
        uiController.updateFinishedTeamList();
    }

    public void undoTeamFinish(int teamID) {
        for (Team team : finishedTeams) {
            if (team.getTeamNumber() == teamID) {
                addRunningTeamWithUpdate(team);
                removeFinishedTeamWithUpdate(team);
                team.undoEndTimeMark();
                break;
            }
        }
    }

    public void addFinalFinishedTeam(Team team) {
        finalFinishedTeams.add(team);
        uiController.addToFinalFinishedTeamListToTop(team);
    }


}