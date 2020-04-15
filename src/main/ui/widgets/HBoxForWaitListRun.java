package ui.widgets;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import models.Run;
import models.Team;
import models.exceptions.AddRunException;
import models.exceptions.CriticalErrorException;
import ui.UIAppLogic;

/**
 * This HBox is specific to the wait list UI list in the edit heat page. It lets the user add the Run to the Heat
 * being edited.
 * Data shown:
 * - Team name
 * - Team number
 * - Team pool
 */
public class HBoxForWaitListRun extends CustomHBox {

    private static final double HBoxSpacing = 10;
    Label teamName = new Label();
    Label id = new Label();
    Label teamPool = new Label();
    Button addTeamButton = new Button();

    public HBoxForWaitListRun(Run run, UIAppLogic controller){
        super(HBoxSpacing, run);

        Team team = run.getTeam();

        id.setText(Integer.toString(team.getTeamNumber()));
        id.setMaxWidth(45);
        HBox.setHgrow(id, Priority.ALWAYS);
        this.teamName.setText(team.getTeamName());
        this.teamName.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this.teamName, Priority.ALWAYS);

        this.teamPool.setText(team.getPoolName());

        addTeamButton.setText("Add Team");
        addTeamButton.setOnAction(event -> {
            try {
                run.moveRun(controller.getStagedHeat());
                controller.getProgram().removeRunFromWaitList(run.getRunNumber());
                controller.getEditHeatController().setTeamHeatListTeams();
                controller.getEditHeatController().setWaitListTeams();
            } catch (AddRunException | CriticalErrorException e) {
                showAlert(Alert.AlertType.ERROR, "If the error persists please " +
                        "contact an admin. Error: " + e.getMessage(),
                        "There has been an error while trying to add wait list team to heat");
            }
        });

        this.getChildren().addAll(id, this.teamName, this.teamPool, addTeamButton);
    }
}
