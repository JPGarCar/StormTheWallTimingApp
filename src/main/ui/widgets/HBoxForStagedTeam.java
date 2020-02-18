package ui.widgets;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import models.enums.Sitrep;
import ui.TimingController;

import java.util.Arrays;

public class HBoxForStagedTeam extends CustomHBox {
    private static final double HBoxSpacing = 12;
    Label label = new Label();
    Label id = new Label();
    ComboBox comboBox = new ComboBox();

    public HBoxForStagedTeam(String idText, String teamName, Sitrep sitrep, TimingController controller) {
        super(HBoxSpacing);

        id.setText(idText);
        id.setMaxWidth(45);
        HBox.setHgrow(id, Priority.ALWAYS);

        label.setText(teamName);
        label.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);


        comboBox.setItems(FXCollections.observableList(Arrays.asList(Sitrep.values())));
        comboBox.setValue(sitrep.name());
        comboBox.setOnAction(event -> {
            updateStatus(Integer.parseInt(idText), comboBox.getValue().toString(), controller, true);
        });

        this.getChildren().addAll(id, label, comboBox);
    }
}