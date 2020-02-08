package tests;

import models.FinalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;

public class TestFinalTimeClass {

    FinalTime finalTime;

    @BeforeEach
    public void BeforeEach() {
        Calendar calendarFinal = Calendar.getInstance();
        calendarFinal.add(Calendar.MINUTE, 5);
        finalTime = new FinalTime(Calendar.getInstance(), calendarFinal);
    }

    @Test
    public void testPrivateCalculations() {
        assertEquals(5, finalTime.getMinutes());
        assertEquals(0, finalTime.getSeconds());
        assertEquals(0, finalTime.getMilliseconds());
    }

    @Test
    public void addNewTimes() {
        Calendar finalCalendar = Calendar.getInstance();
        finalCalendar.add(Calendar.MINUTE, 34);
        finalCalendar.add(Calendar.SECOND, 23);
        finalCalendar.add(Calendar.MILLISECOND, 12);
        finalTime.addTimes(Calendar.getInstance(), finalCalendar);
        finalTime.calculate();
        assertEquals(34, finalTime.getMinutes());
        assertEquals(23, finalTime.getSeconds());
        assertEquals(12, finalTime.getMilliseconds());
    }



}
