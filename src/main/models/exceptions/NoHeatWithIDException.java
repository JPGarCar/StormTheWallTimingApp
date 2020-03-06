package models.exceptions;

public class NoHeatWithIDException extends Exception {

    public NoHeatWithIDException(String s) {
        super("Could not find a heat with that ID. " + s);
    }

}
