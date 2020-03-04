package IO;

import javafx.scene.control.Alert;
import models.Day;
import models.Heat;
import models.Team;
import models.exceptions.AddHeatException;
import models.exceptions.InvalidExcelException;
import models.exceptions.NoDayException;
import models.exceptions.NoHeatWithStartTimeException;
import org.apache.commons.math3.analysis.function.Add;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ui.TimingController;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ExcelInput {

// FINALS //

    final int HEATIDIden = 0;
    final int HEATNUMBERIden = 1;
    final int HEATSTARTTIMEIden = 2;
    final int HEATCATEGORYIden = 3;
    final int HEATDAYIden = 4;

    final int TEAMNAMEIden = 10;
    final int TEAMPOOLNAMEIden = 11;
    final int TEAMNUMBERIden = 12;
    final int TEAMIDIden = 13;
    final int TEAMUNITIden = 14;
    final int TEAMDAYIden = 15;
    final int TEAMRUNTIMEIden = 16;


// VARIABLES //

    private FileInputStream fileInputStream;
    private XSSFWorkbook workbook;
    private XSSFSheet heatsSheet;
    private XSSFSheet teamsSheet;
    private Map<Integer, Integer> colIndentifiers;
    private TimingController controller;


    // Represents all the alerts to send because of errors
    private LinkedList<Alert> alertLinkedList;

// CONSTRUCTORS //

    public ExcelInput(FileInputStream fileInputStream, TimingController controller) {
        this.fileInputStream = fileInputStream;
        this.controller = controller;
        colIndentifiers = new HashMap<>();
        alertLinkedList = new LinkedList<>();
    }

// FUNCTIONS //

    // EFFECTS: public function to do the entire process
    public LinkedList<Alert> inputData(boolean isHeats, boolean isTeams) throws InvalidExcelException {
        createFiles(isHeats, isTeams);
        evaluateData(isHeats, isTeams);
        dataTransition(isHeats, isTeams);
        return alertLinkedList;
    }

    // EFFECTS: evaluates all all the sheets to make sure the data is present and well organized
    private void evaluateData(boolean isHeats, boolean isTeams) throws InvalidExcelException {
        if (isHeats) {
            firstRowEvaluateHeats();
        }
        if (isTeams) {
            firstRowEvaluateTeams();
        }
    }

    // EFFECTS: import the data itself, last of evaluate and create files
    private void dataTransition(boolean isHeats, boolean isTeams) {
        if (isHeats) {
            addHeatsFromData();
        }
        if (isTeams) {
            addTeamsFromData();
        }
    }

    // EFFECTS: starts all the file and var creations
    private void createFiles(boolean isHeats, boolean isTeams) {
        try {
            workbook = new XSSFWorkbook(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (isHeats) {
            heatsSheet = workbook.getSheet("Heats"); // NOTICE file sheet must be named Heats
        }
        if (isTeams) {
            teamsSheet = workbook.getSheet("Teams"); // NOTICE file sheet must be names Teams
        }
    }

    // EFFECTS: evaluate the first row of the excel for heat sheet
    private void firstRowEvaluateHeats() throws InvalidExcelException {
        Row titleRow = heatsSheet.getRow(0);
        int lastColUsed = titleRow.getLastCellNum();
        if (titleRow.getCell(0).getCellType() != CellType.STRING) {
            throw new InvalidExcelException("There are no column values on first row.");
        }

        for (int i = 0; i < lastColUsed; i++) {
            String colName = titleRow.getCell(i).getStringCellValue();
            if (colName.equalsIgnoreCase("Heat ID")) {
                colIndentifiers.put(HEATIDIden, i);
            } else if (colName.equalsIgnoreCase("heat #")) {
                colIndentifiers.put(HEATNUMBERIden, i);
            } else if (colName.replaceAll(" ", "").equalsIgnoreCase("time")) {
                colIndentifiers.put(HEATSTARTTIMEIden, i);
            } else if (colName.replaceAll(" ", "").equalsIgnoreCase("category")) {
                colIndentifiers.put(HEATCATEGORYIden, i);
            } else if (colName.replaceAll(" ", "").equalsIgnoreCase("Day")) {
                colIndentifiers.put(HEATDAYIden, i);
            }
        }
    }

    // EFFECTS: evaluate the first row of the excel for teams sheet
    private void firstRowEvaluateTeams() throws InvalidExcelException {
        Row titleRow = teamsSheet.getRow(0);
        int lastColUsed = titleRow.getLastCellNum();
        if (titleRow.getCell(0).getCellType() != CellType.STRING) {
            throw new InvalidExcelException("There are no column values on first row.");
        }

        for (int i = 0; i < lastColUsed; i++) {
            String colName = titleRow.getCell(i).getStringCellValue();
            if (colName.replaceAll(" ", "").equalsIgnoreCase("teamID")) {
                colIndentifiers.put(TEAMIDIden, i);
            } else if (colName.equalsIgnoreCase("team number")) {
                colIndentifiers.put(TEAMNUMBERIden, i);
            } else if (colName.equalsIgnoreCase("team name")) {
                colIndentifiers.put(TEAMNAMEIden, i);
            } else if (colName.equalsIgnoreCase("Pool name")) {
                colIndentifiers.put(TEAMPOOLNAMEIden, i);
            } else if (colName.replaceAll(" ", "").equalsIgnoreCase("Day")) {
                colIndentifiers.put(TEAMDAYIden, i);
            } else if (colName.equalsIgnoreCase("run time")) {
                colIndentifiers.put(TEAMRUNTIMEIden, i);
            } else if (colName.equalsIgnoreCase("unit")) {
                colIndentifiers.put(TEAMUNITIden, i);
            }
        }
    }

    // EFFECTS: checks all the rows for data and adds teams
    private void addTeamsFromData() {
        for (Row row : teamsSheet) {

            XSSFCell cell = (XSSFCell) row.getCell(colIndentifiers.get(TEAMIDIden));

            if (cell.getCellType() == CellType.NUMERIC) {

                String teamName = row.getCell(colIndentifiers.get(TEAMNAMEIden)).getStringCellValue();
                String poolName = row.getCell(colIndentifiers.get(TEAMPOOLNAMEIden)).getStringCellValue();
                String teamUnit = row.getCell(colIndentifiers.get(TEAMUNITIden)).getStringCellValue();
                String teamDayString = row.getCell(colIndentifiers.get(TEAMDAYIden)).getStringCellValue();

                Heat heat = null;

                XSSFCell runCell = (XSSFCell) row.getCell(colIndentifiers.get(TEAMRUNTIMEIden));
                if (runCell.getCellType() == CellType.STRING) {
                    String string = runCell.getStringCellValue();

                    if (string.matches("\\d{2}:\\d{2}.*") && !string.isEmpty()) {
                        string = string.replaceAll(" ", "");
                        Calendar teamRunTime = Calendar.getInstance();
                        teamRunTime.clear();

                        if (string.matches(".*PM")) {
                            string = string.replaceAll("[a-zA-Z]", "");
                            int hour = Integer.parseInt(string.substring(0, string.indexOf(":")));
                            int minute = Integer.parseInt(string.substring(string.indexOf(":") + 1));

                            if (hour == 12) {
                                teamRunTime.set(Calendar.HOUR_OF_DAY, hour);
                            } else {
                                teamRunTime.set(Calendar.HOUR_OF_DAY, hour + 12);
                            }
                            teamRunTime.set(Calendar.MINUTE, minute);
                        } else if (string.matches(".*AM")) {
                            string = string.replaceAll("[a-zA-Z]", "");
                            int hour = Integer.parseInt(string.substring(0, string.indexOf(":")));
                            int minute = Integer.parseInt(string.substring(string.indexOf(":") + 1));
                            teamRunTime.set(Calendar.HOUR_OF_DAY, hour);
                            teamRunTime.set(Calendar.MINUTE, minute);
                        }

                        try {
                            heat = controller.getProgram().getProgramDays().get(teamDayString.substring(0, teamDayString.indexOf(","))).getHeatByStartTime(teamRunTime);
                        } catch (NoHeatWithStartTimeException e) {
                            alertLinkedList.add(new Alert(Alert.AlertType.WARNING, "Data import was successful, however there was an error while" +
                                    " importing the team: " + teamName + ". " + e.getMessage()));
                        }
                    }
                } else if (runCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(runCell)) {
                    Calendar teamRunTime = Calendar.getInstance();
                    Cell cellDate = row.getCell(colIndentifiers.get(TEAMRUNTIMEIden));
                    teamRunTime.setTime(cellDate.getDateCellValue());
                    try {
                        heat = controller.getProgram().getProgramDay(teamDayString.substring(0, teamDayString.indexOf(","))).getHeatByStartTime(teamRunTime);
                    } catch (NoHeatWithStartTimeException | NoDayException e) {
                        alertLinkedList.add(new Alert(Alert.AlertType.WARNING, "Data import was successful, however there was an error while" +
                                " importing the team: " + teamName + ". " + e.getMessage()));
                    }
                } else {
                    alertLinkedList.add(new Alert(Alert.AlertType.WARNING, "Data import was successful, however there was an error while" +
                            " importing the team: " + teamName + ". The Run Time format is incorrect and so the team was not able to connect to a heat."));
                }


                int teamNumber = (int) row.getCell(colIndentifiers.get(TEAMNUMBERIden)).getNumericCellValue();
                int teamID = (int) row.getCell(colIndentifiers.get(TEAMIDIden)).getNumericCellValue();

                Team team = new Team(poolName, teamNumber, teamName, teamID, teamUnit);
                if (heat != null) {
                    try {
                        team.addHeat(heat);
                    } catch (AddHeatException e) {
                        alertLinkedList.add(new Alert(Alert.AlertType.WARNING, "Data import was successful, however there was an error while" +
                                " importing the following data: " + e.getMessage()));
                    }
                    controller.getProgram().addTeam(team);
                }
            }
        }
    }

    // EFFECTS: checks all the rows for data and adds heats
    private void addHeatsFromData() {

        for (Row row : heatsSheet) {

            XSSFCell cell = (XSSFCell) row.getCell(colIndentifiers.get(HEATNUMBERIden));

            if (cell != null && cell.getCellType() == CellType.NUMERIC && cell.getNumericCellValue() > 0) {

                String category = row.getCell(colIndentifiers.get(HEATCATEGORYIden)).getStringCellValue();
                //int heatID = (int) row.getCell(HEATIDIden).getNumericCellValue(); TODO add this in
                int heatNumber = (int) row.getCell(colIndentifiers.get(HEATNUMBERIden)).getNumericCellValue();

                Calendar startTime = Calendar.getInstance();
                startTime.setTime(row.getCell(colIndentifiers.get(HEATSTARTTIMEIden)).getDateCellValue());

                String dayToRun = row.getCell(colIndentifiers.get(HEATDAYIden)).getStringCellValue();

                Day day = controller.getProgram().getProgramDayOrBuild(dayToRun);

                Heat heat = new Heat(startTime, category, heatNumber, day, row.getRowNum()); // TODO add the heat id not the row number

                try {
                    controller.getProgram().getProgramDays().get(dayToRun).addHeat(heat);
                } catch (AddHeatException e) {
                    alertLinkedList.add(new Alert(Alert.AlertType.WARNING, "Data import was successful, however there was an error while" +
                            " importing the following data: " + e.getMessage()));
                }
            }

        }
    }

}
