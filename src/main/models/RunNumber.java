package models;

import java.util.Objects;

public class RunNumber {

// VARIABLES //
    private int teamNumber;
    private int heatNumber;

// CONSTRUCTORS //

    public RunNumber(int teamNumber, int heatNumber) {
        this.heatNumber = heatNumber;
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
}
