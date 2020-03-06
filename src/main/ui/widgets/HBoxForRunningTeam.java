package ui.widgets;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import models.enums.Sitrep;
import models.exceptions.*;
import ui.TimingController;

import java.util.Arrays;

public class HBoxForRunningTeam extends CustomHBox {
    private static final double HBoxSpacingRunning = 20;
    Label teamName = new Label();
    Label id = new Label();
    Label heatNumber = new Label();
    Label category = new Label();
    Button button = new Button();
    ComboBox comboBox = new ComboBox();

    public HBoxForRunningTeam(String idText, String teamNameText, Sitrep sitrep, int heatNumberInt, String teamTypeText, TimingController controller) {
        super(HBoxSpacingRunning);

        heatNumber.setText(Integer.toString(heatNumberInt));
        heatNumber.setMaxWidth(45);
        HBox.setHgrow(heatNumber, Priority.ALWAYS);

        category.setText(teamTypeText);
        category.setPrefWidth(80);
        category.setMaxWidth(120);
        HBox.setHgrow(category, Priority.ALWAYS);

        id.setText(idText);
        id.setMaxWidth(50);
        HBox.setHgrow(id, Priority.ALWAYS);

        teamName.setText(teamNameText);
        teamName.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(teamName, Priority.ALWAYS);

        button.setText("Finish");
        button.setOnAction(event -> {
            try {
                endTeam(Integer.parseInt(idText), heatNumberInt, controller);
            } catch (NoHeatsException | CouldNotCalculateFinalTimeExcpetion e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "If the error persists please " +
                        "contact an admin. Error: " + e.getMessage());
                alert.setHeaderText("There has been an error while trying to finish a team");
                alert.getDialogPane().getStylesheets().add(controller.getClass().getResource("application.css").toExternalForm());
                alert.show();
            }
        });

        comboBox.setItems(FXCollections.observableList(Arrays.asList(Sitrep.values())));
        comboBox.setValue(sitrep.name());
        comboBox.setOnAction(event -> {
            updateStatusForRunning(Integer.parseInt(idText), comboBox.getValue().toString(), controller);
        });

        this.getChildren().addAll(heatNumber, id, teamName, category, comboBox, button);
    }
}
