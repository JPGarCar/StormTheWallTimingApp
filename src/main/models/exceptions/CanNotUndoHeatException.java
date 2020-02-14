package models.exceptions;

public class CanNotUndoHeatException extends Exception{

    public CanNotUndoHeatException() {
        super("Can not undo heat as it has not started");
    }

}
