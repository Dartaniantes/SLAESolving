package Model;

public class Gauss extends SLAEMethod{

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
                subtrCount++;

                for (int j = i; j < length; j++) {
                    matrix[k][j] -= alfa*matrix[i][j];
                    multCount++;
                    subtrCount++;
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
            subtrCount++;
            divCount++;
        }

        return x;
    }
}
