package ui.widgets;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import models.enums.Sitrep;
import ui.TimingController;

import java.util.Arrays;

public class HBoxForFinishedTeam extends CustomHBox {

    private static final double HBoxSpacing = 10;
    Label teamName = new Label();
    Label id = new Label();
    Label finalTime = new Label();
    ComboBox comboBox = new ComboBox();

    public HBoxForFinishedTeam(String idText, String teamName, String finalTime, Sitrep sitrep, TimingController controller, int heatNumber){
        super(HBoxSpacing);

        id.setText(idText);
        id.setMaxWidth(45);
        HBox.setHgrow(id, Priority.ALWAYS);
        this.teamName.setText(teamName);
        this.teamName.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(this.teamName, Priority.ALWAYS);

        this.finalTime.setText(finalTime);

        comboBox.setItems(FXCollections.observableList(Arrays.asList(Sitrep.values())));
        comboBox.setValue(sitrep.name());
        comboBox.setOnAction(event -> {
            super.updateStatusForFinished(Integer.parseInt(idText), comboBox.getValue().toString(), controller, heatNumber);
        });

        this.getChildren().addAll(id, this.teamName, comboBox, this.finalTime);
    }
}
