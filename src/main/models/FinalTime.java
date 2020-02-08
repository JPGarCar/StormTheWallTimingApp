package models;

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

    public FinalTime(Calendar startTime, Calendar stopTime) {
        this.startTime = startTime;
        this.stopTime = stopTime;
        calculateSeconds();
        calculateFinalTime();
    }

    // MODIFIES: this
    // EFFECTS: add a new start and stop time
    public void addTimes(Calendar startTime, Calendar stopTime) {
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    // EFFECTS: public function to call calculateSeconds and calculateFinalTime
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

    // getters
    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getMilliseconds() {
        return milliseconds;
    }



}