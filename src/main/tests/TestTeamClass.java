package tests;

import models.Day;
import models.Heat;
import models.Team;
import models.enums.LeagueType;
import models.enums.TeamType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;

public class TestTeamClass {

    Team team;

    @BeforeEach
    public void BeforeEach() {
        team = new Team(TeamType.COREC, null, LeagueType.COMP, null, 312, "The Storm Troopers");
    }

    @Test
    public void testAddingHeats() {
        Heat heat = new Heat(Calendar.getInstance(), LeagueType.COMP, TeamType.COREC, 1, new Day(Calendar.getInstance(), 1));
        team.addHeat(heat);

        assertTrue(team.getHeats().contains(heat));
    }




}
