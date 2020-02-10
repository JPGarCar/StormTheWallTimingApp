package models;

import models.exceptions.CouldNotCalculateFinalTimeExcpetion;

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

    // DUMMY CONSTRUCTOR for Jackson JSON
    public FinalTime() {}

    // CONSTRUCTOR
    public FinalTime(Calendar startTime, Calendar stopTime) throws CouldNotCalculateFinalTimeExcpetion {
        this.startTime = startTime;
        this.stopTime = stopTime;
        calculate();
    }

    // SETTERS AND GETTERS, used for Jackson JSON
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

    // MODIFIES: startTime, stopTime
    // EFFECTS: add a new start and stop time
    public void addTimes(Calendar startTime, Calendar stopTime) {
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    // EFFECTS: public function to call calculateSeconds and calculateFinalTime
    public void calculate() throws CouldNotCalculateFinalTimeExcpetion {
        if (startTime == null) {
            throw new CouldNotCalculateFinalTimeExcpetion("start time");
        } else if (stopTime == null) {
            throw new CouldNotCalculateFinalTimeExcpetion("stop time");
        }
        calculateFinalTime();
    }

    // MODIFIES: millisecondOfSet
    // EFFECTS : will calculate the time difference between the start and stop time
    // EXPECTS : start time should be before the stop time in terms of real time
    private void calculateSeconds() {
        long milliStart = startTime.getTimeInMillis();
        long milliEnd = stopTime.getTimeInMillis();
        millisecondOfSet = milliEnd - milliStart;
    }

    // MODIFIES: seconds, minutes, milliseconds
    // EFFECTS: will calculate the seconds, minutes and milliseconds of the final time
    private void calculateFinalTime() {
        calculateSeconds();
        seconds = (int) (millisecondOfSet / 1000);
        minutes = (seconds / 60);
        seconds = seconds % 60;
        milliseconds = (int) (millisecondOfSet % 1000);
    }

    @Override
    public String toString() {
        return "" + getMinutes() + ":" + getSeconds() + ":" + getMilliseconds();
    }
}