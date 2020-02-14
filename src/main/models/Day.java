package models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sun.istack.internal.NotNull;
import models.exceptions.AddHeatException;
import models.exceptions.CanNotUndoHeatException;
import models.exceptions.NoHeatWithIDException;

import java.util.ArrayList;
import java.util.Calendar;

public class Day {
    //private vars
    private Calendar dayToRun;
    private int dayNumber;
    private int atHeat;

    // private connections
    @JsonManagedReference
    private ArrayList<Heat> heats;

    // DUMMY CONSTRUCTOR, used by Jackson JSON
    public Day() {
        heats = new ArrayList<>();
    }

    // CONSTRUCTOR
    public Day(@NotNull Calendar dayToRun, @NotNull int dayNumber) {
        this.dayToRun = dayToRun;
        heats = new ArrayList<Heat>();
        this.dayNumber = dayNumber;
        atHeat = 1;
    }

    // SETTERS AND GETTERS, used by Jackson JSON
    public Calendar getDayToRun() {
        return dayToRun;
    }

    public void setHeats(ArrayList<Heat> heats) {
        this.heats = heats;
    }

    public void setAtHeat(@NotNull int atHeat) {
        this.atHeat = atHeat;
    }

    public int getAtHeat() {
        return atHeat;
    }

    public void atNextHeat() {
        atHeat++;
    }

    public void setDayToRun(@NotNull Calendar dayToRun) {
        this.dayToRun = dayToRun;
    }

    public void setDayNumber(@NotNull int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public int getDayNumber(){
        return dayNumber;
    }

    // EFFECTS: returns the month/day/year of the Day
    public String toString() {
        return dayToRun.get(Calendar.MONTH) + "/" + dayToRun.get(Calendar.DAY_OF_MONTH) + "/" + dayToRun.get(Calendar.YEAR);
    }

    // EFFECTS: Return the Heat list
    public ArrayList<Heat> getHeats() {
        return heats;
    }

    // EFFECTS: add a heat to the list of heats
    public void addHeat(Heat heat) throws AddHeatException {
        if (!heats.contains(heat)) {
            heats.add(heat);
            heat.setDayToRace(this);
        } else {
            throw new AddHeatException("because this heat is already in this day. The ID's match.");
        }
    }

    // EFFECTS: adds all the heats to this day
    public void addHeats(ArrayList<Heat> heats) throws AddHeatException {
        for (Heat heat : heats) {
            addHeat(heat);
        }
    }

    // EFFECTS: remove the heat from this day
    public void removeHeat(Heat heat) {
        heats.remove(heat);
        // TODO delete the heat object by setting everything to null
    }

    // EFFECTS: Return the number of heats in the day
    public int numberOfHeats(){
        return heats.size();
    }

    // EFFECTS: returns heat with specific heat id
    public Heat getHeatByID(int id) throws NoHeatWithIDException {
        for (Heat heat : heats) {
            if (heat.getHeatNumber() == id) {
                return heat;
            }
        }
        throw new NoHeatWithIDException();
    }

    // EFFECTS: will delete start time for teams that heat just started, set heat to not started
    public void undoLastHeatStart() throws NoHeatWithIDException, CanNotUndoHeatException {
        getHeatByID(atHeat - 1).undoHeatStart();
        atHeat --;
    }

}