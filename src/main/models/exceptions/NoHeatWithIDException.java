package models.exceptions;

public class NoHeatWithIDException extends Exception {

    public NoHeatWithIDException() {
        super("Could not find a heat with that ID.");
    }

}
