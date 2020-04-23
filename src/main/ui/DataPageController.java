package ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.*;
import models.exceptions.CriticalErrorException;

public class DataPageController extends UIController{

// VARIABLES //

    private UIAppLogic controller;

// FXML TAGS //

    @FXML
    private TreeTableColumn<Object, String> timeToStartHeatCol;

    @FXML
    private TreeTableColumn<Object, String> actualStartTimeHeatCol;

    @FXML
    private TreeTableColumn<Object, String> heatIDCol;

    @FXML
    private TreeTableView<Object> heatTreeTable;

    @FXML
    private TreeTableColumn<Object, String> categoryHeatCol;

    @FXML
    private Tab heatTab;

    @FXML
    private TreeTableView<Object> teamTreeTable;

    @FXML
    private TreeTableView<Object> runTreeTable;

    @FXML
    private TreeTableColumn<Object, String> heatNumCol;

    @FXML
    private TreeTableColumn<Object, String> extraHeatCol;

    @FXML
    private Tab teamTab;

    @FXML
    private TreeTableColumn<Object, String> teamIDTeamCol;

    @FXML
    private TreeTableColumn<Object, String> teamNameTeamCol;

    @FXML
    private TreeTableColumn<Object, String> teamNumberTeamCol;

    @FXML
    private TreeTableColumn<Object, String> teamUnitTeamCol;

    @FXML
    private TreeTableColumn<Object, String> poolNameTeamCol;

    @FXML
    private TreeTableColumn<Object, String> situationRunCol;

    @FXML
    private TreeTableColumn<Object, String> finalTimeRunCol;

    @FXML
    private TreeTableColumn<Object, String> teamNameRunCol;

    @FXML
    private TreeTableColumn<Object, String> teamNumberRunCol;

    @FXML
    private TreeTableColumn<Object, String> runIDRunCol;

    @FXML
    private TreeTableColumn<Object, String> heatNumberRunCol;

    @FXML
    private TabPane tabePane;

    @FXML
    private Tab runtab;



// CONSTRUCTORS and INITIALIZER //

    public DataPageController(UIAppLogic controller) {
        super(controller);
        this.controller = controller;
    }

    @FXML
    protected void initialize() {
        heatTreeConstructor();
        teamTreeConstructor();
        runTreeConstructor();

        TreeItem<Object> heatRoot = new TreeItem<>();
        TreeItem<Object> teamRoot = new TreeItem<>();
        TreeItem<Object> runRoot = new TreeItem<>();

        addAllHeats(heatRoot);
        addAllTeams(teamRoot);
        addAllRuns(runRoot);

        runTreeTable.setRoot(runRoot);
        runTreeTable.setShowRoot(false);
        // hide this tab
        tabePane.getTabs().remove(runtab);

        teamTreeTable.setRoot(teamRoot);
        teamTreeTable.setShowRoot(false);

        heatTreeTable.setRoot(heatRoot);
        heatTreeTable.setShowRoot(false);
    }

// FUNCTIONS //

    // EFFECTS: adds all the runs from the program
    private void addAllRuns(TreeItem<Object> root) {
        for (Team team : controller.getProgram().getAllTeams().values()) {
            for (Run run : team.getRuns().values()) {
                root.getChildren().add(new TreeItem<>(run));
            }
        }
    }

    // EFFECTS: adds all the teams from the program to the list
    private void addAllTeams(TreeItem<Object> root) {
        for (Team team : controller.getProgram().getAllTeams().values()) {
            TreeItem<Object> teamTreeItem = new TreeItem<>(team);
            TreeItem<Object> tableNamesItem = new TreeItem<>();
            teamTreeItem.getChildren().add(tableNamesItem);
            for (Run run : team.getRuns().values()) {
                Heat heat = run.getHeat();
                TreeItem<Object> heatTreeItem = new TreeItem<>(heat);
                teamTreeItem.getChildren().add(heatTreeItem);
            }
            root.getChildren().add(teamTreeItem);
        }
    }

    // EFFECTS: add all heats to the root
    private void addAllHeats(TreeItem<Object> root) {
        for (Day day : controller.getProgram().getProgramDays().values()) {
            for (Heat heat : day.getHeats().values()) {
                TreeItem<Object> heatTreeItem = new TreeItem<>(heat);
                TreeItem<Object> tableNamesItem = new TreeItem<>();
                heatTreeItem.getChildren().add(tableNamesItem);
                for (Run run : heat.getRuns().values()) {
                    Team team = run.getTeam();
                    TreeItem<Object> teamTreeItem = new TreeItem<>(team);
                    heatTreeItem.getChildren().add(teamTreeItem);
                }
                root.getChildren().add(heatTreeItem);
            }
        }

    }

    // EFFECTS: set all the runTree columns their respective CellValueFactory
    private void runTreeConstructor() {
        runIDRunCol.setCellValueFactory(param -> new SimpleStringProperty(((Run) param.getValue().getValue()).getRunNumber().toString()));
        teamNumberRunCol.setCellValueFactory(param -> new SimpleStringProperty(Integer.toString(((Run) param.getValue().getValue()).getTeam().getTeamNumber())));
        teamNameRunCol.setCellValueFactory(param -> new SimpleStringProperty(((Run) param.getValue().getValue()).getTeam().getTeamName()));
        finalTimeRunCol.setCellValueFactory(param -> {
            Run run = ((Run) param.getValue().getValue());
            if (run.getFinalTime() != null) {
                return new SimpleStringProperty(run.getFinalTime().toString());
            } else {
                return new SimpleStringProperty("N/A");
            }

        });
        situationRunCol.setCellValueFactory(param -> new SimpleStringProperty(((Run) param.getValue().getValue()).getSitrep().name()));
        heatNumberRunCol.setCellValueFactory(param -> new SimpleStringProperty(Integer.toString(((Run) param.getValue().getValue()).getHeat().getHeatNumber())));
    }

    // EFFECTS: set all the heatTree columns their respective CellValueFactory
    private void heatTreeConstructor() {
        heatIDCol.setCellValueFactory(param -> {
            if (param.getValue().getValue() instanceof Heat) {
                return new SimpleStringProperty(Integer.toString(((Heat) param.getValue().getValue()).getHeatID()));
            } else if (param.getValue().getValue() instanceof Team) {
                return new SimpleStringProperty(Integer.toString(((Team) param.getValue().getValue()).getTeamID()));
            } else {
                return new SimpleStringProperty("Team ID");
            }

        });
        actualStartTimeHeatCol.setCellValueFactory( param -> {
            if (param.getValue().getValue() instanceof Heat) {
                return new SimpleStringProperty(((Heat) param.getValue().getValue()).startTimeString());
            } else if (param.getValue().getValue() instanceof Team) {
                try {
                    Heat heat = ((Heat) param.getValue().getParent().getValue());
                    Team team = ((Team) param.getValue().getValue());
                    if (team.getRunByHeatNumber(heat.getHeatNumber()).getFinalTime() != null) {
                        return new SimpleStringProperty(team.getRunByHeatNumber(heat.getHeatNumber()).getFinalTime().toString());
                    } else {
                        return new SimpleStringProperty("Has not run yet.");
                    }

                } catch (CriticalErrorException e) {
                    showAlert(Alert.AlertType.ERROR, "If the error persists please contact an admin. Error: "
                                + e.getMessage(), "There has been an error at actualStartTimeHeatCol cell value factory.", e);
                    return new SimpleStringProperty("");
                }
            } else {
                return new SimpleStringProperty("Final Time");
            }
        });
        timeToStartHeatCol.setCellValueFactory(param -> {
            if (param.getValue().getValue() instanceof Heat) {
                return new SimpleStringProperty(((Heat) param.getValue().getValue()).scheduledTimeString());
            } else if (param.getValue().getValue() instanceof Team) {
                return new SimpleStringProperty(((Team) param.getValue().getValue()).getTeamName());
            } else {
                return new SimpleStringProperty("Team Name");
            }
        });
        categoryHeatCol.setCellValueFactory(param -> {
            if (param.getValue().getValue() instanceof Heat) {
                return new SimpleStringProperty(((Heat) param.getValue().getValue()).getCategory());
            } else if (param.getValue().getValue() instanceof Team) {
                return new SimpleStringProperty(((Team) param.getValue().getValue()).getPoolName());
            } else {
                return new SimpleStringProperty("Pool Name");
            }
        });
        extraHeatCol.setCellValueFactory(param -> {
            if (param.getValue().getValue() instanceof Heat) {
                return new SimpleStringProperty("");
            } else if (param.getValue().getValue() instanceof Team) {
                Heat heat = ((Heat) param.getValue().getParent().getValue());
                Team team = ((Team) param.getValue().getValue());
                try {
                    return new SimpleStringProperty(team.getRunByHeatNumber(heat.getHeatNumber()).getSitrep().name());
                } catch (CriticalErrorException e) {
                    showAlert(Alert.AlertType.ERROR, "If the error persists please contact an admin. Error: "
                            + e.getMessage(), "There has been an error at teamTypeHeatCol cell value factory.", e);
                    return new SimpleStringProperty("");
                }
            }
            return new SimpleStringProperty("Status");
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

    }

    // EFFECTS: set all the teamTree columns their respective CellValueFactory
    private void teamTreeConstructor() {
        teamIDTeamCol.setCellValueFactory(param -> {
            if (param.getValue().getValue() instanceof Team) {
                return new SimpleStringProperty(Integer.toString(((Team) param.getValue().getValue()).getTeamID()));
            } else if (param.getValue().getValue() instanceof Heat) {
                return new SimpleStringProperty(Integer.toString(((Heat) param.getValue().getValue()).getHeatID()));
            } else {
                return new SimpleStringProperty("Heat ID");
            }

        });
        teamNumberTeamCol.setCellValueFactory(param -> {
            if (param.getValue().getValue() instanceof Team) {
                return new SimpleStringProperty(Integer.toString(((Team) param.getValue().getValue()).getTeamNumber()));
            } else if (param.getValue().getValue() instanceof Heat) {
                return new SimpleStringProperty(Integer.toString(((Heat) param.getValue().getValue()).getHeatNumber()));
            } else {
                return new SimpleStringProperty("Heat Number");
            }
        });
        teamNameTeamCol.setCellValueFactory(param -> {
            if (param.getValue().getValue() instanceof Team) {
                return new SimpleStringProperty(((Team) param.getValue().getValue()).getTeamName());
            } else if (param.getValue().getValue() instanceof Heat) {
                return new SimpleStringProperty(((Heat) param.getValue().getValue()).scheduledTimeString());
            } else {
                return new SimpleStringProperty("Start Time");
            }
        });
        poolNameTeamCol.setCellValueFactory(param -> {
            if (param.getValue().getValue() instanceof Team) {
                return new SimpleStringProperty(((Team) param.getValue().getValue()).getPoolName());
            } else if (param.getValue().getValue() instanceof Heat) {
                return new SimpleStringProperty(((Heat) param.getValue().getValue()).getCategory());
            } else {
                return new SimpleStringProperty("Heat Category");
            }
        });
        teamUnitTeamCol.setCellValueFactory(param -> {
            if (param.getValue().getValue() instanceof Team) {
                return new SimpleStringProperty(((Team) param.getValue().getValue()).getTeamUnit());
            } else if (param.getValue().getValue() instanceof Heat) {
                try {
                    Heat heat = ((Heat) param.getValue().getValue());
                    Team team = ((Team) param.getValue().getParent().getValue());
                    if (team.getRunByHeatNumber(heat.getHeatNumber()).getIsDone())
                    {
                        return new SimpleStringProperty(team.getRunByHeatNumber(heat.getHeatNumber()).getFinalTime().toString());
                    } else {
                        return new SimpleStringProperty("Has not run yet.");
                    }
                } catch (CriticalErrorException e) {
                    showAlert(Alert.AlertType.ERROR, "If the error persists please contact an admin. Error: "
                            + e.getMessage(), "There has been an error at teamNameTeamCol cell value factory.", e);
                    return new SimpleStringProperty("");
                }
            } else {
                return new SimpleStringProperty("Final Time on Heat");
            }
        });
    }

// FXML FUNCTIONS //

    // EFFECTS: go back to the main menu within same scene
    @FXML
    private void backToMainMenuButtonAction() {
        backToMainMenu(tabePane.getScene());
    }



}
