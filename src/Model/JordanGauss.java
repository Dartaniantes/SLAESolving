package Model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class JordanGauss extends SLAEMethod{

    public double[] solve() {
        int n = matrix.length;
        if ((matrix[0].length - matrix.length) != 1)
            throw new RuntimeException("Coefficient matrix is not square!");
        for (int i = 0; i < n; i++) {
            BigDecimal permittingElement = matrix[i][i];

            for (int j = 0; j < n+1; j++) {                //making permitting element equal to 1
                matrix[i][j] = matrix[i][j].divide(permittingElement, RoundingMode.HALF_EVEN);
                divCount++;
            }

            permittingElement = matrix[i][i];

            for (int j = i+1; j < matrix[0].length; j++)
                for (int k = 0; k < n; k++)
                    if (k != i) {
                        matrix[k][j] = permittingElement.multiply(matrix[k][j]).subtract(matrix[i][j].multiply(matrix[k][i]));
                        multCount += 2;
                        subtrCount++;
                    }

            for (int j = 0; j < n; j++)
                if (i != j)
                    matrix[j][i] = BigDecimal.ZERO;
        }

        BigDecimal[] x = new BigDecimal[n];
        for (int j = 0; j < n; j++)
            x[j] = matrix[j][n];

        return Model.toDoubleArr(x);
    }
}
