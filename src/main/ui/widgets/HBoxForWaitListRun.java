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
    UIAppLogic controller;

    public HBoxForWaitListRun(Run run, UIAppLogic controller){
        super(HBoxSpacing, run);
        this.controller = controller;

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
                addTeam();
            } catch (AddRunException | CriticalErrorException e) {
                showAlert(Alert.AlertType.ERROR, "If the error persists please " +
                        "contact an admin. Error: " + e.getMessage(),
                        "There has been an error while trying to add wait list team to heat");
            }
        });

        this.getChildren().addAll(id, this.teamName, this.teamPool, addTeamButton);
    }

    /**
     * Moves the Run to the staged {@link models.Heat} being edited calling the Run's moveRunTo helper and will also
     * remove the Run from the wait list. Will also update both UI lists in the edit heat page.
     */
    private void addTeam() throws CriticalErrorException, AddRunException {
        run.moveRunTo(controller.getStagedHeat());
        controller.getProgram().removeRunFromWaitList(run.getRunNumber());
        controller.getEditHeatController().setTeamHeatListTeams();
        controller.getEditHeatController().setWaitListTeams();
    }

    @Override
    public Object newObject(Run run, UIAppLogic controller) {
        return new HBoxForWaitListRun(run , controller);
    }

}
