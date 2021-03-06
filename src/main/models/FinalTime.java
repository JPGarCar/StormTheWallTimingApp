package models;

import com.sun.istack.internal.NotNull;
import models.exceptions.CriticalErrorException;

import java.util.Calendar;

/**
 * This class is used to calculate the total time it took a team to finish
 *
 * <h3>Purpose:</h3> Calculate the total time it took a {@link Team} to finish using start and end time Calendars
 *
 * <h3>Contains:</h3>
 *   - The difference between start and stop times in milliseconds - long
 *   - The actual minutes, seconds and milliseconds it took the team to finish - int x3
 *   - The start and stop time of the team - Calendar x2
 *
 * <h3>Usage:</h3>
 *   - calculate the time it took a team to finish a Run, aka final time
 *   - display the final time in minutes, seconds, and milliseconds, all as a string
 *
 * <h3>Persistence:</h3>
 *   - No persistence of this class, the data is embedded in the Run class
 */
public class FinalTime {

// VARIABLES //

    /**
     * Represents the millisecond difference between the start and stop time
     */
    private long millisecondOfSet;

    /**
     * Represents the minutes, seconds and milliseconds it took the team to finish
     */
    private int minutes;
    private int seconds;
    private int milliseconds;

    /**
     * The Heat's start time and the Run's stop time as Calendars, kept for future reference and for log purposes.
     */
    private Calendar startTime;
    private Calendar stopTime;

// CONSTRUCTORS //

    // DUMMY CONSTRUCTOR for Jackson JSON
    public FinalTime() {}

    // CONSTRUCTOR
    public FinalTime(@NotNull Calendar startTime, @NotNull Calendar stopTime) throws CriticalErrorException {
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

    /**
     * Public function to calculate the final time of a Run. After this we can call the toString method and get the
     * correct final time of a Run.
     *
     * @throws CriticalErrorException   if startTime or stopTime are null.
     * @helper calculateFinalTime()
     */
    public void calculate() throws CriticalErrorException {
        if (startTime == null || stopTime == null) {
            throw new CriticalErrorException("We could not calculate the final time of a team because the heat" +
                    " never started or other problems inside the FinalTime class.");
        }
        calculateFinalTime();
    }

    /**
     * will calculate the seconds, minutes and milliseconds that took the team to finish the Run.
     * @helper calculateSeconds()
     */
    private void calculateFinalTime() {
        calculateSeconds();
        seconds = (int) (millisecondOfSet / 1000);
        minutes = (seconds / 60);
        seconds = seconds % 60;
        milliseconds = (int) (millisecondOfSet % 1000);
    }

    /**
     * Will calculate the time difference between the start and stop time. Expects the startTime to be a time
     * before the endTime in terms of real time.
     */
    private void calculateSeconds() {
        long milliStart = startTime.getTimeInMillis();
        long milliEnd = stopTime.getTimeInMillis();
        millisecondOfSet = milliEnd - milliStart;
    }



    @Override
    public String toString() {
        return "" + getMinutes() + ":" + getSeconds() + ":" + getMilliseconds();
    }
}