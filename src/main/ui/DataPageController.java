package ui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.util.Callback;
import models.Day;
import models.Heat;
import models.Program;
import models.Team;
import models.enums.LeagueType;
import models.enums.TeamType;
import models.exceptions.AddTeamException;

import java.util.Calendar;
import java.util.Random;

public class DataPageController {

// VARIABLES //

    private TimingController controller;

    @FXML
    private TreeTableColumn<Object, String> timeToStartHeatCol;

    @FXML
    private TreeTableColumn<Object, String> actualStartTimeHeatCol;

    @FXML
    private TreeTableColumn<Object, String> heatIDCol;

    @FXML
    private TreeTableView<Object> heatTreeTable;

    @FXML
    private TreeTableColumn<Object, String> leagueTypeHeatCol;

    @FXML
    private Tab heatTab;

    @FXML
    private TreeTableView<Object> teamTreeTable;

    @FXML
    private TreeTableColumn<Object, String> heatNumCol;

    @FXML
    private TreeTableColumn<Object, String> teamTypeHeatCol;

    @FXML
    private Tab teamTab;

    @FXML
    private TreeTableColumn<Object, String> teamIDTeamCol;

    @FXML
    private TreeTableColumn<Object, String> teamNameTeamCol;

    @FXML
    private TreeTableColumn<Object, String> teamNumberTeamCol;

    @FXML
    private TreeTableColumn<Object, String> teamTypeTeamCol;

    @FXML
    private TreeTableColumn<Object, String> leagueTypeTeamCol;


// CONSTRUCTORS //

    public DataPageController(TimingController controller) {
        this.controller = controller;
    }

    @FXML
    protected void initialize() {
        heatIDCol.setCellValueFactory(param -> {
            if (param.getValue().getValue() instanceof Heat) {
                return new SimpleStringProperty(Integer.toString(((Heat) param.getValue().getValue()).getHeatID()));
            } else if (param.getValue().getValue() instanceof Team) {
                return new SimpleStringProperty(Integer.toString(((Team) param.getValue().getValue()).getTeamID()));
            } else {
                return new SimpleStringProperty("Team ID");
            }

        });
        heatNumCol.setCellValueFactory(param -> {
            if (param.getValue().getValue() instanceof Heat) {
                return new SimpleStringProperty(Integer.toString(((Heat) param.getValue().getValue()).getHeatNumber()));
            } else if (param.getValue().getValue() instanceof Team) {
                return new SimpleStringProperty(Integer.toString(((Team) param.getValue().getValue()).getTeamNumber()));
            } else {
                return new SimpleStringProperty("Team Number");
            }
        });
        actualStartTimeHeatCol.setCellValueFactory( param -> {
            if (param.getValue().getValue() instanceof Heat) {
                return new SimpleStringProperty(((Heat) param.getValue().getValue()).getActualStartTimeString());
            } else if (param.getValue().getValue() instanceof Team) {
                return new SimpleStringProperty(""); // TODO
            } else {
                return new SimpleStringProperty("");
            }
        });
        timeToStartHeatCol.setCellValueFactory(param -> {
            if (param.getValue().getValue() instanceof Heat) {
                return new SimpleStringProperty(((Heat) param.getValue().getValue()).getStartTimeString());
            } else if (param.getValue().getValue() instanceof Team) {
                return new SimpleStringProperty(((Team) param.getValue().getValue()).getTeamName()); // TODO
            } else {
                return new SimpleStringProperty("Team Name");
            }
        });
        leagueTypeHeatCol.setCellValueFactory(param -> {
            if (param.getValue().getValue() instanceof Heat) {
                return new SimpleStringProperty(((Heat) param.getValue().getValue()).getLeagueType().name());
            } else if (param.getValue().getValue() instanceof Team) {
                return new SimpleStringProperty(((Team) param.getValue().getValue()).getTeamLeague().name()); // TODO
            } else {
                return new SimpleStringProperty("Team League");
            }
        });
        teamTypeHeatCol.setCellValueFactory(param -> {
            if (param.getValue().getValue() instanceof Heat) {
                return new SimpleStringProperty(((Heat) param.getValue().getValue()).getTeamType().name());
            } else if (param.getValue().getValue() instanceof Team) {
                return new SimpleStringProperty(((Team) param.getValue().getValue()).getTeamType().name()); // TODO
            } else {
                return new SimpleStringProperty("Team Type");
            }
        });

        TreeItem<Object> root = new TreeItem<>();
        populateHeatList();
        addAllHeats(root);
        heatTreeTable.setRoot(root);
        heatTreeTable.setShowRoot(false);
    }

    private void populateHeatList() {
        Program program = controller.getProgram();

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
    }

    // EFFECTS: add all heats to the root
    private void addAllHeats(TreeItem<Object> root) {
        for (Heat heat : controller.getProgram().getDayFromDayNumber(1).getHeats().values()) {
            TreeItem<Object> heatTreeItem = new TreeItem<>(heat);
            TreeItem<Object> tableNamesItem = new TreeItem<>();
            heatTreeItem.getChildren().add(tableNamesItem);
            for (Team team : heat.getTeams().values()) {
                TreeItem<Object> teamTreeItem = new TreeItem<>(team);
                heatTreeItem.getChildren().add(teamTreeItem);
            }
            root.getChildren().add(heatTreeItem);
        }
    }



}
