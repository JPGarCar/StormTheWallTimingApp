package models;

import java.util.Objects;

public class RunNumber implements Comparable {

// VARIABLES //
    private int teamNumber;
    private int heatNumber;

// CONSTRUCTORS //

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

    @Override
    public int compareTo(Object o) {
        if (this.equals(o)) {
            return 0;
        }

        RunNumber runNumber = (RunNumber) o;
        if (this.heatNumber > runNumber.getHeatNumber()) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return teamNumber + "-" + heatNumber;
    }
}
