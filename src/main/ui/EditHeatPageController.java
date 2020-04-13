package ui;

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
import models.Team;
import models.exceptions.AddTeamException;
import ui.widgets.HBoxForEditHeatTeam;
import ui.widgets.HBoxForWaitListTeam;

import java.util.ArrayList;

public class EditHeatPageController {

// VARIABLES //

    // Represents heat being edited
    private Heat heat;
    // Represents the timingController being used during the life of the program
    private TimingController controller;

// CONSTRUCTOR and INITIALIZE //

    public EditHeatPageController(@NotNull TimingController controller) {
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

    public void setTeamHeatListTeams() {
        ArrayList<HBoxForEditHeatTeam> hBoxForEditHeatTeams = new ArrayList<>();
        for (Run run : heat.getRuns().values()) {
            hBoxForEditHeatTeams.add(new HBoxForEditHeatTeam(run, controller));
        }
        teamHeatList.setItems(FXCollections.observableList(hBoxForEditHeatTeams));
    }

    public void setWaitListTeams() {
        ArrayList<HBoxForWaitListTeam> hBoxForWaitListTeams = new ArrayList<>();
        for (Run run : controller.getProgram().getWaitList().values()) {
            hBoxForWaitListTeams.add(new HBoxForWaitListTeam(run, controller));
        }
        availableTeamList.setItems(FXCollections.observableList(hBoxForWaitListTeams));
    }

// FXML TAGS //

    @FXML
    private ListView<HBoxForWaitListTeam> availableTeamList;

    @FXML
    private ListView<HBoxForEditHeatTeam> teamHeatList;

    @FXML
    private TextField addTeamIDField;

    @FXML
    private Label heatNumberLabel;

    @FXML
    private Label categoryLabel;

// FXML FUNCTIONS //

    // EFFECTS: deletes this page to go back to timer, update staged heat list
    @FXML
    public void returnToLastPage() {
        controller.getUiController().updateStagedHeatTeamList();
        Stage stage = (Stage) teamHeatList.getScene().getWindow();
        stage.close();
    }

    // EFFECTS: action for add team by teamNumber, will add that team to this heat
    @FXML
    public void addTeamByID() {
        try {
            controller.getStagedHeat().addRunFromTeam(controller.getProgram().getAllTeams().get(Integer.parseInt(addTeamIDField.getText())));
            setTeamHeatListTeams();
        } catch (AddTeamException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "The team number: " + addTeamIDField.getText() +
                    "is not connected to any team. Error: " + e.getMessage());
            alert.setHeaderText("Could not add the team");
            alert.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            alert.show();
        }
    }
}
