package models;

import javax.persistence.*;
import java.util.Objects;

/*
    Represents a key of a Run
    Purpose: used to differentiate runs by the team and heat numbers
 */

public class RunNumber implements Comparable {

// VARIABLES //

    private int teamNumber;
    private int heatNumber;

// CONSTRUCTORS //

    // USED BY JACKSON
    public RunNumber(){
    };

    public RunNumber(int teamNumber, int heatNumber) {
        this.heatNumber = heatNumber;
        this.teamNumber = teamNumber;
    }

// GETTERS AND SETTERS //

    public int getHeatNumber() {
        return heatNumber;
    }

    public int getTeamNumber() {
        return teamNumber;
    }

    public void setHeatNumber(int heatNumber) {
        this.heatNumber = heatNumber;
    }

    public void setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
    }

// FUNCTIONS //

    // To be able to use it as a key for TreeMap, needed to set all these functions
    // EFFECTS: for runNumber to equal, teamNumber and heatNumber need to be the same
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RunNumber runNumber = (RunNumber) o;
        return teamNumber == runNumber.teamNumber &&
                heatNumber == runNumber.heatNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamNumber, heatNumber);
    }

    // EFFECTS: set to sort the runNumbers by increasing heat number, set to equal each other if heatNumber and
    //          teamNumber are equal
    @Override
    public int compareTo(Object o) {
        if (this.equals(o)) {
            return 0;
        }

        RunNumber runNumber = (RunNumber) o;
        if (this.heatNumber > runNumber.getHeatNumber()) {
            return 1;
        } else if (this.heatNumber == runNumber.getHeatNumber() && this.teamNumber == runNumber.getTeamNumber()) {
            return 0;
        } else {
            return -1;
        }
    }

    // EFFECTS: returns the teamNumber - heatNumber,
    //          if you are going to change, you need to change the json deserializer too
    @Override
    public String toString() {
        return teamNumber + "-" + heatNumber;
    }
}
