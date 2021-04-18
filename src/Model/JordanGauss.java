package Model;

public class JordanGauss extends SLAEMethod {
    private double[][] matrix;
    private int sumCount = 0, substrCount = 0,multCount = 0, divCount = 0;

    public void setMatrix(double[][] matrix) {
        this.matrix = matrix;
    }

    public double[][] getMatrix() {
        return matrix;
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


    public double[] solve() {
        int n = matrix.length;
        if ((matrix[0].length - matrix.length) != 1)
            throw new RuntimeException("Coefficient matrix is not square!");
        for (int i = 0; i < n; i++) {
            int permElIndex = i;
            double permittingElement = matrix[i][i];

            for (int j = 0; j < n+1; j++) {                //making permitting element equal to 1
                matrix[i][j] /= permittingElement;
                divCount++;
            }

            permittingElement = matrix[i][i];

            for (int j = i+1; j < matrix[0].length; j++)
                for (int k = 0; k < n; k++)
                    if (k != i) {
                        matrix[k][j] = permittingElement * matrix[k][j] - matrix[i][j] * matrix[k][i];
                        multCount += 2;
                        substrCount++;
                    }

            for (int j = 0; j < n; j++)
                if (i != j)
                    matrix[j][i] = 0;
        }

        double[] x = new double[n];
        for (int j = 0; j < n; j++)
            x[j] = matrix[j][n];

        return x;
    }

    public void showMatrix(double[][] matrix){
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++)
                System.out.print(matrix[i][j] + " ");
            System.out.println();
        }
    }


}
