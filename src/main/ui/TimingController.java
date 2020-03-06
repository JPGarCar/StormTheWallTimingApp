package ui;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.internal.NotNull;
import javafx.application.Platform;
import models.*;
import models.exceptions.*;
import persistance.DataBaseConnection;
import persistance.PersistanceWithJackson;


import java.util.*;

public class TimingController {

// VARIABLES //

    @JsonIgnore
    final int RUNUNDODELAYTIME = 10000;

    // Contains the current staged heat
    private Heat stagedHeat;

    // Contains all the runs currently running
    private Map<RunNumber, Run> currentRuns;

    // Contains all the runs that have stopped and can be undo
    private Map<RunNumber, Run> stoppedRuns;

    // Contains all the runs that have finished
    private Map<RunNumber, Run> finishedRuns;

    @JsonIgnore
    private Program program;

    @JsonIgnore
    private MainTimingController uiController;

    @JsonIgnore
    private EditHeatPageController editHeatController;

    @JsonIgnore
    private Day currentDay;

    private Map<RunNumber, Timer> timerMap;

    private DataBaseConnection dbConnection;

// CONSTRUCTORS //

    // DUMMY CONSTRUCTOR for Jackson JSON
    public TimingController() {
        currentRuns = new TreeMap<>();
        stoppedRuns = new LinkedHashMap<>();
        finishedRuns = new LinkedHashMap<>();
        timerMap = new HashMap<>();
    }

// GETTERS AND SETTERS, used by Jackson JSON //


    public DataBaseConnection getDbConnection() {
        return dbConnection;
    }

    public Day getCurrentDay() {
        return currentDay;
    }

    public Program getProgram() {
        return program;
    }

    public Map<RunNumber, Run> getStoppedRuns() {
        return stoppedRuns;
    }

    public Map<RunNumber, Run> getCurrentRuns() {
        return currentRuns;
    }

    public Heat getStagedHeat() {
        return stagedHeat;
    }

    public Map<RunNumber, Run> getFinishedRuns() {
        return finishedRuns;
    }

    public MainTimingController getUiController() {
        return uiController;
    }

    public EditHeatPageController getEditHeatController() {
        return editHeatController;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public void setCurrentRuns(Map<RunNumber, Run> currentRuns) {
        this.currentRuns = currentRuns;
    }

    public void setStoppedRuns(Map<RunNumber, Run> stoppedRuns) {
        this.stoppedRuns = stoppedRuns;
    }

    public void setStagedHeat(Heat stagedHeat) {
        this.stagedHeat = stagedHeat;
    }

    public void setFinishedRuns(@NotNull Map<RunNumber, Run> finishedRuns) {
        this.finishedRuns = finishedRuns;
    }

    public void setUiController(@NotNull MainTimingController uiController) {
        this.uiController = uiController;
    }

    public void setEditHeatController(@NotNull EditHeatPageController editHeatController) {
        this.editHeatController = editHeatController;
    }

    public void setCurrentDay(Day currentDay) {
        this.currentDay = currentDay;
    }

    public void setDbConnection(DataBaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // FUNCTIONS //

    // EFFECTS: add a run to the stopped run list, included the task to move to finished
    private void stopRun(@NotNull Run run) {
        stoppedRuns.put(run.getRunNumber(), run);
        uiController.updateStoppedRunList();
        Timer t = new Timer();
        timerMap.put(run.getRunNumber(), t);
        t.schedule( new TimerTask() {
            @Override
            public void run() {
                if (run.getCanUndo()) {
                    run.setCanUndo(false);
                    run.getTeam().markCurrentRun(-1);

                    Platform.runLater(() -> addFinishedRun(run));
                    Platform.runLater(() -> removeStoppedRunWithUpdate(run.getRunNumber()));
                    t.cancel();
                }
            }
        },
        RUNUNDODELAYTIME);
    }

    // EFFECTS: remove a run from the running run list
    public void removeRunningTeam(RunNumber runNumber) {
        currentRuns.remove(runNumber);
        dbConnection.removeRunningRunDBUpdate(runNumber);
    }

    // EFFECTS: remove a run from the running run list and update the ui running run list
    public void removeRunningRunWithUpdate(RunNumber runNumber) {
        removeRunningTeam(runNumber);
        uiController.updateRunningRunList();
    }

    // EFFECTS: add a run to the running run list
    public void addRunningRun(Run run) {
        currentRuns.put(run.getRunNumber(), run);
        dbConnection.addRunningRunDBUpdate(run.getRunNumber());
    }

    // EFFECTS: add a run to the running run list and update the ui running run list
    public void addRunningRunWithUpdate(Run run) {
        addRunningRun(run);
        uiController.addToRunningRunListToTop(run);
    }

    // EFFECTS: add multiple runs to the running team list, input as an array of teams
    public void addRunningRunsFromTeams(ArrayList<Team> teams, int heatNumber) throws NoRunFoundException {
        for (Team team : teams) {
            addRunningRun(team.getRunByHeatNumber(heatNumber));
        }
        uiController.updateRunningRunList();
    }

    // EFFECTS: remove a run from the stopped run list
    public void removeStoppedRun(RunNumber runNumber) {
        stoppedRuns.remove(runNumber);
    }

    // EFFECTS: remove a run from the stopped run list and update ui stopped run list
    public void removeStoppedRunWithUpdate(RunNumber runNumber) {
        removeStoppedRun(runNumber);
        uiController.updateStoppedRunList();
    }

    // EFFECTS: send run back to running run list and remove from stopped list, undo end time too
    public void undoRunStop(int teamNumber, int heatNumber) {
        RunNumber runNumber = new RunNumber(teamNumber, heatNumber);
        timerMap.get(runNumber).cancel();
        Run run = stoppedRuns.get(runNumber);
        run.setCanUndo(false);

        removeStoppedRunWithUpdate(runNumber);
        addRunningRunWithUpdate(run);
    }

    // EFFECTS: add a run to the finished run list and update ui finished run list
    public void addFinishedRun(Run run) {
        finishedRuns.put(run.getRunNumber(), run);
        uiController.addToFinishedRunListToTop(run);
        dbConnection.addFinishedRunNeedDBUpdate(run.getRunNumber());
    }

    // EFFECTS: ends a run, first with team and heat number, second with runNumber
    public void endRun(int teamNumber, int heatNumber) throws NoHeatsException, CouldNotCalculateFinalTimeExcpetion {
        RunNumber runNumber = new RunNumber(teamNumber, heatNumber);
        Run run = currentRuns.get(runNumber);
        run.calculateEndTime(Calendar.getInstance());
        stopRun(run);

        removeRunningRunWithUpdate(runNumber);
        saveData();
    }
    public void endRun(RunNumber runNumber) throws NoHeatsException, CouldNotCalculateFinalTimeExcpetion {

        Run run = currentRuns.get(runNumber);
        run.calculateEndTime(Calendar.getInstance());
        stopRun(run);

        removeRunningRunWithUpdate(runNumber);
        saveData();
    }

    // EFFECTS: save the data to json
    public void saveData() {
        PersistanceWithJackson.toJsonController(this);
        PersistanceWithJackson.toJsonProgram(program);
    }

    // EFFECTS: goes to next heat and does db update
    public void goToNextHeat() {
        getCurrentDay().goToNextHeat();
        dbConnection.updateNextHeat(getCurrentDay().getDayToRun(), getCurrentDay().getAtHeat());
    }


}