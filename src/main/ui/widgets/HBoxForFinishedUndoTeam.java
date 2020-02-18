package ui.widgets;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import ui.TimingController;

import java.util.Timer;
import java.util.TimerTask;

public class HBoxForFinishedUndoTeam extends CustomHBox {

    private static final double HBoxSpacing = 10;
    Label name = new Label();
    Label id = new Label();
    Label finalTimeLabel = new Label();
    Button undoButton = new Button();

    public HBoxForFinishedUndoTeam(String idText, String teamName, String finalTime, TimingController controller){
        super(HBoxSpacing);

        id.setText(idText);
        id.setMaxWidth(45);
        HBox.setHgrow(id, Priority.ALWAYS);
        name.setText(teamName);
        name.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(name, Priority.ALWAYS);

        undoButton.setText("Undo");
        undoButton.setOnAction(event -> {
            controller.undoTeamFinish(Integer.parseInt(idText));
        });

        finalTimeLabel.setText(finalTime);


        this.getChildren().addAll(id, name, finalTimeLabel, undoButton);
    }
}
