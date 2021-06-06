package Model;

import java.math.RoundingMode;

public class Rotation extends SLAEMethod{

    //self-made
    public double[] solve() {
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
                    subtrCount++;
                }
            }
        }

        double sum;
        double[] x = new double[matrix.length];
        for (int i = varNum-1; i >= 0; i--) {
            sum = 0;
            for (int j = i+1; j < eqtNum; j++) {
                sum += matrix[i][j] * x[j];
                multCount++;
                sumCount++;
            }
            x[i] = (matrix[i][varNum] - sum)/matrix[i][i];

            subtrCount++;
            divCount++;
        }

        return x;
    }

}
