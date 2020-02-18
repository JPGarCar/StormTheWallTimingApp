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

    @JsonIgnore
    private EditHeatPageController editHeatController;


    // DUMMY CONSTRUCTOR for Jackson JSON
    public TimingController() {
        runningTeams = new ArrayList<>();
        finishedTeams = new ArrayList<>();
        finalFinishedTeams = new ArrayList<>();
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

    public void setEditHeatController(@NotNull EditHeatPageController editHeatController) {
        this.editHeatController = editHeatController;
    }

    public EditHeatPageController getEditHeatController() {
        return editHeatController;
    }

    // EFFECTS: add a team to the finished team list, included the task to move to final finished
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

    // EFFECTS: remove a team from the running team list
    public void removeRunningTeam(Team team) {
        runningTeams.remove(team);
    }

    // EFFECTS: remove a team from the running team list and update the ui running team list
    public void removeRunningTeamWithUpdate(Team team) {
        removeRunningTeam(team);
        uiController.updateRunningTeamList();
    }

    // EFFECTS: add a team to the running team list
    public void addRunningTeam(Team team) {
        runningTeams.add(team);
    }

    // EFFECTS: add a team to the running team list and update the ui running team list
    public void addRunningTeamWithUpdate(Team team) {
        addRunningTeam(team);
        uiController.addToRunningTeamListToTop(team);
    }

    // EFFECTS: add multiple teams to the running team list, input as an array
    public void addRunningTeams(ArrayList<Team> teamArrayList) {
        for (Team team : teamArrayList) {
            addRunningTeam(team);
        }
        uiController.updateRunningTeamList();
    }

    // EFFECTS: remove a team from the finished team list
    public void removeFinishedTeam(Team team) {
        finishedTeams.remove(team);
    }

    // EFFECTS: remove a team from the finished team list and update ui finished team list
    public void removeFinishedTeamWithUpdate(Team team) {
        removeFinishedTeam(team);
        uiController.updateFinishedTeamList();
    }

    // EFFECTS: send team back to running team list and remove from finished list, undo end time too
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

    // EFFECTS: add a team to the final finished team list and update ui final finished team list
    public void addFinalFinishedTeam(Team team) {
        finalFinishedTeams.add(team);
        uiController.addToFinalFinishedTeamListToTop(team);
    }


}