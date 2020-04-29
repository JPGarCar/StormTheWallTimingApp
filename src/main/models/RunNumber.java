package models;

import java.io.Serializable;
import java.util.Objects;

/**
 * <h3>Represents</h3> a unique identifier to a {@link Run}. This class is used to differentiate between Run(s). It uses
 * a {@link Team}'s number and a {@link Heat}'s number to construct itself. A RunNumber is a string containing the
 * teamNumber + "-" + heatNumber.
 *
 *
 * <h3>Purpose:</h3> used to differentiate Run(s) by the Team's and Heat's number.
 *
 * <h3>Contains:</h3>
 *      - The Team number the Run is associated to - int
 *      - The Heat number the Run is associated to - int
 *
 * <h3>Usage:</h3>
 *      - Functions to make the class comparable and serializable so it can be used as a key in Map(s).
 *
 * <h3>Persistence:</h3>
 *      - No persistence in this class. It will be embedded in the DB.
 */

public class RunNumber implements Comparable, Serializable {

// VARIABLES //

    /**
     * The Run's assocaited Team number
     */
    private int teamNumber;

    /**
     * The Run's associated Run number
     */
    private int heatNumber;

// CONSTRUCTORS //

    // DUMMY CONSTRUCTOR USED BY JACKSON
    public RunNumber(){
    }

    // CONSTRUCTOR
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

    /**
     * To be able to use it as a key for TreeMap, needed to set all these functions.
     * For a RunNumber to equal, teamNumber and heatNumber need to be the same.
     *
     * @param o The RunNumber to be compared to for equality.
     * @return True if the imputed RunNumber is equal to this one.
     */
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

    /**
     * Set to sort the RunNumber(s) by increasing Heat number, set to equal each other
     * if the Heat's number and Team's number are equal
     *
     * @param o The RunNumber to be compared to.
     * @return 0 if bot RunNumber(s) are equal, 1 if this RunNumber goes before the imputed RunNumber,
     *          -1 if the imputed RunNumber goes before this RunNumber.
     */
    @Override
    public int compareTo(Object o) {
        if (this.equals(o)) {
            return 0;
        }

        RunNumber runNumber = (RunNumber) o;
        if (this.heatNumber == runNumber.getHeatNumber() && this.teamNumber == runNumber.getTeamNumber()) {
            return 0;
        } else if (this.heatNumber > runNumber.getHeatNumber()) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * Important: if you are going to change, you need to change the json deserializer too
     *
     * @return  String format of a RunNumber, aka the Team's number + "-" + Heat's number
     */
    @Override
    public String toString() {
        return teamNumber + "-" + heatNumber;
    }
}
