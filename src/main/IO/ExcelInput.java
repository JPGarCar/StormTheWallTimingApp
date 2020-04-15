package IO;

import javafx.scene.control.Alert;
import models.Day;
import models.Heat;
import models.Team;
import models.exceptions.AddRunException;
import models.exceptions.InvalidExcelException;
import models.exceptions.ErrorException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ui.UIAppLogic;
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
    private UIAppLogic controller;


    // Represents all the alerts to send because of errors
    private LinkedList<String> alertMessagesLinkedList;

// CONSTRUCTORS //

    public ExcelInput(FileInputStream fileInputStream, UIAppLogic controller) {
        this.fileInputStream = fileInputStream;
        this.controller = controller;
        colIndentifiers = new HashMap<>();
        alertMessagesLinkedList = new LinkedList<>();
    }

// FUNCTIONS //

    /**
     * Will begin the data import process from an excel file. Calling 3 helper functions.
     *
     * @param isHeats   True if user wants to import Heat(s) from the excel, false otherwise.
     * @param isTeams   True if user wants to import Team(s) from the excel, false otherwise.
     * @throws InvalidExcelException    from evaluateData()
     * @helper createFiles(), evaluateData(), dataTransition()
     */
    public void inputData(boolean isHeats, boolean isTeams) throws InvalidExcelException {
        createFiles(isHeats, isTeams);
        evaluateData(isHeats, isTeams);
        dataTransition(isHeats, isTeams);
    }

    /**
     * @return  the list of alerts generated during the inputData
     */
    public LinkedList<String> getAlerts() {
        return alertMessagesLinkedList;
    }

    /**
     * Will create the workbook and sheets variables from the input excel to work with.
     * For this to work the excel sheets must be names appropriately as shown in the code.
     *
     * @param isHeats   True if user wants to import Heat(s) from the excel, false otherwise.
     * @param isTeams   True if user wants to import Team(s) from the excel, false otherwise.
     */
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

    /**
     * Will call two helper functions to check the first row of every sheet for the key words. Key words are used to
     * know what data is located in what column. Each sheet has its unique set of key words.
     *
     * @param isHeats   True if user wants to import Heat(s) from the excel, false otherwise.
     * @param isTeams   True if user wants to import Team(s) from the excel, false otherwise.
     * @throws InvalidExcelException from firstRowEvaluateHeats() and firstRowEvaluateTeams()
     * @helper firstRowEvaluateHeat(), firstRowEvaluateTeams()
     */
    private void evaluateData(boolean isHeats, boolean isTeams) throws InvalidExcelException {
        if (isHeats) {
            firstRowEvaluateHeats();
        }
        if (isTeams) {
            firstRowEvaluateTeams();
        }
    }

    /**
     * Calls two helper functions to start the data import.
     *
     * @param isHeats   True if user wants to import Heat(s) from the excel, false otherwise.
     * @param isTeams   True if user wants to import Team(s) from the excel, false otherwise.
     * @helper addHeatsFromData(), addTeamsFromData()
     */
    private void dataTransition(boolean isHeats, boolean isTeams) {
        if (isHeats) {
            addHeatsFromData();
        }
        if (isTeams) {
            addTeamsFromData();
        }
    }

    /**
     * Will evaluate the first row of the Heat excel sheet to look for the key words ignoring case.
     * Key words:
     * - Heat ID
     * - heat #
     * - time
     * - category
     * - day
     *
     * The key words are then used to know what data is in what column. The key word must be used appropriately or the
     * program will not work. The number of the Column associated to each key word is placed in the colIdentifier Map
     * using the final variable associated to the key word as a Map key.
     *
     * @throws InvalidExcelException    if the first row does not contain pure String values aka the key words.
     */
    private void firstRowEvaluateHeats() throws InvalidExcelException {
        Row titleRow = heatsSheet.getRow(0);
        int lastColUsed = titleRow.getLastCellNum();

        // check for strings in the first row
        if (titleRow.getCell(0).getCellType() != CellType.STRING) {
            throw new InvalidExcelException("Could not find the key words in the first row of the sheet.");
        }

        // iterate over the row looking for the key words
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

    /**
     * Will evaluate the first row of the Team excel sheet to look for the key words ignoring case.
     * Key words:
     * - teamID
     * - team number
     * - team name
     * - pool name
     * - day
     * - run time
     * - unit
     *
     * The key words are then used to know what data is in what column. The key word must be used appropriately or the
     * program will not work. The number of the Column associated to each key word is placed in the colIdentifier Map
     * using the final variable associated to the key word as a Map key.
     *
     * @throws InvalidExcelException    if the first row does not contain pure String values aka the key words.
     */
    private void firstRowEvaluateTeams() throws InvalidExcelException {
        Row titleRow = teamsSheet.getRow(0);
        int lastColUsed = titleRow.getLastCellNum();

        // check for Strings in the first row
        if (titleRow.getCell(0).getCellType() != CellType.STRING) {
            throw new InvalidExcelException("There are no column values on first row.");
        }

        // iterate over the first row looking for the key words
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

    /**
     * Will check every row of the teamSheet and grab the data from every cell in the row as specified from the
     * colIdentifiers Map.
     *
     * <p>Will first check if the team ID cell is filled, if it is not the program will not
     * grab data from the row and skip it. Then it will grab most of the data. The hard part comes when deciphering
     * the Team's run time. The hard part is that the time could be set as a string, a number, or a time. All three are
     * different type of data for an excel sheet and so we have to treat the string option different from the time or
     * number option. First part of the conditional deals with string option, second deals with number or time option.
     * There is a fail safe, if the time is not imported correctly, a Heat is not grabbed and so the entire Team is not
     * imported. An error message is sent to the user, he can try to change the data type and do the import again.</p>
     */
    private void addTeamsFromData() {
        for (Row row : teamsSheet) {

            XSSFCell cell = (XSSFCell) row.getCell(colIndentifiers.get(TEAMIDIden));

            // check that the row has data by ensuring there is a numeric value in the team id cell
            if (cell.getCellType() == CellType.NUMERIC) {

                // most of the data imported
                String teamName = row.getCell(colIndentifiers.get(TEAMNAMEIden)).getStringCellValue();
                String poolName = row.getCell(colIndentifiers.get(TEAMPOOLNAMEIden)).getStringCellValue();
                String teamUnit = row.getCell(colIndentifiers.get(TEAMUNITIden)).getStringCellValue();
                String teamDayString = row.getCell(colIndentifiers.get(TEAMDAYIden)).getStringCellValue();
                int teamNumber = (int) row.getCell(colIndentifiers.get(TEAMNUMBERIden)).getNumericCellValue();
                int teamID = (int) row.getCell(colIndentifiers.get(TEAMIDIden)).getNumericCellValue();

                // work to get the Team run time begins here and ends after the if-elseif-else
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
                        } catch (ErrorException e) {
                            alertMessagesLinkedList.add("Data import was successful, however there was an error while" +
                                    " importing the team: " + teamName + ". " + e.getMessage());
                        }
                    }
                } else if (runCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(runCell)) {
                    Calendar teamRunTime = Calendar.getInstance();
                    Cell cellDate = row.getCell(colIndentifiers.get(TEAMRUNTIMEIden));
                    teamRunTime.setTime(cellDate.getDateCellValue());
                    try {
                        heat = controller.getProgram().getProgramDay(
                                teamDayString.substring(0, teamDayString.indexOf(","))).getHeatByStartTime(teamRunTime);
                    } catch (ErrorException e) {
                        alertMessagesLinkedList.add("Data import was successful, however there was an error while" +
                                " importing the team: " + teamName + ". " + e.getMessage());
                    }
                } else {
                    alertMessagesLinkedList.add("Data import was successful, however there was an error while" +
                            " importing the team: " + teamName + ". The Run Time format is incorrect and so the " +
                            "team was not able to connect to a heat.");
                }

                // final step, creation of the team and import to the Program
                Team team = new Team(poolName, teamNumber, teamName, teamID, teamUnit);
                if (heat != null) {
                    try {
                        team.addRunFromHeat(heat);
                    } catch (AddRunException e) {
                        alertMessagesLinkedList.add("Data import was successful, however there was an error while" +
                                " importing the following data: " + e.getMessage());
                    }
                    controller.getProgram().addTeam(team);
                }
            }
        }
    }

    /**
     * Will check every row of the Heat's sheet to import data. Before importing data from a row, it will check
     * to see if there is a Heat number in the appropriate cell. If there is nothing found or the data is non
     * numeric the row will not be imported.
     */
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

                controller.getProgram().getProgramDays().get(dayToRun).addHeat(heat);
            }

        }
    }

}
