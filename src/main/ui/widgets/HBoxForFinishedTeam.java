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

public class HBoxForFinishedTeam extends CustomHBox {

    private static final double HBoxSpacing = 10;
    Label teamName = new Label();
    Label id = new Label();
    Label finalTime = new Label();
    ComboBox comboBox = new ComboBox();

    public HBoxForFinishedTeam(Run run, UIAppLogic controller){
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
            super.updateStatus(comboBox.getValue().toString(), controller);
        });

        this.getChildren().addAll(id, this.teamName, comboBox, this.finalTime);
    }
}
