package ui;

import com.sun.istack.internal.NotNull;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.*;
import models.exceptions.*;
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
        program = controller.getProgram();
    }

    @FXML
    protected void initialize() {
        updateFinishedRunList();
        updateStoppedRunList();
        updateRunningRunList();

        initStuff();
    }

    // EFFECTS: set running run list to the controller's running run list
    public void updateRunningRunList() {
        ArrayList<HBoxForRunningTeam> hBoxForRunningTeams = new ArrayList<>();
        for (Run run : controller.getCurrentRuns().values()) {
            Team team = run.getTeam();
            HBoxForRunningTeam hBox = new HBoxForRunningTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), run.getSitrep(), run.getHeatNumber(), team.getPoolName(), controller);
            hBox.getStylesheets().add(getClass().getResource("hBoxInList.css").toExternalForm());
            hBoxForRunningTeams.add(hBox);
        }
        runningTeamsList.setItems(FXCollections.observableList(hBoxForRunningTeams));
    }

    // EFFECTS: add a run to the running run list, first is private, second and third are the public ones to use
    public void addToRunningRunList(Run run, boolean top) {
        Team team = run.getTeam();
        ArrayList<HBoxForRunningTeam> hBoxForRunningTeams = new ArrayList<>();
        HBoxForRunningTeam hBox = new HBoxForRunningTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), run.getSitrep(), run.getHeatNumber(), team.getPoolName(), controller);
        hBox.getStylesheets().add(getClass().getResource("hBoxInList.css").toExternalForm());
        hBoxForRunningTeams.add(hBox);
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
    public void updateStoppedRunList() {
        ArrayList<HBoxForFinishedUndoTeam> hBoxForFinishedUndoTeams = new ArrayList<>();
        for (Run run : controller.getStoppedRuns().values()) {
            Team team = run.getTeam();
            HBoxForFinishedUndoTeam hBox = new HBoxForFinishedUndoTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), run.getFinalTime().toString(), controller, run.getHeatNumber());
            hBox.getStylesheets().add(getClass().getResource("hBoxInList.css").toExternalForm());
            hBoxForFinishedUndoTeams.add(hBox);
        }
        undoFinishedTeamList.setItems(FXCollections.observableList(hBoxForFinishedUndoTeams));
    }

    // EFFECTS: add a team to the finished team list, first is private, second and third are the public ones to use
    private void addToStoppedRunList(Run run, boolean top) {
        Team team = run.getTeam();
        ArrayList<HBoxForFinishedUndoTeam> hBoxForFinishedUndoTeams = new ArrayList<>();
        HBoxForFinishedUndoTeam hBox = new HBoxForFinishedUndoTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), run.getFinalTime().toString(), controller, run.getHeatNumber());
        hBox.getStylesheets().add(getClass().getResource("hBoxInList.css").toExternalForm());
        hBoxForFinishedUndoTeams.add(hBox);
        if (top) {
            undoFinishedTeamList.setItems(FXCollections.concat(FXCollections.observableList(hBoxForFinishedUndoTeams), undoFinishedTeamList.getItems()));
        } else {
            undoFinishedTeamList.setItems(FXCollections.concat(undoFinishedTeamList.getItems(), FXCollections.observableList(hBoxForFinishedUndoTeams)));
        }
    }
    public void addToStoppedRunListToTop(@NotNull Run run) {
        addToStoppedRunList(run, true);
    }
    public void addToStoppedRunListToBottom(@NotNull Run run) {
        addToStoppedRunList(run, false);
    }


    // EFFECTS: set the list of final finished teams to all those in the controller´s final finished teams list
    public void updateFinishedRunList() {
        ArrayList<HBoxForFinishedTeam> hBoxForFinishedTeams = new ArrayList<>();
        for (Run run : controller.getFinishedRuns().values()) {
            Team team = run.getTeam();
            HBoxForFinishedTeam hBox =  new HBoxForFinishedTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), run.getFinalTime().toString(), run.getSitrep(), controller, run.getHeatNumber());
            hBox.getStylesheets().add(getClass().getResource("hBoxInList.css").toExternalForm());
            hBoxForFinishedTeams.add(hBox);
        }

        finishedTeamsList.setItems(FXCollections.observableList(hBoxForFinishedTeams));
    }

    // EFFECTS: add a team to the final finished team list, first is private, second and third are the public ones to use
    private void addToFinishedRunList(@NotNull Run run, boolean top) {
        Team team = run.getTeam();
        ArrayList<HBoxForFinishedTeam> hBoxForFinishedTeams = new ArrayList<>();
        HBoxForFinishedTeam hBox = new HBoxForFinishedTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), run.getFinalTime().toString(), run.getSitrep(), controller, run.getHeatNumber());
        hBox.getStylesheets().add(getClass().getResource("hBoxInList.css").toExternalForm());
        hBoxForFinishedTeams.add(hBox);
        if (top) {
            finishedTeamsList.setItems(FXCollections.concat(FXCollections.observableList(hBoxForFinishedTeams), finishedTeamsList.getItems()));
        } else {
            finishedTeamsList.setItems(FXCollections.concat(finishedTeamsList.getItems(), FXCollections.observableList(hBoxForFinishedTeams)));
        }
    }
    public void addToFinishedRunListToTop(@NotNull Run run) {
        addToFinishedRunList(run, true);
    }
    public void addToFinishedRunListToBottom(@NotNull Run run) {
        addToFinishedRunList(run, false);
    }

    // EFFECTS: set the list for teams in the staged list from the controller's staged heat heat
    public void updateStagedHeatTeamList() {
        ArrayList<HBoxForStagedTeam> list = new ArrayList<>();
        for (Team team : controller.getStagedHeat().getTeams().values()) {
            try {
                HBoxForStagedTeam hBoxForStagedTeam = new HBoxForStagedTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), team.getRunByHeatNumber(controller.getStagedHeat().getHeatNumber()).getSitrep(), controller);
                hBoxForStagedTeam.getStylesheets().add(getClass().getResource("hBoxInList.css").toExternalForm());
                list.add(hBoxForStagedTeam);
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
        stageHeatNumber.setText(Integer.toString(controller.getCurrentDay().getAtHeat()));
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
    private Label categoryLabel;

    @FXML
    private Button editHeatButton;

    @FXML
    private ListView<HBoxForFinishedUndoTeam> undoFinishedTeamList;


    @FXML
    private void stageHeat() {
        try {
            controller.setStagedHeat(controller.getCurrentDay().getHeatByHeatNumber(Integer.parseInt(stageHeatNumber.getText())));
        } catch (NoHeatWithIDException e) {
            return; // TODO
        }
        Heat stagedHeat = controller.getStagedHeat();
        if (!stagedHeat.isHasStarted()) {
            updateStagedHeatTeamList();

            timeToStartLabel.setText(stagedHeat.timeToStartString());
            categoryLabel.setText(stagedHeat.getCategory());

        }
    }

    @FXML
    private void startHeat() {
        Heat stagedHeat = controller.getStagedHeat();
        if (stagedHeat != null) {
            stagedHeat.markActualStartTime(Calendar.getInstance());
            controller.addRunningRunsFromTeams(stagedHeat.teamsThatWillRun(), stagedHeat.getHeatNumber());

            controller.getCurrentDay().atNextHeat();
            stageHeatNumber.setText(Integer.toString(controller.getCurrentDay().getAtHeat()));
            stageHeatTeamList.setItems(null);

            timeToStartLabel.setText("Stage Heat to Get Info");
            categoryLabel.setText("Stage Heat to Get Info");
        }
        controller.saveData();
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
                        return;
                    }
                }

            } catch (NoHeatsException e) {
                e.printStackTrace();
            } catch (CouldNotCalculateFinalTimeExcpetion couldNotCalculateFinalTimeExcpetion) {
                couldNotCalculateFinalTimeExcpetion.printStackTrace();
            }
        }
        stopTeamNumber.setText("");
        controller.saveData();
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
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("hBoxInList.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void undoHeatStartAction() {
        if (undoHeatTimer == null) {
            // TODO
            return;
        }
        if (Calendar.getInstance().getTimeInMillis() - undoHeatTimer.getTimeInMillis() < 10000 ) {
            returnTeams(controller.getCurrentDay().getAtHeat() - 1);
            try {
                controller.getCurrentDay().undoLastHeatStart();
            } catch (NoHeatWithIDException | CanNotUndoHeatException e) {
                e.printStackTrace();
                // TODO
            }
            stageHeatNumber.setText(Integer.toString(controller.getCurrentDay().getAtHeat()));
        }
        controller.saveData();
        // TODO exception to taken to much time to undo
    }

    @FXML
    private void backToMainMenuButtonAction() {
        try {
            controller.saveData();
            FXMLLoader root = new FXMLLoader(getClass().getResource("MainPage.fxml"));
            root.setControllerFactory(c -> new MainPageController(controller));
            runningTeamsList.getScene().setRoot(root.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void skipHeatButtonAction() {
        if (undoHeatTimer == null) {
            // TODO
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to skip this heat?");
        alert.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        alert.showAndWait();

        if (alert.getResult() == ButtonType.OK) {
            controller.getCurrentDay().atNextHeat();
            stageHeatNumber.setText(Integer.toString(controller.getCurrentDay().getAtHeat()));

            timeToStartLabel.setText("Stage Heat to Get Info");
            categoryLabel.setText("Stage Heat to Get Info");
        }
    }

}

