package persistance;

import models.Run;
import models.RunNumber;
import models.exceptions.CriticalErrorException;
import ui.UIAppLogic;

import javax.persistence.*;
import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

/**
 *  The purpose of this class is to keep track of the running and finished runs, as well as the next heat
 *      this class will be polling the data from the database every second, and will use local data as much as possible.
 */

@Entity
public class RunPolling extends Observable {

// VARIABLES //

    // represents the next heat to stage, depends on day
    private int nextHeatToStage;

    Connection connection;

    UIAppLogic controller;

    Statement statement;

    ArrayList<Run> newActiveRuns;
    ArrayList<Run> newFinishedRuns;

// CONSTRUCTOR //

    public RunPolling(Connection connection, UIAppLogic controller) throws SQLException {
        this.connection = connection;
        this.controller = controller;
        statement = connection.createStatement();

        newActiveRuns = new ArrayList<>();
        newFinishedRuns = new ArrayList<>();

        addObserver(controller);
    }

// GETTERS AND SETTERS //

    public int getNextHeatToStage() {
        return nextHeatToStage;
    }

    public void setNextHeatToStage(int nextHeatToStage) {
        this.nextHeatToStage = nextHeatToStage;
    }

    public ArrayList<Run> getNewActiveRuns() {
        return newActiveRuns;
    }

    public ArrayList<Run> getNewFinishedRuns() {
        return newFinishedRuns;
    }

    // FUNCTIONS //

    /**
     * Grabs the active and finished Run(s) from the database and adds them to the UI lists.
     *
     * @throws SQLException from executeQuery.
     * @throws CriticalErrorException from runNumberToRun.
     */
    public void initialDataGrab() throws SQLException, CriticalErrorException {
        ResultSet rs = statement.executeQuery("SELECT * FROM activeRuns");
        while (rs.next()) {
            RunNumber runNumber = stringToRunNumber(rs.getString(0));

            controller.addActiveRun(runNumberToRun(runNumber));
        }
        controller.getUiController().updateActiveRunList();

        rs = statement.executeQuery("SELECT * FROM finishedRuns");
        while (rs.next()) {
            RunNumber runNumber = stringToRunNumber(rs.getString(0));

            controller.addFinishedRun(runNumberToRun(runNumber));
        }
        controller.getUiController().updateFinishedRunList();
        rs.close();
        // TODO next heat to stage
    }

    /**
     *  Does one poll sequence. It will grab all the activeRuns and finishedRuns from the db and compare to controller
     *  active and finished runs. If any of the db Run(s) are not found in the program it is added to the newRuns array
     *  list and calls the observers.
     *
     * @throws SQLException from executeQuery.
     * @throws CriticalErrorException from runNumberToRun.
     */
    public void poll() throws SQLException, CriticalErrorException {

        boolean toNotify = false;

        ResultSet rs = statement.executeQuery("SELECT * FROM activeRuns");
        while (rs.next()) {
            RunNumber runNumber = stringToRunNumber(rs.getString(0));

            if (!controller.getActiveRuns().containsKey(runNumber)) {
                toNotify = true;
                newActiveRuns.add(runNumberToRun(runNumber));
            }
        }

        rs = statement.executeQuery("SELECT * FROM finishedRuns");
        while (rs.next()) {
            RunNumber runNumber = stringToRunNumber(rs.getString(0));

            if (!controller.getFinishedRuns().containsKey(runNumber)) {
                toNotify = true;
                newFinishedRuns.add(runNumberToRun(runNumber));
            }
        }

        if (toNotify) {
            setChanged();
            notifyObservers();
        }
        rs.close();
    }

    /**
     * Clears the newFinishedRuns and newActiveRuns list. To be called after the lists are used once to update the
     * UI program.
     */
    public void clearNewLists() {
        newFinishedRuns.clear();
        newActiveRuns.clear();
    }

    /**
     * Creates a RunNumber object from a String containing a RunNumber.
     *
     * @param str   String that contains the RunNumber.
     * @return      The RunNumber crated from the String.
     */
    private RunNumber stringToRunNumber(String str) {
        return new RunNumber(Integer.parseInt(str.substring(0, str.indexOf("-"))),
                Integer.parseInt(str.substring(str.indexOf("-") + 1)));
    }

    /**
     * Grabs a Run from the Program by grabbing a Team from the AllTeam list using the RunNumber's Team number, and
     * then from the Team it grabs the Run by the RunNumber's Heat number.
     *
     * @param runNumber RunNumber to be used to grab a Run from the Program.
     * @return  The Run assocaited to the RunNumber.
     * @throws CriticalErrorException   from getRunByHeatNumber.
     */
    private Run runNumberToRun(RunNumber runNumber) throws CriticalErrorException {
        return controller.getProgram().getAllTeams().get(
                runNumber.getTeamNumber()).getRunByHeatNumber(runNumber.getHeatNumber());
    }

}
