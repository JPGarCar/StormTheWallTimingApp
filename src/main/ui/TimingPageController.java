package ui;

import com.sun.istack.internal.NotNull;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.*;
import models.exceptions.*;
import ui.widgets.*;

import java.io.IOException;
import java.util.*;

public class TimingPageController extends UIController {

// VARIABLES //

    // represents milliseconds to wait before not undoing a heat
    final int UNDOHEATAMOUNT = 20000;

    private UIAppLogic controller;
    private Calendar undoHeatTimer;

// CONTROLLER and INITIALIZER //

    public TimingPageController(UIAppLogic controller) {
        super(controller);
        this.controller = controller;
        controller.setUiController(this);
    }

    @FXML
    protected void initialize() {
        updateFinishedRunList();
        updatePausedRunList();
        updateActiveRunList();

        initStuff();
    }

// FUNCTIONS //

    // EFFECTS: set running run list to the controller's running run list
    public void updateActiveRunList() {
        ArrayList<HBoxForActiveRun> hBoxForActiveRuns = new ArrayList<>();
        for (Run run : controller.getActiveRuns().values()) {
            HBoxForActiveRun hBox = new HBoxForActiveRun(run, controller);
            hBox.getStylesheets().add(getClass().getResource("hBoxInList.css").toExternalForm());
            hBoxForActiveRuns.add(hBox);
        }
        runningTeamsList.setItems(FXCollections.observableList(hBoxForActiveRuns));
    }

    // EFFECTS: add a run to the running run list, first is private, second and third are the public ones to use
    public void addToActiveRunList(Run run, boolean top) {
        ArrayList<HBoxForActiveRun> hBoxForActiveRuns = new ArrayList<>();
        HBoxForActiveRun hBox = new HBoxForActiveRun(run, controller);
        hBox.getStylesheets().add(getClass().getResource("hBoxInList.css").toExternalForm());
        hBoxForActiveRuns.add(hBox);
        if (top) {
            runningTeamsList.setItems(FXCollections.concat(FXCollections.observableList(hBoxForActiveRuns), runningTeamsList.getItems()));
        } else {
            runningTeamsList.setItems(FXCollections.concat(runningTeamsList.getItems(), FXCollections.observableList(hBoxForActiveRuns)));
        }
    }
    public void addToActiveRunListToBottom(@NotNull Run run) {
        addToActiveRunList(run, false);
    }
    public void addToActiveRunListToTop(@NotNull Run run) {
        addToActiveRunList(run, true);
    }

    // EFFECTS: set the UI paused list with the paused Run list
    public void updatePausedRunList() {
        ArrayList<HBoxForPausedRun> hBoxForPausedRuns = new ArrayList<>();
        for (Run run : controller.getPausedRuns().values()) {
            HBoxForPausedRun hBox = new HBoxForPausedRun(run, controller);
            hBox.getStylesheets().add(getClass().getResource("hBoxInList.css").toExternalForm());
            hBoxForPausedRuns.add(hBox);
        }
        undoFinishedTeamList.setItems(FXCollections.observableList(hBoxForPausedRuns));
    }

    // EFFECTS: add a Run to the paused Run list, first is private, second and third are the public ones to use
    private void addToPausedRunList(Run run, boolean top) {
        ArrayList<HBoxForPausedRun> hBoxForPausedRuns = new ArrayList<>();
        HBoxForPausedRun hBox = new HBoxForPausedRun(run, controller);
        hBox.getStylesheets().add(getClass().getResource("hBoxInList.css").toExternalForm());
        hBoxForPausedRuns.add(hBox);
        if (top) {
            undoFinishedTeamList.setItems(FXCollections.concat(FXCollections.observableList(hBoxForPausedRuns), undoFinishedTeamList.getItems()));
        } else {
            undoFinishedTeamList.setItems(FXCollections.concat(undoFinishedTeamList.getItems(), FXCollections.observableList(hBoxForPausedRuns)));
        }
    }
    public void addToPausedRunListToTop(@NotNull Run run) {
        addToPausedRunList(run, true);
    }
    public void addToPausedRunListToBottom(@NotNull Run run) {
        addToPausedRunList(run, false);
    }


    // EFFECTS: set the list of finished Runs to all those in the controller´s final Runs list
    public void updateFinishedRunList() {
        ArrayList<HBoxForFinishedRun> hBoxForFinishedRuns = new ArrayList<>();
        for (Run run : controller.getFinishedRuns().values()) {
            HBoxForFinishedRun hBox =  new HBoxForFinishedRun(run, controller);
            hBox.getStylesheets().add(getClass().getResource("hBoxInList.css").toExternalForm());
            hBoxForFinishedRuns.add(hBox);
        }

        finishedTeamsList.setItems(FXCollections.observableList(hBoxForFinishedRuns));
    }

    // EFFECTS: add a Run to the finished Run list, first is private, second and third are the public ones to use
    private void addToFinishedRunList(@NotNull Run run, boolean top) {
        ArrayList<HBoxForFinishedRun> hBoxForFinishedRuns = new ArrayList<>();
        HBoxForFinishedRun hBox = new HBoxForFinishedRun(run, controller);
        hBox.getStylesheets().add(getClass().getResource("hBoxInList.css").toExternalForm());
        hBoxForFinishedRuns.add(hBox);
        if (top) {
            finishedTeamsList.setItems(FXCollections.concat(FXCollections.observableList(hBoxForFinishedRuns), finishedTeamsList.getItems()));
        } else {
            finishedTeamsList.setItems(FXCollections.concat(finishedTeamsList.getItems(), FXCollections.observableList(hBoxForFinishedRuns)));
        }
    }
    public void addToFinishedRunListToTop(@NotNull Run run) {
        addToFinishedRunList(run, true);
    }
    public void addToFinishedRunListToBottom(@NotNull Run run) {
        addToFinishedRunList(run, false);
    }

    // EFFECTS: set the UI staged Heat Run list to the Runs of the staged heat
    public void updateStagedHeatRunList() {
        ArrayList<HBoxForStagedRun> list = new ArrayList<>();
        for (Run run : controller.getStagedHeat().getRuns().values()) {
            HBoxForStagedRun hBoxForStagedRun = new HBoxForStagedRun(run, controller);
            hBoxForStagedRun.getStylesheets().add(getClass().getResource("hBoxInList.css").toExternalForm());
            list.add(hBoxForStagedRun);
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

// FXML TAGS //

    @FXML
    private ListView<HBoxForActiveRun> runningTeamsList;

    @FXML
    private TextField stageHeatNumber;

    @FXML
    private ListView<HBoxForFinishedRun> finishedTeamsList;

    @FXML
    private ListView<HBoxForStagedRun> stageHeatTeamList;

    @FXML
    private TextField stopTeamNumber;

    @FXML
    private Label timeToStartLabel;

    @FXML
    private Label categoryLabel;

    @FXML
    private ListView<HBoxForPausedRun> undoFinishedTeamList;

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
                updateStagedHeatRunList();
                timeToStartLabel.setText(stagedHeat.scheduledTimeString());
                categoryLabel.setText(stagedHeat.getCategory());
            }

        } catch (CriticalErrorException e) {
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
            stagedHeat.markStartTime(Calendar.getInstance());

            // try to add all the Runs from the heat
            controller.addActiveRunsFromRunList(stagedHeat.listOfRunsWithoutDNS());

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
                for (Run run : controller.getActiveRuns().values()) {
                    if (run.getTeam().getTeamNumber() == Integer.parseInt(stopTeamNumber.getText())) {
                        controller.endRun(run.getRunNumber());
                        stopTeamNumber.setText("");
                        controller.saveData();
                        return;
                    }
                }
            } catch (CriticalErrorException e) {
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

            // ensure the user wants to undo the heat
            Alert alert = showAlert(Alert.AlertType.CONFIRMATION, "Are you sure you want to undo the " +
                    "last heat, heat num: " + (controller.getCurrentDay().getAtHeat() - 1), "Are you sure you want to undo?");

            if (alert.getResult() == ButtonType.OK) {

                try {
                    controller.undoLastHeat();
                } catch (CriticalErrorException |CanNotUndoHeatException e) {
                    showAlert(Alert.AlertType.ERROR, "Please contact an admin if the error persists. Error: " +
                            e.getMessage(), "There has been an error.");
                }
                stageHeatNumber.setText(Integer.toString(controller.getCurrentDay().getAtHeat()));
                controller.saveData();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Can not undo the heat because it has been more than 10 " +
                    "seconds since the last heat started.", "Can not undo heat.");
        }
    }

    // EFFECTS: go back to the main menu window and close this window, will also save data
    @FXML
    private void backToMainMenuButtonAction() {
        backToMainMenu(timeToStartLabel.getScene());
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
            Alert alert = showAlert(Alert.AlertType.CONFIRMATION, "Are you sure you want to skip this heat?", "Confirm Heat Skip");

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

