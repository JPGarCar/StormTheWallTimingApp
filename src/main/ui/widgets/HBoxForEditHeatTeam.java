package ui.widgets;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import models.exceptions.NoTeamException;
import ui.TimingController;

public class HBoxForEditHeatTeam extends CustomHBox {

    private static final double HBoxSpacing = 10;
    Label teamName = new Label();
    Label id = new Label();
    Label teamType = new Label();
    Button removeButton = new Button();
    Button waitListButton = new Button();
    Button moveToButton = new Button();

    public HBoxForEditHeatTeam(String idText, String teamName, String category, TimingController controller){
        super(HBoxSpacing);

        id.setText(idText);
        id.setMaxWidth(45);
        HBox.setHgrow(id, Priority.ALWAYS);
        this.teamName.setText(teamName);
        this.teamName.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this.teamName, Priority.ALWAYS);

        this.teamType.setText(category);

        removeButton.setText("Remove");
        removeButton.setOnAction(event -> {
            try {
                controller.getStagedHeat().removeTeam(Integer.parseInt(idText));
                controller.getEditHeatController().setTeamHeatListTeams();
            } catch (NoTeamException e) {
                e.printStackTrace();
                // TODO
            }
        });

        waitListButton.setText("Wait List");
        waitListButton.setOnAction(event -> {
            try {
                controller.getProgram().addTeamToWaitList(controller.getStagedHeat().removeTeam(Integer.parseInt(idText)));
                controller.getEditHeatController().setWaitListTeams();
                controller.getEditHeatController().setTeamHeatListTeams();
            } catch (NoTeamException e) {
                e.printStackTrace();
            }
        });

        moveToButton.setText("Move To");
        moveToButton.setOnAction(event -> {
            // TODO makes a pop up a pear to ask for heat to move to.
        });

        this.getChildren().addAll(id, this.teamName, this.teamType, removeButton, waitListButton, moveToButton);
    }
}
