package tests;

import models.Day;
import models.Heat;
import models.enums.LeagueType;
import models.enums.TeamType;
import org.junit.jupiter.api.Test;
import persistance.PersistanceWithJackson;

import java.util.Calendar;

public class testJsonifier {

    @Test
    public void testJsonifierFirst() {
        Day day = new Day(Calendar.getInstance(), 1);
        Heat heat = new Heat(Calendar.getInstance(), LeagueType.JFF, TeamType.OPEN, 2314, day);
        day.addHeat(heat);

        PersistanceWithJackson.toJsonDay(day);


    }

}
