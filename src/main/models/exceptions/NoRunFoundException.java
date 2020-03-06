package models.exceptions;

public class NoRunFoundException extends Exception{

    public NoRunFoundException(String s) {
        super("There is no Run available with that ID. " + s);
    }

}
