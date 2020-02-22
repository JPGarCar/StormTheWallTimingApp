package tests;

import models.Day;
import models.Heat;
import models.Team;
import models.Run;
import models.enums.LeagueType;
import models.enums.TeamType;
import models.exceptions.AddHeatException;
import models.exceptions.CouldNotCalculateFinalTimeExcpetion;
import models.exceptions.NoHeatsException;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;


public class TestRunClass {

    Run run;

    @Test
    public void TestConstructor() {
        Day day = new Day(Calendar.getInstance(),1);
        Heat heat = null;
        heat = new Heat(Calendar.getInstance(), LeagueType.COMP, TeamType.COREC, 1, day, 1);
        Team team = new Team(TeamType.OPEN, LeagueType.JFF, 1232, "The Invinsibles", 1232);
        run = new Run(heat.getHeatNumber(), team);

        assertEquals(heat.getHeatNumber(), run.getHeatNumber());
    }

    @Test
    public void TestSetEndTime() {
        Day day = new Day(Calendar.getInstance(),1);
        Heat heat = null;
        heat = new Heat(null, LeagueType.COMP, TeamType.COREC, 1, day, 1);
        heat.markActualStartTime(Calendar.getInstance());
        Team team = new Team(TeamType.OPEN, LeagueType.JFF, 1232, "The Invinsibles", 1232);
        try {
            team.addHeat(heat);
        } catch (AddHeatException e) {
            e.printStackTrace();
        }
        run = new Run(heat.getHeatNumber(), team);

        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.MINUTE, 5);
        try {
            run.calculateEndTime(endTime);
        } catch (NoHeatsException | CouldNotCalculateFinalTimeExcpetion e) {
            e.printStackTrace();
        }

        assertNotNull(run.getFinalTime());
    }


}
