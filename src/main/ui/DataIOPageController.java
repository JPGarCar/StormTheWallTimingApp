package ui;

import IO.ExcelInput;
import com.sun.istack.internal.NotNull;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import models.Program;
import models.exceptions.AddHeatException;
import models.exceptions.AddHeatRuntimeException;
import models.exceptions.InvalidExcelException;
import models.exceptions.NoHeatWithStartTimeException;
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
    private Label fileNotificationLabel;

    @FXML
    private CheckBox importTeamCheck;

    @FXML
    private CheckBox importHeatCheck;

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
            fileNotificationLabel.setText("File received successfully with name: " + selectedFile.getName());
        }
    }

    @FXML
    public void importDataButtonAction() {
        FileInputStream fileInputStream = null;
        controller.setProgram(new Program());

        try {
             fileInputStream = new FileInputStream(new File(fileToImport));
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "There was no file provided or the file is corrupted, please select a file and try again.");
            alert.show();
        }

        ExcelInput excelInput = new ExcelInput(fileInputStream, controller);
        try {
            excelInput.inputData(importHeatCheck.isSelected(), importTeamCheck.isSelected());
        } catch (AddHeatRuntimeException | NoHeatWithStartTimeException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Data import was successful, however there was an error while" +
                    "importing the following data: " + e.getMessage());
            alert.show();
        } catch (InvalidExcelException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "The file you are trying to use has no " +
                    "column names in the first row. Please look at the Info page for more information. The import did not occur.");
            alert.show();
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data has been imported successfully from the excel.");
        alert.show();


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
            controller.saveData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void populateFromSaveData() {
        controller = PersistanceWithJackson.toJavaController();
        assert controller != null;
        controller.setProgram(PersistanceWithJackson.toJavaProgram());
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data was successfully imported from the local save.");
        alert.show();
    }





}
