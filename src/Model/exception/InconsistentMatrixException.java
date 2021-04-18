package Model.exception;

public class InconsistentMatrixException extends RuntimeException{
    public InconsistentMatrixException() {

    }

    public InconsistentMatrixException(String m) {
        super(m);
    }
}
