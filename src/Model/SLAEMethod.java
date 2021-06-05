package Model;

import java.math.BigDecimal;

public abstract class SLAEMethod {
    protected BigDecimal[][] matrix;
    protected int varNum;
    protected int eqtNum;
    protected static int sumCount = 0, subtrCount = 0,multCount = 0, divCount = 0;

    public abstract double[] solve();

    protected void nullifyCounters() {
        sumCount = 0;
        subtrCount = 0;
        multCount = 0;
        divCount = 0;
    }

    public void setMatrix(double[][] matrix){
        this.matrix = Model.toBigDecimalMatrix(matrix);
        varNum = matrix[0].length-1;
        eqtNum = matrix.length;
    }

    public double[][] getMatrix() {
        return Model.toDoubleMatrix(matrix);
    }

    public void showMatrix() {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public int getSumCount() {
        return sumCount;
    }

    public int getSubstrCount() {
        return subtrCount;
    }

    public int getMultCount() {
        return multCount;
    }

    public int getDivCount() {
        return divCount;
    }

    public static int getMatrixConsistence(double[][] m) {
        makeTriangleView(m);
        int extendedMatrixRank = getExtendedMatrixRank(m);
        if(extendedMatrixRank < m[0].length-1)
            return 1;
        return Integer.compare(getRegularMatrixRank(m), extendedMatrixRank);
    }

    private static int getExtendedMatrixRank(double[][] m) {
        int nonZeroRawCounter = 0;
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                if(m[i][j] != 0){
                    nonZeroRawCounter++;
                    j = m[0].length;
                }
            }
        }
        return nonZeroRawCounter;
    }

    private static int getRegularMatrixRank(double[][] m) {
        int nonZeroRawCounter = 0;
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length-1; j++) {
                if(m[i][j] != 0){
                    nonZeroRawCounter++;
                    j = m[0].length;
                }
            }
        }
        return nonZeroRawCounter;
    }

    private static void makeTriangleView(double[][] matrix) {
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

}
