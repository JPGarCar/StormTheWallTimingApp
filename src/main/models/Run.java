package models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.internal.NotNull;
import models.enums.Sitrep;
import models.exceptions.CouldNotCalculateFinalTimeExcpetion;
import models.exceptions.NoHeatsException;

import javax.persistence.*;
import java.util.Calendar;

/*
    Represents a specific team in a specific heat
    Purpose: Controls the end time and status of the team in this run
    Contains:
    - The heat number this run is going to run in - int
    - Id of this run, used by db - int - UNIQUE
    - The time it takes the team to finish the run - FinalTime
    - The situation of the team, if any, ex. DNS, DQ, etc - Sitrep
    - The team that is running this run - Team

    Usage:
    -

    Persistence:
    - This class is an entity of the table name "run_table"
    - sitrep and finalTime will be changing during the program's life, must persist with db, all other vars
        should not change much or at all
    - Embedded class FinalTime as var finalTime in db
    - Many To One relationship with Team
 */


@Entity
@Table(name = "run_table")
public class Run {

// VARIABLES //

    // Represents the heat number of the heat in which the team is running
    private int heatNumber;

    // Represents the id of the run to be used by db, thus no need to instantiate in program - UNIQUE
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    // Represents the time it took the team to complete the run, it is embedded to the db
    @Embedded
    private FinalTime finalTime;

    // Represents the situation of the team in this run, ex. DNS, DNF, DQ, etc
    private Sitrep sitrep;

    // Contains the team that is running this run
    @ManyToOne
    @JsonBackReference
    private Team team;

// CONSTRUCTORS //

    // DUMMY CONSTRUCTOR for Jackson JSON
    public Run() {
    }

    // CONSTRUCTOR
    public Run(@NotNull int heatNumber, @NotNull Team team) {
        this.heatNumber = heatNumber;
        this.team = team;
        this.sitrep = Sitrep.NONE;
    }

// GETTERS AND SETTERS, used for Jackson JSON //

    public FinalTime getFinalTime() {
        return finalTime;
    }

    public int getHeatNumber() {
        return heatNumber;
    }

    public Team getTeam() {
        return team;
    }

    public Sitrep getSitrep() {
        return sitrep;
    }

    public void setFinalTime(@NotNull FinalTime finalTime) {
        this.finalTime = finalTime;
    }

    public void setHeatNumber(@NotNull int heatNumber) {
        this.heatNumber = heatNumber;
    }

    public void setTeam(@NotNull Team team) {
        this.team = team;
    }

    public void setSitrep(@NotNull Sitrep sitrep) {
        this.sitrep = sitrep;
    }

// FUNCTIONS //

    // EFFECTS: constructs a FinalTime with the heat's start and input end time
    public void calculateEndTime(@NotNull Calendar endTime) throws NoHeatsException, CouldNotCalculateFinalTimeExcpetion {
        Heat heat = getHeatFromTeam();
        if (heat == null) {
            throw new NoHeatsException();
        }

        finalTime = new FinalTime(heat.getStartTime(), endTime);
    }


    // EFFECTS: return the heat´s number associated with the TeamHeat
    private Heat getHeatFromTeam() {
        return team.getHeats().get(heatNumber);
    }

}
