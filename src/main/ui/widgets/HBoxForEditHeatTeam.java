package ui.widgets;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import models.Run;
import models.Team;
import models.exceptions.*;
import ui.UIAppLogic;

public class HBoxForEditHeatTeam extends CustomHBox {

    private static final double HBoxSpacing = 10;

// FXML OBJECT VARIABLES //

    Label teamName = new Label();
    Label id = new Label();
    Label teamType = new Label();
    Button removeButton = new Button();
    Button waitListButton = new Button();
    Button moveToButton = new Button();

// CONSTRUCTOR //

    public HBoxForEditHeatTeam(Run run, UIAppLogic controller){
        super(HBoxSpacing, run);

        Team team = run.getTeam();

        id.setText(Integer.toString(team.getTeamNumber()));
        id.setMaxWidth(45);
        HBox.setHgrow(id, Priority.ALWAYS);

        this.teamName.setText(team.getTeamName());
        this.teamName.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this.teamName, Priority.ALWAYS);

        this.teamType.setText(team.getPoolName());

        removeButton.setText("Remove");
        removeButton.setOnAction(event -> {
            try {
                controller.getStagedHeat().removeRun(run.getRunNumber());
                controller.getEditHeatController().setTeamHeatListTeams();
            } catch (NoTeamException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "If the error persists please " +
                        "contact an admin. Error: " + e.getMessage());
                alert.setHeaderText("There has been an error while trying to remove a team from this heat");
                alert.getDialogPane().getStylesheets().add(controller.getClass().getResource("application.css").toExternalForm());
                alert.show();
            }
        });

        waitListButton.setText("Wait List");
        waitListButton.setOnAction(event -> {
            try {
                controller.getProgram().addRunToWaitList(controller.getStagedHeat().removeRun(run.getRunNumber()));
                controller.getEditHeatController().setWaitListTeams();
                controller.getEditHeatController().setTeamHeatListTeams();
            } catch (NoTeamException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "If the error persists please " +
                        "contact an admin. Error: " + e.getMessage());
                alert.setHeaderText("There has been an error while trying to wait list the team");
                alert.getDialogPane().getStylesheets().add(controller.getClass().getResource("application.css").toExternalForm());
                alert.show();
            }
        });

        moveToButton.setText("Move To");
        moveToButton.setOnAction(event -> {
            TextInputDialog textInputDialog = new TextInputDialog();
            textInputDialog.getDialogPane().getStylesheets().add(controller.getClass().getResource("application.css").toExternalForm());
            textInputDialog.showAndWait();

            if (textInputDialog.getEditor().getText().matches("[0-9]*")) {
                int newHeatNumber = Integer.parseInt(textInputDialog.getEditor().getText());
                try {
                    // make sure the user wants to do the move
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want " +
                            "to move the team to heat " + textInputDialog.getEditor().getText());
                    alert.setHeaderText("Confirm movement");
                    alert.getDialogPane().getStylesheets().add(controller.getClass().getResource("application.css").toExternalForm());
                    alert.showAndWait();

                    if (alert.getResult() == ButtonType.OK) {
                        controller.getStagedHeat().removeRun(run.getRunNumber());
                        controller.getCurrentDay().getHeatByHeatNumber(newHeatNumber).addRun(run);

                        // inform user that change was successful
                        Alert informAlert = new Alert(Alert.AlertType.INFORMATION, "Team moved successfully to heat " + textInputDialog.getEditor().getText());
                        informAlert.setHeaderText("Move successful");
                        informAlert.getDialogPane().getStylesheets().add(controller.getClass().getResource("application.css").toExternalForm());
                        informAlert.show();
                    }
                } catch (NoTeamException | AddTeamException | NoHeatWithIDException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "That heat already has this team in it.");
                    alert.setHeaderText("There has been an error while trying to move team to another heat");
                    alert.getDialogPane().getStylesheets().add(controller.getClass().getResource("application.css").toExternalForm());
                    alert.show();
                }
                controller.getEditHeatController().setTeamHeatListTeams();
            }
        });

        this.getChildren().addAll(id, this.teamName, this.teamType, removeButton, waitListButton, moveToButton);
    }
}
