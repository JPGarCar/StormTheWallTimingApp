package tests;

import models.Day;
import models.Heat;
import models.Team;
import models.enums.LeagueType;
import models.enums.TeamType;
import models.exceptions.AddHeatException;
import models.exceptions.AddTeamException;
import models.exceptions.NoTeamException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

public class TestHeatClass {

    Heat heat;

    @Test
    public void TestConstructor() throws AddHeatException {
        Calendar timeToStart = Calendar.getInstance();
        Day day = new Day(Calendar.getInstance(), 1);
        heat = new Heat(timeToStart, LeagueType.JFF, TeamType.OPEN, 123, day, 123);

        assertEquals(timeToStart.get(Calendar.HOUR_OF_DAY) + ":" + timeToStart.get(Calendar.MINUTE), heat.timeToStartString());
        assertEquals(LeagueType.JFF, heat.getLeagueType());
        assertEquals(TeamType.OPEN, heat.getTeamType());
        assertEquals(123, heat.getHeatNumber());
        assertEquals(day, heat.getDayToRace());
        assertEquals(0, heat.getTeams().size());
    }

    @Test
    public void TestAddTeams() throws AddHeatException, AddTeamException {
        Calendar timeToStart = Calendar.getInstance();
        Day day = new Day(Calendar.getInstance(), 1);
        heat = new Heat(timeToStart, LeagueType.JFF, TeamType.OPEN, 123, day, 123);


        Team team = new Team(TeamType.COREC, LeagueType.COMP, 312, "The Storm Troopers", 312);
        Team team1 = new Team(TeamType.COREC, LeagueType.COMP, 423, "The Storm Troopers", 423);
        Team team2 = new Team(TeamType.COREC, LeagueType.COMP, 32, "The Storm Troopers", 32);

        heat.addTeam(team);
        assertEquals(1, heat.getTeams().size());

        ArrayList<Team> teams = new ArrayList<>();
        teams.add(team);
        teams.add(team1);
        teams.add(team2);

        heat.addTeams(teams);
        assertEquals(3, heat.getTeams().size());

        assertEquals(1, team.getHeats().size());

    }

    @Test
    public void TestRemoveTeam() throws AddHeatException, AddTeamException, NoTeamException {
        Calendar timeToStart = Calendar.getInstance();
        Day day = new Day(Calendar.getInstance(), 1);
        heat = new Heat(timeToStart, LeagueType.JFF, TeamType.OPEN, 123, day, 123);

        Team team = new Team(TeamType.COREC, LeagueType.COMP, 312, "The Storm Troopers", 312);
        heat.addTeam(team);
        assertFalse(heat.getTeams().isEmpty());

        heat.removeTeam(team.getTeamNumber());
        assertTrue(heat.getTeams().isEmpty());
        assertTrue(team.getHeats().isEmpty());
    }


}
