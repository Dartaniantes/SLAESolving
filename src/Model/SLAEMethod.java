package Model;

import Model.exception.InconsistentMatrixException;

public abstract class SLAEMethod {
    protected double[][] matrix;
    protected double[] result;
    protected int length;
    protected int width;
    protected int sumCount = 0, substrCount = 0,multCount = 0, divCount = 0;

    protected SLAEMethod() {}

    protected SLAEMethod(double[][] matrix) {
        this.matrix = matrix;
        this.length = matrix.length;
        this.width = matrix[0].length;
        this.result = new double[length];
    }

//    protected abstract void makeTriangleView();

    public double[] solve() {
//        makeTriangleView();
        int consistence = consistence();
        if(consistence < 0)
            throw new InconsistentMatrixException("Given matrix is inconsistent");
        else if(consistence > 0){
            return null;
        }
        backtrace();
        return result;
    }

    protected void checkMatrix() {

    }

    private void backtrace() {
        double sum;
        for (int i = length-1; i >= 0; i--) {
            sum = 0;
            for (int j = i+1; j < length; j++) {
                sum += matrix[i][j] * result[j];
                multCount++;
                sumCount++;
            }
            result[i] = (matrix[i][width-1] - sum)/matrix[i][i];
            substrCount++;
            divCount++;
        }
    }

    private int consistence() {
        int regularMatrixRank = getRegularMatrixRank();
        int extendedMatrixRank = getExtendedMatrixRank();
        return regularMatrixRank < extendedMatrixRank ? -1 : (regularMatrixRank == extendedMatrixRank ? 0 : 1);
    }

    private int getExtendedMatrixRank() {
        int nonZeroRawCounter = 0;
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                if(matrix[i][j] != 0){
                    nonZeroRawCounter++;
                    j = width;
                }
            }
        }
        return nonZeroRawCounter;
    }

    private int getRegularMatrixRank() {
        int nonZeroRawCounter = 0;
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width-1; j++) {
                if(matrix[i][j] != 0){
                    nonZeroRawCounter++;
                    j = width;
                }
            }
        }
        return nonZeroRawCounter;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(double[][] matrix) {
        nullifyCounters();
        this.matrix = matrix;
        this.length = matrix.length;
        this.width = matrix[0].length;
    }

    void nullifyCounters() {
        sumCount = 0;
        substrCount = 0;
        multCount = 0;
        divCount = 0;
    }

    public int getSumCount() {
        return sumCount;
    }

    public int getSubstrCount() {
        return substrCount;
    }

    public int getMultCount() {
        return multCount;
    }

    public int getDivCount() {
        return divCount;
    }
}
