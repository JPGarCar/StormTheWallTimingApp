package ui.inputOutputPages;

import IO.ExcelInput;
import com.sun.istack.internal.NotNull;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import models.Program;
import models.exceptions.InvalidExcelException;
import persistance.PersistenceWithJackson;
import persistance.RunPolling;
import ui.UIAppLogic;
import ui.UIController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class DataIOPageController extends UIController {

// VARIABLES //

    private final String dbUrl = "jdbc:postgresql://localhost:5432/RECTimingApp";
    private final String dbUser = "tester";
    private final String dbPassword = "testPassword";

    private String fileToImport;
    private UIAppLogic controller;

// CONSTRUCTOR //

    public DataIOPageController(@NotNull UIAppLogic controller) {
        super(controller);
        this.controller = controller;
        fileToImport = "";
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

        // check if a file was selected
        if (fileToImport.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "There is no file selected to import.",
                    "Please select a file first!");
            return;
        }


        // if there is no controller, so new data, build new controller
        if (controller.getProgram() == null)
            controller.setProgram(new Program());


        // try to load the file selected
        try {
             fileInputStream = new FileInputStream(new File(fileToImport));
        } catch (FileNotFoundException e) {
            showAlert(Alert.AlertType.ERROR, "There was no file provided or the file is corrupted, " +
                            "please select a file and try again.", "There has been an error!", e);
        }

        ExcelInput excelInput = new ExcelInput(fileInputStream, controller);

        ImportExecutor importExecutor = new ImportExecutor(1);
        importExecutor.addTask(() -> {
            try {
                excelInput.inputData(importHeatCheck.isSelected(), importTeamCheck.isSelected());
            } catch (InvalidExcelException e) {
                showAlert(Alert.AlertType.ERROR, "The file you are trying to use has no " +
                        "column names in the first row. Please look at the Info page for more information. " +
                        "The import did not occur.", "There has been an error!", e);
            }
        });
        importExecutor.allDone();
        importExecutor.shutDown();
        LinkedList<String> alertLinkedList = excelInput.getAlerts();
        for (String string : alertLinkedList) {
            showAlert(Alert.AlertType.WARNING, string, "Excel Input warning.");
        }

        // inform the user that the import was successful
        showAlert(Alert.AlertType.INFORMATION, "Data has been imported successfully from the excel.",
                "Action was successful");
    }

    // EFFECTS: export the data in this computer to an excel file of given name
    @FXML
    public void exportFileButtonAction() {
        // TODO get info on how they want the data, heat per sheet? team per sheet?
    }

    // EFFECTS: return to main menu within same scene
    @FXML
    private void backToMainMenuButtonAction() {
        backToMainMenu(fileNotificationLabel.getScene());
    }

    // EFFECTS: grabs the data form the local json save file and import it to the program
    @FXML
    private void populateFromSaveData() {

        ImportExecutor importExecutor = new ImportExecutor(2);
        importExecutor.addTask(() -> controller = PersistenceWithJackson.toJavaController(controller.getPathToFileSave()));
        importExecutor.addTask(() -> controller.setProgram(PersistenceWithJackson.toJavaProgram(controller.getPathToFileSave())));
        importExecutor.allDone();
        importExecutor.shutDown();

        // inform the user the data import was successful
        showAlert(Alert.AlertType.INFORMATION, "Data was successfully imported from the local save.",
                "Action Was Successful");
    }

    /**
     * Button will import the data from the database and override any local data stored.
     * Need to warn user about local data being scrapped!
     *
     * This button is for the offline mode (NO constant communication) but you still want to
     * download the online data once to then use offline.
     *
     * Will need to add an upload button to upload the new data from this local save to the database.
     */
    @FXML
    private void importFromDBActionButton() {
    }

    /**
     * Internal class to use threads when importing data. The big thing is to block the program until all the
     * data has been imported.
     */
    private class ImportExecutor {

        List<Future<?>> futures;
        ExecutorService executorService;

        public ImportExecutor(int i) {
            futures = new ArrayList<>();
            executorService = Executors.newFixedThreadPool(i);
        }

        /**
         * Will add a task to the Executor Service
         * @param x Runnable to be submitted to the Executor Service
         */
        public void addTask(Runnable x) {
            Future<?> f = executorService.submit(x);
            futures.add(f);
        }

        /**
         * To be called after all the tasks are added. Will block the program until all tasks are complete.
         */
        public void allDone() {
            Alert alert = showAlert(Alert.AlertType.INFORMATION, "Data is being imported please hold on. This " +
                    "message will close once the data import is complete.", "Please wait.");

            // block the program until all the tasks are complete
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            alert.close();
        }

        /**
         * Shut down the executor once all the tasks are complete. Just to not waste resources.
         */
        public void shutDown() {
            executorService.shutdown();
        }

    }



}
