package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.Random;

public class mainController {
    @FXML
    private Label myMessage;


    public void generateRandom(ActionEvent event) {
        Random rand = new Random();
        int myRand = rand.nextInt(50);
        myMessage.setText(Integer.toString(myRand));
        System.out.println(myRand);
    }

}
