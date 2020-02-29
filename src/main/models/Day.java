package models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sun.istack.internal.NotNull;
import models.exceptions.AddHeatException;
import models.exceptions.CanNotUndoHeatException;
import models.exceptions.NoHeatWithIDException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/*
    Represents a day of the event and everything that it entails
    Purpose: Control the heats to be run during that day.
    Contains:
    - Calendar representation of the day - Calendar
    - Integer representation of the day, starts at 1 in chronological order - int
    - Next heat number to be staged - int
    - All the heats that will run during this day - Map<Integer, Heat>

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
    public void atNextHeat() {
        atHeat++;
    }

    // EFFECTS: add a heat to the list of heats
    public void addHeat(@NotNull Heat heat) throws AddHeatException {
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
    public void addHeats(@NotNull ArrayList<Heat> heats) throws AddHeatException {
        for (Heat heat : heats) {
            addHeat(heat);
        }
    }

    // EFFECTS: remove the heat from this day by the heats number
    public void removeHeat(@NotNull int heatNumber) {
        heats.remove(heatNumber);
        // TODO delete the heat object by setting everything to null
    }

    // EFFECTS: Return the number of heats in the day
    public int numberOfHeats(){
        return heats.size(); // TODO, if not used delete
    }

    // EFFECTS: returns heat with specific heat number
    public Heat getHeatByHeatNumber(@NotNull int heatNumber) throws NoHeatWithIDException {
        Heat heat = heats.get(heatNumber);
        if (heat == null) {
            throw new NoHeatWithIDException();
        }
        return heat;
    }

    // EFFECTS: will delete start time for teams that heat just started, set heat to not started
    public void undoLastHeatStart() throws NoHeatWithIDException, CanNotUndoHeatException {
        getHeatByHeatNumber(atHeat - 1).undoHeatStart();
        atHeat --;
    }

    // EFFECTS: return a heat by its start time
    public Heat getHeatByStartTime(Calendar calendar) {
        for (Heat heat : heats.values()) {
            if (heat.getTimeToStart().get(Calendar.HOUR_OF_DAY) == calendar.get(Calendar.HOUR_OF_DAY) &&
            heat.getTimeToStart().get(Calendar.MINUTE) == calendar.get(Calendar.MINUTE)) {
                return heat;
            }
        }
        return null; // TODO return exception
    }
}