package Model.exception;

public class InfiniteSolutionsAmountException extends RuntimeException{
    public InfiniteSolutionsAmountException() {
    }

    public InfiniteSolutionsAmountException(String message) {
        super(message);
    }
}
