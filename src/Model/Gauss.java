package Model;

public class Gauss extends SLAEMethod{

    private static final double EPSILON = 0.00001;
    private double[][] matrix;
    private double[] vector;

    public void setMatrix(double[][] matrix) {
        this.matrix = matrix;
    }

    public void setVector(double[] vector) {
        this.vector = vector;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    public double[] getVector() {
        return vector;
    }

    public double[] solve() {
        makeTriangleView();
        return backtrace();

    }

    protected void makeTriangleView() {
        int length = matrix.length;
        int width = matrix[0].length;
        for (int i = 0; i < length; i++) {
            int max = i;
            for (int j = i+1; j < length; j++)
                if (Math.abs(matrix[j][i]) < Math.abs(matrix[max][j]))
                    max = j;


            //matrix[i][width-1] matrix[max][width-1]
            //vector[i] vector[max]
            double[] temp = matrix[i];
            matrix[i] = matrix[max];
            matrix[max] = temp;
            double v = matrix[i][width-1];
            matrix[i][width-1] = matrix[max][width-1];
            matrix[max][width-1] = v;

            /*if (Math.abs(matrix[i][i]) <= EPSILON)
                throw new RuntimeException("Matrix is singular");*/

            for (int k = i+1; k < length; k++) {
                double alfa = matrix[k][i] / matrix[i][i];
                divCount++;
                //matrix[k][width]  matrix[i][width]
                //vector[k]  vector[i]
                matrix[k][width-1] -= alfa * matrix[i][width-1];
                multCount++;
                substrCount++;

                for (int j = i; j < length; j++) {
                    matrix[k][j] -= alfa*matrix[i][j];
                    multCount++;
                    substrCount++;
                }
            }
        }

    }

    protected double[] backtrace() {
        int length = matrix.length;
        int width = matrix[0].length;
        double[] x = new double[length];

        for (int i = length-1; i >= 0; i--) {
            double sum = 0;
            for (int j = i+1; j < length; j++) {
                sum += matrix[i][j] * x[j];
                multCount++;
                sumCount++;
            }
            //matrix[i][width-1]
            //vector[i]
            x[i] = (matrix[i][width-1] - sum)/matrix[i][i];
            substrCount++;
            divCount++;
        }

        this.result = x;

        return x;
    }
    //origin method, DONT DELETE
    /*public double[] solve() {
        int n = matrix.length;

        for (int i = 0; i < n; i++) {
            int max = i;
            for (int j = i+1; j < n; j++)
                if (Math.abs(matrix[j][i]) > Math.abs(matrix[max][j]))
                    max = j;


            double[] temp = matrix[i];
            matrix[i] = matrix[max];
            matrix[max] = temp;
            double v = vector[i];
            vector[i] = vector[max];
            vector[max] = v;

            if (Math.abs(matrix[i][i]) <= EPSILON)
                throw new RuntimeException("Matrix is singular");

            for (int k = i+1; k < n; k++) {
                double alfa = matrix[k][i] / matrix[i][i];
                divCount++;
                //matrix[k][width-1]  matrix[i][width-1]
                vector[k] -= alfa * vector[i];
                multCount++;
                substrCount++;

                for (int j = i; j < n; j++) {
                    matrix[k][j] -= alfa*matrix[i][j];
                    multCount++;
                    substrCount++;
                }
            }
        }
        double[] x = new double[n];

        for (int i = n-1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i+1; j < n; j++) {
                sum += matrix[i][j] * x[j];
                multCount++;
                sumCount++;
            }
            //matrix[i][width-1]
            x[i] = (vector[i] - sum)/matrix[i][i];
            substrCount++;
            divCount++;
        }
        this.result = x;

        return x;
    }*/
}
