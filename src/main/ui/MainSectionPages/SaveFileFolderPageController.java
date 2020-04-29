package ui.MainSectionPages;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import ui.UIAppLogic;
import ui.UIController;

import javax.swing.*;
import java.io.IOException;

public class SaveFileFolderPageController extends UIController {

    private String pathToUse;

    @FXML
    private Label confirmAndMoveOnButton;

    @FXML
    private Button selectFileButton;

    @FXML
    private Label fileNotificationLabel;

    // no need to use the UIAPPLogic controller
    public SaveFileFolderPageController() {
        super(null);
    }

    @FXML
    void selectFileButtonAction() {

        JFileChooser chooser = new JFileChooser();

        // only allow to choose directories
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.showSaveDialog(null);

        pathToUse = chooser.getSelectedFile().getAbsolutePath();

        // set text for success file selection
        fileNotificationLabel.setText("Directory chosen is: " + pathToUse);
    }

    @FXML
    void confirmAndMoveOnAction() {

        // make sure the path has been selected
        if (pathToUse == null) {
            showAlert(Alert.AlertType.WARNING, "You can not move forward without choosing a folder to save the " +
                    "program's data, please use the previous button to choose a folder.", "Please choose a " +
                    "folder");
        } else {
            FXMLLoader root = new FXMLLoader(getClass().getResource("/ui/MainSectionPages/RaceChooserPage.fxml"));
            root.setControllerFactory(c -> new RaceChooserPageController(pathToUse));

            try {
                confirmAndMoveOnButton.getScene().setRoot(root.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
