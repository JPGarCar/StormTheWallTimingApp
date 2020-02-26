package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Day;
import models.Heat;
import models.Program;
import models.enums.LeagueType;
import models.enums.TeamType;
import models.exceptions.AddTeamException;
import sun.applet.Main;

import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

public class main extends Application {

    private TimingController controller;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader root = new FXMLLoader(getClass().getResource("MainPage.fxml"));
        controller = new TimingController();

        populateHeatList();

        root.setControllerFactory(c -> new MainPageController(controller));
        Scene scene = new Scene(root.load());
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        primaryStage.setTitle("Storm the Wall Timing App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void populateHeatList() {
        Program program = controller.getProgram();
        Day day = new Day(Calendar.getInstance(), 1);
        program.addDay(day);
        Random random = new Random();
        for (int i = 1; i <= 4; i++) {
            Heat heat;
            int number = random.nextInt(100);
            heat = new Heat(Calendar.getInstance(), LeagueType.JFF, TeamType.OPEN, i, day, number);
            for (int j = 1; j <= 3; j++) {
                try {
                    number = random.nextInt(2000);
                    heat.addTeam(program.createTeam(TeamType.OPEN, LeagueType.JFF, number, "Cool Name" + (number), number));
                } catch (AddTeamException e) {
                    e.printStackTrace();
                }
            }
        }
        day.setAtHeat(1);
    }


}
