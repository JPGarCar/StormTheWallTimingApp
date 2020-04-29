package ui.MainSectionPages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import ui.UIAppLogic;

import java.io.IOException;

public class RaceChooserPageController {

    /**
     * Path where to save json files.
     */
    private String saveDataPath;

    public RaceChooserPageController(String saveDataPath) {
        this.saveDataPath = saveDataPath;
    }

    @FXML
    private ImageView firstImage;

    @FXML
    private ImageView secondImage;

    /**
     * Assigns the images to the ImageView variables and sets the action aka start each race to the OnMousePressed
     * event handler of each ImageView.
     */
    @FXML
    protected void initialize() {
        firstImage.setImage(new Image(getClass().getResource(
                "/resources/stormTheWall_Logo.png").toExternalForm()));
        secondImage.setImage(new Image(getClass().getResource(
                "/resources/dayOfTheLongBoatImage.jpg").toExternalForm()));

        firstImage.setOnMousePressed(click -> startStormTheWall());

        secondImage.setOnMousePressed(click -> startLongBoat());

    }

    /**
     * Helper function to start the storm the wall timing app.
     */
    private void startStormTheWall() {
        FXMLLoader root = new FXMLLoader(getClass().getResource("/ui/MainSectionPages/MainPage.fxml"));
        UIAppLogic controller = new UIAppLogic();
        controller.setPathToFileSave(saveDataPath);
        root.setControllerFactory(c -> new MainPageController(controller));

        try {
            firstImage.getScene().setRoot(root.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper function to start the long boat app.
     */
    private void startLongBoat() {

    }

}
