package models.exceptions;

public class NoCurrentHeatIDException extends Exception {

    public NoCurrentHeatIDException() {
        super("There is no current heat ID available");
    }

}
