package models;

import java.util.Calendar;

public class FinalTime {
    // private vars
    private long millisecondOfSet;
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

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public void setMillisecondOfSet(long millisecondOfSet) {
        this.millisecondOfSet = millisecondOfSet;
    }

    public void setMilliseconds(int milliseconds) {
        this.milliseconds = milliseconds;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public void setStopTime(Calendar stopTime) {
        this.stopTime = stopTime;
    }

    public long getMillisecondOfSet() {
        return millisecondOfSet;
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
        long milliStart = startTime.getTimeInMillis();
        long milliEnd = stopTime.getTimeInMillis();
        millisecondOfSet = milliEnd - milliStart;
    }

    // MODIFIES: this
    // EFFECTS: will calculate the seconds, minutes and milliseconds of the final time
    private void calculateFinalTime() {
        seconds = (int) (millisecondOfSet / 1000);
        minutes = (seconds / 60);
        seconds = seconds % 60;
        milliseconds = (int) (millisecondOfSet % 1000);
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

    public Calendar getStartTime() {
        return startTime;
    }

    public Calendar getStopTime() {
        return stopTime;
    }

    @Override
    public String toString() {
        return "" + getMinutes() + ":" + getSeconds() + ":" + getMilliseconds();
    }
}