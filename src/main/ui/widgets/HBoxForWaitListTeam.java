package ui.widgets;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import models.exceptions.AddTeamException;
import ui.TimingController;

public class HBoxForWaitListTeam extends CustomHBox {

    private static final double HBoxSpacing = 10;
    Label teamName = new Label();
    Label id = new Label();
    Label teamType = new Label();
    Button addTeamButton = new Button();

    public HBoxForWaitListTeam(String idText, String teamName, String category, TimingController controller){
        super(HBoxSpacing);

        id.setText(idText);
        id.setMaxWidth(45);
        HBox.setHgrow(id, Priority.ALWAYS);
        this.teamName.setText(teamName);
        this.teamName.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this.teamName, Priority.ALWAYS);

        this.teamType.setText(category);

        addTeamButton.setText("Add Team");
        addTeamButton.setOnAction(event -> {
            try {
                controller.getStagedHeat().addTeam(controller.getProgram().getTeamByTeamNumber(Integer.parseInt(idText)));
                controller.getProgram().removeTeamFromWaitList(Integer.parseInt(idText));
                controller.getEditHeatController().setTeamHeatListTeams();
                controller.getEditHeatController().setWaitListTeams();
            } catch (AddTeamException e) {
                e.printStackTrace();
                // TODO
            }
        });

        this.getChildren().addAll(id, this.teamName, this.teamType, addTeamButton);
    }
}
