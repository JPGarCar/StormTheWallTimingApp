package models.exceptions;

public class AddTeamException extends Exception {

    public AddTeamException() {
        super("Can not add the team as there is a team with that id on the heat already.");
    }

}
