package tests;

import models.Day;
import models.Heat;
import models.enums.LeagueType;
import models.exceptions.AddHeatException;
import org.junit.jupiter.api.Test;
import persistance.PersistanceWithJackson;

import java.util.Calendar;

public class testJsonifier {

    @Test
    public void testJsonifierFirst() throws AddHeatException {
        Day day = new Day("Monday", 1);
        Heat heat = new Heat(Calendar.getInstance(), "Women", 2314, day, 2314);
        day.addHeat(heat);

        PersistanceWithJackson.toJsonDay(day);


    }

}
