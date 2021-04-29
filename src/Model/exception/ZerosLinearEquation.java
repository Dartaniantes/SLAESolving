package Model.exception;

public class ZerosLinearEquation extends RuntimeException{

    public ZerosLinearEquation() {
    }

    public ZerosLinearEquation(String m) {
        super(m);
    }

}
