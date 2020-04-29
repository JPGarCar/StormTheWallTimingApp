package persistance;

import javafx.beans.property.StringProperty;
import models.RunNumber;
import models.exceptions.CriticalErrorException;
import ui.UIAppLogic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataBaseConnection {

// VARIABLES //

    private UIAppLogic controller;

    private Connection connection;

    private Statement statement;

    private RunPolling runPolling;

// CONSTRUCTORS //

    public DataBaseConnection(UIAppLogic controller, String url, String user, String password)
            throws SQLException, ClassNotFoundException {
        this.controller = controller;
        controller.setDbConnection(this);


        // Make DB connection
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(
                url, user, password
        );

        statement = connection.createStatement();
    }

// FUNCTIONS //

    /**
     * Starts to poll data from the database after grabbing the initial data from it. This will only grab data for
     * the active Run(s) and finished Run(s).
     *
     * @throws SQLException From creating the RunPolling and initialDataGrab.
     * @throws CriticalErrorException From initialDataGrab.
     */
    public void startPolling() throws SQLException, CriticalErrorException {

        runPolling = new RunPolling(connection, controller);
        runPolling.initialDataGrab();


        RunPolling finalRunPolling = runPolling;
        Runnable polling = () -> {
            try {
                finalRunPolling.poll();
            } catch (SQLException | CriticalErrorException e) {
                e.printStackTrace();
            }
        };

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(polling, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Removes the specified RunNumber from the activeRuns db list for other computers to update their UI list.
     *
     * @param runNumber RunNumber to be removed from the activeRuns db list.
     * @throws SQLException From execute statement.
     */
    public void removeActiveRun(RunNumber runNumber) throws SQLException {
        statement.execute("DELETE from activeRuns where RunNumber = " + runNumber.toString() + ";");
    }

    /**
     * Adds the specified RunNumber to the activeRuns db list for other computers to update their UI list.
     *
     * @param runNumber RunNumber to be added to the activeRuns db list.
     * @throws SQLException From execute statement.
     */
    public void addActiveRun(RunNumber runNumber) throws SQLException {
        statement.execute("INSERT INTO activeRuns (RunNumber) VALUES (" + runNumber.toString() + ");");
    }

    /**
     * Removes the specified RunNumber from the finishedRuns db list for other computers to update their UI list.
     *
     * @param runNumber RunNumber to be removed from the finishedRuns db list.
     * @throws SQLException From execute statement.
     */
    public void removeFinishedRun(RunNumber runNumber) throws SQLException {
        statement.execute("DELETE from finishedRuns where RunNumber = " + runNumber.toString() + ";");
    }

    /**
     * Adds the specified RunNumber to the finishedRuns db list for other computers to update their UI list.
     *
     * @param runNumber RunNumber to be added to the finishedRuns db list.
     * @throws SQLException From execute statement.
     */
    public void addFinishedRun(RunNumber runNumber) throws SQLException {
        statement.execute("INSERT INTO finishedRuns (RunNumber) VALUES (" + runNumber.toString() + ");");
    }


}
