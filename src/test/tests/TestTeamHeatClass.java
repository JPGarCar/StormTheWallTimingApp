package tests;

import models.Day;
import models.Heat;
import models.Team;
import models.TeamHeat;
import models.enums.LeagueType;
import models.enums.TeamType;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;


public class TestTeamHeatClass {

    TeamHeat teamHeat;

    @Test
    public void TestConstructor() {
        Day day = new Day(Calendar.getInstance(),1);
        Heat heat = new Heat(Calendar.getInstance(), LeagueType.COMP, TeamType.COREC, 1, day);
        Team team = new Team(TeamType.OPEN, LeagueType.JFF, 1232, "The Invinsibles");
        teamHeat = new TeamHeat(heat.getHeatNumber(), team);

        assertNotNull(teamHeat.getHeatID());
        assertEquals(heat.getHeatNumber(), teamHeat.getHeatID());
    }

    @Test
    public void TestSetEndTime() {
        Day day = new Day(Calendar.getInstance(),1);
        Heat heat = new Heat(null, LeagueType.COMP, TeamType.COREC, 1, day);
        heat.markStartTimeStarted(Calendar.getInstance());
        Team team = new Team(TeamType.OPEN, LeagueType.JFF, 1232, "The Invinsibles");
        team.addHeat(heat);
        teamHeat = new TeamHeat(heat.getHeatNumber(), team);

        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.MINUTE, 5);
        teamHeat.setEndTime(endTime);

        assertNotNull(teamHeat.getFinalTime());
    }


}
