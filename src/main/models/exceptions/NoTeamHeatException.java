package models.exceptions;

public class NoTeamHeatException extends Exception{

    public NoTeamHeatException() {
        super("There is no TeamHeat available with that ID.");
    }

}
