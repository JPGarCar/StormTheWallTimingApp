package models.exceptions;

public class NoTeamException extends RuntimeException{

    public NoTeamException() {
        super("There is no team available to grab.");
    }

    public NoTeamException(String s) {
        super("There is no team available to grab " + s + " from.");
    }

}
