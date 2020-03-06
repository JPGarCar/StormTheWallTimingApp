package persistance;

import models.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import ui.TimingController;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DataBaseConnection {

// VARIABLES //

    // controller to change data in
    private TimingController controller;

    // dataBase session
    private Session session;

    // represents the runPolling
    private RunPolling runPolling;

// CONSTRUCTOR //

    public DataBaseConnection(TimingController controller) {
        this.controller = controller;
        controller.setDbConnection(this);
        setDataBaseConnection();

        runPolling = session.get(RunPolling.class, 0);

        if (runPolling == null) {
            runPolling = new RunPolling();
        }
    }

// GETTERS AND SETTERS //

    public RunPolling getRunPolling() {
        return runPolling;
    }

// FUNCTIONS //

    // EFFECTS: uploads all the local data to db
    public void uploadAllData() {
        Transaction transaction = session.beginTransaction();
        for (Day day : controller.getProgram().getProgramDays().values()) {
            session.update(day);
        }
        transaction.commit();
    }

    // EFFECTS: download all the db data
    public void downloadAllData() {
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Day> criteriaQuery = builder.createQuery(Day.class);
        Root<Day> dayRoot = criteriaQuery.from(Day.class);
        criteriaQuery.select(dayRoot);
        List<Day> days = session.createQuery(criteriaQuery).getResultList();

        for (Day day : days) {
            controller.getProgram().addDay(day);
        }
    }

    // EFFECTS: start polling data
    public void startPolling() {
        Runnable runnable = () -> {
            pollUIData();
            compareControllerToRunPolling();
        };

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);
    }

    // EFFECTS: set all the database connection
    private void setDataBaseConnection() {
        Configuration configuration = new Configuration().configure().addAnnotatedClass(Program.class);
        configuration.addAnnotatedClass(Day.class);
        configuration.addAnnotatedClass(Heat.class);
        configuration.addAnnotatedClass(Team.class);
        configuration.addAnnotatedClass(Run.class);
        configuration.addAnnotatedClass(RunNumber.class);
        configuration.addAnnotatedClass(RunPolling.class);

        SessionFactory sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();
    }

    // EFFECTS: polls the database every second to see if there is a change
    private void pollUIData() {
        runPolling = session.get(RunPolling.class, 0);
    }

    // EFFECTS: compares the runPolling with the controller
    private void compareControllerToRunPolling() {
        // compare running teams
        for (RunNumber runNumber : runPolling.getRunningTeamsRunNumbers()) {
            if (controller.getCurrentRuns().get(runNumber) == null) {
                controller.addRunningRunWithUpdate(session.get(Run.class, (Serializable) runNumber));
            }
        }

        // compare finished teams
        for (RunNumber runNumber : runPolling.getFinishedTeamsRunNumbers()) {
            if (controller.getFinishedRuns().get(runNumber) == null) {
                controller.addFinishedRun(session.get(Run.class, (Serializable) runNumber));
            }
        }

        // compare next heat
        for (Day day : controller.getProgram().getProgramDays().values()) {
            if (day.getAtHeat() != runPolling.getNextHeatToStage().get(day.getDayToRun())) {
                day.setAtHeat(runPolling.getNextHeatToStage().get(day.getDayToRun()));
            }
        }
    }

    // EFFECTS: commits the runPolling
    private void commitRunPolling() {
        Transaction transaction = session.beginTransaction();
        session.update(runPolling);
        transaction.commit();
    }

    // effect started a team
    public void addRunningRunDBUpdate(RunNumber runNumber) {
        runPolling.addRunToRunning(runNumber);
        commitRunPolling();
    }

    // effect ended a team
    public void removeRunningRunDBUpdate(RunNumber runNumber) {
        runPolling.removeRunFromRunning(runNumber);
        commitRunPolling();
    }

    // effect started a team
    public void addFinishedRunNeedDBUpdate(RunNumber runNumber) {
        runPolling.addRunToFinished(runNumber);
        commitRunPolling();
    }

    // update run
    public void runDBUpdate(Run run) {
        Transaction transaction = session.beginTransaction();
        session.update(run);
        transaction.commit();
    }

    // EFFECTS: updates the next heat
    public void updateNextHeat(String day, int heatNumber) {
        runPolling.updateNextHeat(day, heatNumber);
        commitRunPolling();
    }

}
