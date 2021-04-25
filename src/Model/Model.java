package Model;

import java.io.*;

public class Model {
    BufferedReader br;
    BufferedWriter bw;
    SLAEMethod method;
    Gauss gauss = new Gauss();
    JordanGauss jordanGauss = new JordanGauss();
    Rotation rotation = new Rotation();
    private double[][] originSlae;
    private double[][] resultSlae;
    private double[] result;

    public boolean checkFile(String input) {
        String[] temp;
        try {
            br = new BufferedReader(new FileReader(input));
            temp = br.readLine().split(" ");
            int width = temp.length;
            int length = temp.length-1;
            originSlae = new double[length][width];
            for (int i = 0; i < length; i++) {
                for (int k = 0; k < temp[i].length(); k++) {
                    if((int)temp[i]
                            .charAt(k) < 48 || (int)temp[i].charAt(k) > 57)
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
                            if( 48 > (int)temp[j].charAt(k) ||  57 < (int)temp[j].charAt(k))
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
            bw.write("Input slae :\n");
            for (int i = 0; i < originSlae.length; i++)
                for (int j = 0; j < originSlae[0].length; j++) {
                    int index = j+1;
                    if(j < originSlae[0].length-1){
                        if (j < originSlae[0].length-2)
                            bw.write(String.format(" %.2f x" + index + "  +  ", originSlae[i][j]));
                        else
                            bw.write(String.format(" %.2f x" + index, originSlae[i][j]));
                    } else
                        bw.write(String.format("   =   %.2f " + "%n", originSlae[i][j]));
                }
            bw.write("\nResult slae : \n");
            for (int i = 0; i < resultSlae.length; i++)
                for (int j = 0; j < resultSlae[0].length; j++) {
                    int index = j+1;
                    if(j < resultSlae[0].length-1){
                        if (j < resultSlae[0].length-2)
                            bw.write(String.format(" %.2f x" + index + "  +  ", resultSlae[i][j]));
                        else
                            bw.write(String.format(" %.2f x" + index, resultSlae[i][j]));
                    } else
                        bw.write(String.format("   =   %.2f " + "%n", resultSlae[i][j]));
                }
            bw.write("\nResult :\n");
            for (int i = 0; i < result.length; i++) {
                int index = i + 1;
                bw.write(String.format("x" + index + " = " + " %.3f \n", result[i]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void generateFloatMatrix(int varNum) {
        double[][] matrix = new double[varNum][varNum + 1];
        for (int i = 0; i < varNum; i++)
            for (int j = 0; j < varNum + 1; j++)
                matrix[i][j] = Math.random() * 10;
        this.originSlae = matrix;
    }

    public double[][] generateIntMatrix(int varNum) {
        double[][] matrix = new double[varNum][varNum + 1];
        for (int i = 0; i < varNum; i++)
            for (int j = 0; j < varNum + 1; j++)
                matrix[i][j] = Math.round(Math.random() * 10);
        this.originSlae = matrix;
        return matrix;
    }


    //add InconsistentMatrixException catch
    public void solveMatrixByMethod(String methodName) {
        if (methodName == "Gauss") {
//            gauss.setMatrix(extractMatrix(cloneMatrix(originSlae)));
//            gauss.setVector(extractVector(cloneMatrix(originSlae)));
            gauss.setMatrix(cloneMatrix(originSlae));
            result = gauss.solve();
            resultSlae = gauss.getMatrix();
//            resultSlae = makeSlae(gauss.getMatrix(), result);
        } else if (methodName == "Jordan-Gauss") {
            jordanGauss.setMatrix(cloneMatrix(originSlae));
            result = jordanGauss.solve();
            resultSlae = jordanGauss.getMatrix();
        } else if (methodName == "Rotation") {
            rotation.setMatrix(cloneMatrix(originSlae));
            result = rotation.solve();
            resultSlae = rotation.getMatrix();
        }
//            result = method.solve();
    }

    private double[][] cloneMatrix(double[][] matrix) {
        double[][] result = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++)
            for (int j = 0; j < matrix[0].length; j++)
                result[i][j] = matrix[i][j];
        return result;
    }

    public void showMatrix(double[][]matrix) {
        for (int i = 0; i < matrix.length; i++) {
            System.out.print("|");
            for (int j = 0; j < matrix[0].length; j++) {
                if (j < matrix[0].length - 1)
                    System.out.print(String.format(" %.3f |", matrix[i][j]));
                else
                    System.out.println(String.format(" %.3f |", matrix[i][j]));
            }
        }
    }

    public int getVarNum() {
        return originSlae[0].length-1;
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
            vector[i] = slae[i][slae[0].length-1];
        return vector;
    }

    private double[][] makeSlae(double[][] matrix, double[] resVector) {
        double[][] slae = new double[matrix.length][matrix.length + 1];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length+1; j++) {
                if(j < matrix.length)
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
}



