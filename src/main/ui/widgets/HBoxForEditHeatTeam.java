package ui.widgets;

import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import models.enums.Sitrep;
import models.enums.TeamType;
import models.exceptions.NoTeamException;
import ui.TimingController;

import java.util.Arrays;

public class HBoxForEditHeatTeam extends CustomHBox {

    private static final double HBoxSpacing = 10;
    Label teamName = new Label();
    Label id = new Label();
    Label teamType = new Label();
    Button removeButton = new Button();
    Button waitListButton = new Button();
    Button moveToButton = new Button();

    public HBoxForEditHeatTeam(String idText, String teamName, TeamType teamType, TimingController controller){
        super(HBoxSpacing);

        id.setText(idText);
        id.setMaxWidth(25);
        HBox.setHgrow(id, Priority.ALWAYS);
        this.teamName.setText(teamName);
        this.teamName.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this.teamName, Priority.ALWAYS);

        this.teamType.setText(teamType.name());

        removeButton.setText("Remove");
        removeButton.setOnAction(event -> {
            try {
                controller.getStagedHeat().removeTeam(controller.getStagedHeat().getTeamFromHeatByID(Integer.parseInt(idText)));
                controller.getEditHeatController().setTeamHeatListTeams();
            } catch (NoTeamException e) {
                e.printStackTrace();
                // TODO
            }
        });

        waitListButton.setText("Wait List");
        waitListButton.setOnAction(event -> {

        });

        moveToButton.setText("Move To");
        moveToButton.setOnAction(event -> {
            // TODO makes a pop up a pear to ask for heat to move to.
        });

        this.getChildren().addAll(id, this.teamName, this.teamType, removeButton, waitListButton, moveToButton);
    }
}
