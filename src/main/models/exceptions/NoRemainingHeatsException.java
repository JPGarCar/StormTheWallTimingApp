package models.exceptions;

public class NoRemainingHeatsException extends Exception{

    public NoRemainingHeatsException() {
        super("Can not mark end time as there are no remaining heats to mark from.");
    }


}
