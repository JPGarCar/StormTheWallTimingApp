package ui.widgets;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import models.Run;
import models.Team;
import models.exceptions.*;
import ui.UIAppLogic;

/**
 * This HBox is specific for the Edit Heat page. It has the possibility to add, remove and wait list teams that are
 * associated to the Heat being edited by using the Run(s).
 * Data shown:
 * - Team name
 * - Team number
 * - Team type
 */
public class HBoxForEditHeatRun extends CustomHBox {

    private static final double HBoxSpacing = 10;

// FXML OBJECT VARIABLES //

    Label teamName = new Label();
    Label id = new Label();
    Label teamType = new Label();
    Button removeButton = new Button();
    Button waitListButton = new Button();
    Button moveToButton = new Button();
    UIAppLogic controller;

// CONSTRUCTOR //

    public HBoxForEditHeatRun(Run run, UIAppLogic controller) {
        super(HBoxSpacing, run);
        this.controller = controller;

        Team team = run.getTeam();

        id.setText(Integer.toString(team.getTeamNumber()));
        id.setMaxWidth(45);
        HBox.setHgrow(id, Priority.ALWAYS);

        this.teamName.setText(team.getTeamName());
        this.teamName.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this.teamName, Priority.ALWAYS);

        this.teamType.setText(team.getPoolName());

        removeButton.setText("Remove");
        removeButton.setOnAction(event -> removeTeam());

        waitListButton.setText("Wait List");
        waitListButton.setOnAction(event -> waitListTeam());

        moveToButton.setText("Move To");
        moveToButton.setOnAction(event -> moveTeamTo());

        this.getChildren().addAll(id, this.teamName, this.teamType, removeButton, waitListButton, moveToButton);
    }

    /**
     * Helper function to remove a Team from the Heat by disassociating the Run from the Heat and from the Team. For
     * this we call the Run selfDelete() function. Finally we update the UI lists.
     */
    private void removeTeam() {
        try {
            run.selfDelete();
            controller.getEditHeatController().setTeamHeatListTeams();
        } catch (CriticalErrorException e) {
            showAlert(Alert.AlertType.ERROR, "If the error persists please " +
                            "contact an admin. Error: " + e.getMessage(),
                    "There has been an error while trying to remove a team from this heat");
        }
    }

    /**
     * Helper function to move a Team to the wait list. It will disassociate the Run from the Heat and then
     * move the Run to the Program's wait list. Finally, it will update the UI Run lists.
     */
    private void waitListTeam() {
        try {
            controller.getProgram().addRunToWaitList(controller.getStagedHeat().removeRun(run.getRunNumber()));
            controller.getEditHeatController().setWaitListTeams();
            controller.getEditHeatController().setTeamHeatListTeams();
        } catch (CriticalErrorException e) {
            showAlert(Alert.AlertType.ERROR, "If the error persists please " +
                            "contact an admin. Error: " + e.getMessage(),
                    "There has been an error while trying to wait list the team");
        }
    }

    /**
     * Helper function to move a Team to another Heat by asking the user to input the Heat's heat number.
     * It will first disassociate the Run to its initial Heat and then associate it to the new Heat.
     * Finally it will update the Heat's UI Run list.
     */
    private void moveTeamTo() {

        // input dialog to ask the user what heat to move the team to
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.getDialogPane().getStylesheets().add(controller.getClass().getResource("application.css").toExternalForm());
        textInputDialog.setTitle("Please input heat number");
        textInputDialog.setContentText("Please input the heat number of the Heat to move this team to.");
        textInputDialog.showAndWait();

        // make sure the input is numbers only
        if (textInputDialog.getEditor().getText().matches("[0-9]*") && !textInputDialog.getEditor().getText().isEmpty()) {
            int newHeatNumber = Integer.parseInt(textInputDialog.getEditor().getText());
            try {
                // make sure the user wants to do the move
                Alert alert = showAlert(Alert.AlertType.CONFIRMATION, "Are you sure you want " +
                                "to move the team to heat " + textInputDialog.getEditor().getText(),
                        "Confirm movement");

                if (alert.getResult() == ButtonType.OK) {
                    run.moveRunTo(controller.getCurrentDay().getHeatByHeatNumber(newHeatNumber));

                    // inform user that change was successful
                    showAlert(Alert.AlertType.INFORMATION, "Team moved successfully to heat " +
                            textInputDialog.getEditor().getText(), "Move successful");
                }
            } catch (AddRunException | CriticalErrorException e) {
                showAlert(Alert.AlertType.ERROR, "There has been an error while trying to move a team to " +
                        "a different Heat. " + e.getMessage(), "There has been an error");
            }
            controller.getEditHeatController().setTeamHeatListTeams();
        } else {
            showAlert(Alert.AlertType.ERROR, "Heat numbers should only contain numbers, please try it again.",
                    "Retry the action");
        }
    }

    @Override
    public Object newObject(Run run, UIAppLogic controller) {
        return new HBoxForEditHeatRun(run , controller);
    }
}
