package tests;

import models.Day;
import models.Heat;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Calendar;


public class TestDayClass {

    Day day;

    @Test
    public void TestConstructor() {
        day = new Day("Sunday", 1);

        assertEquals("Sunday", day.getDayToRun());
        assertEquals(1, day.getDayNumber());
        assertTrue(day.getHeats().isEmpty());
    }

    @Test
    public void TestAddAndRemoveHeat() {
        day = new Day("Saturday", 1);

        Calendar timeToStart = Calendar.getInstance();
        Day day = new Day("Saturday", 1);
        Heat heat = new Heat(timeToStart, "Just for fun", 123, day, 123);

        day.addHeat(heat);
        assertFalse(day.getHeats().isEmpty());

        Heat heat1 = new Heat(timeToStart, "Just for fun", 32, day, 23);
        Heat heat2 = new Heat(timeToStart, "Just for fun", 234, day, 234);

        ArrayList<Heat> heats = new ArrayList<>();
        heats.add(heat);
        heats.add(heat1);
        heats.add(heat2);

        day.addHeats(heats);

    }



}
