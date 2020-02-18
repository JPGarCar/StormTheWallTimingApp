package ui;

import com.sun.istack.internal.NotNull;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Day;
import models.Heat;
import models.Program;
import models.Team;
import models.enums.LeagueType;
import models.enums.TeamType;
import models.exceptions.*;
import persistance.PersistanceWithJackson;
import ui.widgets.HBoxForFinishedTeam;
import ui.widgets.HBoxForFinishedUndoTeam;
import ui.widgets.HBoxForRunningTeam;
import ui.widgets.HBoxForStagedTeam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;
import java.util.Random;

public class mainTimingController {


    // private vars, for now it contains the MainPageController and the Day being used

    private TimingController controller;
    private Program program;
    private Calendar undoHeatTimer;

    public mainTimingController(TimingController controller) {
        this.controller = controller;
        controller.setUiController(this);
    }

    // EFFECTS: set running team list to the controller's running team list
    public void updateRunningTeamList() {
        ArrayList<HBoxForRunningTeam> hBoxForRunningTeams = new ArrayList<>();
        for (Team team : controller.getRunningTeams()) {
            try {
                hBoxForRunningTeams.add(new HBoxForRunningTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), team.getTeamHeatByHeatIDFromRemaining(team.getCurrentHeatID()).getSitrep(), team.getCurrentHeatID(), team.getTeamType().name(), controller));
            } catch (NoTeamHeatException e) {
                e.printStackTrace();
            }
        }
        runningTeamsList.setItems(FXCollections.observableList(hBoxForRunningTeams));
    }

    // EFFECTS: add a team to the running team list, first is private, second and third are the public ones to use
    public void addToRunningTeamList(Team team, boolean top) {
        ArrayList<HBoxForRunningTeam> hBoxForRunningTeams = new ArrayList<>();
        try {
            hBoxForRunningTeams.add(new HBoxForRunningTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), team.getTeamHeatByHeatIDFromRemaining(team.getCurrentHeatID()).getSitrep(), team.getCurrentHeatID(), team.getTeamType().name(), controller));
        } catch (NoTeamHeatException e) {
            e.printStackTrace();
        }
        if (top) {
            runningTeamsList.setItems(FXCollections.concat(FXCollections.observableList(hBoxForRunningTeams), runningTeamsList.getItems()));
        } else {
            runningTeamsList.setItems(FXCollections.concat(runningTeamsList.getItems(), FXCollections.observableList(hBoxForRunningTeams)));
        }
    }
    public void addToRunningTeamListToBottom(@NotNull Team team) {
        addToRunningTeamList(team, false);
    }
    public void addToRunningTeamListToTop(@NotNull Team team) {
        addToRunningTeamList(team, true);
    }

    // EFFECTS: set finished but possible undo team list to the controller's finished team list
    public void updateFinishedTeamList() {
        ArrayList<HBoxForFinishedUndoTeam> hBoxForFinishedUndoTeams = new ArrayList<>();
        for (Team team : controller.getFinishedTeams()) {
            hBoxForFinishedUndoTeams.add(new HBoxForFinishedUndoTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), team.getDoneHeats().get(team.getDoneHeats().size() - 1).getFinalTime().toString(), controller));
        }
        undoFinishedTeamList.setItems(FXCollections.observableList(hBoxForFinishedUndoTeams));
    }

    // EFFECTS: add a team to the finished team list, first is private, second and third are the public ones to use
    private void addToFinishedTeamList(Team team, boolean top) {
        ArrayList<HBoxForFinishedUndoTeam> hBoxForFinishedUndoTeams = new ArrayList<>();
        hBoxForFinishedUndoTeams.add(new HBoxForFinishedUndoTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), team.getDoneHeats().get(team.getDoneHeats().size() - 1).getFinalTime().toString(), controller));
        if (top) {
            undoFinishedTeamList.setItems(FXCollections.concat(FXCollections.observableList(hBoxForFinishedUndoTeams), undoFinishedTeamList.getItems()));
        } else {
            undoFinishedTeamList.setItems(FXCollections.concat(undoFinishedTeamList.getItems(), FXCollections.observableList(hBoxForFinishedUndoTeams)));
        }
    }
    public void addToFinishedTeamListToTop(@NotNull Team team) {
        addToFinishedTeamList(team, true);
    }
    public void addToFinishedTeamListToBottom(@NotNull Team team) {
        addToFinishedTeamList(team, false);
    }


    // EFFECTS: set the list of final finished teams to all those in the controller´s final finished teams list
    public void updateFinalFinishedTeamList() {
        ArrayList<HBoxForFinishedTeam> hBoxForFinishedTeams = new ArrayList<>();
        for (Team team : controller.getFinalFinishedTeams()) {
            try {
                hBoxForFinishedTeams.add(new HBoxForFinishedTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), team.getDoneHeats().get(team.getDoneHeats().size() - 1).getFinalTime().toString(), team.getTeamHeatByHeatIDFromDone(team.getCurrentHeatID()).getSitrep(), controller));
            } catch (NoTeamHeatException e) {
                e.printStackTrace();
            }
        }

        finishedTeamsList.setItems(FXCollections.observableList(hBoxForFinishedTeams));
    }

    // EFFECTS: add a team to the final finished team list, first is private, second and third are the public ones to use
    private void addToFinalFinishedTeamList(@NotNull Team team, boolean top) {
        ArrayList<HBoxForFinishedTeam> hBoxForFinishedTeams = new ArrayList<>();
        try {
            hBoxForFinishedTeams.add(new HBoxForFinishedTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), team.getDoneHeats().get(team.getDoneHeats().size() - 1).getFinalTime().toString(), team.getTeamHeatByHeatIDFromDone(team.getCurrentHeatID()).getSitrep(), controller));
        } catch (NoTeamHeatException e) {
            e.printStackTrace();
        }
        if (top) {
            finishedTeamsList.setItems(FXCollections.concat(FXCollections.observableList(hBoxForFinishedTeams), finishedTeamsList.getItems()));
        } else {
            finishedTeamsList.setItems(FXCollections.concat(finishedTeamsList.getItems(), FXCollections.observableList(hBoxForFinishedTeams)));
        }
    }
    public void addToFinalFinishedTeamListToTop(@NotNull Team team) {
        addToFinalFinishedTeamList(team, true);
    }
    public void addToFinalFinishedTeamListToBottom(@NotNull Team team) {
        addToFinalFinishedTeamList(team, false);
    }

    // EFFECTS: set the list for teams in the staged list from the controller's staged heat heat
    public void updateStagedHeatTeamList() {
        ArrayList<HBoxForStagedTeam> list = new ArrayList<>();
        for (Team team : controller.getStagedHeat().getTeams()) {
            try {
                list.add(new HBoxForStagedTeam(Integer.toString(team.getTeamNumber()), team.getTeamName(), team.getTeamHeatByHeatIDFromRemaining(controller.getStagedHeat().getHeatNumber()).getSitrep(), controller));
            } catch (NoTeamHeatException e) {
                e.printStackTrace();
            }
        }

        stageHeatTeamList.setItems(FXCollections.observableList(list));
    }

    // EFFECTS: makes sure text properties only accept numerical values and initializes the
    //          stageHeatNumber to the current heat available
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
        stageHeatNumber.setText(Integer.toString(program.getProgramDays().get(0).getAtHeat()));
    }

    // EFFECTS: save the data to json
    private void saveData() {
        PersistanceWithJackson.toJsonDay(program.getProgramDays().get(0));
        PersistanceWithJackson.toJsonController(controller);
    }

    // EFFECTS: move the teams from heatID back to staging from running list
    private void returnTeams(int heatID) {
        ArrayList<Team> runningTeams = controller.getRunningTeams();

        for (int i = 0; i < runningTeams.size(); i++) {
            Team team = runningTeams.get(i);
            if (team.getCurrentHeatID() == heatID) {
                controller.removeRunningTeam(team);
                i--;
            }
        }
        updateRunningTeamList();
    }

    @FXML
    private ListView<HBoxForRunningTeam> runningTeamsList;

    @FXML
    private TextField stageHeatNumber;

    @FXML
    private ListView<HBoxForFinishedTeam> finishedTeamsList;

    @FXML
    private ListView<HBoxForStagedTeam> stageHeatTeamList;

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
    private ListView<HBoxForFinishedUndoTeam> undoFinishedTeamList;


    @FXML
    private void populateFromSaveData() {
        program = controller.getProgram();
        program.getProgramDays().add(PersistanceWithJackson.toJavaDate());
        controller = PersistanceWithJackson.toJavaController();

        updateFinishedTeamList();
        updateRunningTeamList();

        initStuff();
    }

    @FXML
    private void populateHeatList() {
        program = controller.getProgram();
        Day day = new Day(Calendar.getInstance(), 1);
        program.addDay(day);
        Random random = new Random();
        for (int i = 1; i < 4; i++) {
            Heat heat = null;
            try {
                heat = new Heat(Calendar.getInstance(), LeagueType.JFF, TeamType.OPEN, i, day);
            } catch (AddHeatException e) {
                e.printStackTrace();
            }
            for (int j = 1; j <= 5; j++) {
                try {
                    heat.addTeam(program.createTeam(TeamType.OPEN, LeagueType.JFF, random.nextInt(2000), "Cool Name" + (j+i)));
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
            controller.setStagedHeat(program.getProgramDays().get(0).getHeatByID(Integer.parseInt(stageHeatNumber.getText())));
        } catch (NoHeatWithIDException e) {
            e.printStackTrace();
        }
        Heat stagedHeat = controller.getStagedHeat();
        if (!stagedHeat.isHasStarted()) {
            updateStagedHeatTeamList();

            timeToStartLabel.setText(stagedHeat.timeToStartString());
            teamTypeLabel.setText(stagedHeat.getTeamType().name());
            heatTypeLabel.setText(stagedHeat.getLeagueType().name());

        }
    }

    @FXML
    private void startHeat() {
        Heat stagedHeat = controller.getStagedHeat();
        if (stagedHeat != null) {
            stagedHeat.markStartTimeStarted(Calendar.getInstance());
            controller.addRunningTeams(stagedHeat.getTeamsThatWillRun());


            program.getProgramDays().get(0).atNextHeat();
            stageHeatNumber.setText(Integer.toString(program.getProgramDays().get(0).getAtHeat()));
            stageHeatTeamList.setItems(null);

            timeToStartLabel.setText("Stage Heat to Get Info");
            teamTypeLabel.setText("Stage Heat to Get Info");
            heatTypeLabel.setText("Stage Heat to Get Info");
        }
        //saveData();
        undoHeatTimer = Calendar.getInstance();
    }

    @FXML
    private void endTeamForButton() {
        if (!stopTeamNumber.getText().equals("")) {
            try {
                for (Team team : controller.getRunningTeams()) {
                    if (team.getTeamNumber() == Integer.parseInt(stopTeamNumber.getText())){
                        controller.removeRunningTeamWithUpdate(team);
                        controller.addFinishedTeam(team);
                        team.markEndTime(Calendar.getInstance());
                        break;
                    }
                }
            } catch (NoHeatsException e) {
                e.printStackTrace();
            } catch (CouldNotCalculateFinalTimeExcpetion couldNotCalculateFinalTimeExcpetion) {
                couldNotCalculateFinalTimeExcpetion.printStackTrace();
            } catch (NoCurrentHeatIDException e) {
                e.printStackTrace();
            } catch (NoRemainingHeatsException e) {
                e.printStackTrace();
            }
        }
        stopTeamNumber.setText("");
    }

    @FXML
    private void editHeatAction() {
        FXMLLoader root;
        try {
            root = new FXMLLoader(getClass().getResource("EditHeatPage.fxml"));
            Stage stage = new Stage();
            root.setControllerFactory(c -> new EditHeatPageController(controller));
            Scene scene = new Scene(root.load());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void undoHeatStartAction() {
        if (Calendar.getInstance().getTimeInMillis() - undoHeatTimer.getTimeInMillis() < 10000 ) {
            returnTeams(program.getProgramDays().get(0).getAtHeat() - 1);
            try {
                program.getProgramDays().get(0).undoLastHeatStart();
            } catch (NoHeatWithIDException | CanNotUndoHeatException e) {
                e.printStackTrace();
            }
            stageHeatNumber.setText(Integer.toString(program.getProgramDays().get(0).getAtHeat()));
        }
        // TODO exception to taken to much time to undo
    }

}

