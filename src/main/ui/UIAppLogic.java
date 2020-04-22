package ui;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.internal.NotNull;
import javafx.application.Platform;
import models.*;
import models.exceptions.*;
import persistance.PersistenceWithJackson;

import java.util.*;

public class UIAppLogic {

// VARIABLES //

    /**
     * Amount of seconds for Run to be paused.
     */
    final int RUNUNDODELAYTIME = 20000;

    /**
     * The current staged heat if any, null otherwise.
     */
    private Heat stagedHeat;

    /**
     * Map of Run with RunNumber keys. Holds the Run(s) that are currently active, aka the Heat(s) associated to the
     * Run(s) have started.
     */
    private Map<RunNumber, Run> activeRuns;

    /**
     * Map of Run with RunNumber keys. Holds the Run(s) that are paused, aka the team ended the race but the Run
     * can still be undo back to active.
     */
    private Map<RunNumber, Run> pausedRuns;

    /**
     * Map of Run with RunNumber keys. Holds the Run(s) that are finished, aka the team ended the race and the pause
     * timer associated to the Run has ended and so the Run can't be undo back to active.
     */
    private Map<RunNumber, Run> finishedRuns;

    /**
     * The Program that holds all the days and race data.
     */
    @JsonIgnore
    private Program program;

    /**
     * The TimingPageController to be used to update the UI.
     */
    @JsonIgnore
    private TimingPageController uiController;

    /**
     * The EditHeatPageController to be used to update the UI and to have a connection to all CustomHBox(s).
     */
    @JsonIgnore
    private EditHeatPageController editHeatController;

    /**
     * Connection to the Day being used by the timer, aka the Day the race is in.
     */
    @JsonIgnore
    private Day currentDay;

    /**
     * Map of Timer with RunNumber keys. Holds all the Timer(s) used to control paused Run(s).
     */
    private Map<RunNumber, Timer> timerMap;

// CONSTRUCTORS //

    // DUMMY CONSTRUCTOR for Jackson JSON
    public UIAppLogic() {
        activeRuns = new TreeMap<>();
        pausedRuns = new LinkedHashMap<>();
        finishedRuns = new LinkedHashMap<>();
        timerMap = new HashMap<>();
    }

// GETTERS AND SETTERS, used by Jackson JSON //

    public Day getCurrentDay() {
        return currentDay;
    }

    public Program getProgram() {
        return program;
    }

    public Map<RunNumber, Run> getPausedRuns() {
        return pausedRuns;
    }

    public Map<RunNumber, Run> getActiveRuns() {
        return activeRuns;
    }

    public Heat getStagedHeat() {
        return stagedHeat;
    }

    public Map<RunNumber, Run> getFinishedRuns() {
        return finishedRuns;
    }

    public TimingPageController getUiController() {
        return uiController;
    }

    public EditHeatPageController getEditHeatController() {
        return editHeatController;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public void setActiveRuns(Map<RunNumber, Run> activeRuns) {
        this.activeRuns = activeRuns;
    }

    public void setPausedRuns(Map<RunNumber, Run> pausedRuns) {
        this.pausedRuns = pausedRuns;
    }

    public void setStagedHeat(Heat stagedHeat) {
        this.stagedHeat = stagedHeat;
    }

    public void setFinishedRuns(@NotNull Map<RunNumber, Run> finishedRuns) {
        this.finishedRuns = finishedRuns;
    }

    public void setUiController(@NotNull TimingPageController uiController) {
        this.uiController = uiController;
    }

    public void setEditHeatController(@NotNull EditHeatPageController editHeatController) {
        this.editHeatController = editHeatController;
    }

    public void setCurrentDay(Day currentDay) {
        this.currentDay = currentDay;
    }

// FUNCTIONS //

    /**
     * Remove a Run from the activeRun list. Uses the RunNumber associated to the Run to remove it from the list.
     *
     * @param runNumber from the run to be removed from activeRuns list.
     */
    public void removeActiveRun(RunNumber runNumber) {
        activeRuns.remove(runNumber);
    }

    /**
     * Remove a Run from the activeRun list and updates the UI activeRuns list. Uses the RunNumber from the Run
     * to remove it form the list.
     *
     * @param runNumber from the run to be removed from activeRuns list.
     */
    public void removeActiveRunWithUpdate(RunNumber runNumber) {
        removeActiveRun(runNumber);
        uiController.updateActiveRunList();
    }

    /**
     * Add a Run to the activeRuns list.
     *
     * @param run   to be added to the activeRuns list.
     */
    public void addActiveRun(Run run) {
        activeRuns.put(run.getRunNumber(), run);
    }

    /**
     * Add a Run to the activeRuns list and updates the UI activeRuns list.
     *
     * @param run   to be added to the activeRuns list.
     */
    public void addActiveRunWithUpdate(Run run) {
        addActiveRun(run);
        uiController.addToActiveRunListToTop(run);
    }

    /**
     * Add multiple Runs to the activeRuns list. After all the additions it will update the UI activeRuns list.
     *
     * @param runs  ArrayList of Run that will get added to the activeRuns list.
     */
    public void addActiveRunsFromRunList(ArrayList<Run> runs) {
        for (Run run : runs) {
            addActiveRun(run);
        }
        uiController.updateActiveRunList();
    }

    /**
     * Remove a Run from the pausedRuns list. It uses the RunNumber associated to the Run to remove the Run.
     *
     * @param runNumber to be used to remove the Run from the pausedRuns list.
     */
    public void removePausedRun(RunNumber runNumber) {
        pausedRuns.remove(runNumber);
    }

    /**
     * Remove a Run from the pausedRuns list and update the UI pausedRuns list. It uses the RunNumber associated
     * to the Run to remove it.
     *
     * @param runNumber to be used to remove the Run from the pausedRuns list.
     */
    public void removePausedRunWithUpdate(RunNumber runNumber) {
        removePausedRun(runNumber);
        uiController.updatePausedRunList();
    }

    /**
     * Will undo a paused Run. This will move it back to the activeRuns list and remove it from the pausedRuns list.
     * This will also set the Run available to undo, and cancel and remove the timer associated to
     * this run from the timerMap.
     *
     * @param runNumber to be used to undo the Run associated to this RunNumber.
     */
    public void undoPausedRun(RunNumber runNumber) {
        timerMap.get(runNumber).cancel();
        timerMap.remove(runNumber);
        Run run = pausedRuns.get(runNumber);
        run.setCanUndo(false);

        removePausedRunWithUpdate(runNumber);
        addActiveRunWithUpdate(run);
    }

    /**
     * Add a Run to the finishedRuns list and update the UI finishedRuns list.
     *
     * @param run   to be added to the finishedRuns list.
     */
    public void addFinishedRun(Run run) {
        finishedRuns.put(run.getRunNumber(), run);
        uiController.addToFinishedRunListToTop(run);
    }

    /**
     * End a Run. Grabs the Run associated to the RunNumber from activeRuns and calls pauseRun(Run). This will
     * also remove the Run from the activeRuns list.
     *
     * @param runNumber to be used to end the Run associated.
     * @throws CriticalErrorException  from calculateEndTime
     * @helper pauseRunEndRunHelper()
     * @savesData
     */
    public void endRun(RunNumber runNumber) throws CriticalErrorException {

        Run run = activeRuns.get(runNumber);
        run.calculateEndTime(Calendar.getInstance());
        pauseRunEndRunHelper(run);

        removeActiveRunWithUpdate(runNumber);
        saveData();
    }

    /**
     * Will pause a run by adding the run to the pausedRuns map, will also update UI list and
     * add a Timer to the timerMap map.
     *
     * <p>The Timer is used to know for how long the run will be kept in the pausedRun list,
     * it is controlled by the final variable RUNUNDODELAYTIME. Once the timer ends, the run is
     * moved to the finishedRuns list, removed from the pausedRun list and set to notUndo. We also
     * cancel and remove the Timer from the timerMap Map.</p>
     *
     * @param run   run to be paused.
     */
    private void pauseRunEndRunHelper(@NotNull Run run) {
        pausedRuns.put(run.getRunNumber(), run);
        uiController.updatePausedRunList();

        Timer t = new Timer();
        timerMap.put(run.getRunNumber(), t);
        t.schedule( new TimerTask() {
                        @Override
                        public void run() {
                            if (run.getCanUndo()) {
                                run.setCanUndo(false);

                                Platform.runLater(() -> addFinishedRun(run));
                                Platform.runLater(() -> removePausedRunWithUpdate(run.getRunNumber()));
                                t.cancel();
                                timerMap.remove(run.getRunNumber());
                            }
                        }
                    },
                RUNUNDODELAYTIME);
    }

    /**
     * Undo the previous heat. Calls returnRunsDueToUndoHeat(int) to move Run from active to staged. Will
     * also call the currentDay undoLastHeatStart() function.
     *
     * @throws CriticalErrorException  from undoLastHeatStart
     * @throws CanNotUndoHeatException  from undoLastHeatStart
     * @helper returnRunsDueToUndoHeat()
     */
    public void undoLastHeat() throws CriticalErrorException, CanNotUndoHeatException {
        // grab the previous heat -> assumes heat are constant
        int lastHeat = currentDay.getAtHeat() - 1;

        returnRunsDueToUndoHeat(lastHeat);
        currentDay.undoLastHeatStart();
    }

    /**
     * Will remove the Run(s) associated to the heat from the activeRuns list. After all the removes, the
     * UI activeRuns list gets updated.
     *
     * @param heatNumber    to be used to select the heat from which to get Run(s) to remove.
     */
    private void returnRunsDueToUndoHeat(int heatNumber) {
        /*
        // This code does not work, I don't know why! -> there are runs that don't get removed
        for (Run run : controller.getCurrentDay().getHeatByHeatNumber(heatNumber).getRuns().values()) {
            controller.removeRunningTeam(run.getRunNumber());
        }
        */

        List<Object> runList = Arrays.asList(activeRuns.values().toArray());
        for (int i = 0; i < runList.size(); i++) {
            Run run = (Run) runList.get(i);
            if (run.getHeat().getHeatNumber() == heatNumber) {
                removeActiveRun(run.getRunNumber());
            }
        }
        uiController.updateActiveRunList();
    }

    /**
     * Save the program data by using JSON files. Will save both this controller and the Program.
     */
    public void saveData() {
        PersistenceWithJackson.toJsonController(this);
        PersistenceWithJackson.toJsonProgram(program);
    }

    /**
     * Will call the currentDay goToNextHeat() function to move to the next heat.
     */
    public void goToNextHeat() {
        getCurrentDay().goToNextHeat();
    }


}