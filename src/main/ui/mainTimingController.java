package ui;

import com.sun.istack.internal.NotNull;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.*;
import models.enums.LeagueType;
import models.enums.TeamType;
import models.exceptions.*;
import persistance.PersistanceWithJackson;
import ui.widgets.HBoxForFinishedTeam;
import ui.widgets.HBoxForFinishedUndoTeam;
import ui.widgets.HBoxForRunningTeam;
import ui.widgets.HBoxForStagedTeam;

import java.io.IOException;
import java.util.*;

public class mainTimingController {


    // private vars, for now it contains the MainPageController and the Day being used

    private TimingController controller;
    private Program program;
    private Calendar undoHeatTimer;

    public mainTimingController(TimingController controller) {
        this.controller = controller;
        controller.setUiController(this);
    }

    // EFFECTS: set running run list to the controller's running run list
    public void updateRunningRunList() {
        ArrayList<HBoxForRunningTeam> hBoxForRunningTeams = new ArrayList<>();
        for (Run run : controller.getCurrentRuns().values()) {
            Team team = run.getTeam();
            hBoxForRunningTeams.add(new HBoxForRunningTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), run.getSitrep(), run.getHeatNumber(), team.getTeamType().name(), controller));
        }
        runningTeamsList.setItems(FXCollections.observableList(hBoxForRunningTeams));
    }

    // EFFECTS: add a run to the running run list, first is private, second and third are the public ones to use
    public void addToRunningRunList(Run run, boolean top) {
        Team team = run.getTeam();
        ArrayList<HBoxForRunningTeam> hBoxForRunningTeams = new ArrayList<>();
        hBoxForRunningTeams.add(new HBoxForRunningTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), run.getSitrep(), run.getHeatNumber(), team.getTeamType().name(), controller));
        if (top) {
            runningTeamsList.setItems(FXCollections.concat(FXCollections.observableList(hBoxForRunningTeams), runningTeamsList.getItems()));
        } else {
            runningTeamsList.setItems(FXCollections.concat(runningTeamsList.getItems(), FXCollections.observableList(hBoxForRunningTeams)));
        }
    }
    public void addToRunningRunListToBottom(@NotNull Run run) {
        addToRunningRunList(run, false);
    }
    public void addToRunningRunListToTop(@NotNull Run run) {
        addToRunningRunList(run, true);
    }

    // EFFECTS: set finished but possible undo team list to the controller's finished team list
    public void updateFinishedRunList() {
        ArrayList<HBoxForFinishedUndoTeam> hBoxForFinishedUndoTeams = new ArrayList<>();
        for (Run run : controller.getStoppedRuns().values()) {
            Team team = run.getTeam();
            hBoxForFinishedUndoTeams.add(new HBoxForFinishedUndoTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), run.getFinalTime().toString(), controller, run.getHeatNumber()));
        }
        undoFinishedTeamList.setItems(FXCollections.observableList(hBoxForFinishedUndoTeams));
    }

    // EFFECTS: add a team to the finished team list, first is private, second and third are the public ones to use
    private void addToFinishedRunList(Run run, boolean top) {
        Team team = run.getTeam();
        ArrayList<HBoxForFinishedUndoTeam> hBoxForFinishedUndoTeams = new ArrayList<>();
        hBoxForFinishedUndoTeams.add(new HBoxForFinishedUndoTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), run.getFinalTime().toString(), controller, run.getHeatNumber()));
        if (top) {
            undoFinishedTeamList.setItems(FXCollections.concat(FXCollections.observableList(hBoxForFinishedUndoTeams), undoFinishedTeamList.getItems()));
        } else {
            undoFinishedTeamList.setItems(FXCollections.concat(undoFinishedTeamList.getItems(), FXCollections.observableList(hBoxForFinishedUndoTeams)));
        }
    }
    public void addToFinishedRunListToTop(@NotNull Run run) {
        addToFinishedRunList(run, true);
    }
    public void addToFinishedRunListToBottom(@NotNull Run run) {
        addToFinishedRunList(run, false);
    }


    // EFFECTS: set the list of final finished teams to all those in the controller´s final finished teams list
    public void updateFinalFinishedRunList() {
        ArrayList<HBoxForFinishedTeam> hBoxForFinishedTeams = new ArrayList<>();
        for (Run run : controller.getFinishedRuns().values()) {
            Team team = run.getTeam();
            hBoxForFinishedTeams.add(new HBoxForFinishedTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), run.getFinalTime().toString(), run.getSitrep(), controller, run.getHeatNumber()));
        }

        finishedTeamsList.setItems(FXCollections.observableList(hBoxForFinishedTeams));
    }

    // EFFECTS: add a team to the final finished team list, first is private, second and third are the public ones to use
    private void addToFinalFinishedRunList(@NotNull Run run, boolean top) {
        Team team = run.getTeam();
        ArrayList<HBoxForFinishedTeam> hBoxForFinishedTeams = new ArrayList<>();
        hBoxForFinishedTeams.add(new HBoxForFinishedTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), run.getFinalTime().toString(), run.getSitrep(), controller, run.getHeatNumber()));
        if (top) {
            finishedTeamsList.setItems(FXCollections.concat(FXCollections.observableList(hBoxForFinishedTeams), finishedTeamsList.getItems()));
        } else {
            finishedTeamsList.setItems(FXCollections.concat(finishedTeamsList.getItems(), FXCollections.observableList(hBoxForFinishedTeams)));
        }
    }
    public void addToFinalFinishedRunListToTop(@NotNull Run run) {
        addToFinalFinishedRunList(run, true);
    }
    public void addToFinalFinishedRunListToBottom(@NotNull Run run) {
        addToFinalFinishedRunList(run, false);
    }

    // EFFECTS: set the list for teams in the staged list from the controller's staged heat heat
    public void updateStagedHeatTeamList() {
        ArrayList<HBoxForStagedTeam> list = new ArrayList<>();
        for (Team team : controller.getStagedHeat().getTeams().values()) {
            try {
                list.add(new HBoxForStagedTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), team.getRunByHeatNumber(controller.getStagedHeat().getHeatNumber()).getSitrep(), controller));
            } catch (NoTeamHeatException e) {
                e.printStackTrace();
            }
        }

        stageHeatTeamList.setItems(FXCollections.observableList(list));
    }

    // EFFECTS: makes sure text properties only accept numerical values and initializes the
    //          stageHeatNumber to the current heat available
    private void initStuff() {
        stageHeatNumber.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                stageHeatNumber.setText(oldValue);
            }
        });

        stopTeamNumber.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                stageHeatNumber.setText(oldValue);
            }
        });
        stageHeatNumber.setText(Integer.toString(program.getProgramDays().get(1).getAtHeat()));
    }

    // EFFECTS: save the data to json
    private void saveData() {
        PersistanceWithJackson.toJsonDay(program.getProgramDays().get(1));
        PersistanceWithJackson.toJsonController(controller);
    }

    // EFFECTS: move the teams from heat number back to staging from running list
    private void returnTeams(int heatNumber) {
        List<Object> runList = Arrays.asList(controller.getCurrentRuns().values().toArray());
        for (int i = 0; i < runList.size(); i++) {
            Run run = (Run) runList.get(i);
            if (run.getHeatNumber() == heatNumber) {
                controller.removeRunningTeam(run.getRunNumber());
            }
        }
        updateRunningRunList();
    }

    @FXML
    private ListView<HBoxForRunningTeam> runningTeamsList;

    @FXML
    private TextField stageHeatNumber;

    @FXML
    private ListView<HBoxForFinishedTeam> finishedTeamsList;

    @FXML
    private ListView<HBoxForStagedTeam> stageHeatTeamList;

    @FXML
    private TextField stopTeamNumber;

    @FXML
    private Label timeToStartLabel;

    @FXML
    private Label teamTypeLabel;

    @FXML
    private Label heatTypeLabel;

    @FXML
    private Button editHeatButton;

    @FXML
    private ListView<HBoxForFinishedUndoTeam> undoFinishedTeamList;


    @FXML
    private void populateFromSaveData() {
        program = controller.getProgram();
        Day day = PersistanceWithJackson.toJavaDate();
        program.getProgramDays().put(day.getDayNumber(), day);
        controller = PersistanceWithJackson.toJavaController();

        updateFinishedRunList();
        updateRunningRunList();

        initStuff();
    }

    @FXML
    private void populateHeatList() {
        program = controller.getProgram();
        Day day = new Day(Calendar.getInstance(), 1);
        program.addDay(day);
        Random random = new Random();
        for (int i = 1; i <= 4; i++) {
            Heat heat = null;
            int number = random.nextInt(100);
            heat = new Heat(Calendar.getInstance(), LeagueType.JFF, TeamType.OPEN, i, day, number);
            for (int j = 1; j <= 3; j++) {
                try {
                    number = random.nextInt(2000);
                    heat.addTeam(program.createTeam(TeamType.OPEN, LeagueType.JFF, number, "Cool Name" + (number), number));
                } catch (AddTeamException e) {
                    e.printStackTrace();
                }
            }
        }
        day.setAtHeat(1);
        initStuff();

    }

    @FXML
    private void stageHeat() {
        try {
            controller.setStagedHeat(program.getProgramDays().get(1).getHeatByHeatNumber(Integer.parseInt(stageHeatNumber.getText())));
        } catch (NoHeatWithIDException e) {
            e.printStackTrace();
        }
        Heat stagedHeat = controller.getStagedHeat();
        if (!stagedHeat.isHasStarted()) {
            updateStagedHeatTeamList();

            timeToStartLabel.setText(stagedHeat.timeToStartString());
            teamTypeLabel.setText(stagedHeat.getTeamType().name());
            heatTypeLabel.setText(stagedHeat.getLeagueType().name());

        }
    }

    @FXML
    private void startHeat() {
        Heat stagedHeat = controller.getStagedHeat();
        if (stagedHeat != null) {
            stagedHeat.markActualStartTime(Calendar.getInstance());
            controller.addRunningRunsFromTeams(stagedHeat.getTeamsThatWillRun(), stagedHeat.getHeatNumber());


            program.getProgramDays().get(1).atNextHeat();
            stageHeatNumber.setText(Integer.toString(program.getProgramDays().get(1).getAtHeat()));
            stageHeatTeamList.setItems(null);

            timeToStartLabel.setText("Stage Heat to Get Info");
            teamTypeLabel.setText("Stage Heat to Get Info");
            heatTypeLabel.setText("Stage Heat to Get Info");
        }
        //saveData();
        undoHeatTimer = Calendar.getInstance();
        controller.setStagedHeat(null);
    }

    @FXML
    private void endTeamForButton() {
        if (!stopTeamNumber.getText().equals("")) {
            try {
                for (Run run : controller.getCurrentRuns().values()) {
                    if (run.getTeam().getTeamNumber() == Integer.parseInt(stopTeamNumber.getText())) {
                        controller.endRun(run.getRunNumber());
                    }
                }

            } catch (NoHeatsException e) {
                e.printStackTrace();
            } catch (CouldNotCalculateFinalTimeExcpetion couldNotCalculateFinalTimeExcpetion) {
                couldNotCalculateFinalTimeExcpetion.printStackTrace();
            }
        }
        stopTeamNumber.setText("");
    }

    @FXML
    private void editHeatAction() {
        if (controller.getStagedHeat() == null) {
            return;
        }
        FXMLLoader root;
        try {
            root = new FXMLLoader(getClass().getResource("EditHeatPage.fxml"));
            Stage stage = new Stage();
            root.setControllerFactory(c -> new EditHeatPageController(controller));
            Scene scene = new Scene(root.load());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void undoHeatStartAction() {
        if (Calendar.getInstance().getTimeInMillis() - undoHeatTimer.getTimeInMillis() < 10000 ) {
            returnTeams(program.getProgramDays().get(1).getAtHeat() - 1);
            try {
                program.getProgramDays().get(1).undoLastHeatStart();
            } catch (NoHeatWithIDException | CanNotUndoHeatException e) {
                e.printStackTrace();
            }
            stageHeatNumber.setText(Integer.toString(program.getProgramDays().get(1).getAtHeat()));
        }
        // TODO exception to taken to much time to undo
    }

    @FXML
    private void backToMainMenuButtonAction() {
        try {
            FXMLLoader root = new FXMLLoader(getClass().getResource("MainPage.fxml"));
            root.setControllerFactory(c -> new MainPageController(controller));
            runningTeamsList.getScene().setRoot(root.load());
            // TODO add save functionality
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

