package Model;

public class Rotation {

    private static double[][] matrix;
    private double[] result;
    private int length;
    private int width;
    private int sumCount = 0, substrCount = 0,multCount = 0, divCount = 0;

    public void setMatrix(double[][] matrix) {
        this.matrix = matrix;
        this.length = matrix.length;
        this.width = matrix[0].length;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    //self-made
    public double[] solve() {
        result = new double[matrix.length];
        double c,s, mik;
        for (int i = 0 ; i < matrix.length; i++) {
            for (int j = i+1; j < matrix.length; j++) {
                c = matrix[i][i];
                s = matrix[j][i];
                for (int k = 0; k < matrix[0].length; k++) {
                    mik = matrix[i][k];
                    matrix[i][k] = c * matrix[i][k] + s * matrix[j][k];
                    matrix[j][k] = c * matrix[j][k] - s * mik;
                    multCount += 4;
                    sumCount++;
                    substrCount++;
                }
            }
        }

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

        return result;
    }

}
