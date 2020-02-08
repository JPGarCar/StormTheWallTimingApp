package models;

import java.util.ArrayList;
import java.util.Calendar;

public class Day {
    //private vars
    private Calendar day;
    private int dayNumber;

    // private connections
    private ArrayList<Heat> heats;

    public Day(Calendar day, int dayNumber) {
        this.day = day;
        heats = new ArrayList<Heat>();
        this.dayNumber = dayNumber;
    }

    // EFFECTS: return the day var
    public Calendar getDay() {
        return day;
    }

    // EFFECTS: returns the month/day/year of the Day
    public String toString() {
        return day.get(Calendar.MONTH) + "/" + day.get(Calendar.DAY_OF_MONTH) + "/" + day.get(Calendar.YEAR);
    }

    // EFFECTS: Return the Heat list
    public ArrayList<Heat> getHeats() {
        return heats;
    }

    // EFFECTS: add a heat to the list of heats
    public void addHeat(Heat heat) {
        heats.add(heat);
    }

    // EFFECTS: Return the number of heats in the day
    public int numberOfHeats(){
        return heats.size();
    }

    // EFFECTS: return day number int
    public int getDayNumber(){
        return dayNumber;
    }

}