package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


public class main extends Application {

    private TimingController controller;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader root = new FXMLLoader(getClass().getResource("MainPage.fxml"));
        controller = new TimingController();

        //populateHeatList();

        root.setControllerFactory(c -> new MainPageController(controller));
        Scene scene = new Scene(root.load());
        scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        primaryStage.setTitle("Storm the Wall Timing App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

/*    private void populateHeatList() {
        Program program = controller.getProgram();
        Day day = new Day("Saturday", 1);
        program.addDay(day);
        Random random = new Random();
        for (int i = 1; i <= 4; i++) {
            Heat heat;
            int number = random.nextInt(100);
            heat = new Heat(Calendar.getInstance(), "Just for Fun", i, day, number);
            for (int j = 1; j <= 3; j++) {
                try {
                    number = random.nextInt(2000);
                    heat.addTeam(program.createTeam(TeamType.OPEN, "Just for Fun", number, "Cool Name" + (number), number));
                } catch (AddTeamException e) {
                    e.printStackTrace();
                }
            }
        }
        day.setAtHeat(1);
    }*/


}
