package ui;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.internal.NotNull;
import javafx.application.Platform;
import models.Heat;
import models.Program;
import models.Team;
import models.exceptions.CouldNotCalculateFinalTimeExcpetion;
import models.exceptions.NoCurrentHeatIDException;
import models.exceptions.NoHeatsException;
import models.exceptions.NoRemainingHeatsException;


import java.util.*;

public class TimingController {

    // private vars
    private Heat stagedHeat;
    private Map<Integer, Team> runningTeams;
    private Map<Integer, Team> finishedTeams;
    private Map<Integer, Team> finalFinishedTeams;
    private Program program;

    @JsonIgnore
    private mainTimingController uiController;

    @JsonIgnore
    private EditHeatPageController editHeatController;

    @JsonIgnore
    private Map<Integer, Timer> timerMap;


    // DUMMY CONSTRUCTOR for Jackson JSON
    public TimingController() {
        runningTeams = new HashMap<>();
        finishedTeams = new HashMap<>();
        finalFinishedTeams = new HashMap<>();
        program = new Program();
        timerMap = new HashMap<>();
    }

    // GETTERS AND SETTERS, used by Jackson JSON

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public Map<Integer, Team> getFinishedTeams() {
        return finishedTeams;
    }

    public Map<Integer, Team> getRunningTeams() {
        return runningTeams;
    }

    public Heat getStagedHeat() {
        return stagedHeat;
    }

    public void setRunningTeams(Map<Integer, Team> runningTeams) {
        this.runningTeams = runningTeams;
    }

    public void setFinishedTeams(Map<Integer, Team> finishedTeams) {
        this.finishedTeams = finishedTeams;
    }

    public void setStagedHeat(Heat stagedHeat) {
        this.stagedHeat = stagedHeat;
    }

    public Map<Integer, Team> getFinalFinishedTeams() {
        return finalFinishedTeams;
    }

    public mainTimingController getUiController() {
        return uiController;
    }

    public void setFinalFinishedTeams(@NotNull Map<Integer, Team> finalFinishedTeams) {
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
    public void addFinishedTeam(@NotNull Team team) {
        int teamNumber = team.getTeamNumber();
        finishedTeams.put(teamNumber, team);
        uiController.updateFinishedTeamList();
        Timer t = new Timer();
        timerMap.put(teamNumber, t);
        t.schedule( new TimerTask() {
            @Override
            public void run() {
                if (team.getPossibleUndo()) {
                    team.setPossibleUndo(false);
                    Platform.runLater(() -> addFinalFinishedTeam(team));
                    Platform.runLater(() -> removeFinishedTeamWithUpdate(teamNumber));
                    t.cancel();
                }
            }
        },
        10000);
    }

    // EFFECTS: remove a team from the running team list
    public void removeRunningTeam(int teamID) {
        runningTeams.remove(teamID);
    }

    // EFFECTS: remove a team from the running team list and update the ui running team list
    public void removeRunningTeamWithUpdate(int teamID) {
        removeRunningTeam(teamID);
        uiController.updateRunningTeamList();
    }

    // EFFECTS: add a team to the running team list
    public void addRunningTeam(Team team) {
        runningTeams.put(team.getTeamNumber(), team);
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
    public void removeFinishedTeam(int teamID) {
        finishedTeams.remove(teamID);
    }

    // EFFECTS: remove a team from the finished team list and update ui finished team list
    public void removeFinishedTeamWithUpdate(int teamID) {
        removeFinishedTeam(teamID);
        uiController.updateFinishedTeamList();
    }

    // EFFECTS: send team back to running team list and remove from finished list, undo end time too
    public void undoTeamFinish(int teamID) {
        timerMap.get(teamID).cancel();
        removeFinishedTeamWithUpdate(teamID);

        Team team = finishedTeams.get(teamID);
        team.undoEndTimeMark();
        addRunningTeamWithUpdate(team);
    }

    // EFFECTS: add a team to the final finished team list and update ui final finished team list
    public void addFinalFinishedTeam(Team team) {
        finalFinishedTeams.put(team.getTeamNumber(), team);
        uiController.addToFinalFinishedTeamListToTop(team);
    }

    // EFFECTS: ends a team
    public void endTeam(int teamID) throws NoHeatsException, CouldNotCalculateFinalTimeExcpetion, NoCurrentHeatIDException, NoRemainingHeatsException {
        Team team = runningTeams.get(teamID);
        team.markEndTime(Calendar.getInstance());
        addFinishedTeam(team);

        removeRunningTeamWithUpdate(teamID);
    }


}