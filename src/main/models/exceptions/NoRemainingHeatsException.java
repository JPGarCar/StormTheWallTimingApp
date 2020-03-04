package models.exceptions;

public class NoRemainingHeatsException extends Exception{

    public NoRemainingHeatsException(String s) {
        super("Can not mark end time as there are no remaining heats to mark from. " + s);
    }


}
