package Model;

public class Gauss extends SLAEMethod{

//    private static final double EPSILON = 0.00001;
    private double[][] matrix;
//    private double[] vector;

    public void setMatrix(double[][] matrix) {
        this.matrix = matrix;
    }

    public double[][] getMatrix() {
        return matrix;
    }

    void showMatrix() {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /*public double[] solve() {
        //origin method, DONT DELETE
        int n = matrix.length;
        int freeElement = matrix[0].length-1;

        for (int i = 0; i < n; i++) {
            int max = i;
            for (int j = i+1; j < n; j++)
                if (Math.abs(matrix[j][i]) > Math.abs(matrix[max][j])) {
                    max = j;
                }

            *//*double[] temp = matrix[i];
            matrix[i] = matrix[max];
            matrix[max] = temp;
            double v = vector[i];
            vector[i] = vector[max];
            vector[max] = v;*//*

            double[] temp = matrix[i];
            matrix[i] = matrix[max];
            matrix[max] = temp;

            //dont need it if vector is not in use(extended matrix in 'matrix' variable)
            *//*double v = matrix[i][freeElement];
            matrix[i][freeElement] = matrix[max][freeElement];
            matrix[max][freeElement] = v;*//*

            if (Math.abs(matrix[i][i]) <= EPSILON) {
                throw new RuntimeException("Matrix is singular");
            }

            for (int k = i+1; k < n; k++) {
                double alfa = matrix[k][i] / matrix[i][i];
                divCount++;
                matrix[k][freeElement] -= alfa * matrix[i][freeElement];
//                vector[k] -= alfa * vector[i];
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
            x[i] = (matrix[i][freeElement] - sum) / matrix[i][i];
//            x[i] = (vector[i] - sum)/matrix[i][i];
            substrCount++;
            divCount++;
        }
        this.result = x;

        return x;

    }*/

    public double[] solve() {
        makeTriangleView();
        return backtrace();
    }

    protected void makeTriangleView() {
        int length = matrix.length;
        int freeElement = matrix[0].length - 1;

        for (int i = 0; i < length; i++) {
            int max = i;
            for (int j = i+1; j < length; j++)
                if (Math.abs(matrix[j][i]) < Math.abs(matrix[max][j]))
                    max = j;


            //matrix[i][freeElement] matrix[max][freeElement]
            //vector[i] vector[max]
            double[] temp = matrix[i];
            matrix[i] = matrix[max];
            matrix[max] = temp;

            //don't need this line if vector is not in use(extended matrix in 'matrix' variable)
            /*double v = matrix[i][freeElement];
            matrix[i][freeElement] = matrix[max][freeElement];
            matrix[max][freeElement] = v;*/

            /*if (Math.abs(matrix[i][i]) <= EPSILON)
                throw new RuntimeException("Matrix is singular");*/

            for (int k = i+1; k < length; k++) {
                double alfa = matrix[k][i] / matrix[i][i];
                divCount++;
                //matrix[k][freeElement]  matrix[i][freeElement]
                //vector[k]  vector[i]
                matrix[k][freeElement] -= alfa * matrix[i][freeElement];
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
}
