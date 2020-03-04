package models.exceptions;

public class AddHeatException extends Exception {

    public AddHeatException(String s) {
        super("Can not add heat " + s);
    }

}
