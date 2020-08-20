import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class Matrix implements Cloneable{
    private int[][] matrix;
    private int length;
    private int width;

    public Matrix(int[][] a) {
        matrix = a.clone();
        length = matrix.length;
        width = matrix[0].length;
    }

    public Matrix(int raws, int columns) {
        this.length = raws;
        this.width = columns;
        matrix = new int[length][width];
    }

    public Matrix(Matrix m) {
        matrix = m.getMatrix();
        length = m.getLength();
        width = m.getWidth();
    }

    /*public void involute(int exp) {
        int[][] temp = matrix.clone();
        for (int i = 0; i < exp - 1; i++) {
            multiply(temp);
        }
    }*/

    public void involute(int exp) {
        Matrix temp = new Matrix(matrix.clone());
        for (int i = 0; i < exp - 1; i++) {
            multiply(temp);
        }
    }

    public Matrix involuteReturn(int exp) {
        Matrix temp = new Matrix(matrix.clone());
        for (int i = 0; i < exp - 1; i++) {
            multiply(temp);
        }
        return temp;
    }
    public void multiply(int[][] a) {
        int[][] temp = null;

        int m = matrix.length;
        int n = matrix[0].length;
        int q = a[0].length;

        temp = new int[m][q];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < q; j++) {
                int sum = 0;

                for (int k = 0; k < n; k++) {
                    sum = sum + matrix[i][k] * a[k][j];
                }

                temp[i][j] = sum;
            }
        }

        matrix = temp.clone();
    }
    public void multiply(Matrix mtrx) {
        int[][] a = mtrx.getMatrix().clone();
        int[][] temp = null;

        int m = matrix.length;
        int n = matrix[0].length;
        int q = a[0].length;

        temp = new int[m][q];
        int i, j, k, sum = 0;
        for (i = 0; i < m; i++) {
            for (j = 0; j < q; j++) {
                for (k = 0; k < n; k++) {
                    sum = sum + matrix[i][k] * a[k][j];
                }

                temp[i][j] = sum;
            }
        }

        matrix = temp.clone();
    }

    public static Matrix multiply(Matrix m1, Matrix m2) {
        int m = m1.length;
        int n = m1.width;
        int q = m2.length;
        Matrix result = new Matrix(m, q);
        int sum = 0, i,j,k;
        for (i = 0; i < m; i++) {
            for (j = 0; j < q; j++) {
                for (k = 0; k < n; k++) {
                    sum = sum + m1.getElement(i,k) * m2. getElement(k,j);
                }
                result.setElement(i,j, sum);
            }
        }
        return result;
    }
    public static Integer[][] sum(Integer[][] fMatrix, Integer[][] sMatrix){
        Integer[][] sumMatrix = new Integer[fMatrix.length][fMatrix.length];
        for(int i = 0; i<fMatrix.length; i++){
            for(int j = 0; j<fMatrix[i].length;j++){
                sumMatrix[i][j] = fMatrix[i][j] + sMatrix[i][j];
            }
        }
        return sumMatrix;
    }
    public static Matrix sum(Matrix m1, Matrix m2) {
        Matrix sumMatrix = new Matrix(new int[m1.length][m1.length]);
        int new_element;
        for(int i = 0; i<m1.length; i++){
            for(int j = 0; j<m1.width;j++){
                new_element = m1.getElement(i,j) + m2.getElement(i,j);
                sumMatrix.setElement(i, j, new_element);
            }
        }
        return sumMatrix;
    }

    public static Matrix booleanOr(Matrix m1, Matrix m2) {
        int[][] intResult = new int [m1.length][m1.width];
        Matrix result;
        for (int i = 0; i < m1.length; i++) {
            for (int j = 0; j < m1.width; j++) {
                if(m1.getElement(i,j) > 0 || m2.getElement(i, j) > 0)
                    intResult[i][j] = 1;
                else
                    intResult[i][j] = 0;
            }
        }
        result = new Matrix(intResult);
        return result;
    }


    public int[] findRowsMaxs(){
        int[] rowsMaxs = new int[length];
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++)
                if(matrix[i][j] > max)
                    max = matrix[i][j];
            rowsMaxs[i] = max;
        }
        return rowsMaxs;
    }

    public static Integer[][] transpose(Integer[][] matrix){
        Integer[][] transposeMatrix = new Integer[matrix.length][matrix.length];

        for(int i = 0; i<transposeMatrix.length; i++){
            for(int j = 0; j<transposeMatrix[i].length;j++){
                transposeMatrix[i][j] = matrix[j][i];
            }
        }
        return transposeMatrix;
    }
    public static Integer[][] booleanTransf(Integer[][] tMatrix){
        for (int i = 0; i < tMatrix.length; i++) {
            for (int j = 0; j < tMatrix[i].length; j++) {
                if (tMatrix[i][j] > 1)
                    tMatrix[i][j] = 1;
            }
        }
        return tMatrix;
    }

   /* public void booleanTransf() {
        for (int i = 0; i < length; i++)
            for (int j = 0; j < width; j++)
                if (this.matrix[i][j] > 0)
                    this.matrix[i][j] = 1;
    }*/

    public int findMaxElement(){
        int max = 0;
        for (int i = 0; i < length; i++)
            for (int j = 0; j < width; j++)
                if(matrix[i][j] > max)
                    max = matrix[i][j];
        return max;
    }

    public static Integer[][] toIntegerArray(int[][] array){
        Integer[][] integers = new Integer[array.length][array[1].length];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j<array[i].length; j++){
                integers[i][j] = array[i][j];
            }
        }
        return integers;
    }
    public static int[][] toIntArray(Integer[][] array){
        int[][] intArray = new int[array.length][array[1].length];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j<array[i].length; j++){
                intArray[i][j] = array[i][j];
            }
        }
        return intArray;
    }

    public Matrix cloneMatrix(){
        Matrix result;
        int[][] cloned = new int[this.length][this.width];
        for (int i = 0; i < this.length; i++) {
            for (int j = 0; j < this.width; j++) {
                cloned[i][j] = matrix[i][j];
            }
        }
        result = new Matrix(cloned);
        return result;
    }
    public static void cloneM1ToM2(int[][] m1,int[][] m2){
        Matrix result;
        for (int i = 0; i < m1.length; i++) {
            for (int j = 0; j < m1[0].length; j++) {
                m2[i][j] = m1[i][j];
            }
        }
    }

    public void show() {
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                if (j == matrix[0].length - 1)
                    System.out.println(matrix[i][j]);
                else
                    System.out.print(matrix[i][j] + " ");
            }
        }
    }

    public static void showMatrix(Matrix m){
        for (int i = 0; i < m.matrix.length; i++) {
            for (int j = 0; j < m.matrix[0].length; j++) {
                if (j == m.matrix[0].length - 1)
                    System.out.println(m.matrix[i][j]);
                else
                    System.out.print(m.matrix[i][j] + " ");
            }
        }
        System.out.println();
    }
    public static void showMatrix(int[][] m){
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                if (j == m[0].length - 1)
                    System.out.println(m[i][j]);
                else
                    System.out.print(m[i][j]);
            }
        }
        System.out.println();
    }
    public static void showMatrix(Integer[][] m) {
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                if (j == m[0].length - 1)
                    System.out.println(m[i][j]);
                else
                    System.out.print(m[i][j]);
            }
        }
        System.out.println();
    }
    public static Matrix identityMatrix(Matrix mtrx) {
        Matrix identity_m = new Matrix(new int[mtrx.length][mtrx.width]);
        for (int i = 0; i < identity_m.length; i++) {
            identity_m.setElement(i, i, 1);
        }
        return identity_m;
    }

    public void writeToFile(String path) {
        try (BufferedWriter br = new BufferedWriter(new FileWriter(path))){
            for (int i = 0; i < length; i++) {
                for (int j = 0; j < width; j++) {
                    if(j == width - 1)
                        br.write(matrix[i][j] + "\n");
                    else
                        br.write(matrix[i][j] + " ");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Matrix clone() throws CloneNotSupportedException{
        return (Matrix) super.clone();
    }
    public int[][] getMatrix(){
        return matrix;
    }
    public int getElement(int i, int j) {
        return matrix[i][j];
    }
    public void setElement(int i, int j, int el) {
        matrix[i][j] = el;
    }
    public int getLength() {
        return length;
    }
    public int getWidth() {
        return width;
    }
}
