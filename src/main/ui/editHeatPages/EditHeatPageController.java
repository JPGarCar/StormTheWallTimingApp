package ui.editHeatPages;

import com.sun.istack.internal.NotNull;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Heat;
import models.Run;
import models.exceptions.AddRunException;
import ui.UIAppLogic;
import ui.UIController;
import ui.widgets.HBoxForEditHeatRun;
import ui.widgets.HBoxForWaitListRun;

import java.util.ArrayList;

public class EditHeatPageController extends UIController {

// VARIABLES //

    // Represents heat being edited
    private Heat heat;
    // Represents the timingController being used during the life of the program
    private UIAppLogic controller;

// CONSTRUCTOR and INITIALIZE //

    public EditHeatPageController(@NotNull UIAppLogic controller) {
        super(controller);
        this.heat = controller.getStagedHeat();
        this.controller = controller;
        controller.setEditHeatController(this);
    }

    @FXML
    protected void initialize() {
        setTeamHeatListTeams();
        setWaitListTeams();
        heatNumberLabel.setText(Integer.toString(heat.getHeatNumber()));
        categoryLabel.setText(heat.getCategory());
    }

// FUNCTIONS //

    /**
     * Updates the UI TeamHeatList in the edit heat page by grabbing the {@link Run}(s) assocaited to
     * the {@link Heat} and creating a {@link HBoxForEditHeatRun} for each Run. The HBoxes are added to the UI list.
     */
    public void setTeamHeatListTeams() {
        ArrayList<HBoxForEditHeatRun> hBoxForEditHeatRuns = new ArrayList<>();
        for (Run run : heat.getRuns().values()) {
            hBoxForEditHeatRuns.add(new HBoxForEditHeatRun(run, controller));
        }
        teamHeatList.setItems(FXCollections.observableList(hBoxForEditHeatRuns));
    }

    /**
     * Updates the UI availableTeamList in the edit heat page by grabbing the {@link Run}(s)
     * in the {@link models.Program}'s wait list Map and creating a {@link HBoxForWaitListRun} for each Run.
     */
    public void setWaitListTeams() {
        ArrayList<HBoxForWaitListRun> hBoxForWaitListRuns = new ArrayList<>();
        for (Run run : controller.getProgram().getWaitList().values()) {
            hBoxForWaitListRuns.add(new HBoxForWaitListRun(run, controller));
        }
        availableTeamList.setItems(FXCollections.observableList(hBoxForWaitListRuns));
    }

// FXML TAGS //

    @FXML
    private ListView<HBoxForWaitListRun> availableTeamList;

    @FXML
    private ListView<HBoxForEditHeatRun> teamHeatList;

    @FXML
    private TextField addTeamIDField;

    @FXML
    private Label heatNumberLabel;

    @FXML
    private Label categoryLabel;

// FXML FUNCTIONS //

    // EFFECTS: deletes this page to go back to timer, update staged heat list
    @FXML
    private void returnToLastPage() {
        controller.getUiController().updateStagedHeatRunList();
        Stage stage = (Stage) teamHeatList.getScene().getWindow();
        stage.close();
    }

    // EFFECTS: action for add team by teamNumber, will add that team to this heat
    @FXML
    private void addTeamByID() {
        try {
            controller.getStagedHeat().addRunFromTeam(controller.getProgram().getAllTeams().get(Integer.parseInt(addTeamIDField.getText())));
            setTeamHeatListTeams();
        } catch (AddRunException e) {
            showAlert(Alert.AlertType.WARNING, "The team number: " + addTeamIDField.getText() +
                    "is not connected to any team. Error: " + e.getMessage(), "Could not add the team", e);
        }
    }
}
