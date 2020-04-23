package persistance;

import models.Run;
import models.RunNumber;
import models.exceptions.CriticalErrorException;
import ui.UIAppLogic;

import javax.persistence.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *  The purpose of this class is to keep track of the running and finished runs, as well as the next heat
 *      this class will be polling the data from the database every second, and will use local data as much as possible.
 */

@Entity
public class RunPolling {

// VARIABLES //

    // represents the runs running right now, using RunNumbers
    private ArrayList<RunNumber> activeRuns;

    // represents the finished runs by the RunNumber
    private ArrayList<RunNumber> finishedRuns;

    // represents the next heat to stage, depends on day
    private int nextHeatToStage;

    Connection connection;

    UIAppLogic controller;

    Statement statement;


// CONSTRUCTOR //

    public RunPolling(Connection connection, UIAppLogic controller) throws SQLException {
        this.connection = connection;
        this.controller = controller;
        activeRuns = new ArrayList();
        finishedRuns = new ArrayList();
        statement = connection.createStatement();
    }

// GETTERS AND SETTERS //


    public ArrayList<RunNumber> getActiveRuns() {
        return activeRuns;
    }

    public void setActiveRuns(ArrayList<RunNumber> activeRuns) {
        this.activeRuns = activeRuns;
    }

    public ArrayList<RunNumber> getFinishedRuns() {
        return finishedRuns;
    }

    public void setFinishedRuns(ArrayList<RunNumber> finishedRuns) {
        this.finishedRuns = finishedRuns;
    }

    public int getNextHeatToStage() {
        return nextHeatToStage;
    }

    public void setNextHeatToStage(int nextHeatToStage) {
        this.nextHeatToStage = nextHeatToStage;
    }

// FUNCTIONS //

    /**
     * Grabs the active and finished Run(s) from the database and adds them to the UI lists.
     */
    public void initialDataGrab() throws SQLException, CriticalErrorException {
        ResultSet rs = statement.executeQuery("SELECT * FROM activeRuns");
        while (rs.next()) {
            String str = rs.getString(0);
            RunNumber runNumber = new RunNumber(Integer.parseInt(str.substring(0, str.indexOf("-"))),
                    Integer.parseInt(str.substring(str.indexOf("-") + 1)));

            controller.addActiveRun(
                    controller.getProgram().getAllTeams().get(
                            runNumber.getTeamNumber()).getRunByHeatNumber(runNumber.getHeatNumber()));
        }
        controller.getUiController().updateActiveRunList();

        rs = statement.executeQuery("SELECT * FROM finishedRuns");
        while (rs.next()) {
            String str = rs.getString(0);
            RunNumber runNumber = new RunNumber(Integer.parseInt(str.substring(0, str.indexOf("-"))),
                    Integer.parseInt(str.substring(str.indexOf("-") + 1)));

            controller.addFinishedRun(
                    controller.getProgram().getAllTeams().get(
                            runNumber.getTeamNumber()).getRunByHeatNumber(runNumber.getHeatNumber()));
        }
        controller.getUiController().updateFinishedRunList();
        rs.close();
        // TODO next heat to stage
    }

    /**
     *
     */
    public void poll() {

    }

}
