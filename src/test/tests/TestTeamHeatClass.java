package tests;

import models.Day;
import models.Heat;
import models.TeamHeat;
import models.enums.LeagueType;
import models.enums.TeamType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;


public class TestTeamHeatClass {

    TeamHeat teamHeat;

    @Test
    public void TestConstructor() {
        Day day = new Day(Calendar.getInstance(),1);
        Heat heat = new Heat(Calendar.getInstance(), LeagueType.COMP, TeamType.COREC, 1, day);
        teamHeat = new TeamHeat(heat);

        assertNotNull(teamHeat.getHeatNumber());
        assertEquals(heat.getHeatNumber(), teamHeat.getHeatNumber());
    }

    @Test
    public void TestSetEndTime() {
        Day day = new Day(Calendar.getInstance(),1);
        Heat heat = new Heat(null, LeagueType.COMP, TeamType.COREC, 1, day);
        heat.setStartTime(Calendar.getInstance());
        teamHeat = new TeamHeat(heat);

        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.MINUTE, 5);
        teamHeat.setEndTime(endTime);

        assertNotNull(teamHeat.getFinalTime());
    }


}
