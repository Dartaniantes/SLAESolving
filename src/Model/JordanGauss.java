package Model;

public class JordanGauss extends SLAEMethod {

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
                        subtrCount++;
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

}
