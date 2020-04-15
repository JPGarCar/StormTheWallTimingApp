package models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sun.istack.internal.NotNull;
import models.exceptions.*;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a day of the race. It will contain all the Heat(s) to run during the day, will also keep track of
 * the next heat number. Will also deal with what needs to happen when the past Heat has to undo.
 *
 * There can not be any duplicate Heat(s) associated to a Day.
 *
 * Heat(s) numbers must follow a numerical order without jumping any numbers, aka 101 -> 102 -> 103, etc no 101 -> 103
 *
 * Purpose: Control the Heat(s) to be run during that day.
 *
 * Contains:
 *   - String representation of the day, ex "Saturday" - String
 *   - Integer representation of the day, starts at 1 in chronological order - int
 *   - Next heat number to be staged - int
 *   - All the heats that will run during this day, key is the heat´s number - Map<Integer, Heat>
 *
 * Usage:
 *   - main timing controller to advance current heat (atHeat)
 *   - main timing controller to get heat by its number
 *   - main timing controller to undo the last heat start
 *   - Heat to add/remove a heat to this day
 *
 *
 * Persistence:
 *   - Class is an entity in the db in table name "day_table"
 *   - atHeat and heats will be changing throughout the program life, dayToRun and dayNumber not much
 *       will have to keep persistent with db
 *   - One to Many relation with Heat, aka a Day can have many Heat(s) but every Heat can only have one Day associated
 */
@Entity
@Table(name = "day_table")
public class Day {

// VARIABLES //

    /**
     * Represents the day in string, ex "Saturday", it is used to input data from excel so these Strings need to be
     * unique and any spelling mistake can make a difference.
     */
    private String dayToRun;

    /**
     * Represents the day in an int for easier access starting at 1
     */
    @Id
    private int dayNumber;

    /**
     * Represents the next heat to be staged in this day by its heat number
     */
    private int atHeat;

    /**
     * All the heats to be run during this day. It is a one to many relation with Heat class.
     */
    @OneToMany
    @JsonManagedReference
    private Map<Integer, Heat> heats;

// CONSTRUCTORS //

    // DUMMY CONSTRUCTOR, used by Jackson JSON
    public Day() {
        heats = new HashMap<>();
    }

    // CONSTRUCTOR
    public Day(@NotNull String dayToRun, @NotNull int dayNumber) {
        this.dayToRun = dayToRun;
        heats = new HashMap<>();
        this.dayNumber = dayNumber;
        atHeat = -1;
    }

// SETTERS AND GETTERS, used by Jackson JSON

    public void setDayToRun(@NotNull String dayToRun) {
        this.dayToRun = dayToRun;
    }

    public void setDayNumber(@NotNull int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public void setHeats(@NotNull Map<Integer, Heat> heats) {
        this.heats = heats;
    }

    public void setAtHeat(@NotNull int atHeat) {
        this.atHeat = atHeat;
    }

    public int getAtHeat() {
        return atHeat;
    }

    public Map<Integer, Heat> getHeats() {
        return heats;
    }

    public int getDayNumber(){
        return dayNumber;
    }

    public String getDayToRun() {
        return dayToRun;
    }

// FUNCTIONS //

    /**
     * @return String with the string representation of the Day.
     */
    @Override
    public String toString() {
        return dayToRun;
    }

    /**
     * Will increase atHeat by one to move to the next heat to stage. This assumes the Heat's numbers are in order and
     * do not jump any numbers.
     */
    public void goToNextHeat() {
        atHeat++;
    }

    /**
     * Will associate a Heat with this Day by adding it to the heats Map using the Heat's number as a key.
     * Will also associate this Day to the Heat by calling the Heat's setDayToRace() function.
     * Makes sure this Heat has not been added already to this Day as there can not be any duplicate Heat(s).
     *
     * @param heat  Heat to be associated to this Day.
     */
    public void addHeat(@NotNull Heat heat) {
        if (!heats.containsKey(heat.getHeatNumber())) {
            heats.put(heat.getHeatNumber(), heat);
            heat.setDayToRace(this);
            if (atHeat == -1) {
                atHeat = heat.getHeatNumber();
            }
        } else {
            // nothing because we expect this due to one to one connection
        }
    }

    /**
     * Will search the Day's heats Map and return the Heat linked to the imputed key. If the imputed key is not in
     * the Map then an exception is thrown.
     *
     * @param heatNumber    int to be used as key for the heats Map that is linked to a Heat.
     * @return  The Heat linked to the imputed key.
     * @throws CriticalErrorException    if the imputed key is not in the Heats Map, aka the Heat we are looking for
     *                                  does not exist.
     */
    public Heat getHeatByHeatNumber(@NotNull int heatNumber) throws CriticalErrorException {
        Heat heat = heats.get(heatNumber);
        if (heat == null) {
            throw new CriticalErrorException(" We could not find a heat with heat number: " + heatNumber
                    + " in the Day: " + dayToRun);
        }
        return heat;
    }

    /**
     * Will call the undoHeatStart() function of the previous Heat by subtracting one from the atHeat variable. This
     * assumes that the Heat(s) numbers are in order and do not jump any numbers.
     *
     * @throws CriticalErrorException from getHeatByHeatNumber()
     * @throws CanNotUndoHeatException from undoHeatStart()
     */
    public void undoLastHeatStart() throws CriticalErrorException, CanNotUndoHeatException {
        getHeatByHeatNumber(atHeat - 1).undoHeatStart();
        atHeat --;
    }

    /**
     * Will search the heats Map to find a Heat that has the startTime imputed. For the startTime to be the same
     * we look at the HOUR_OF_DAY and MINUTE Calendar properties to be the same.
     *
     * @param calendar  Calendar to be used to search for the Heat.
     * @return  The Heat with its startTime equal to the imputed Calendar.
     * @throws ErrorException If there is no Heat with the searched start time.
     */
    public Heat getHeatByStartTime(Calendar calendar) throws ErrorException {
        for (Heat heat : heats.values()) {
            if (heat.getScheduledTime().get(Calendar.HOUR_OF_DAY) == calendar.get(Calendar.HOUR_OF_DAY) &&
            heat.getScheduledTime().get(Calendar.MINUTE) == calendar.get(Calendar.MINUTE)) {
                return heat;
            }
        }

        // Used to format the time strings to two digits
        DecimalFormat decimalFormat = new DecimalFormat("00");
        throw new ErrorException(" We could not find a heat with the start time: " +
                decimalFormat.format(calendar.get(Calendar.HOUR_OF_DAY)) + ":" +
                decimalFormat.format(calendar.get(Calendar.MINUTE)) +
                ". Day affected: " + dayToRun);
    }

    /**
     * Will disassociate a Heat from this Day by removing it from the heats Map and setting the Heat's Day to null.
     *
     * @param heatNumber    int that connects to the Heat we want to disassociate from this Day.
     */
    public void removeHeatByHeatNumber(int heatNumber) {
        Heat heat = heats.remove(heatNumber);
        heat.setDayToRace(null);
    }
}