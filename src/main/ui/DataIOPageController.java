package ui;

import IO.ExcelInput;
import com.sun.istack.internal.NotNull;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import models.Day;
import persistance.PersistanceWithJackson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DataIOPageController {

    private String fileToImport;
    private TimingController controller;

    public DataIOPageController(@NotNull TimingController controller) {
        this.controller = controller;
    }


    @FXML
    private Button selectFileButton;

    @FXML
    private TextField exportFileName;

    @FXML
    public void selectFileButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel Files", "*.xls"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            fileToImport = selectedFile.getAbsolutePath();
        }
    }

    @FXML
    public void importDataButtonAction() {
        FileInputStream fileInputStream = null;

        try {
             fileInputStream = new FileInputStream(new File(fileToImport));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ExcelInput excelInput = new ExcelInput(fileInputStream, controller);


    }

    @FXML
    public void exportFileButtonAction() {
        // TODO get info on how they want the data, heat per sheet? team per sheet?
    }

    @FXML
    private void backToMainMenuButtonAction() {
        try {
            FXMLLoader root = new FXMLLoader(getClass().getResource("MainPage.fxml"));
            root.setControllerFactory(c -> new MainPageController(controller));
            selectFileButton.getScene().setRoot(root.load());
            // TODO add save functionality
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void populateFromSaveData() {
        controller = PersistanceWithJackson.toJavaController();
        controller.setProgram(PersistanceWithJackson.toJavaProgram());
    }





}
