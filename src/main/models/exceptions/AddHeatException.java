package models.exceptions;

public class AddHeatException extends RuntimeException {

    public AddHeatException() {
        super("Can not add heat");
    }

    public AddHeatException(String reason) {
        super("Can not add heat because: " + reason);
    }

}
