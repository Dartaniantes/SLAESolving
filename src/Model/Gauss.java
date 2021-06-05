package Model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Gauss extends SLAEMethod{


    public double[] solve() {
        makeTriangleView();
        return backtrace();
    }

    protected double[] backtrace() {
        int length = matrix.length;
        int width = matrix[0].length;
        BigDecimal[] x = new BigDecimal[length];

        for (int i = length-1; i >= 0; i--) {
            BigDecimal sum = BigDecimal.ZERO;
            for (int j = i+1; j < length; j++) {
                sum = sum.add(matrix[i][j].multiply(x[j]));
                multCount++;
                sumCount++;
            }
            //matrix[i][width-1]
            //vector[i]
            x[i] = matrix[i][width-1].subtract(sum).divide(matrix[i][i], RoundingMode.HALF_EVEN);
            subtrCount++;
            divCount++;
        }

        return Model.toDoubleArr(x);
    }
    protected void makeTriangleView() {
        int length = matrix.length;
        int freeElement = matrix[0].length - 1;

        for (int i = 0; i < length; i++) {
            int max = i;
            for (int j = i+1; j < length; j++)
                if (Math.abs(matrix[j][i].doubleValue()) < Math.abs(matrix[max][j].doubleValue()))
                    max = j;


            //matrix[i][freeElement] matrix[max][freeElement]
            //vector[i] vector[max]
            BigDecimal[] temp = matrix[i];
            matrix[i] = matrix[max];
            matrix[max] = temp;

            for (int k = i+1; k < length; k++) {
                BigDecimal alfa = matrix[k][i].divide(matrix[i][i], RoundingMode.HALF_EVEN);
                divCount++;
                //matrix[k][freeElement]  matrix[i][freeElement]
                //vector[k]  vector[i]
                matrix[k][freeElement] = matrix[k][freeElement].subtract(alfa.multiply(matrix[i][freeElement]));
                multCount++;
                subtrCount++;

                for (int t = i; t < length; t++) {
                    matrix[k][t] = matrix[k][t].subtract(alfa.multiply(matrix[i][t]));
                    multCount++;
                    subtrCount++;
                }
            }
        }
    }
}
