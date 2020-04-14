package models.exceptions;

/**
 * Checked exception to be used when there is a problem with the excels used to input data to the program.
 */
public class InvalidExcelException extends Exception {
    public InvalidExcelException(String s) {
        super(s);
    }
}
