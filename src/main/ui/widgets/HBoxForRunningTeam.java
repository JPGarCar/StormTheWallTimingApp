package ui.widgets;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import models.Run;
import models.Team;
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

    public HBoxForRunningTeam(Run run, TimingController controller) {
        super(HBoxSpacingRunning, run);

        Team team = run.getTeam();

        heatNumber.setText(Integer.toString(run.getHeat().getHeatNumber()));
        heatNumber.setMaxWidth(45);
        HBox.setHgrow(heatNumber, Priority.ALWAYS);

        category.setText(run.getHeat().getCategory());
        category.setPrefWidth(80);
        category.setMaxWidth(120);
        HBox.setHgrow(category, Priority.ALWAYS);

        id.setText(Integer.toString(team.getTeamNumber()));
        id.setMaxWidth(50);
        HBox.setHgrow(id, Priority.ALWAYS);

        teamName.setText(team.getTeamName());
        teamName.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(teamName, Priority.ALWAYS);

        button.setText("Finish");
        button.setOnAction(event -> {
            try {
                endTeam(controller);
            } catch (NoHeatsException | CouldNotCalculateFinalTimeExcpetion e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "If the error persists please " +
                        "contact an admin. Error: " + e.getMessage());
                alert.setHeaderText("There has been an error while trying to finish a team");
                alert.getDialogPane().getStylesheets().add(controller.getClass().getResource("application.css").toExternalForm());
                alert.show();
            }
        });

        comboBox.setItems(FXCollections.observableList(Arrays.asList(Sitrep.values())));
        comboBox.setValue(run.getSitrep().name());
        comboBox.setOnAction(event -> {
            updateStatus(comboBox.getValue().toString(), controller);
        });

        this.getChildren().addAll(heatNumber, id, teamName, category, comboBox, button);
    }
}
