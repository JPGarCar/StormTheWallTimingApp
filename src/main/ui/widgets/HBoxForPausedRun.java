package ui.widgets;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import models.Run;
import models.Team;
import ui.UIAppLogic;

/**
 * This HBox is specific to the paused Run UI list. This HBox lets Run(s) to be undo back to the active list.
 * Data shown:
 * - Team name
 * - Team number
 * - Team final time
 */
public class HBoxForPausedRun extends CustomHBox {

    private static final double HBoxSpacing = 10;
    Label teamName = new Label();
    Label id = new Label();
    Label finalTimeLabel = new Label();
    Button undoButton = new Button();

    public HBoxForPausedRun(Run run, UIAppLogic controller){
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
            controller.undoPausedRun(run.getRunNumber());
        });

        finalTimeLabel.setText(run.getFinalTime().toString());


        this.getChildren().addAll(id, this.teamName, finalTimeLabel, undoButton);
    }
}
