package Model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Rotation extends SLAEMethod{

    //self-made
    public double[] solve() {
        BigDecimal c,s, mik;
        for (int i = 0 ; i < matrix.length; i++) {
            for (int j = i+1; j < matrix.length; j++) {
                c = matrix[i][i];
                s = matrix[j][i];
                for (int k = 0; k < matrix[0].length; k++) {
                    mik = matrix[i][k];
                    matrix[i][k] = c.multiply(matrix[i][k]).add(s.multiply(matrix[j][k]));
                    matrix[j][k] = c.multiply(matrix[j][k]).subtract(s.multiply(mik));
                    multCount += 4;
                    sumCount++;
                    subtrCount++;
                }
            }
        }

        BigDecimal sum;
        BigDecimal[] result = new BigDecimal[matrix[0].length-1];
        for (int i = varNum -1; i >= 0; i--) {
            sum = BigDecimal.ZERO;
            for (int j = i+1; j < varNum; j++) {
                sum = sum.add(matrix[i][j].multiply(result[j]));
                multCount++;
                sumCount++;
            }
            result[i] = matrix[i][eqtNum -1].subtract(sum).divide(matrix[i][i], RoundingMode.HALF_EVEN);
            subtrCount++;
            divCount++;
        }

        return Model.toDoubleArr(result);
    }

}
