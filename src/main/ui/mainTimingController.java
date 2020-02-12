package ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import models.Day;
import models.Heat;
import models.Team;
import models.enums.LeagueType;
import models.enums.Sitrep;
import models.enums.TeamType;
import models.exceptions.*;
import persistance.PersistanceWithJackson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class mainTimingController {

    // Constants used for our widgets
    final double HBoxSpacing = 10;
    final double HBoxSpacingRunning = 12;

    // special class for the list, has two texts, a drop down and a button at the end
    public class HBoxForRunningTeam extends HBox {
        Label teamName = new Label();
        Label id = new Label();
        Label heatNumber = new Label();
        Label teamType = new Label();
        Button button = new Button();
        ComboBox comboBox = new ComboBox();

        HBoxForRunningTeam(String idText, String labelText, String buttonText, Sitrep sitrep, String heatNumberText, String teamTypeText) {
            super(HBoxSpacingRunning);

            heatNumber.setText(heatNumberText);
            heatNumber.setMaxWidth(40);
            HBox.setHgrow(heatNumber, Priority.ALWAYS);

            teamType.setText(teamTypeText);
            teamType.setMaxWidth(45);
            HBox.setHgrow(teamType, Priority.ALWAYS);

            id.setText(idText);
            id.setMaxWidth(40);
            HBox.setHgrow(id, Priority.ALWAYS);

            teamName.setText(labelText);
            teamName.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(teamName, Priority.ALWAYS);

            button.setText(buttonText);
            button.setOnAction(event -> {
                try {
                    endTeam(Integer.parseInt(idText));
                } catch (NoHeatsException e) {
                    e.printStackTrace();
                } catch (CouldNotCalculateFinalTimeExcpetion couldNotCalculateFinalTimeExcpetion) {
                    couldNotCalculateFinalTimeExcpetion.printStackTrace();
                } catch (NoCurrentHeatIDException e) {
                    e.printStackTrace();
                } catch (NoTeamException e) {
                    e.printStackTrace();
                }
            });

            comboBox.setItems(FXCollections.observableList(Arrays.asList(Sitrep.values())));
            comboBox.setValue(sitrep.name());
            comboBox.setOnAction(event -> {
                updateStatus(Integer.parseInt(idText), comboBox.getValue().toString());
            });

            this.getChildren().addAll(heatNumber, id, teamName, teamType, comboBox, button);
        }
    }

    // special class for the list, has two texts and a drop down at the end
    public class HBoxForStage extends HBox {
        Label label = new Label();
        Label id = new Label();
        ComboBox comboBox = new ComboBox();

        HBoxForStage(String idText, String teamName, Sitrep sitrep) {
            super(HBoxSpacing);

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
    public class HBoxForFinishedTeam extends HBox {
        Label label = new Label();
        Label id = new Label();
        Label thirdLabel = new Label();
        ComboBox comboBox = new ComboBox();

        HBoxForFinishedTeam(String idText, String teamName, String finalTime, Sitrep sitrep) {
            super(HBoxSpacing);

            id.setText(idText);
            id.setMaxWidth(25);
            HBox.setHgrow(id, Priority.ALWAYS);

            label.setText(teamName);
            label.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(label, Priority.ALWAYS);

            thirdLabel.setText(finalTime);

            comboBox.setItems(FXCollections.observableList(Arrays.asList(Sitrep.values())));
            comboBox.setValue(sitrep.name());
            comboBox.setOnAction(event -> {
                updateStatus(Integer.parseInt(idText), comboBox.getValue().toString());
            });

            this.getChildren().addAll(id, label, comboBox, thirdLabel);
        }
    }

    // private vars, for now it contains the MainPageController and the Day being used
    private Day day = new Day(Calendar.getInstance(), 1);
    private TimingController controller = new TimingController();

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
            list.add(new HBoxForRunningTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), "Finish",  team.getSitRep(), Integer.toString(team.getCurrentHeatID()), team.getTeamType().name()));
        }

        return FXCollections.observableList(list);
    }

    // EFFECTS: take team out of the running team list, set its final time and show in finished team list
    // ASSUME: id is part of a current team running
    private void endTeam(int id) throws NoHeatsException, CouldNotCalculateFinalTimeExcpetion, NoCurrentHeatIDException, NoTeamException {
        ArrayList<HBoxForFinishedTeam> finalTeamL = new ArrayList<>();
        ArrayList<Team> runningTeams = controller.getRunningTeams();
        for (Team team : runningTeams) {
            if (team.getTeamNumber() == id) {
                team.markEndTime(Calendar.getInstance());
                runningTeams.remove(team);
                controller.addFinishedTeam(team);
                finalTeamL.add(new HBoxForFinishedTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), team.getDoneHeats().get(team.getDoneHeats().size() - 1).getFinalTime().toString(), team.getSitRep()));
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
    private ListView<HBoxForFinishedTeam> finishedTeamsList;

    @FXML
    private ListView<HBoxForStage> stageHeatTeamList;

    @FXML
    private TextField stopTeamNumber;

    @FXML
    private Label timeToStartLabel;

    @FXML
    private Label teamTypeLabel;

    @FXML
    private Label heatTypeLabel;

    @FXML
    private Button editHeatButton;


    @FXML
    private void populateFromSaveData() {
        day = PersistanceWithJackson.toJavaDate();
        controller = PersistanceWithJackson.toJavaController();

        ArrayList<HBoxForFinishedTeam>  threeStrings = new ArrayList<>();
        for (Team team : controller.getFinishedTeams()) {
            threeStrings.add(new HBoxForFinishedTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), team.getDoneHeats().get(team.getDoneHeats().size() - 1).getFinalTime().toString(), team.getSitRep()));
        }

        ArrayList<HBoxForRunningTeam>  withButtons = new ArrayList<>();
        for (Team team : controller.getRunningTeams()) {
            withButtons.add(new HBoxForRunningTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), "Finish",  team.getSitRep(), Integer.toString(team.getCurrentHeatID()), team.getTeamType().name()));
        }


        finishedTeamsList.setItems(FXCollections.observableList(threeStrings));
        runningTeamsList.setItems(FXCollections.observableList(withButtons));
        initStuff();
    }

    @FXML
    private void populateHeatList() {
        for (int i = 1; i < 200; i++) {
            Heat heat = null;
            try {
                heat = new Heat(Calendar.getInstance(), LeagueType.JFF, TeamType.OPEN, i, day);
            } catch (AddHeatException e) {
                e.printStackTrace();
            }
            for (int j = 1; j < 5; j++) {
                try {
                    heat.addTeam(new Team(TeamType.OPEN, LeagueType.JFF, j*i, "Cool Name" + (j+i)));
                } catch (AddTeamException e) {
                    e.printStackTrace();
                }
            }
        }
        day.setAtHeat(1);
        initStuff();

    }

    @FXML
    private void stageHeat() {
        try {
            controller.setStagedHeat(day.getHeatByID(Integer.parseInt(stageHeatNumber.getText())));
        } catch (NoHeatWithIDException e) {
            e.printStackTrace();
        }
        Heat stagedHeat = controller.getStagedHeat();
        if (!stagedHeat.isHasStarted()) {
            ArrayList<HBoxForStage> list = new ArrayList<>();
            for (Team team : stagedHeat.getTeams()) {
                list.add(new HBoxForStage(Integer.toString(team.getTeamNumber()), team.getTeamName(), team.getSitRep()));
            }

            ObservableList<HBoxForStage> myObservableList = FXCollections.observableList(list);
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
            try {
                endTeam(Integer.parseInt(stopTeamNumber.getText()));
            } catch (NoHeatsException e) {
                e.printStackTrace();
            } catch (CouldNotCalculateFinalTimeExcpetion couldNotCalculateFinalTimeExcpetion) {
                couldNotCalculateFinalTimeExcpetion.printStackTrace();
            } catch (NoCurrentHeatIDException e) {
                e.printStackTrace();
            } catch (NoTeamException e) {
                e.printStackTrace();
            }
        }
        stopTeamNumber.setText("");
    }

    @FXML
    private void editHeatAction() {
        Parent root;
        try {
            root = FXMLLoader.load(getClass().getResource("EditHeatPage.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

