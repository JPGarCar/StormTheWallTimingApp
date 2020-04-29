package ui.MainSectionPages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import ui.UIAppLogic;

import java.io.IOException;

public class IntroPageController {

    @FXML
    private Button nextButton;

    @FXML
    protected void nextButtonAction() {
        FXMLLoader root = new FXMLLoader(getClass().getResource("/ui/MainSectionPages/SaveFileFolderPage.fxml"));
        root.setControllerFactory(c -> new SaveFileFolderPageController());

        try {
            nextButton.getScene().setRoot(root.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
