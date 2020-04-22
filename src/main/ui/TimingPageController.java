package ui;

import com.sun.istack.internal.NotNull;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
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

    /**
     * Updates the UI list activeRunsUIList with the active Run(s) from the {@link UIAppLogic} controller.
     *
     * @helper runListToUIList
     */
    public void updateActiveRunList() {
        activeRunsUIList.setItems(runListToUIList(controller.getActiveRuns().values(), HBoxForActiveRun::new));
    }

    /**
     * Adds a {@link Run} to the bottom of the UI runningTeamsList.
     *
     * @param run   Run to be added to the UI list.
     */
    public void addToActiveRunListToBottom(@NotNull Run run) {
        addToActiveRunList(run, false);
    }

    /**
     * Adds a {@link Run} to the top of the UI runningTeamsList.
     *
     * @param run   Run to be added to the UI list.
     */
    public void addToActiveRunListToTop(@NotNull Run run) {
        addToActiveRunList(run, true);
    }

    /**
     * Helper function to add a {@link Run} to the UI list runningTeamsList,
     * can add it to the top or bottom of the list.
     *
     * @param run   Run to be added to the UI list.
     * @param top   Boolean to know if it should go on the top of the list, if false then goes in the bottom.
     * @helper runToUIList
     */
    private void addToActiveRunList(Run run, boolean top) {
        if (top) {
            activeRunsUIList.setItems(FXCollections.concat(runToUIList(run, HBoxForActiveRun::new), activeRunsUIList.getItems()));
        } else {
            activeRunsUIList.setItems(FXCollections.concat(activeRunsUIList.getItems(), runToUIList(run, HBoxForActiveRun::new)));
        }
    }

    /**
     * Updates the UI list pausedRunsUIList to the {@link Run}(s) in the {@link UIAppLogic}
     * controller's pausedRuns Map.
     *
     * @helper runListToUIList
     */
    public void updatePausedRunList() {
        pausedRunsUIList.setItems(runListToUIList(controller.getPausedRuns().values(), HBoxForPausedRun::new));
    }

    /**
     * Adds a {@link Run} to the top of the UI pausedRunList.
     *
     * @param run   Run to be added to the UI list.
     */
    public void addToPausedRunListToTop(@NotNull Run run) {
        addToPausedRunList(run, true);
    }

    /**
     * Adds a {@link Run} to the bottom of the UI pausedRunList.
     *
     * @param run   Run to be added to the UI list.
     */
    public void addToPausedRunListToBottom(@NotNull Run run) {
        addToPausedRunList(run, false);
    }

    /**
     * Helper function to add a {@link Run} to the UI list pausedRunsUIList,
     * can add it to the top or bottom of the list.
     *
     * @param run   Run to be added to the UI list.
     * @param top   Boolean to know if it should go on the top of the list, if false then goes in the bottom.
     * @helper runToUIList
     */
    private void addToPausedRunList(Run run, boolean top) {
        if (top) {
            pausedRunsUIList.setItems(FXCollections.concat(runToUIList(run, HBoxForPausedRun::new), pausedRunsUIList.getItems()));
        } else {
            pausedRunsUIList.setItems(FXCollections.concat(pausedRunsUIList.getItems(), runToUIList(run, HBoxForPausedRun::new)));
        }
    }

    /**
     * Updates the UI list finishedTeamsList to the {@link Run}(s) in the {@link UIAppLogic}
     * controller's finishedRuns Map.
     *
     * @helper runListToUIList
     */
    public void updateFinishedRunList() {
        finishedRunsUIList.setItems(runListToUIList(controller.getFinishedRuns().values(), HBoxForFinishedRun::new));
    }

    /**
     * Adds a {@link Run} to the top of the UI finishedRunList.
     *
     * @param run   Run to be added to the UI list.
     */
    public void addToFinishedRunListToTop(@NotNull Run run) {
        addToFinishedRunList(run, true);
    }

    /**
     * Adds a {@link Run} to the bottom of the UI finishedRunList.
     *
     * @param run   Run to be added to the UI list.
     */
    public void addToFinishedRunListToBottom(@NotNull Run run) {
        addToFinishedRunList(run, false);
    }

    /**
     * Helper function to add a {@link Run} to the UI list finishedRunsUIList,
     * can add it to the top or bottom of the list.
     *
     * @param run   Run to be added to the UI list.
     * @param top   Boolean to know if it should go on the top of the list, if false then goes in the bottom.
     * @helper runToUIList
     */
    private void addToFinishedRunList(@NotNull Run run, boolean top) {
        if (top) {
            finishedRunsUIList.setItems(FXCollections.concat(runToUIList(run, HBoxForFinishedRun::new), finishedRunsUIList.getItems()));
        } else {
            finishedRunsUIList.setItems(FXCollections.concat(finishedRunsUIList.getItems(), runToUIList(run, HBoxForFinishedRun::new)));
        }
    }

    /**
     * Updates the UI list stagedHeatUIList to the {@link Run}(s) in the {@link UIAppLogic}
     * controller's stagedHeat runs Map.
     *
     * @helper runListToUIList
     */
    public void updateStagedHeatRunList() {
        stagedHeatUIList.setItems(runListToUIList(controller.getStagedHeat().getRuns().values(), HBoxForStagedRun::new));
    }

    /**
     * Creates an observable list to be added to UI lists with the {@link Run}(s) specified
     * and the {@link CustomHBox} specified. The CustomHBox must implement {@link CustomHBoxFactory}.
     *
     * @param runList   The Run(s) to be used to create the CustomHBox(s).
     * @param hBoxFactory   The type of CustomHBox to be used.
     * @param <K>           The type of CustomHBox to be used.
     * @return  A list of the specified CustomHBox(s) as an observableList to be set to a UI list.
     */
    private <K> ObservableList<K> runListToUIList(Iterable<? extends Run> runList, CustomHBoxFactory<K> hBoxFactory) {
        ArrayList<K> hBoxList = new ArrayList<>();

        for (Run run: runList) {
            K hBox = hBoxFactory.newObject(run, controller);
            ((HBox) hBox).getStylesheets().add(getClass().getResource(CSSHBOXRESOURCE).toExternalForm());
            hBoxList.add(hBox);
        }

        return FXCollections.observableList(hBoxList);
    }

    /**
     * Creates an observable list to be added to UI lists with the {@link Run} specified
     * and the {@link CustomHBox} specified. The CustomHBox must implement {@link CustomHBoxFactory}.
     *
     * @param run   The Run to be used to create the CustomHBox.
     * @param hBoxFactory   The type of CustomHBox to be used.
     * @param <K>           The type of CustomHBox to be used.
     * @return  A list of the specified CustomHBox as an observableList to be set to a UI list.
     */
    private <K> ObservableList<K> runToUIList(Run run, CustomHBoxFactory<K> hBoxFactory) {
        ArrayList<K> hBoxList = new ArrayList<>();
        K hBox = hBoxFactory.newObject(run, controller);
        ((HBox) hBox).getStylesheets().add(getClass().getResource(CSSHBOXRESOURCE).toExternalForm());
        hBoxList.add(hBox);
        return FXCollections.observableList(hBoxList);
    }

    /**
     * Makes sure text properties only accept numerical values and initializes the
     * stageHeatNumber to the current heat available
     */
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
    private ListView<HBoxForActiveRun> activeRunsUIList;

    @FXML
    private TextField stageHeatNumber;

    @FXML
    private ListView<HBoxForFinishedRun> finishedRunsUIList;

    @FXML
    private ListView<HBoxForStagedRun> stagedHeatUIList;

    @FXML
    private TextField stopTeamNumber;

    @FXML
    private Label scheduledStartLabel;

    @FXML
    private Label categoryLabel;

    @FXML
    private ListView<HBoxForPausedRun> pausedRunsUIList;

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
                scheduledStartLabel.setText(stagedHeat.scheduledTimeString());
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
            stagedHeatUIList.setItems(null);

            scheduledStartLabel.setText("Stage Heat to Get Info");
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
                scene.getStylesheets().add(getClass().getResource(CSSAPPRESOURCE).toExternalForm());
                scene.getStylesheets().add(getClass().getResource(CSSHBOXRESOURCE).toExternalForm());
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
        backToMainMenu(scheduledStartLabel.getScene());
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

                scheduledStartLabel.setText("Stage Heat to Get Info");
                categoryLabel.setText("Stage Heat to Get Info");
            }
        }
    }

}

