package IO;

import models.Day;
import models.Heat;
import models.Team;
import models.exceptions.AddHeatException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import ui.TimingController;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
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
    private HSSFWorkbook workbook;
    private HSSFSheet heatsSheet;
    private HSSFSheet teamsSheet;
    private Map<Integer, Integer> colIndentifiers;
    private TimingController controller;

// CONSTRUCTORS //

    public ExcelInput(FileInputStream fileInputStream, TimingController controller) {
        this.fileInputStream = fileInputStream;
        this.controller = controller;
        colIndentifiers = new HashMap<>();
        createFiles();

        firstRowEvaluateHeats();
        try {
            addHeatsFromData();
        } catch (AddHeatException e) {
            e.printStackTrace();
        }

        firstRowEvaluateTeams();
        try {
            addTeamsFromData();
        } catch (AddHeatException e) {
            e.printStackTrace();
        }
    }

// FUNCTIONS //

    // EFFECTS: starts all the file and var creations
    private void createFiles() {
        try {
            workbook = new HSSFWorkbook(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        heatsSheet = workbook.getSheet("Heats"); // NOTICE file sheet must be named Heats
        teamsSheet = workbook.getSheet("Teams"); // NOTICE file sheet must be names Teams
    }

    // EFFECTS: evaluate the first row of the excel for heat sheet
    private void firstRowEvaluateHeats() {
        Row titleRow = heatsSheet.getRow(0);
        int lastColUsed = titleRow.getLastCellNum();

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
    private void firstRowEvaluateTeams() {
        Row titleRow = teamsSheet.getRow(0);
        int lastColUsed = titleRow.getLastCellNum();

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
    private void addTeamsFromData() throws AddHeatException {
        for (Row row : teamsSheet) {

            HSSFCell cell = (HSSFCell) row.getCell(colIndentifiers.get(TEAMIDIden));

            if (cell.getCellTypeEnum() == CellType.NUMERIC) {

                String teamName = row.getCell(colIndentifiers.get(TEAMNAMEIden)).getStringCellValue();
                String poolName = row.getCell(colIndentifiers.get(TEAMPOOLNAMEIden)).getStringCellValue();
                String teamUnit = row.getCell(colIndentifiers.get(TEAMUNITIden)).getStringCellValue(); // TODO add unit to the team class
                String teamDayString = row.getCell(colIndentifiers.get(TEAMDAYIden)).getStringCellValue();

                Heat heat = null;

                HSSFCell runCell = (HSSFCell) row.getCell(colIndentifiers.get(TEAMRUNTIMEIden));
                if (runCell.getCellTypeEnum() != CellType.STRING) {
                    Calendar teamRunTime = Calendar.getInstance();
                    teamRunTime.setTime(row.getCell(colIndentifiers.get(TEAMRUNTIMEIden)).getDateCellValue());

                    heat = controller.getProgram().getProgramDays().get(teamDayString.substring(0, teamDayString.indexOf(","))).getHeatByStartTime(teamRunTime);
                }


                int teamNumber = (int) row.getCell(colIndentifiers.get(TEAMNUMBERIden)).getNumericCellValue();
                int teamID = (int) row.getCell(colIndentifiers.get(TEAMIDIden)).getNumericCellValue();

                Team team = new Team(poolName, teamNumber, teamName, teamID, teamUnit);
                if (heat != null) {
                    team.addHeat(heat);
                    controller.getProgram().addTeam(team);
                }
            }
        }
    }

    // EFFECTS: checks all the rows for data and adds heats
    private void addHeatsFromData() throws AddHeatException {

        for (Row row : heatsSheet) {

            HSSFCell cell = (HSSFCell) row.getCell(colIndentifiers.get(HEATNUMBERIden));

            if (cell != null && cell.getCellTypeEnum() == CellType.NUMERIC && cell.getNumericCellValue() > 0) {

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
