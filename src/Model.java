import Model.exception.InconsistentMatrixException;
import Model.exception.InfiniteSolutionsAmountException;
import Model.exception.InvalidInputException;

import java.io.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Model {
    // зчитувач та записувач у файли
    private BufferedReader br;
    private BufferedWriter bw;
    //вхідна СЛАР та її розмірність
    private double[][] originSlae;
    private int eqtNum;
    private int varNum;
    //значення сумісності та єдиності рішення СЛАР, результативна СЛАР та масив результату
    private int consistence;
    private double[][] resultSlae;
    private double[] result;
    //вихідні та вихідні дані у вигляді рядків для зручного відображення у файлі та інтерфейсі
    private String methodName;
    private String originSlaeString, resultSlaeString, resultString, arithmeticsString, totalResultString;

    //лічильники елементарних арифметичних операцій
    private static int sumCount = 0, subtrCount = 0, multCount = 0, divCount = 0;

    //поветає ХИБНЕ, якщо файл містить некорректні символи
    //Якщо файл задовільний - записує матрицю з нього до поля класу
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

    //з вхідного масиву коефіцієнтів у вигляді масиву рядків генерує масив дробових значень величиною рівною другому параметру
    private double[] getEquationCoefs(String[] s, int eqtWidth) {
        double[] coefs = new double[eqtWidth];
        for (int i = 0; i < eqtWidth; i++)
            if (i < s.length && !s[i].isEmpty() & !s[i].isBlank())
                coefs[i] = Double.parseDouble(s[i]);
            else
                coefs[i] = 0;
        return coefs;
    }

    //записує результат у файл, що знаходиться у вказаній папці
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

    // повертає шлях до вихідного файлу, назву обраного методу вирішення СЛАР, вхідну, вихідну матриці,
    // результат та кількість зроблених арфиметичних операцій у вигляді одного рядка
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

    //повертає кількість викоинаних арифметичних операцій у вигляді одного рядка
    private String makeArithmeticsString() {
        return arithmeticsString = "Additions: " + sumCount + "\n" +
                "Subtractions: " + subtrCount + "\n" +
                "Multiplies: " + multCount + "\n" +
                "Divisions: " + divCount + "\n";
    }

    //повертає вхідну матрицю у вигляді одного рядка
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

    //повертає вихідну матрицю у вигляді одного рядка
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
    //повертає результат у вигляді одного рядка
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

    //генерує та поміщає в поле класу СЛАР з дробовими значеннями заданими велечинами
    public void generateFloatMatrix(int varNum, int eqtNum) {
        double[][] matrix = new double[eqtNum][varNum + 1];
        for (int i = 0; i < eqtNum; i++)
            for (int j = 0; j < varNum + 1; j++)
                matrix[i][j] = Math.random() * 10;
        this.originSlae = matrix;
        this.varNum = varNum;
        this.eqtNum = eqtNum;
    }

    //генерує та поміщає в поле класу СЛАР з цілими значеннями заданими велечинами
    public void generateIntMatrix(int varNum, int eqtNum) {
        double[][] matrix = new double[eqtNum][varNum + 1];
        for (int i = 0; i < eqtNum; i++)
            for (int j = 0; j < varNum + 1; j++)
                matrix[i][j] = Math.round(Math.random() * 10);
        this.originSlae = matrix;
        this.varNum = varNum;
        this.eqtNum = eqtNum;
    }

    //конвертує матрицю типу BigDecimal до типу double
    public static double[][] toDoubleMatrix(BigDecimal[][] m) {
        double[][] res = new double[m.length][m[0].length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                res[i][j] = m[i][j].doubleValue();

        return res;
    }

    //конвертує матрицю типу double до типу BigDecimal
    public static BigDecimal[][] toBigDecimalMatrix(double[][] m) {
        BigDecimal[][] res = new BigDecimal[m.length][m[0].length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                res[i][j] = BigDecimal.valueOf(m[i][j]);

        return res;
    }
 // онулює значення лічильників арифметичних опреаций
    private void nullifyCounters() {
        sumCount = 0;
        subtrCount = 0;
        multCount = 0;
        divCount = 0;
    }

    //перевіряє матрицю з поля класу на єдиність рішення, вирішує заданим методом та
    // записує результат у поле результату класу
    public void solveMatrixByMethod(String methodName) {
        if (!matrixIsReady()) {
            throw new InvalidInputException("Generate or read matrix from file firstly");
        }
        this.eqtNum = originSlae.length;
        this.varNum = originSlae[0].length - 1;
        if ((this.consistence = getMatrixConsistence(cloneMatrix(originSlae))) == -1) {
            throw new InconsistentMatrixException("Matrix is inconsistent");
        } else if (this.consistence == 1)
            throw new InfiniteSolutionsAmountException("Matrix has infinite solutions amount");
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

    //зводить вхідну матрицю до трикутного вигляду
    public static void makeTriangleView(double[][] matrix) {
        int eqtNum = matrix.length;
        int eqtWidth = matrix[0].length - 1;

        for (int i = 0; i < eqtNum; i++) {
            int max = i;
            for (int j = i+1; j < eqtNum; j++)
                if (Math.abs(matrix[j][i]) < Math.abs(matrix[max][j]))
                    max = j;

            double[] temp = matrix[i];
            matrix[i] = matrix[max];
            matrix[max] = temp;

            for (int k = i+1; k < eqtNum; k++) {
                if (matrix[i][i] == 0) {
                    addOneToEachElement(matrix[i]);
                    sumCount += matrix[i].length;
                }
                double alfa = matrix[k][i] / matrix[i][i];
                divCount++;
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

    //додає до кожного елемента вхідного масиву 1
    private static void addOneToEachElement(double[] arr) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] += 1;
        }
    }

    //повертає масив результату з раніше передбачуваної трикутної матриці
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

    //вирішує передану слар методом Жордана-Гауса, записує результат у поле класу та повертає його
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

    //вирішує передану слар методом обертань, записує результат у поле класу та повертає його
    public double[] rotationSolve(double[][] matrix) {
        double c,s, mik;
        for (int i = 0 ; i < matrix.length; i++) {
            if(numIsTooBigWithTooMuchZeros(matrix[i][i]))
                divideEach(matrix[i], Math.pow(10, getMinZerosAmountFromIndex(matrix[i], i)));
            for (int j = i+1; j < matrix.length; j++) {
                if(numIsTooBigWithTooMuchZeros(matrix[j][i]) && getMinZerosAmountFromIndex(matrix[j], i)-1 > 1)
                    divideEach(matrix[j], Math.pow(10, getMinZerosAmountFromIndex(matrix[j], i)-1));
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

//        showMatrix(matrix);

        double sum;
        double[] x = new double[varNum];
        for (int i = varNum-1; i >= 0; i--) {
            sum = 0;
            for (int j = i+1; j < eqtNum; j++) {
                sum += matrix[i][j] * x[j];
                multCount++;
                sumCount++;
            }
            if (matrix[i][i] == 0) {
                addOneToEachElement(matrix[i]);
                sum += matrix[i].length;
            }
            x[i] = (matrix[i][varNum] - sum)/matrix[i][i];

            subtrCount++;
            divCount++;
        }
        this.resultSlae = matrix;

        return x;
    }

    public static void showMatrix(double[][] m) {
        for (int i = 0; i < m.length; i++) {
            System.out.print("|");
            for (int j = 0; j < m[0].length; j++) {
                System.out.print(m[i][j] + "|");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        /*double[] n = new double[6];
        n[0] = 312345124500000000000000000000000000000000000000000000000000000000.21;
        n[1] = 312345124500000000000000000000000000000000000000000000000000.21;
        n[2] = 3123451245000000000000000000000000000000000000.21;
        n[3] = 31234512450000000000000000000000000000000000000000000000000000.21;
        n[4] = 31234512450000000000000000000000000000000000000000000000000000.21;
        n[5] = 31234512450000000000000000000000000000000000000000000000000000.21;
        System.out.println(String.format("%f", n[4]));
        System.out.println(Arrays.toString(n));
        System.out.println("num is too long = "+numIsTooBigWithTooMuchZeros(n[0]));
        System.out.println("min zeros = " + getMinZerosAmountFromIndex(n, 0));
        divideEach(n, Math.pow(10, getMinZerosAmountFromIndex(n, 0)-1));
        System.out.println(Arrays.toString(n));*/
        double a = -3;
        double b = 0;

        System.out.println(a/b);
    }

    private static void divideEach(double[] arr, double val) {
        for (int i = 0; i < arr.length; i++)
            if (arr[i] != 0) {
                System.out.printf("before divide:%f", arr[i]);
                arr[i] /= val;
                System.out.printf("after divide:%f", arr[i]);
            }

    }

    private static int getMinZerosAmountFromIndex(double[] arr, int index) {
        int min = getZerosAmount(arr[index]);
        int zeros;
        for (int i = index+1; i < arr.length; i++)
            if((zeros = getZerosAmount(arr[i])) < min)
                min = zeros;

        return min;
    }

    private static int getZerosAmount(double num) {
        String[] arr =String.format("%f", num).split(",");
        String valS = arr[0];
        int zerosCounter = 0;
        int index = valS.length()-1;
        char ch = valS.charAt(index);
        System.out.println("vals = " +valS);
        while (ch == '0' & index > 0 ) {
            zerosCounter++;
            ch = valS.charAt(index);
            index--;
        }
        return zerosCounter;
    }

    // HHHEEE'SSS AALIIIIIIVEEE
    private static boolean numIsTooBigWithTooMuchZeros(double num) {
        if(num == 0 || num < 9999999999999999.9)
            return false;
        String[] arr =String.format("%f", num).split(",");
        if(arr[0].charAt(0) == '0' || arr[0].length() < 15)
            return false;
        String valS = String.format("%f", num).split(",")[0];
        int zerosAmount = getZerosAmount(num);
        int nonZeroDigitsAmount = valS.length()-zerosAmount;
        if (nonZeroDigitsAmount < 5 & zerosAmount > 7)
            return true;

        if(nonZeroDigitsAmount >= 5 & zerosAmount > 5)
            return true;

        return false;
    }


    //повертає -1, якщо матриця несумісна, 0, якщо має єдине значення, 1, якщо безліч
    public static int getMatrixConsistence(double[][] m) {
        makeTriangleView(m);
        int extendedMatrixRank = getExtendedMatrixRank(m);
        if(extendedMatrixRank < m[0].length-1)
            return 1;
        return Integer.compare(getRegularMatrixRank(m), extendedMatrixRank);
    }

    //повертає значення рангу розширеної вхідної матриці
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

    //повертає значення рангу стандартної частини вхідної матриці
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

    //очищає всі результати у вигляді рядків
    public void nullifyResultingStrings() {
        originSlaeString = "";
        resultSlaeString = "";
        resultString = "";
        arithmeticsString = "";
        totalResultString = "";
    }

    //повертає ТАК, якщо поле вхідної матриці класу не дорівнює NULL
    public boolean matrixIsReady() {
        return this.originSlae != null;
    }

    //поветає копію вхідної матриці, що дохволяє проводити над нею зміни не втрачаючи початкового стану
    public static double[][] cloneMatrix(double[][] matrix) {
        if(matrix == null)
            return null;
        double[][] result = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix[0].length; j++)
                result[i][j] = matrix[i][j];
        return result;
    }

    // поветрає значення кількості змінних СЛАР
    public int getVarNum() {
        return varNum;
    }

    // поветрає значення кількості рівнянь СЛАР
    public int getEquationsNum() {
        return eqtNum;
    }

    //передає вхідну матрицю та її размірність до відповіних полів класу
    public void setMatrix(double[][] matrix) {
        this.originSlae = matrix;
        this.varNum = matrix[0].length - 1;
        this.eqtNum = matrix.length;
    }

    //повертає ТАК, якщо маси результату не дорівнює null
    public boolean resultExists() {
        return result != null;
    }

    //повертає вхідну матрицю
    public double[][] getOriginSlae() {
        return originSlae;
    }

}



