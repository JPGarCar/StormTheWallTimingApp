package models;

import java.util.ArrayList;
import java.util.Calendar;

public class Team {

    // private variables
    private TeamType teamType;
    private Sitrep sitRep;
    private LeagueType teamLeague;
    private String notes;

    // private team vars
    private int teamNumber;
    private String teamName;

    // private connections
    private ArrayList<Heat> heats;

    // private time vars
    private Calendar startTime;
    private Calendar endTime;
    private Seconds totalSeconds;

    public Team(TeamType teamType, Sitrep sitrep, LeagueType teamLeague, String notes, int teamNumber, String teamName) {
        this.teamType = teamType;
        this.sitRep = sitrep;
        this.teamLeague = teamLeague;
        this.notes = notes;
        this.teamNumber = teamNumber;
        this.teamName = teamName;

        heats = new ArrayList<Heat>();
    }

}