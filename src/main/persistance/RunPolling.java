package persistance;

import models.RunNumber;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.annotations.NaturalId;
import org.hibernate.cfg.Configuration;
import ui.TimingController;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *  The purpose of this class is to keep track of the running and finished runs, as well as the next heat
 *      this class will be polling the data from the database every second, and will use local data as much as possible
 */

@Entity
public class RunPolling {

// VARIABLES //

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    // represents the runs running right now, using RunNumbers
    private ArrayList<RunNumber> runningTeamsRunNumbers;

    // represents the finished runs by the RunNumber
    private ArrayList<RunNumber> finishedTeamsRunNumbers;

    // represents the next heat to stage, depends on day
    private Map<String, Integer> nextHeatToStage;

    // represents the controller
    TimingController controller;

// CONSTRUCTOR //

    public RunPolling(TimingController controller) {
        runningTeamsRunNumbers = new ArrayList();
        finishedTeamsRunNumbers = new ArrayList();
        nextHeatToStage = new HashMap<>();
        this.controller = controller;
    }

// GETTERS AND SETTERS //


    public ArrayList<RunNumber> getRunningTeamsRunNumbers() {
        return runningTeamsRunNumbers;
    }

    public void setRunningTeamsRunNumbers(ArrayList<RunNumber> runningTeamsRunNumbers) {
        this.runningTeamsRunNumbers = runningTeamsRunNumbers;
    }

    public ArrayList<RunNumber> getFinishedTeamsRunNumbers() {
        return finishedTeamsRunNumbers;
    }

    public void setFinishedTeamsRunNumbers(ArrayList<RunNumber> finishedTeamsRunNumbers) {
        this.finishedTeamsRunNumbers = finishedTeamsRunNumbers;
    }

    public Map<String, Integer> getNextHeatToStage() {
        return nextHeatToStage;
    }

    public void setNextHeatToStage(Map<String, Integer> nextHeatToStage) {
        this.nextHeatToStage = nextHeatToStage;
    }

// FUNCTIONS //

    // EFFECTS: update the db
    private void updateDB() {
        Transaction transaction = controller.getSession().beginTransaction();
        controller.getSession().update(this);
        transaction.commit();
    }

    // EFFECTS: add a RunNumber to running runs
    public void addRunToRunning(RunNumber runNumber) {
        runningTeamsRunNumbers.add(runNumber);
        updateDB();
    }

    // EFFECTS: remove a RunNumber from running runs
    public void removeRunFromRunning(RunNumber runNumber) {
        runningTeamsRunNumbers.remove(runNumber);
        updateDB();
    }

    // EFFECTS: add a RunNumber to finished runs
    public void addRunToFinished(RunNumber runNumber) {
        finishedTeamsRunNumbers.add(runNumber);
        updateDB();
    }

    // EFFECTS: remove a runNumber from the finished runs
    public void removeRunFromFinished(RunNumber runNumber) {
        finishedTeamsRunNumbers.remove(runNumber);
        updateDB();
    }

    // EFFECTS: will update the heat to stage for a certain day
    public void updateNextHeat(String day, int nextHeat) {
        nextHeatToStage.remove(day);
        nextHeatToStage.put(day, nextHeat);
        updateDB();
    }



}
