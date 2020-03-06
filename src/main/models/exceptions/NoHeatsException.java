package models.exceptions;

public class NoHeatsException extends Exception{

    public NoHeatsException() {
        super("There are no heats available to grab.");
    }

    public NoHeatsException(String s) {
        super("There are no heats available to grab." + s);
    }

}
