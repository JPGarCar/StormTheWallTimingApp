package models;

import com.sun.istack.internal.localization.NullLocalizable;

import java.util.Calendar;

public class FinalTime {
    // private vars
    private int millisecondOfSet;
    private int minutes;
    private int seconds;
    private int milliseconds;

    // private time vars
    private Calendar startTime;
    private Calendar stopTime;

    public FinalTime() {
    }

    public FinalTime(Calendar startTime, Calendar stopTime) {
        this.startTime = startTime;
        this.stopTime = stopTime;
        calculateSeconds();
        calculateFinalTime();
    }

    public void addTimes(Calendar startTime, Calendar stopTime) {
        this.startTime = startTime;
        this.stopTime = stopTime;
    }


    public void calculate() {
        if (startTime != null && stopTime != null) {
            calculateSeconds();
            calculateFinalTime();
        }
    }

    // MODIFIES: this.numSeconds
    // EFFECTS : will calculate the time difference between the start and stop time
    // EXPECTS : start time should be before the stop time in terms of real time
    private void calculateSeconds() {
        int millisecondOfSet = startTime.compareTo(stopTime);
    }

    // MODIFIES: this
    // EFFECTS: will calculate the seconds, minutes and milliseconds of the final time
    private void calculateFinalTime() {
        seconds = (millisecondOfSet / 1000);
        minutes = (seconds / 60);
        seconds = seconds % 60;
        milliseconds = millisecondOfSet % 1000;

    }

}