package tests;

import models.Day;
import models.Heat;
import models.Team;
import models.exceptions.AddTeamException;
import models.exceptions.NoTeamException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

public class TestHeatClass {

    Heat heat;

    @Test
    public void TestConstructor() {
        Calendar timeToStart = Calendar.getInstance();
        Day day = new Day("Saturday", 1);
        heat = new Heat(timeToStart, "Competitive", 123, day, 123);

        assertEquals(timeToStart.get(Calendar.HOUR_OF_DAY) + ":" + timeToStart.get(Calendar.MINUTE), heat.scheduledTimeString());
        assertEquals(123, heat.getHeatNumber());
        assertEquals(day, heat.getDayToRace());
        assertEquals(0, heat.getTeams().size());
    }

    @Test
    public void TestAddTeams() throws AddTeamException {
        Calendar timeToStart = Calendar.getInstance();
        Day day = new Day("Saturday", 1);
        heat = new Heat(timeToStart, "Competitive", 123, day, 123);


        Team team = new Team("LeagueType", 312, "The Storm Troopers", 312, "CWD");
        Team team1 = new Team("LeagueType", 423, "The Storm Troopers", 423, "CWD");
        Team team2 = new Team("LeagueType", 32, "The Storm Troopers", 32, "CWD");

        heat.addTeam(team);
        assertEquals(1, heat.getTeams().size());

        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team);
        teams.add(team1);
        teams.add(team2);

        assertEquals(3, heat.getTeams().size());

        assertEquals(1, team.getHeats().size());

    }

    @Test
    public void TestRemoveTeam() throws AddTeamException, NoTeamException {
        Calendar timeToStart = Calendar.getInstance();
        Day day = new Day("Saturday", 1);
        heat = new Heat(timeToStart, "Competitive", 123, day, 123);

        Team team = new Team("Competitive", 312, "The Storm Troopers", 312, "CWD");
        heat.addTeam(team);
        assertFalse(heat.getTeams().isEmpty());

        heat.removeTeam(team.getTeamNumber());
        assertTrue(heat.getTeams().isEmpty());
        assertTrue(team.getHeats().isEmpty());
    }


}
