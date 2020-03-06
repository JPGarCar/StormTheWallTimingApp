package models.exceptions;

public class CanNotUndoHeatException extends Exception{

    public CanNotUndoHeatException(String s) {
        super("Can not undo heat as it has not started. " + s);
    }

}
