package ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import models.Day;
import models.Heat;
import models.Team;
import models.enums.LeagueType;
import models.enums.Sitrep;
import models.enums.TeamType;
import persistance.PersistanceWithJackson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class mainController {

    // special class for the list, has two texts, a drop down and a button at the end
    public class HBoxForRunningTeam extends HBox {
        Label label = new Label();
        Label id = new Label();
        Button button = new Button();
        ComboBox comboBox = new ComboBox();

        HBoxForRunningTeam(String idText, String labelText, String buttonText, Sitrep sitrep) {
            super();

            id.setText(idText);
            id.setMaxWidth(25);
            HBox.setHgrow(id, Priority.ALWAYS);

            label.setText(labelText);
            label.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(label, Priority.ALWAYS);

            button.setText(buttonText);
            button.setOnAction(event -> {
                endTeam(Integer.parseInt(idText));
            });

            comboBox.setItems(FXCollections.observableList(Arrays.asList(Sitrep.values())));
            comboBox.setValue(sitrep.name());
            comboBox.setOnAction(event -> {
                updateStatus(Integer.parseInt(idText), comboBox.getValue().toString());
            });

            this.getChildren().addAll(id, label, button);
        }
    }

    // special class for the list, has two texts and a drop down at the end
    public class HBoxForStageAndDone extends HBox {
        Label label = new Label();
        Label id = new Label();
        ComboBox comboBox = new ComboBox();

        HBoxForStageAndDone(String idText, String teamName, Sitrep sitrep) {
            super();

            id.setText(idText);
            id.setMaxWidth(25);
            HBox.setHgrow(id, Priority.ALWAYS);

            label.setText(teamName);
            label.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(label, Priority.ALWAYS);


            comboBox.setItems(FXCollections.observableList(Arrays.asList(Sitrep.values())));
            comboBox.setValue(sitrep.name());
            comboBox.setOnAction(event -> {
                updateStatus(Integer.parseInt(idText), comboBox.getValue().toString());
            });

            this.getChildren().addAll(id, label, comboBox);
        }
    }

    // special class for the list, has three texts on the horizontal box
    public class HBoxWithThreeStrings extends HBox {
        Label label = new Label();
        Label id = new Label();
        Label thirdLabel = new Label();

        HBoxWithThreeStrings(String idText, String labelText, String thirdText) {
            super();

            id.setText(idText);
            id.setMaxWidth(25);
            HBox.setHgrow(id, Priority.ALWAYS);

            label.setText(labelText);
            label.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(label, Priority.ALWAYS);

            thirdLabel.setText(thirdText);

            this.getChildren().addAll(id, label, thirdLabel);
        }
    }

    // private vars, for now it contains the MainPageController and the Day being used
    private Day day = new Day(Calendar.getInstance(), 1);
    private MainPageController controller = new MainPageController();

    // EFFECTS: makes sure text properties only accept numerical values and initializes the
    //          stageHeatNumber to the current heat available
    private void initStuff() {
        stageHeatNumber.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                    stageHeatNumber.setText(oldValue);
                }
            }
        });

        stopTeamNumber.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                    stageHeatNumber.setText(oldValue);
                }
            }
        });
        stageHeatNumber.setText(Integer.toString(day.getAtHeat()));
    }

    // EFFECTS: returns an observable list with all the teams given
    private ObservableList<HBoxForRunningTeam> getObservableList(ArrayList<Team> teams) {

        ArrayList<HBoxForRunningTeam> list = new ArrayList<>();
        for (Team team : teams) {
            list.add(new HBoxForRunningTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), "Finish",  team.getSitRep()));
        }

        return FXCollections.observableList(list);
    }

    // EFFECTS: take team out of the running team list, set its final time and show in finished team list
    // ASSUME: id is part of a current team running
    private void endTeam(int id) {
        ArrayList<HBoxWithThreeStrings> finalTeamL = new ArrayList<>();
        ArrayList<Team> runningTeams = controller.getRunningTeams();
        for (Team team : runningTeams) {
            if (team.getTeamNumber() == id) {
                team.markEndTime(Calendar.getInstance());
                runningTeams.remove(team);
                controller.addFinishedTeam(team);
                finalTeamL.add(new HBoxWithThreeStrings(Integer.toString(team.getTeamNumber()), team.getTeamName(), team.getDoneHeats().get(team.getDoneHeats().size() - 1).getFinalTime().toString()));
                break;
            }
        }

        runningTeamsList.setItems(getObservableList(runningTeams));
        finishedTeamsList.setItems(FXCollections.concat(FXCollections.observableList(finalTeamL), finishedTeamsList.getItems()));
        saveData();
    }

    // EFFECTS: update the status of a specific team
    private void updateStatus(int id, String sitrep) {
        for (Team team : controller.getStagedHeat().getTeams()) {
            if (team.getTeamNumber() == id) {
                team.setSitRep(Sitrep.valueOf(sitrep));
            }
        }
    }

    // EFFECTS: save the data to json
    private void saveData() {
        PersistanceWithJackson.toJsonDay(day);
        PersistanceWithJackson.toJsonController(controller);
    }

    @FXML
    private ListView<HBoxForRunningTeam> runningTeamsList;

    @FXML
    private TextField stageHeatNumber;

    @FXML
    private ListView<HBoxWithThreeStrings> finishedTeamsList;

    @FXML
    private ListView<HBoxForStageAndDone> stageHeatTeamList;

    @FXML
    private TextField stopTeamNumber;

    @FXML
    private Label timeToStartLabel;

    @FXML
    private Label teamTypeLabel;

    @FXML
    private Label heatTypeLabel;


    @FXML
    private void populateFromSaveData() {
        day = PersistanceWithJackson.toJavaDate();
        controller = PersistanceWithJackson.toJavaController();

        ArrayList<HBoxWithThreeStrings>  threeStrings = new ArrayList<>();
        for (Team team : controller.getFinishedTeams()) {
            threeStrings.add(new HBoxWithThreeStrings(Integer.toString(team.getTeamNumber()), team.getTeamName(), team.getDoneHeats().get(team.getDoneHeats().size() - 1).getFinalTime().toString()));
        }

        ArrayList<HBoxForRunningTeam>  withButtons = new ArrayList<>();
        for (Team team : controller.getRunningTeams()) {
            withButtons.add(new HBoxForRunningTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), "Finish",  team.getSitRep()));
        }


        finishedTeamsList.setItems(FXCollections.observableList(threeStrings));
        runningTeamsList.setItems(FXCollections.observableList(withButtons));
        initStuff();
    }

    @FXML
    private void populateHeatList() {
        for (int i = 0; i < 5; i++) {
            Heat heat = new Heat(Calendar.getInstance(), LeagueType.JFF, TeamType.OPEN, i, day);
            for (int j = 0; j < 3; j++) {
                heat.addTeam(new Team(TeamType.OPEN, LeagueType.JFF, j*i, "Cool Name" + (j+i)));
            }
            day.addHeat(heat);
        }
        day.setAtHeat(1);
        initStuff();

    }

    @FXML
    private void stageHeat() {
        controller.setStagedHeat(day.getHeatByID(Integer.parseInt(stageHeatNumber.getText())));
        Heat stagedHeat = controller.getStagedHeat();
        if (!stagedHeat.isHasStarted()) {
            ArrayList<HBoxForStageAndDone> list = new ArrayList<>();
            for (Team team : stagedHeat.getTeams()) {
                list.add(new HBoxForStageAndDone(Integer.toString(team.getTeamNumber()), team.getTeamName(), team.getSitRep()));
            }

            ObservableList<HBoxForStageAndDone> myObservableList = FXCollections.observableList(list);
            stageHeatTeamList.setItems(myObservableList);

            timeToStartLabel.setText(stagedHeat.timeToStartString());
            teamTypeLabel.setText(stagedHeat.getTeamType().name());
            heatTypeLabel.setText(stagedHeat.getLeagueType().name());

        }
    }

    @FXML
    private void startHeat() {
        if (controller.getStagedHeat() != null) {
            Heat stagedHeat = controller.getStagedHeat();
            stagedHeat.markStartTimeStarted(Calendar.getInstance());

            runningTeamsList.setItems(FXCollections.concat(runningTeamsList.getItems(), getObservableList(stagedHeat.getTeams())));

            controller.getRunningTeams().addAll(stagedHeat.getTeams());
            day.atNextHeat();
            stageHeatNumber.setText(Integer.toString(day.getAtHeat()));
            stageHeatTeamList.setItems(null);

            timeToStartLabel.setText("Stage Heat to Get Info");
            teamTypeLabel.setText("Stage Heat to Get Info");
            heatTypeLabel.setText("Stage Heat to Get Info");
        }
        saveData();
    }

    @FXML
    private void endTeamForButton() {
        if (!stopTeamNumber.getText().equals("")) {
            endTeam(Integer.parseInt(stopTeamNumber.getText()));
        }
        stopTeamNumber.setText("");
    }

}

