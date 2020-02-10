package tests;

import models.FinalTime;
import models.exceptions.CouldNotCalculateFinalTimeExcpetion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;

public class TestFinalTimeClass {

    FinalTime finalTime;

    @BeforeEach
    public void BeforeEach() {
        Calendar calendarFinal = Calendar.getInstance();
        Calendar startTime = Calendar.getInstance();
        startTime.clear();
        startTime.setTimeInMillis(calendarFinal.getTimeInMillis());
        calendarFinal.add(Calendar.MINUTE, 5);
        try {
            finalTime = new FinalTime(startTime, calendarFinal);
        } catch (CouldNotCalculateFinalTimeExcpetion couldNotCalculateFinalTimeExcpetion) {
            couldNotCalculateFinalTimeExcpetion.printStackTrace();
        }
    }

    @Test
    public void testPrivateCalculations() {
        assertEquals(0, finalTime.getMilliseconds());
        assertEquals(0, finalTime.getSeconds());
        assertEquals(5, finalTime.getMinutes());
    }

    @Test
    public void addNewTimes() {
        Calendar finalCalendar = Calendar.getInstance();
        finalCalendar.add(Calendar.MINUTE, 34);
        finalCalendar.add(Calendar.SECOND, 23);
        finalCalendar.add(Calendar.MILLISECOND, 12);
        finalTime.addTimes(Calendar.getInstance(), finalCalendar);
        try {
            finalTime.calculate();
        } catch (CouldNotCalculateFinalTimeExcpetion couldNotCalculateFinalTimeExcpetion) {
            couldNotCalculateFinalTimeExcpetion.printStackTrace();
        }
        assertEquals(34, finalTime.getMinutes());
        assertEquals(23, finalTime.getSeconds());
        assertEquals(12, finalTime.getMilliseconds());
    }



}
