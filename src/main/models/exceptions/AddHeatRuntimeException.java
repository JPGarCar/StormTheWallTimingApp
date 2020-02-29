package models.exceptions;

public class AddHeatRuntimeException extends RuntimeException {

    public AddHeatRuntimeException(String s) {
        super("Can not add heat " + s);
    }

}
