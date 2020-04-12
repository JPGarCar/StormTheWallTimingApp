package ui.widgets;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import ui.TimingController;

public class HBoxForFinishedUndoTeam extends CustomHBox {

    private static final double HBoxSpacing = 10;
    Label teamName = new Label();
    Label id = new Label();
    Label finalTimeLabel = new Label();
    Button undoButton = new Button();

    public HBoxForFinishedUndoTeam(String idText, String teamName, String finalTime, TimingController controller, int heatNumber){
        super(HBoxSpacing);

        id.setText(idText);
        id.setMaxWidth(45);
        HBox.setHgrow(id, Priority.ALWAYS);
        this.teamName.setText(teamName);
        this.teamName.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this.teamName, Priority.ALWAYS);

        undoButton.setText("Undo");
        undoButton.setOnAction(event -> {
            controller.undoRunStop(Integer.parseInt(idText), heatNumber);
        });

        finalTimeLabel.setText(finalTime);


        this.getChildren().addAll(id, this.teamName, finalTimeLabel, undoButton);
    }
}
