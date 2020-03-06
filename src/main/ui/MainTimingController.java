package ui;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.internal.NotNull;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.*;
import models.exceptions.*;
import ui.widgets.HBoxForFinishedTeam;
import ui.widgets.HBoxForFinishedUndoTeam;
import ui.widgets.HBoxForRunningTeam;
import ui.widgets.HBoxForStagedTeam;

import java.io.IOException;
import java.util.*;

public class MainTimingController {

// VARIABLES //

    // represents milliseconds to wait before not undoing a heat
    @JsonIgnore
    final int UNDOHEATAMOUNT = 10000;

    private TimingController controller;
    private Calendar undoHeatTimer;

// CONTROLLER and INITIALIZER //

    public MainTimingController(TimingController controller) {
        this.controller = controller;
        controller.setUiController(this);
    }

    @FXML
    protected void initialize() {
        updateFinishedRunList();
        updateStoppedRunList();
        updateRunningRunList();

        initStuff();
    }

// FUNCTIONS //

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
            } catch (NoRunFoundException e) {
                showAlert(Alert.AlertType.ERROR, "Please talk to an admin if the error persists" + e.getMessage(),
                        "There has been an updateStagedHeatTeamList Error");
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

    // EFFECTS: move the teams from heatNumber back to staging list from running list
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

    // EFFECTS: shows an alert
    private void showAlert(Alert.AlertType alertType, String message, String header) {
        Alert alert = new Alert(alertType, message);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        alert.setHeaderText(header);
        alert.show();
    }

// FXML TAGS //

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
    private ListView<HBoxForFinishedUndoTeam> undoFinishedTeamList;

// FXML FUNCTIONS //

    // EFFECTS: stage a heat
    @FXML
    private void stageHeat() {
        // try to stage the heat with the given heat number from stageHeatNumber
        try {
            controller.setStagedHeat(controller.getCurrentDay().getHeatByHeatNumber(Integer.parseInt(stageHeatNumber.getText())));
            Heat stagedHeat = controller.getStagedHeat();

            // make sure the heat has not run yet
            if (!stagedHeat.isHasStarted()) {
                updateStagedHeatTeamList();
                timeToStartLabel.setText(stagedHeat.timeToStartString());
                categoryLabel.setText(stagedHeat.getCategory());
            }

        } catch (NoHeatWithIDException e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage(), "The following error while staging the heat has come up." );
            stageHeatNumber.setText(Integer.toString(controller.getCurrentDay().getAtHeat()));
        }
    }

    // EFFECTS: action button to start a heat, will make sure a heat is staged
    @FXML
    private void startHeat() {
        Heat stagedHeat = controller.getStagedHeat();

        // make sure there is a staged heat
        if (stagedHeat != null) {
            stagedHeat.markActualStartTime(Calendar.getInstance());

            // try to add a run from every team in the heat
            try {
                controller.addRunningRunsFromTeams(stagedHeat.teamsThatWillRun(), stagedHeat.getHeatNumber());
            } catch (NoRunFoundException | NoHeatsException e) {
                showAlert(Alert.AlertType.WARNING, "The heat started properly, however, " +
                        "the following error came up. " + e.getMessage(),
                        "There has been an error while starting the heat.");
            }

            // update controller next heat, staged heat list, and text fields
            controller.goToNextHeat();
            stageHeatNumber.setText(Integer.toString(controller.getCurrentDay().getAtHeat()));
            stageHeatTeamList.setItems(null);

            timeToStartLabel.setText("Stage Heat to Get Info");
            categoryLabel.setText("Stage Heat to Get Info");

            controller.saveData();
            undoHeatTimer = Calendar.getInstance();
            controller.setStagedHeat(null);
        } else {
            showAlert(Alert.AlertType.WARNING, "Please stage a heat to be able to start a heat.",
                        "Could not start heat");
        }
    }

    // EFFECTS: action for the end team button, will end the run by team number
    @FXML
    private void endTeamButtonAction() {
        if (!stopTeamNumber.getText().equals("")) {
            try {
                for (Run run : controller.getCurrentRuns().values()) {
                    if (run.getTeam().getTeamNumber() == Integer.parseInt(stopTeamNumber.getText())) {
                        controller.endRun(run.getRunNumber());
                        stopTeamNumber.setText("");
                        controller.saveData();
                        return;
                    }
                }
            } catch (NoHeatsException | CouldNotCalculateFinalTimeExcpetion e) {
                showAlert(Alert.AlertType.WARNING, "The heat started properly, however, " +
                                "the following error came up. " + e.getMessage(),
                        "There has been an error while ending the team.");
            }
        }
    }

    // EFFECTS: open page to edit a heat
    @FXML
    private void editHeatAction() {
        if (controller.getStagedHeat() == null) {
            showAlert(Alert.AlertType.WARNING, "Please stage a heat to be able to edit a heat",
                    "Could not edit heat");
        } else {
            FXMLLoader root;
            try {
                root = new FXMLLoader(getClass().getResource("EditHeatPage.fxml"));
                Stage stage = new Stage();
                root.setControllerFactory(c -> new EditHeatPageController(controller));
                Scene scene = new Scene(root.load());
                scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
                scene.getStylesheets().add(getClass().getResource("hBoxInList.css").toExternalForm());
                stage.setScene(scene);
                stage.setOnCloseRequest(Event::consume);
                stage.show();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, e.getMessage(), "There has been an error while trying " +
                        "to open the edit heat page. Please talk to an admin if the error persists.");
            }
        }
    }

    // EFFECTS: undo a heat start within UNDOHEATAMOUNT
    @FXML
    private void undoHeatStartAction() {

        // to undo heat, heat must have started within UNDOHEATAMOUNT milliseconds
        if (undoHeatTimer != null && Calendar.getInstance().getTimeInMillis() - undoHeatTimer.getTimeInMillis() < UNDOHEATAMOUNT ) {
            returnTeams(controller.getCurrentDay().getAtHeat() - 1);
            try {
                controller.getCurrentDay().undoLastHeatStart();
            } catch (NoHeatWithIDException | CanNotUndoHeatException e) {
               showAlert(Alert.AlertType.ERROR, "Please contact an admin if the error persists. Error: " +
                       e.getMessage(), "There has been an error.");
            }
            stageHeatNumber.setText(Integer.toString(controller.getCurrentDay().getAtHeat()));
            controller.saveData();
        } else {
            showAlert(Alert.AlertType.WARNING, "Can not undo the heat because it has been more than 10 " +
                    "seconds since the last heat started.", "Can not undo heat.");
        }
    }

    // EFFECTS: go back to the main menu window and close this window, will also safe data
    @FXML
    private void backToMainMenuButtonAction() {
        try {
            controller.saveData();
            FXMLLoader root = new FXMLLoader(getClass().getResource("MainPage.fxml"));
            root.setControllerFactory(c -> new MainPageController(controller));
            runningTeamsList.getScene().setRoot(root.load());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Please contact an admin if the problem persists. Error: "
                    + e.getMessage(), "There has been an error while trying to go back to the main menu.");
        }
    }

    // EFFECTS: skip the staged heat, the heat will just not run
    @FXML
    private void skipHeatButtonAction() {

        // to skip heat, heat must be staged
        if (controller.getStagedHeat() == null) {
            showAlert(Alert.AlertType.WARNING, "Please stage a heat to be able to skip a heat",
                    "Could not skip heat");
        } else {

            // alert to confirm the skip heat function
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to skip this heat?");
            alert.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            alert.setHeaderText("Confirm Heat Skip");
            alert.showAndWait();

            // only proceed if the alert got an ok
            if (alert.getResult() == ButtonType.OK) {
                controller.getCurrentDay().goToNextHeat();
                stageHeatNumber.setText(Integer.toString(controller.getCurrentDay().getAtHeat()));

                timeToStartLabel.setText("Stage Heat to Get Info");
                categoryLabel.setText("Stage Heat to Get Info");
            }
        }
    }

}

