package Model;

import Model.exception.InconsistentMatrixException;
import Model.exception.InfiniteSolutionsAmountException;

import java.io.*;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class Model {
    private BufferedReader br;
    private BufferedWriter bw;
    private double[][] originSlae;
    private int eqtNum;
    private int varNum;
    private int consistence;
    private double[][] resultSlae;
    private double[] result;
    private String methodName;
    private String originSlaeString, resultSlaeString, resultString, arithmeticsString, totalResultString;

    private static int sumCount = 0, subtrCount = 0, multCount = 0, divCount = 0;

    public boolean fileIsValid(File input) {
        List<String[]> tempList = new LinkedList<>();
        try {
            br = new BufferedReader(new FileReader(input));
            while (br.ready())
                tempList.add(br.readLine().split(" +"));
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int maxWidth = 0;
        for (int i = 0; i < tempList.size(); i++)
            if(tempList.get(i)[0].isEmpty() | tempList.get(i)[0].isBlank())
                tempList.remove(i);
            else {
                for (String s:tempList.get(i)) {
                    if(!s.matches("-?\\d+([.,]\\d+)?"))
                        return false;
                    else if (s.contains(","))
                        s.replace(',', '.');
                }
                if (tempList.get(i).length > maxWidth)
                    maxWidth = tempList.get(i).length;

            }
        int eqtNum = 2, eqtWidth = 2;
        if (tempList.size() > eqtNum)
            eqtNum = tempList.size();
        if (maxWidth > eqtWidth)
            eqtWidth = maxWidth;

        originSlae = new double[eqtNum][eqtWidth];
        for (int i = 0; i < eqtNum; i++) {
            originSlae[i] = getEquationCoefs(tempList.get(i), eqtWidth);
        }
        return true;
    }

    private double[] getEquationCoefs(String[] s, int eqtWidth) {
        double[] coefs = new double[eqtWidth];
        for (int i = 0; i < eqtWidth; i++)
            if (i < s.length && !s[i].isEmpty() & !s[i].isBlank())
                coefs[i] = Double.parseDouble(s[i]);
            else
                coefs[i] = 0;
        return coefs;
    }

    public void writeResultToFile(String outPath) {
        File outF = new File(outPath);
        if (outF.isDirectory()) {
            outF = new File(outPath + "\\" + (outF.listFiles().length+1) + ".txt");
            try {
                outF.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            bw = new BufferedWriter(new FileWriter(outF));
            bw.write(totalResultString);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String makeTotalResultString() {
        return totalResultString =
                "Method: "+ methodName + "\n" +
                        "Input slae :\n" +
                        makeOriginSlaeString() +
                        "\nResult slae : \n" +
                        makeResultSlaeString() +
                        "\nResult :\n" +
                        makeResultString()+
                        makeArithmeticsString();
    }

    private String makeArithmeticsString() {
        return arithmeticsString = "Additions: " + sumCount + "\n" +
                "Subtractions: " + subtrCount + "\n" +
                "Multiplies: " + multCount + "\n" +
                "Divisions: " + divCount + "\n";
    }

    private String makeOriginSlaeString() {
        if (originSlae[0].length == 3) {
            for (int i = 0; i < originSlae.length; i++) {
                originSlaeString += originSlae[i][0] + " x" + (i + 1) + "  " + (originSlae[i][1] >= 0 ? "+" + originSlae[i][1] : originSlae[i][1])
                        + " y" + (i + 1) + "  = " + originSlae[i][2] + "\n";
            }
        } else {
            for (int i = 0; i < originSlae.length; i++)
                for (int j = 0; j < originSlae[0].length; j++) {
                    int index = j + 1;
                    if (j < originSlae[0].length - 1) {
                        if (j < originSlae[0].length - 2) {
                            originSlaeString += String.format("%.2f x" + index + (originSlae[i][j + 1] >= 0 ? "  +" : "  "), originSlae[i][j]);
                        } else {
                            originSlaeString += String.format(" %.2f x" + index + " ", originSlae[i][j]);
                        }
                    } else {
                        originSlaeString += String.format("   =   %.2f " + "%n", originSlae[i][j]);
                    }
                }
        }
        return originSlaeString;
    }

    private String makeResultSlaeString() {
        for (int i = 0; i < resultSlae.length; i++)
            for (int j = 0; j < resultSlae[0].length; j++) {
                int index = j + 1;
                if (j < resultSlae[0].length - 1)
                    if (j < resultSlae[0].length - 2)
                        resultSlaeString += String.format("%.2f x" + index + (originSlae[i][j + 1] >= 0 ? "  +" : "  "), resultSlae[i][j]);
                    else
                        resultSlaeString += String.format(" %.2f x" + index, resultSlae[i][j]);
                else
                    resultSlaeString += String.format("   =   %.2f " + "%n", resultSlae[i][j]);

            }
        return resultSlaeString;
    }

    private String makeResultString()  {
        if (result.length == 2)
            resultString += String.format("x" + " = " + " %.3f \n", result[0]) + String.format("y" + " = " + " %.3f \n", result[1]);
        else
            for (int i = 0; i < result.length; i++) {
                int index = i + 1;
                resultString += String.format("x" + index + " = " + " %.3f \n", result[i]);
            }
            return resultString;

    }

    public void generateFloatMatrix(int varNum, int eqtNum) {
        double[][] matrix = new double[eqtNum][varNum + 1];
        for (int i = 0; i < eqtNum; i++)
            for (int j = 0; j < varNum + 1; j++)
                matrix[i][j] = Math.random() * 10;
        this.originSlae = matrix;
        this.varNum = varNum;
        this.eqtNum = eqtNum;
    }

    public void generateIntMatrix(int varNum, int eqtNum) {
        double[][] matrix = new double[eqtNum][varNum + 1];
        for (int i = 0; i < eqtNum; i++)
            for (int j = 0; j < varNum + 1; j++)
                matrix[i][j] = Math.round(Math.random() * 10);
        this.originSlae = matrix;
        this.varNum = varNum;
        this.eqtNum = eqtNum;
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

    private void nullifyCounters() {
        sumCount = 0;
        subtrCount = 0;
        multCount = 0;
        divCount = 0;
    }

    public void solveMatrixByMethod(String methodName) {
        if (matrixIsReady()) {
            this.eqtNum = originSlae.length;
            this.varNum = originSlae[0].length - 1;
            if ((this.consistence = getMatrixConsistence(cloneMatrix(originSlae))) == -1) {
                throw new InconsistentMatrixException("Matrix is inconsistent");
            } else if (this.consistence == 1)
                throw new InfiniteSolutionsAmountException("Matrix has infinite solutions amount");
        }
        this.methodName = methodName;
        nullifyCounters();
        if (methodName == "Gauss") {
            result = gaussSolve(cloneMatrix(originSlae));
        }
        else if (methodName == "Jordan-Gauss") {
            result = jordanGaussSolve(cloneMatrix(originSlae));
        }
        else if (methodName == "Rotation") {
            result = rotationSolve(cloneMatrix(originSlae));
        }
    }
    public double[] gaussSolve(double[][] matrix) {
        makeTriangleView(matrix);
        return backtrace(matrix);
    }

    public static void makeTriangleView(double[][] matrix) {
        int eqtNum = matrix.length;
        int eqtWidth = matrix[0].length - 1;

        for (int i = 0; i < eqtNum; i++) {
            int max = i;
            for (int j = i+1; j < eqtNum; j++)
                if (Math.abs(matrix[j][i]) < Math.abs(matrix[max][j]))
                    max = j;


            //matrix[i][eqtWidth] matrix[max][eqtWidth]
            //vector[i] vector[max]
            double[] temp = matrix[i];
            matrix[i] = matrix[max];
            matrix[max] = temp;

            //don't need this line if vector is not in use(extended matrix in 'matrix' variable)
            /*double v = matrix[i][eqtWidth];
            matrix[i][eqtWidth] = matrix[max][eqtWidth];
            matrix[max][eqtWidth] = v;*/

            /*if (Math.abs(matrix[i][i]) <= EPSILON)
                throw new RuntimeException("Matrix is singular");*/

            for (int k = i+1; k < eqtNum; k++) {
                if(matrix[i][i] == 0)
                    addOneToEachElement(matrix[i]);
                double alfa = matrix[k][i] / matrix[i][i];
                divCount++;
                //matrix[k][eqtWidth]  matrix[i][eqtWidth]
                //vector[k]  vector[i]
                matrix[k][eqtWidth] -= alfa * matrix[i][eqtWidth];
                multCount++;
                subtrCount++;

                for (int j = i; j < eqtNum; j++) {
                    matrix[k][j] -= alfa*matrix[i][j];
                    multCount++;
                    subtrCount++;
                }
            }
        }
    }

    private static void addOneToEachElement(double[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] += 1;
        }
    }

    private double[] backtrace(double[][] matrix) {
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
        this.resultSlae = matrix;
        return x;
    }

    public double[] jordanGaussSolve(double[][] matrix) {
        int n = matrix.length;
        if ((matrix[0].length - matrix.length) != 1)
            throw new RuntimeException("Coefficient matrix is not square!");
        if (matrix[0][0] == 0) {
            for (int i = 0; i < varNum; i++) {
                matrix[0][i] += 1;
            }
        }
        for (int i = 0; i < n; i++) {
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
        this.resultSlae = matrix;

        return x;
    }

    public double[] rotationSolve(double[][] matrix) {
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
        double[] x = new double[varNum];
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
        this.resultSlae = matrix;

        return x;
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

    public void nullifyResultingStrings() {
        originSlaeString = "";
        resultSlaeString = "";
        resultString = "";
        arithmeticsString = "";
        totalResultString = "";
    }
    public boolean matrixIsReady() {
        return this.originSlae != null;
    }

    public static double[][] cloneMatrix(double[][] matrix) {
        double[][] result = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix[0].length; j++)
                result[i][j] = matrix[i][j];
        return result;
    }


    public int getVarNum() {
        return varNum;
    }

    public int getEquationsNum() {
        return eqtNum;
    }

    public void setMatrix(double[][] matrix) {
        this.originSlae = matrix;
        this.varNum = matrix[0].length - 1;
        this.eqtNum = matrix.length;
    }

    public boolean resultExists() {
        return result != null;
    }

    public double[][] getOriginSlae() {
        return originSlae;
    }

}



