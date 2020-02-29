package tests;

import models.Day;
import models.Heat;
import models.Team;
import models.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Calendar;

public class TestTeamClass {

    Team team;

    @BeforeEach
    public void BeforeEach() {
        team = new Team("Competitive", 312, "The Storm Troopers", 312, "CWD");
    }

    @Test
    public void testConstructor() {
        assertEquals(312, team.getTeamNumber());
        assertEquals("The Storm Troopers", team.getTeamName());
        assertTrue(team.getHeats().isEmpty());
        assertTrue(team.getRuns().isEmpty());
    }

    @Test
    public void testAddingHeats() throws AddHeatException {
        Day day = new Day("Sunday",1);
        Heat heat = new Heat(null, "LeagueType.COMP", 1, day, 1);
        team.addHeat(heat);
        assertTrue(team.getHeats().containsKey(heat.getHeatNumber()));
        assertEquals(1, team.getRuns().size());

        Heat heat1 = new Heat(Calendar.getInstance(), "LeagueType", 2, day, 2);
        Heat heat2 = new Heat(Calendar.getInstance(), "LeagueType", 3, day, 3);

        ArrayList<Heat> heats = new ArrayList<>();
        heats.add(heat1);
        heats.add(heat2);
        heats.add(heat);

        team.addHeats(heats);
        assertEquals(3, team.getHeats().size());
        assertEquals(3, team.getRuns().size());

        assertEquals(1, heat.getTeams().size());
    }

    @Test
    public void testSetEndTime() throws AddHeatException {
        Day day = new Day("Sunday",1);
        Calendar startTime = Calendar.getInstance();
        Heat heat = new Heat(null, "LeagueType", 1, day, 1);
        team.addHeat(heat);
        heat.markActualStartTime(startTime);

        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.MINUTE, 5);
        try {
            team.markEndTime(endTime);
        } catch (NoHeatsException e) {
            e.printStackTrace();
        } catch (NoCurrentHeatIDException e) {
            e.printStackTrace();
        } catch (CouldNotCalculateFinalTimeExcpetion couldNotCalculateFinalTimeExcpetion) {
            couldNotCalculateFinalTimeExcpetion.printStackTrace();
        } catch (NoRemainingHeatsException e) {
            e.printStackTrace();
        }

        assertTrue(team.getRuns().isEmpty());
        assertFalse(team.getHeats().isEmpty());
    }

    @Test
    public void TestRemoveHeat() throws AddHeatException, NoHeatsException {
        Day day = new Day("Sunday",1);
        Heat heat = new Heat(null, "LeagueType", 1, day, 1);
        team.addHeat(heat);
        assertEquals(1, team.getHeats().size());
        assertFalse(heat.getTeams().isEmpty());

        team.removeHeat(heat.getHeatNumber());
        assertTrue(team.getHeats().isEmpty());
        assertTrue(team.getRuns().isEmpty());
        assertTrue(heat.getTeams().isEmpty());

    }




}
