package ui.widgets;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import models.Run;
import models.Team;
import models.enums.Sitrep;
import ui.UIAppLogic;

import java.util.Arrays;

/**
 * This HBox is specific to the UI finished list in the timer page. This HBox can change the status of Run(s).
 * Data shown:
 * - Team name
 * - Team number
 * - Run final time
 */
public class HBoxForFinishedRun extends CustomHBox {

    private static final double HBoxSpacing = 10;
    Label teamName = new Label();
    Label id = new Label();
    Label finalTime = new Label();
    ComboBox comboBox = new ComboBox();

    public HBoxForFinishedRun(Run run, UIAppLogic controller){
        super(HBoxSpacing, run);

        Team team = run.getTeam();

        id.setText(Integer.toString(team.getTeamNumber()));
        id.setMaxWidth(45);
        HBox.setHgrow(id, Priority.ALWAYS);

        this.teamName.setText(team.getTeamName());
        this.teamName.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this.teamName, Priority.ALWAYS);

        this.finalTime.setText(run.getFinalTime().toString());

        comboBox.setItems(FXCollections.observableList(Arrays.asList(Sitrep.values())));
        comboBox.setValue(run.getSitrep().name());
        comboBox.setOnAction(event -> {
            super.updateStatus(comboBox.getValue().toString());
        });

        this.getChildren().addAll(id, this.teamName, comboBox, this.finalTime);
    }
}
