package models.exceptions;

public class NoHeatWithStartTimeException extends RuntimeException {

    public NoHeatWithStartTimeException() {
        super("Could not find a heat with that start tme.");
    }

    public NoHeatWithStartTimeException(String s) {
        super("Could not find a heat with that start tme. " + s);
    }

}
