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
 * This HBox is specific to the staged Heat Run UI list. It lets change the status of a Run.
 * Data shown:
 * - Team name
 * - Team number
 */
public class HBoxForStagedRun extends CustomHBox {
    private static final double HBoxSpacing = 12;
    Label teamNameLabel = new Label();
    Label id = new Label();
    ComboBox comboBox = new ComboBox();

    public HBoxForStagedRun(Run run, UIAppLogic controller) {
        super(HBoxSpacing, run);

        Team team = run.getTeam();

        id.setText(Integer.toString(team.getTeamNumber()));
        id.setMaxWidth(45);
        HBox.setHgrow(id, Priority.ALWAYS);

        teamNameLabel.setText(team.getTeamName());
        teamNameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(teamNameLabel, Priority.ALWAYS);


        comboBox.setItems(FXCollections.observableList(Arrays.asList(Sitrep.values())));
        comboBox.setValue(run.getSitrep().name());
        comboBox.setOnAction(event -> {
            updateStatus(comboBox.getValue().toString());
        });

        this.getChildren().addAll(id, teamNameLabel, comboBox);
    }

    @Override
    public Object newObject(Run run, UIAppLogic controller) {
        return new HBoxForStagedRun(run , controller);
    }
}