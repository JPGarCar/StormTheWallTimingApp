package models.exceptions;

public class NoDayRuntimeException extends RuntimeException {

    public NoDayRuntimeException() {
        super("Could not find that day. ");
    }

    public NoDayRuntimeException(String s) {
        super("Could not find that day. " + s);
    }

}
