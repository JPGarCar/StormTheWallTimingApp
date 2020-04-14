package models.exceptions;

/**
 * Checked exception for when there is a critical error that the user can not fix due to the nature of the program.
 * This exception is to be used to notify the user of the problem but there is nothing the user can do to fix it.
 */
public class CriticalErrorException extends Exception {

    public CriticalErrorException(String s) {
        super("There has been a critical error. " + s);
    }

}
