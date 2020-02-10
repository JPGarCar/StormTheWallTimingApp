package tests;

import models.Day;
import models.Heat;
import models.enums.LeagueType;
import models.enums.TeamType;
import models.exceptions.AddHeatException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Calendar;


public class TestDayClass {

    Day day;

    @Test
    public void TestConstructor() {
        Calendar calendar = Calendar.getInstance();
        day = new Day(calendar, 1);

        assertEquals(calendar, day.getDayToRun());
        assertEquals(1, day.getDayNumber());
        assertTrue(day.getHeats().isEmpty());
        assertEquals(0, day.numberOfHeats());
    }

    @Test
    public void TestAddAndRemoveHeat() throws AddHeatException {
        Calendar calendar = Calendar.getInstance();
        day = new Day(calendar, 1);

        Calendar timeToStart = Calendar.getInstance();
        Day day = new Day(Calendar.getInstance(), 1);
        Heat heat = new Heat(timeToStart, LeagueType.JFF, TeamType.OPEN, 123, day);

        day.addHeat(heat);
        assertFalse(day.getHeats().isEmpty());

        Heat heat1 = new Heat(timeToStart, LeagueType.JFF, TeamType.OPEN, 32, day);
        Heat heat2 = new Heat(timeToStart, LeagueType.JFF, TeamType.OPEN, 234, day);

        ArrayList<Heat> heats = new ArrayList<>();
        heats.add(heat);
        heats.add(heat1);
        heats.add(heat2);

        day.addHeats(heats);
        assertEquals(3, day.numberOfHeats());

        day.removeHeat(heat);
        assertEquals(2, day.numberOfHeats());

    }



}
