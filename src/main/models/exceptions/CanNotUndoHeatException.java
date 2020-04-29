package models.exceptions;

/**
 * Checked exception for when a Heat is not able to be Undo. This can happen if the Heat has not started.
 */
public class CanNotUndoHeatException extends Exception{

    public CanNotUndoHeatException(String s) {
        super("Can not undo heat as it has not started. " + s);
    }

}
