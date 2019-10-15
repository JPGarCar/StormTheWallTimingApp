package models;

import java.util.ArrayList;
import java.util.Calendar;

public class Day {
    //private vars
    private Calendar day;

    // private connections
    private ArrayList<Heat> heats;

    public Day(Calendar day) {
        this.day = day;
        heats = new ArrayList<Heat>();
    }

    // EFFECTS: return the day field
    public Calendar getDay() {
        return day;
    }

    // EFFECTS: returns the month/day/year of the Day
    public String toString() {
        return day.get(Calendar.MONTH) + "/" + day.get(Calendar.DAY_OF_MONTH) + "/" + day.get(Calendar.YEAR);
    }
}