package ui;

import com.sun.istack.internal.NotNull;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Heat;
import models.Team;
import models.exceptions.AddTeamException;
import sun.security.ssl.Debug;
import ui.widgets.HBoxForEditHeatTeam;
import ui.widgets.HBoxForWaitListTeam;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

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
            hBoxForEditHeatTeams.add(new HBoxForEditHeatTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), team.getTeamType(), controller));
        }
        teamHeatList.setItems(FXCollections.observableList(hBoxForEditHeatTeams));
    }

    public void setWaitListTeams() {
        ArrayList<HBoxForWaitListTeam> hBoxForWaitListTeams = new ArrayList<>();
        for (Team team : controller.getProgram().getWaitList().values()) {
            hBoxForWaitListTeams.add(new HBoxForWaitListTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), team.getTeamType(), controller));
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
    protected void initialize() {
        setTeamHeatListTeams();
        setWaitListTeams();
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
        // TODO get team from database
    }
}
