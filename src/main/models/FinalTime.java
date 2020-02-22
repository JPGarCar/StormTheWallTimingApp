package models;

import com.sun.istack.internal.NotNull;
import models.exceptions.CouldNotCalculateFinalTimeExcpetion;

import java.util.Calendar;

/*
    This class is used to calculate the total time it took a team to finish
    Purpose: Calculate the total time it took a team to finish using start and end time Calendars
    Contains:
    - The difference between start and stop times in milliseconds - long
    - The actual minutes, seconds and milliseconds it took the team to finish - int x3
    - The start and stop time of the team - Calendar x2

    Usage:
    -

    Persistence:
    - No persistence of this class, the data is embedded in the Run class
 */

public class FinalTime {

// VARIABLES //

    // Represents the millisecond difference between the start and stop time
    private long millisecondOfSet;

    // Represents the minutes, seconds and milliseconds it took the team to finish
    private int minutes;
    private int seconds;
    private int milliseconds;

    // Represents the start and stop times as calendars
    private Calendar startTime;
    private Calendar stopTime;

// CONSTRUCTORS //

    // DUMMY CONSTRUCTOR for Jackson JSON
    public FinalTime() {}

    // CONSTRUCTOR
    public FinalTime(@NotNull Calendar startTime, @NotNull Calendar stopTime) throws CouldNotCalculateFinalTimeExcpetion {
        this.startTime = startTime;
        this.stopTime = stopTime;
        calculate();
    }

// SETTERS AND GETTERS, used for Jackson JSON //

    public void setStartTime(@NotNull Calendar startTime) {
        this.startTime = startTime;
    }

    public void setMillisecondOfSet(@NotNull long millisecondOfSet) {
        this.millisecondOfSet = millisecondOfSet;
    }

    public void setMilliseconds(@NotNull int milliseconds) {
        this.milliseconds = milliseconds;
    }

    public void setMinutes(@NotNull int minutes) {
        this.minutes = minutes;
    }

    public void setSeconds(@NotNull int seconds) {
        this.seconds = seconds;
    }

    public void setStopTime(@NotNull Calendar stopTime) {
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

// FUNCTIONS //

    // MODIFIES: startTime, stopTime
    // EFFECTS: add a new start and stop time
    public void addTimes(@NotNull Calendar startTime, @NotNull Calendar stopTime) {
        this.startTime = startTime;
        this.stopTime = stopTime;
    }

    // EFFECTS: public function to call calculateSeconds and calculateFinalTime
    public void calculate() throws CouldNotCalculateFinalTimeExcpetion {
        if (startTime == null || stopTime == null) {
            throw new CouldNotCalculateFinalTimeExcpetion();
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