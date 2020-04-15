package models;

import com.sun.istack.internal.NotNull;
import models.exceptions.ErrorException;

import java.util.*;

/**
 * <h3>Represents</h3> a race in the real world. Top most class of the application. It has a list of all
 * the Team(s) involved, as well as a list of all the Day(s) the race will run. The Day(s) include Heat(s) and Run(s).
 * The Program also controls the wait list, a list to be used to hold Run(s) for which the teams might have missed and
 * are going to be assigned later.
 *
 * <h3>Purpose:</h3> Control the structure of the race, analogous to a race class.
 *
 * <h3>Contains:</h3>
 *   - A TreeMap of all the teams available in the program - TreeMap<Integer, Team>
 *   - A TreeMap of teams that are currently wait listed by the user - TreeMap<Integer, Team>
 *   - A TreeMap of all the days available in the program - TreeMap<String, Day>
 *
 * <h3>Usage:</h3>
 *   - Add and remove team Run(s) from the wait list
 *   - General list of all the Team(s) to be accessed by anyone
 *   - Add and remove Day(s) to the Program
 *   - Able to search for a Day and if not found, build a new Day
 *
 * <h3>Persistence:</h3>
 *   - This class is not in the database, it will get built every time the program starts using db data
 *   - Must keep all three map lists persistent with db
 */
public class Program {

// VARIABLES //

    /**
     * A Map of all the Day(s) associated to this Program, aka a list of all the Day(s) the race is going to happen.
     * There are no duplicate Day(s).
     */
    private Map<String, Day> programDays;

    /**
     * A Map of all the Teams in this program. This is a general repository of all the Teams participating in the
     * race. There are no duplicate Team(s) in the Map.
     */
    private Map<Integer, Team> allTeams;

    /**
     * A Map with all the Run(s) that have been wait listed. This list is supposed to be unique for every day but for
     * now we will keep it as general for the entire race, aka Program.
     */
    private Map<RunNumber, Run> waitList;

// CONSTRUCTORS //

    public Program() {
        programDays = new TreeMap<>();
        allTeams = new TreeMap<Integer, Team>();
        waitList = new TreeMap<RunNumber, Run>();
    }

// GETTERS AND SETTERS //

    public Map<String, Day> getProgramDays() {
        return programDays;
    }

    public Map<Integer, Team> getAllTeams() {
        return allTeams;
    }


    public Map<RunNumber, Run> getWaitList() {
        return waitList;
    }

    public void setAllTeams(Map<Integer, Team> allTeams) {
        this.allTeams = allTeams;
    }

    public void setProgramDays(Map<String, Day> programDays) {
        this.programDays = programDays;
    }

    public void setWaitList(Map<RunNumber, Run> waitList) {
        this.waitList = waitList;
    }

// FUNCTIONS //

    /**
     * Associate a Day to the Program by adding the Dat to the programDays Map using the Day's dayToRun (a string) as
     * the key.
     *
     * @param day   Day to be associated with this Program.
     */
    public void addDay(@NotNull Day day) {
        programDays.put(day.getDayToRun(), day);
    }

    /**
     *  Associate a Team to the Program by adding the Team to the allTeams Map using the Team's number as a key.
     *
     * @param team  Team to be associated to this Program.
     */
    public void addTeam(@NotNull Team team) {
        allTeams.put(team.getTeamNumber(), team);
    }

    /**
     * Will move a Run to the wait list by adding it to the waitList Map using the Run's RunNumber as a key.
     *
     * @param run   Run to be moved to the wait list.
     */
    public void addRunToWaitList(@NotNull Run run) {
        waitList.put(run.getRunNumber(), run);
    }


    /**
     * Removes a Run from the wait list by removing it from the waitList Map.
     *
     * @param runNumber RunNumber linked to the Run to be removed from the wait list.
     */
    public void removeRunFromWaitList(@NotNull RunNumber runNumber) {
        waitList.remove(runNumber);
    }

    /**
     * Will first try to return a Day from this Program by searching the programDays Map with a String key. If the
     * key returns no Day, then a new Day is created and added to this Program.
     *
     * @param programDay    String that links to a possible Day in this Program, or the key and program Day
     *                          for the new Day.
     * @return  A Day, either from the programDays or a new Day just added to programDays.
     */
    public Day getProgramDayOrBuild(String programDay) {
        if (programDays.get(programDay) != null) {
            return programDays.get(programDay);
        } else {
            Day day = new Day(programDay, programDays.size());
            addDay(day);
            return day;
        }
    }

    /**
     * Will return the Day from the programDays Map with the key imputed as a parameter. If there is no Day linked to
     * the key imputed, then an exception is thrown.
     *
     * @param programDay    String to be used to search for the Day, this string should be a key in the progarmDays Map.
     * @return  The Day linked to the imputed String.
     * @throws ErrorException   If the imputed String is not a key in the programDays Map.
     */
    public Day getProgramDay(String programDay) throws ErrorException {
        if (programDays.get(programDay) != null) {
            return programDays.get(programDay);
        } else {
            throw new ErrorException("We could not find the day specified: " + programDay + " in the program.");
        }
    }


}
