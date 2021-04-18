package Model.exception;

public class InvalidInputException extends RuntimeException{
    public InvalidInputException() {

    }

    public InvalidInputException(String m) {
        super(m);
    }
}
