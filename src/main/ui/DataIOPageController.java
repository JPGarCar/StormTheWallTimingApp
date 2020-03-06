package ui;

import IO.ExcelInput;
import com.sun.istack.internal.NotNull;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import models.Program;
import models.exceptions.InvalidExcelException;
import persistance.DataBaseConnection;
import persistance.PersistanceWithJackson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

public class DataIOPageController {

// VARIABLES //

    private String fileToImport;
    private TimingController controller;

// CONSTRUCTOR //

    public DataIOPageController(@NotNull TimingController controller) {
        this.controller = controller;
    }

// FXML TAGS //

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

// FXML FUNCTIONS //

    // EFFECTS: action for selectFile button, will open a file display to select a file from the computer
    @FXML
    public void selectFileButtonAction() {
        FileChooser fileChooser = new FileChooser();

        // only allow .xlsx files
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            fileToImport = selectedFile.getAbsolutePath();

            // set text for success file selection
            fileNotificationLabel.setText("File received successfully with name: " + selectedFile.getName());
        }
    }

    // EFFECTS: action for the importData button, wil call ExcelInput from IO package
    @FXML
    public void importDataButtonAction() {
        FileInputStream fileInputStream = null;

        // if there is no controller, so new data, build new controller
        if (controller.getProgram() == null) {
            controller.setProgram(new Program());
        }

        // try to load the file selected
        try {
             fileInputStream = new FileInputStream(new File(fileToImport));
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "There was no file provided or the file is corrupted, please select a file and try again.");
            alert.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            alert.show();
        }

        ExcelInput excelInput = new ExcelInput(fileInputStream, controller);

        // try to input the data, for any alerts from ExcelInput, show them
        try {
            LinkedList<Alert> alertLinkedList = excelInput.inputData(importHeatCheck.isSelected(), importTeamCheck.isSelected());
            for (Alert alert : alertLinkedList) {
                alert.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
                alert.show();
            }
        } catch (InvalidExcelException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "The file you are trying to use has no " +
                    "column names in the first row. Please look at the Info page for more information. The import did not occur.");
            alert.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            alert.show();
        }

        // inform the user that the import was successful
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data has been imported successfully from the excel.");
        alert.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        alert.show();
    }

    // EFFECTS: export the data in this computer to an excel file of given name
    @FXML
    public void exportFileButtonAction() {
        // TODO get info on how they want the data, heat per sheet? team per sheet?
    }

    // EFFECTS: return to main menu within same scene
    @FXML
    private void backToMainMenuButtonAction() {
        try {
            FXMLLoader root = new FXMLLoader(getClass().getResource("MainPage.fxml"));
            root.setControllerFactory(c -> new MainPageController(controller));
            selectFileButton.getScene().setRoot(root.load());
            controller.saveData();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "If the error persists please " +
                    "contact an admin. Error: " + e.getMessage());
            alert.setHeaderText("There has been an error while trying to go back to main menu");
            alert.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            alert.show();
        }
    }

    // EFFECTS: grabs the data form the local json save file and import it to the program
    @FXML
    private void populateFromSaveData() {
        controller = PersistanceWithJackson.toJavaController();
        assert controller != null;
        controller.setProgram(PersistanceWithJackson.toJavaProgram());

        // inform the user the data import was successful
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data was successfully imported from the local save.");
        alert.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
        alert.show();
    }

    @FXML
    private void connectToDBActionButton() {
        DataBaseConnection dataBaseConnection = new DataBaseConnection(controller);

        // if there is no controller, so new data, build new controller and download all available data
        if (controller.getProgram() == null) {
            controller.setProgram(new Program());
            dataBaseConnection.downloadAllData();
        } else {
            dataBaseConnection.uploadAllData();
        }

        dataBaseConnection.startPolling();
    }





}
