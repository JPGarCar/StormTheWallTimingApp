package ui;

import com.sun.istack.internal.NotNull;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Heat;
import models.Team;
import models.exceptions.AddTeamException;
import ui.widgets.HBoxForEditHeatTeam;
import ui.widgets.HBoxForWaitListTeam;

import java.util.ArrayList;

public class EditHeatPageController {

    private Heat heat;
    private TimingController controller;

    public EditHeatPageController(@NotNull TimingController controller) {
        this.heat = controller.getStagedHeat();
        this.controller = controller;
        controller.setEditHeatController(this);
    }

    public void setTeamHeatListTeams() {
        ArrayList<HBoxForEditHeatTeam> hBoxForEditHeatTeams = new ArrayList<>();
        for (Team team : heat.getTeams().values()) {
            hBoxForEditHeatTeams.add(new HBoxForEditHeatTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), team.getPoolName(), controller));
        }
        teamHeatList.setItems(FXCollections.observableList(hBoxForEditHeatTeams));
    }

    public void setWaitListTeams() {
        ArrayList<HBoxForWaitListTeam> hBoxForWaitListTeams = new ArrayList<>();
        for (Team team : controller.getProgram().getWaitList().values()) {
            hBoxForWaitListTeams.add(new HBoxForWaitListTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), team.getPoolName(), controller));
        }
        availableTeamList.setItems(FXCollections.observableList(hBoxForWaitListTeams));
    }

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

    @FXML
    protected void initialize() {
        setTeamHeatListTeams();
        setWaitListTeams();
        heatNumberLabel.setText(Integer.toString(heat.getHeatNumber()));
        categoryLabel.setText(heat.getCategory());
    }

    @FXML
    public void returnToLastPage() {
        controller.getUiController().updateStagedHeatTeamList();
        Stage stage = (Stage) teamHeatList.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void addTeamByID() {
        try {
            controller.getStagedHeat().addTeam(controller.getProgram().getAllTeams().get(Integer.parseInt(addTeamIDField.getText())));
            setTeamHeatListTeams();
        } catch (AddTeamException e) {
            e.printStackTrace();
        }
    }
}
