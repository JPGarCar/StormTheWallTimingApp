package persistance;

import models.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import ui.TimingController;

import java.io.Serializable;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class dataBaseConnection {

// VARIABLES //

    // controller to change data in
    private TimingController controller;

    // dataBase session
    private Session session;

    // represents the runPolling
    private RunPolling runPolling;

    public dataBaseConnection(TimingController controller) {
        this.controller = controller;
        setDataBaseConnection();

        Runnable runnable = () -> {
            pollUIData();
            compareControllerToRunPolling();
        };

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);

    }

    // EFFECTS: set all the database connection
    private void setDataBaseConnection() {
        Configuration configuration = new Configuration().configure().addAnnotatedClass(RunPolling.class);
        configuration.addAnnotatedClass(RunNumber.class);
        configuration.addAnnotatedClass(Heat.class);
        configuration.addAnnotatedClass(Team.class);
        configuration.addAnnotatedClass(Run.class);
        configuration.addAnnotatedClass(Program.class);
        configuration.configure().addAnnotatedClass(Day.class);

        SessionFactory sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();
    }

    // EFFECTS: polls the database every second to see if there is a change
    public void pollUIData() {
        runPolling = session.get(RunPolling.class, 0);
    }

    // EFFECTS: compares the runPolling with the controller
    private void compareControllerToRunPolling() {
        // compare running teams
        for (RunNumber runNumber : runPolling.getRunningTeamsRunNumbers()) {
            if (controller.getCurrentRuns().get(runNumber) == null) {
                controller.addRunningRun(session.get(Run.class, (Serializable) runNumber));
            }
        }

        for (RunNumber runNumber : runPolling.getFinishedTeamsRunNumbers()) {
            if (controller.getFinishedRuns().get(runNumber) == null) {
                controller.addFinishedRun(session.get(Run.class, (Serializable) runNumber));
            }
        }

        for (Day day : controller.getProgram().getProgramDays().values()) {
            if (day.getAtHeat() != runPolling.getNextHeatToStage().get(day.getDayToRun())) {
                day.setAtHeat(runPolling.getNextHeatToStage().get(day.getDayToRun()));
            }
        }
    }

}
