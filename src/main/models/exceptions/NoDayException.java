package models.exceptions;

public class NoDayException extends Exception {

    public NoDayException() {
        super("Could not find that day. ");
    }

    public NoDayException(String s) {
        super("Could not find that day. " + s);
    }

}
