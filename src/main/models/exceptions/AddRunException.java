package models.exceptions;

/**
 * Checked exception for when it was not possible to add a heat to the program. Causes for this exception include
 * the Day already having the same Heat or a Heat with the same time.
 */
public class AddRunException extends Exception {

    public AddRunException(String s) {
        super(s);
    }

}
