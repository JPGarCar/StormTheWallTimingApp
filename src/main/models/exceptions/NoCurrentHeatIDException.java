package models.exceptions;

public class NoCurrentHeatIDException extends RuntimeException {

    public NoCurrentHeatIDException() {
        super("There is no current heat ID available");
    }

}
