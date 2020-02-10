package models.exceptions;

public class NoTimeException extends Error {

    public NoTimeException() {
        super("There is not start or stop time to calculate.");
    }

    public NoTimeException(String time, String calculate) {
        super("There is no " + time + " to calculate " + calculate +".");
    }

}
