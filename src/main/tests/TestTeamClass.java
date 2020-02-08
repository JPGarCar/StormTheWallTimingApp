package tests;

import models.Day;
import models.Heat;
import models.Team;
import models.TeamHeat;
import models.enums.LeagueType;
import models.enums.Sitrep;
import models.enums.TeamType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Calendar;

public class TestTeamClass {

    Team team;

    @BeforeEach
    public void BeforeEach() {
        team = new Team(TeamType.COREC, LeagueType.COMP, 312, "The Storm Troopers");
    }

    @Test
    public void testConstructor() {
        assertEquals("", team.getNotes());
        assertEquals(TeamType.COREC, team.getTeamType());
        assertEquals(LeagueType.COMP, team.getTeamLeague());
        assertEquals(312, team.getTeamNumber());
        assertEquals("The Storm Troopers", team.getTeamName());
        assertNull(team.getSitRep());
        assertTrue(team.getHeats().isEmpty());
        assertTrue(team.getRemainingHeats().isEmpty());
        assertTrue(team.getDoneHeats().isEmpty());
    }

    @Test
    public void testSetters() {
        team.setSitRep(Sitrep.DNS);
        assertEquals(Sitrep.DNS, team.getSitRep());

        team.addNotes("Notes");
        assertEquals("Notes", team.getNotes());
    }



    @Test
    public void testAddingHeats() {
        Day day = new Day(Calendar.getInstance(),1);
        Heat heat = new Heat(null, LeagueType.COMP, TeamType.COREC, 1, day);
        team.addHeat(heat);
        assertTrue(team.getHeats().contains(heat));
        assertEquals(1, team.getRemainingHeats().size());

        Heat heat1 = new Heat(Calendar.getInstance(), LeagueType.COMP, TeamType.COREC, 2, day);
        Heat heat2 = new Heat(Calendar.getInstance(), LeagueType.JFF, TeamType.OPEN, 3, day);

        ArrayList<Heat> heats = new ArrayList<>();
        heats.add(heat1);
        heats.add(heat2);
        heats.add(heat);

        team.addHeats(heats);
        assertEquals(3, team.getHeats().size());
        assertEquals(3, team.getRemainingHeats().size());
    }

    @Test
    public void testSetEndTime() {
        Day day = new Day(Calendar.getInstance(),1);
        Calendar startTime = Calendar.getInstance();
        Heat heat = new Heat(null, LeagueType.COMP, TeamType.COREC, 1, day);
        team.addHeat(heat);
        heat.setStartTime(startTime);

        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.MINUTE, 5);
        team.setEndTime(1, endTime);

        assertTrue(team.getRemainingHeats().isEmpty());
        assertEquals(1, team.getDoneHeats().size());
        assertFalse(team.getHeats().isEmpty());
    }






}
