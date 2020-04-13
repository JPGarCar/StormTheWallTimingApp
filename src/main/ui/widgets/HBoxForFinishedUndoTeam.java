package ui.widgets;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import models.Run;
import models.Team;
import ui.TimingController;

public class HBoxForFinishedUndoTeam extends CustomHBox {

    private static final double HBoxSpacing = 10;
    Label teamName = new Label();
    Label id = new Label();
    Label finalTimeLabel = new Label();
    Button undoButton = new Button();

    public HBoxForFinishedUndoTeam(Run run, TimingController controller){
        super(HBoxSpacing, run);

        Team team = run.getTeam();

        id.setText(Integer.toString(team.getTeamNumber()));
        id.setMaxWidth(45);
        HBox.setHgrow(id, Priority.ALWAYS);
        this.teamName.setText(team.getTeamName());
        this.teamName.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this.teamName, Priority.ALWAYS);

        undoButton.setText("Undo");
        undoButton.setOnAction(event -> {
            controller.undoRunStop(run.getRunNumber());
        });

        finalTimeLabel.setText(run.getFinalTime().toString());


        this.getChildren().addAll(id, this.teamName, finalTimeLabel, undoButton);
    }
}
