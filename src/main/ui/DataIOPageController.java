package ui;

import com.sun.istack.internal.NotNull;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import models.Heat;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;

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

        // Workbook instance that refers to .xls file
        HSSFWorkbook workbook = null;
        try {
             workbook = new HSSFWorkbook(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HSSFSheet heatsSheet = workbook.getSheet("Heats"); // NOTICE file sheet must be named Heats

        // to evaluate cell types
        FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

        if (heatsSheet != null) {
            for (Row row : heatsSheet) {
                Heat heat = new Heat();

                for (Cell cell : row) {
                    // TODO
                }

                // controller.getProgram().getDayFromDayNumber(1).addHeat(); // TODO add different day system
            }
        }


    }

    @FXML
    public void exportFileButtonAction() {
        // TODO get info on how they want the data, heat per sheet? team per sheet?
    }





}
