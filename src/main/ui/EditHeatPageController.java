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
import sun.security.ssl.Debug;
import ui.widgets.HBoxForEditHeatTeam;

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
        for (Team team : heat.getTeams()) {
            hBoxForEditHeatTeams.add(new HBoxForEditHeatTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), team.getTeamType(), controller));
        }
        teamHeatList.setItems(FXCollections.observableList(hBoxForEditHeatTeams));
    }

    @FXML
    private ListView<?> availableTeamList;

    @FXML
    private ListView<HBoxForEditHeatTeam> teamHeatList;

    @FXML
    private TextField addTeamIDField;

    @FXML
    protected void initialize() {
        setTeamHeatListTeams();
    }

    @FXML
    public void returnToLastPage() {
        controller.getUiController().updateStagedHeatTeamList();
        Stage stage = (Stage) teamHeatList.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void addTeamByID() {
        //controller.getStagedHeat().addTeam();
        // TODO get team from database
    }
}
