package Model;

import Model.exception.InconsistentMatrixException;
import Model.exception.InfiniteSolutionsAmountException;

import java.io.*;
import java.math.BigDecimal;
import java.util.Arrays;

public class Model {
    BufferedReader br;
    BufferedWriter bw;
    SLAEMethod method;
    Gauss gauss = new Gauss();
    JordanGauss jordanGauss = new JordanGauss();
    Rotation rotation = new Rotation();
    private double[][] originSlae;
    private int length;
    private int width;
    private int consistence;
    private double[][] resultSlae;
    private double[] result;
    private String methodName;
    private String originSlaeString, resultSlaeString, resultString, arithmeticsString;

    private int sumCount = 0, substrCount = 0, multCount = 0, divCount = 0;

    public static void main(String[] args) {
        BigDecimal num = BigDecimal.valueOf(3);
        System.out.println(num.add(num));
        System.out.println(num);
    }

    public boolean checkFile(String input) {
        String[] temp;
        try {
            br = new BufferedReader(new FileReader(input));
            temp = br.readLine().split(" ");
            int width = temp.length;
            int length = temp.length - 1;
            originSlae = new double[length][width];
            for (int i = 0; i < length; i++) {
                for (int k = 0; k < temp[i].length(); k++) {
                    if ((int) temp[i]
                            .charAt(k) < 48 || (int) temp[i].charAt(k) > 57)
                        return false;
                }
            }
            for (int i = 0; i < width; i++) {
                originSlae[0][i] = Double.parseDouble(temp[i]);
            }
            while (br.ready()) {
                for (int i = 1; i < length; i++) {
                    temp = br.readLine().split(" ");
                    for (int j = 0; j < width; j++) {
                        for (int k = 0; k < temp[j].length(); k++)
                            if (48 > (int) temp[j].charAt(k) || 57 < (int) temp[j].charAt(k))
                                return false;
                        originSlae[i][j] = Double.parseDouble(temp[j]);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void writeResultToFile(String outPath) {
        try {
            bw = new BufferedWriter(new FileWriter(outPath));
            bw.write("Method: " + methodName + "\n");
            bw.write("Input slae :\n");
            writeOriginSlae();
            bw.write("\nResult slae : \n");
            writeResultSlae();
            bw.write("\nResult :\n");
            writeResult();
            writeArithmetics();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeArithmetics() throws IOException {
        bw.write((arithmeticsString = "Additions: " + getSumCount() + "\n" +
                "Subtractions: " + getSubstrCount() + "\n" +
                "Multiplies: " + getMultCount() + "\n" +
                "Divisions: " + getDivCount() + "\n"));

    }

    ;

    private void writeOriginSlae() throws IOException {
        String temp;
        if (originSlae[0].length == 3) {
            for (int i = 0; i < originSlae.length; i++) {
                temp = originSlae[i][0] + " x" + (i + 1) + "  " + (originSlae[i][1] >= 0 ? "+" + originSlae[i][1] : originSlae[i][1])
                        + " y" + (i + 1) + "  = " + originSlae[i][2] + "\n";
                bw.write(temp);
                originSlaeString += temp;
            }
        } else {
            for (int i = 0; i < originSlae.length; i++)
                for (int j = 0; j < originSlae[0].length; j++) {
                    int index = j + 1;
                    if (j < originSlae[0].length - 1) {
                        if (j < originSlae[0].length - 2) {
                            temp = String.format("%.2f x" + index + (originSlae[i][j + 1] >= 0 ? "  +" : "  "), originSlae[i][j]);
                            bw.write(String.format("%.2f x" + index + (originSlae[i][j + 1] >= 0 ? "  +" : "  "), originSlae[i][j]));
                            originSlaeString += temp;
                        } else {
                            temp = String.format(" %.2f x" + index + " ", originSlae[i][j]);
                            bw.write(String.format(" %.2f x" + index + " ", originSlae[i][j]));
                            originSlaeString += temp;
                        }
                    } else {
                        temp = String.format("   =   %.2f " + "%n", originSlae[i][j]);
                        bw.write(String.format("   =   %.2f " + "%n", originSlae[i][j]));
                        originSlaeString += temp;
                    }
                }
        }
    }

    private void writeResultSlae() throws IOException {
        String temp;
        if (originSlae[0].length == 3) {
            for (int i = 0; i < originSlae.length; i++) {
                temp = resultSlae[i][0] + " x" + (i + 1) + "  " + (resultSlae[i][1] >= 0 ? "+" + resultSlae[i][1] : resultSlae[i][1])
                        + " y" + (i + 1) + "  = " + resultSlae[i][2] + "\n";
                bw.write(temp);
                resultSlaeString +=temp;
            }
        }
        for (int i = 0; i < resultSlae.length; i++)
            for (int j = 0; j < resultSlae[0].length; j++) {
                int index = j + 1;
                if (j < resultSlae[0].length - 1) {
                    if (j < resultSlae[0].length - 2) {
                        temp = String.format("%.2f x" + index + (originSlae[i][j + 1] >= 0 ? "  +" : "  "), resultSlae[i][j]);
                        bw.write(temp);
                        resultSlaeString += temp;
                    } else {
                        temp = String.format(" %.2f x" + index, resultSlae[i][j]);
                        bw.write(temp);
                        resultSlaeString += temp;
                    }
                } else {
                    temp = String.format("   =   %.2f " + "%n", resultSlae[i][j]);
                    bw.write(temp);
                    resultSlaeString += temp;
                }
            }
    }

    private void writeResult() throws IOException {
        String temp;
        if (result.length == 2) {
            temp = String.format("x" + " = " + " %.3f \n", result[0]);
            bw.write(temp);
            resultString += temp;
            temp = String.format("y" + " = " + " %.3f \n", result[1]);
            bw.write(String.format("y" + " = " + " %.3f \n", result[1]));
            resultString += temp;
        } else {
            for (int i = 0; i < result.length; i++) {
                int index = i + 1;
                temp = String.format("x" + index + " = " + " %.3f \n", result[i]);
                bw.write(String.format("x" + index + " = " + " %.3f \n", result[i]));
                resultString += temp;
            }
        }
    }


    public String getMethodName() {
        return methodName;
    }


    public void generateFloatMatrix(int varNum, int eqtNum) {
        double[][] matrix = new double[eqtNum][varNum + 1];
        for (int i = 0; i < eqtNum; i++)
            for (int j = 0; j < varNum + 1; j++)
                matrix[i][j] = Math.random() * 10;
        this.originSlae = matrix;
    }

    public double[][] generateIntMatrix(int varNum, int eqtNum) {
        double[][] matrix = new double[eqtNum][varNum + 1];
        for (int i = 0; i < eqtNum; i++)
            for (int j = 0; j < varNum + 1; j++)
                matrix[i][j] = Math.round(Math.random() * 10);
        this.originSlae = matrix;
        return matrix;
    }

    public void solve(double[][] matrix, String methodName) {
        setMatrix(matrix);
        solveMatrixByMethod(methodName);
    }

    public static double[][] toDoubleMatrix(BigDecimal[][] m) {
        double[][] res = new double[m.length][m[0].length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                res[i][j] = m[i][j].doubleValue();

        return res;
    }
    public static BigDecimal[][] toBigDecimalMatrix(double[][] m) {
        BigDecimal[][] res = new BigDecimal[m.length][m[0].length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                res[i][j] = BigDecimal.valueOf(m[i][j]);

        return res;
    }

    public static double[] toDoubleArr(BigDecimal[] arr) {
        double[] res = new double[arr.length];
        for (int i = 0; i < arr.length; i++)
            res[i] = arr[i].doubleValue();
        return res;
    }
    public static BigDecimal[] toBigDecimalArr(double[] arr) {
        BigDecimal[] res = new BigDecimal[arr.length];
        for (int i = 0; i < arr.length; i++)
            res[i] = BigDecimal.valueOf(arr[i]);
        return res;
    }



    public void solveMatrixByMethod(String methodName) {
        if (matixIsReady()) {
            this.length = originSlae.length;
            this.width = originSlae[0].length;
            if ((this.consistence = SLAEMethod.getMatrixConsistence(cloneMatrix(originSlae))) == -1) {
                throw new InconsistentMatrixException("Matrix is inconsistent");
            } else if (this.consistence == 1)
                throw new InfiniteSolutionsAmountException("Matrix has infinite solutions amount");
        }
        System.out.println("originSlae before coutings:");
        showMatrix(originSlae);
        this.methodName = methodName;
        if (methodName == "Gauss")
            method = gauss;
        else if (methodName == "Jordan-Gauss")
            method = jordanGauss;
        else if (methodName == "Rotation")
            method = rotation;
        method.nullifyCounters();
        synchronizeModelNMethodCounters();
        method.setMatrix(cloneMatrix(originSlae));
        result = method.solve();
        synchronizeModelNMethodCounters();
        this.resultSlae = method.getMatrix();
    }

    public void nullifyResultingStrings() {
        originSlaeString = "";
        resultSlaeString = "";
        resultString = "";
        arithmeticsString = "";
    }

    private void synchronizeModelNMethodCounters() {
        this.divCount = method.divCount;
        this.multCount = method.multCount;
        this.sumCount = method.sumCount;
        this.substrCount = method.subtrCount;
    }

    public boolean matixIsReady() {
        return this.originSlae != null;
    }

    public static double[][] cloneMatrix(double[][] matrix) {
        double[][] result = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix[0].length; j++)
                result[i][j] = matrix[i][j];
        return result;
    }


    public static void showMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            System.out.print("|");
            for (int j = 0; j < matrix[0].length; j++) {
                if (j < matrix[0].length - 1)
                    System.out.print(String.format(" %.3f |", matrix[i][j]));
                else
                    System.out.println(String.format(" %.3f |", matrix[i][j]));
            }
        }
        System.out.println();
    }

    public int getVarNum() {
        return originSlae[0].length - 1;
    }

    public int getEqationsNum() {
        return originSlae.length;
    }


    public double[][] extractMatrix(double[][] slae) {
        double[][] matrix = new double[slae.length][slae.length];
        for (int i = 0; i < slae.length; i++)
            for (int j = 0; j < slae.length; j++)
                matrix[i][j] = slae[i][j];
        return matrix;
    }

    public double[] extractVector(double[][] slae) {
        double[] vector = new double[slae.length];
        for (int i = 0; i < slae.length; i++)
            vector[i] = slae[i][slae[0].length - 1];
        return vector;
    }

    private double[][] makeSlae(double[][] matrix, double[] resVector) {
        double[][] slae = new double[matrix.length][matrix.length + 1];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length + 1; j++) {
                if (j < matrix.length)
                    slae[i][j] = matrix[i][j];
                else
                    slae[i][j] = resVector[i];
            }
        }
        return slae;
    }

    public void setMatrix(double[][] matrix) {
        this.originSlae = matrix;
    }

    public boolean resultExists() {
        return result != null;
    }

    public boolean matrixExists() {
        return originSlae != null;
    }

    public double[][] getOriginSlae() {
        return originSlae;
    }

    public double[][] getResultSlae() {
        return resultSlae;
    }

    public double[] getResult() {
        return result;
    }

    public int getSumCount() {
        return sumCount;
    }

    public int getSubstrCount() {
        return substrCount;
    }

    public String getOriginSlaeString() {
        return originSlaeString;
    }

    public String getResultSlaeString() {
        return resultSlaeString;
    }

    public String getResultString() {
        return resultString;
    }

    public String getArithmeticsString() {
        return arithmeticsString;
    }

    public int getMultCount() {
        return multCount;
    }

    public int getDivCount() {
        return divCount;
    }
}



