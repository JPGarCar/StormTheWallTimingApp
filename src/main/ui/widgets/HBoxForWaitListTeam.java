package ui.widgets;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import models.Run;
import models.Team;
import models.exceptions.AddTeamException;
import ui.UIAppLogic;

public class HBoxForWaitListTeam extends CustomHBox {

    private static final double HBoxSpacing = 10;
    Label teamName = new Label();
    Label id = new Label();
    Label teamType = new Label();
    Button addTeamButton = new Button();

    public HBoxForWaitListTeam(Run run, UIAppLogic controller){
        super(HBoxSpacing, run);

        Team team = run.getTeam();

        id.setText(Integer.toString(team.getTeamNumber()));
        id.setMaxWidth(45);
        HBox.setHgrow(id, Priority.ALWAYS);
        this.teamName.setText(team.getTeamName());
        this.teamName.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this.teamName, Priority.ALWAYS);

        this.teamType.setText(team.getPoolName());

        addTeamButton.setText("Add Team");
        addTeamButton.setOnAction(event -> {
            try {
                controller.getStagedHeat().addRun(run);
                controller.getProgram().removeRunFromWaitList(run.getRunNumber());
                controller.getEditHeatController().setTeamHeatListTeams();
                controller.getEditHeatController().setWaitListTeams();
            } catch (AddTeamException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "If the error persists please " +
                        "contact an admin. Error: " + e.getMessage());
                alert.setHeaderText("There has been an error while trying to add wait list team to heat");
                alert.getDialogPane().getStylesheets().add(controller.getClass().getResource("application.css").toExternalForm());
                alert.show();
            }
        });

        this.getChildren().addAll(id, this.teamName, this.teamType, addTeamButton);
    }
}
