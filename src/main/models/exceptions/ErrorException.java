package models.exceptions;

/**
 * Checked exception to be used when there is an error in the program but the user can do something differently to try
 * and fix the error.
 */
public class ErrorException extends Exception {

    public ErrorException(String s) {
        super("There has been an error. " + s);
    }

}
