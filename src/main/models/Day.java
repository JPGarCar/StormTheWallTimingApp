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

/*
    Represents a day of the event and everything that it entails
    Purpose: Control the heats to be run during that day.
    Contains:
    - String representation of the day, ex "Saturday" - String
    - Integer representation of the day, starts at 1 in chronological order - int
    - Next heat number to be staged - int
    - All the heats that will run during this day, key is the heat´s number - Map<Integer, Heat>

    Usage:
    - main timing controller to advance current heat (atHeat)
    - main timing controller to get heat by its number
    - main timing controller to undo the last heat start
    - Heat to add/remove a heat to this day

    Persistence:
    - Class is an entity in the db in table name "day_table"
    - atHeat and heats will be changing throughout the program life, dayToRun and dayNumber not much
        will have to keep persistent with db
    - One to Many relation with Heat
 */


@Entity
@Table(name = "day_table")
public class Day {

// VARIABLES //

    // Represents the day in string, ex "Saturday"
    private String dayToRun;

    // Represents the day in an int for easier access starting at 1
    @Id
    private int dayNumber;

    // Represents the next heat to be staged in this day by its heat number
    private int atHeat;

    // All the heats to be run during this day
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

    // EFFECTS: returns the month/day/year of the Day
    @Override
    public String toString() {
        return dayToRun;
    }

    // EFFECTS: increase atHeat by one, to move to next heat
    public void goToNextHeat() {
        atHeat++;
    }

    // EFFECTS: add a heat to the list of heats
    public void addHeat(@NotNull Heat heat) throws AddRunException {
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

    // EFFECTS: adds all the heats to this day
    public void addHeats(@NotNull ArrayList<Heat> heats) throws AddRunException {
        for (Heat heat : heats) {
            addHeat(heat);
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

    // EFFECTS: will delete start time for teams that heat just started, set heat to not started
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

    // EFFECTS: remove a heat from the heats TreeMap
    public void removeHeatByHeatNumber(int heatNumber) {
        heats.remove(heatNumber);
    }
}