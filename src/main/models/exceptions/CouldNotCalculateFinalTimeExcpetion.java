package models.exceptions;

public class CouldNotCalculateFinalTimeExcpetion extends Exception {

    public CouldNotCalculateFinalTimeExcpetion() {
        super("There is not start or stop time to calculate the final time.");
    }

    public CouldNotCalculateFinalTimeExcpetion(String time) {
        super("There is no " + time + " to calculate the final time.");
    }

}
