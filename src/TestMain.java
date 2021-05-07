import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class TestMain extends Application {

    private Stage st;
    private Scene sc;
    private Group group;
    private double[][] m;
    private int height = 720;
    private int width = 1080;

    public static void main(String[] args) {
        launch(args);
    }

    public TestMain() {

    }

    public TestMain(double[][] m) {
        this.m = m;
    }

    @Override
    public void start(Stage stage) {

        stage.setTitle("Chart");
        this.st = stage;
        this.group = new Group();

        Scene scene = new Scene(group);
        this.sc = scene;

        stage.setHeight(height);
        stage.setWidth(width);
//        drawCoordinates(height, width);

//        drawFunc(graph, height, width, f);
//        getGraphSolve(graph, height, width, this.m);
        getGraphSolve(height, width, new double[][]{{10,1,8},
                                                    {1,2,9}});
        stage.setScene(scene);
        stage.show();
    }

    private void getGraphSolve(int height, int width, double[][] m) {
        if (m.length != 2 && m[0].length != 3)
            throw new RuntimeException("Given slae have more or less than two variables and two equations");
        drawCoordinates(height, width);
        int scaler = 30;

        transposedFunctionsResultsForEquation(m[0]);
        transposedFunctionsResultsForEquation(m[1]);

        Line graph1 = drawFuncGraph(height, width,
                BigDecimal.valueOf(m[0][0]),
                BigDecimal.valueOf(m[0][1]),
                BigDecimal.valueOf(m[0][2])
        );

        Line graph2 = drawFuncGraph(height, width,
                BigDecimal.valueOf(m[1][0]),
                BigDecimal.valueOf(m[1][1]),
                BigDecimal.valueOf(m[1][2])
        );

        Shape s = Line.union(graph1,graph2);
        
        System.out.println("Stage shape center coordinates:x="+s.getBoundsInParent().getCenterX() + ", y=" + s.getBoundsInParent().getCenterY());
        showStageCrossCoordinates(graph1,graph2);
        designateCrossDot(graph1, graph2);
        group.getChildren().addAll(graph1, graph2);
        subscribeGraph(graph1, "1st");
        subscribeGraph(graph2, "2nd");

    }

    private void drawCoordinates(int height, int width) {
        group.getChildren().clear();
        Line xLine = new Line(0, height / 2, width, height / 2);
        Line yLine = new Line(width / 2, 0, width / 2, height);
        xLine.setStrokeWidth(2);
        yLine.setStrokeWidth(2);
        int xCenter = width / 2;
        int yCenter = height / 2;
        group.getChildren().addAll(xLine, yLine);
        for (int i = 0; i < width / 2 | i < height / 2; i += 10) {
            if (i % 20 == 0) {
                if (i < width / 2) {
                    group.getChildren().add(new Line(xCenter + i, yCenter+4, xCenter + i, yCenter-4));
                    group.getChildren().add(new Line(xCenter - i, yCenter+4, xCenter - i, yCenter-4));
                }
                if (i < height / 2) {
                    group.getChildren().add(new Line(xCenter + 4, yCenter + i, xCenter - 4, yCenter + i));
                    group.getChildren().add(new Line(xCenter + 4, yCenter - i, xCenter - 4, yCenter - i));
                }
            } else {
                if (i < width / 2) {
                    group.getChildren().add(new Line(xCenter + i, yCenter + 2, xCenter + i, yCenter - 2));
                    group.getChildren().add(new Line(xCenter - i, yCenter + 2, xCenter - i, yCenter - 2));
                }
                if (i < height / 2) {
                    group.getChildren().add(new Line(xCenter + 2, yCenter + i, xCenter - 2, yCenter + i));
                    group.getChildren().add(new Line(xCenter + 2, yCenter - i, xCenter - 2, yCenter - i));
                }
            }
        }
    }

    //all double values became BigDecimal
    private Line drawFuncGraph(int height, int width, BigDecimal xCoef, BigDecimal yCoef, BigDecimal freeCoef){
        if(yCoef.equals(BigDecimal.valueOf(0)) & xCoef.equals(BigDecimal.valueOf(0)))
            throw new RuntimeException("Cant draw graph");
        Coordinates start = new Coordinates();
        Coordinates end = new Coordinates();
        BigDecimal scaler = BigDecimal.valueOf(10);
        BigDecimal halfHeight = BigDecimal.valueOf(height).divide(BigDecimal.valueOf(2));
        BigDecimal halfWidth = BigDecimal.valueOf(width).divide(BigDecimal.valueOf(2));
        MathFunctionBD f = getStraightFunc(xCoef, yCoef, freeCoef,scaler);
        System.out.println("\nDelta y = " + getDelta(f));

        if (f != null && Math.abs(getDelta(f)) >= 1) { //yCoef != 0 & func with xVariable fits in height size
            if ((f = getStraightFunc(yCoef, xCoef, freeCoef)) != null) {        //xCoef != 0
                System.out.println("Delta x = " + getDelta(f));
                start.x = f.count(halfHeight)
                        .add(halfWidth)
                        .doubleValue();
                start.y = 0;
                end.x = f.count(halfHeight.negate())
                        .add(halfWidth)
                        .doubleValue();
                end.y = height;
            } else {            //xCoef == 0
                double yVal = freeCoef.divide(yCoef.negate())
                        .add(halfHeight)
                        .doubleValue();
                start.x = 0;
                start.y = yVal;
                end.x = width;
                end.y = yVal;
                return new Line(
                        0,
                        yVal,
                        width,
                        yVal
                );
            }
        } else if(f != null){
            start.x = 0;
            start.y = f.count(halfWidth.negate()).negate()
                    .add(halfHeight)
                    .doubleValue();
            end.x = width;
            end.y = f.count(halfWidth).negate()
                    .add(halfHeight)
                    .doubleValue();
        } else if(!xCoef.equals(0)){        // yCoef == 0 & xCoef != 0
            start.x = freeCoef.divide(yCoef.negate())
                    .add(halfWidth)
                    .doubleValue();
            start.y = 0;
            end.x = freeCoef.divide(yCoef)
                    .add(halfWidth)
                    .doubleValue();
            end.y = height;
        }
        return new Line(start.x, start.y, end.x, end.y);
    }

    private void subscribeGraph(Line graph, String text) {
        Text t = new Text(graph.getStartX() + 50,graph.getStartY() + 50,text);
        t.setStroke(Color.RED);
        ((Group)graph.getParent()).getChildren().add(t);
    }

    private MathFunction getStraightFunc(double varCoef, double denominator, double freeVal){
        if (denominator == 0)
            return null;
        return var -> (freeVal - (varCoef*var))/denominator;
    }

    private MathFunctionBD getStraightFunc(BigDecimal varCoef, BigDecimal denominator, BigDecimal freeVal){
        if (denominator.equals(BigDecimal.valueOf(0)))
            return null;
        return var -> (freeVal.subtract(varCoef.multiply(var))).divide(denominator);
    }
    private MathFunctionBD getStraightFunc(BigDecimal varCoef, BigDecimal denominator, BigDecimal freeVal, BigDecimal scaler){
        if (denominator.equals(BigDecimal.valueOf(0)))
            return null;
        return var -> (freeVal.subtract(varCoef.multiply(var.divide(scaler, RoundingMode.HALF_UP)))).divide(denominator).multiply(scaler);
    }

    private void transposedFunctionsResultsForEquationDouble(double[] eqt) {
        MathFunction f1 = getStraightFunc(eqt[0],eqt[1], eqt[2]);
        MathFunction f1T = getStraightFunc(eqt[1],eqt[0], eqt[2]);
        showTransposedFunctionsResults(eqt, f1, f1T);
    }

    private void transposedFunctionsResultsForEquation(double[] eqt) {
        BigDecimal xCoef1b = BigDecimal.valueOf(eqt[0]);
        BigDecimal yCoef1b = BigDecimal.valueOf(eqt[1]);
        BigDecimal freeCoef1b = BigDecimal.valueOf(eqt[2]);
        MathFunctionBD f1 = getStraightFunc(xCoef1b,yCoef1b,freeCoef1b);
        MathFunctionBD f1T = getStraightFunc(yCoef1b,xCoef1b,freeCoef1b);
        showTransposedFunctionsResults(eqt, f1, f1T);
    }

    private void showTransposedFunctionsResults(double[] m, MathFunction f1, MathFunction f1T) {
        System.out.println(Arrays.toString(m));
        double val = 0;
        double f1Res = f1.count(val);
        double f1TRes = f1T.count(f1Res);
        System.out.println("y("+val+")="+f1Res+", x("+f1Res+")="+f1TRes);
    }

    private void showTransposedFunctionsResults(double[] m, MathFunctionBD f1, MathFunctionBD f1T) {
        System.out.println(Arrays.toString(m));
        BigDecimal val = BigDecimal.valueOf(0);
        BigDecimal f1Res = f1.count(val);
        BigDecimal f1TRes = f1T.count(f1Res);
        System.out.println("y("+val+")="+f1Res+", x("+f1Res+")="+f1TRes);
    }

    private void showSceneCrossCoordinates(Line l1, Line l2) {
        Shape cross = Line.intersect(l1, l2);
        if (cross != null) {
            System.out.println("Scene cross x=" + (cross.getBoundsInParent().getCenterX() - width / 2/*- - 30*/));
            System.out.println("Scene cross y=" + (-cross.getBoundsInParent().getCenterY() + height / 2/*- 30*/));
        }
    }

    private void showStageCrossCoordinates(Line l1, Line l2) {
        Shape cross = Line.intersect(l1, l2);
        if (cross != null) {
            System.out.println("Stage cross x=" + (cross.getBoundsInParent().getCenterX()));
            System.out.println("Stage cross y=" + (cross.getBoundsInParent().getCenterY()));
        }
    }

    private void designateCrossDot(Line l1, Line l2) {
        Coordinates cross = getStageCrossCoordinates(l1,l2);
        drawLine(Color.RED, 2,cross.x, cross.y, width / 2, cross.y);
        drawLine(Color.RED, 2,cross.x, cross.y, cross.x, height/2);
    }

    private Coordinates getStageCrossCoordinates(Line l1, Line l2) {
        Shape intersection = Line.intersect(l1, l2);
        Bounds b = intersection.getBoundsInParent();
        return b.isEmpty() ? null : new Coordinates(b.getCenterX(), b.getCenterY());
    }

    private Coordinates getSceneCrossCoordinates(Line l1, Line l2) {
        Shape intersection = Line.intersect(l1, l2);
        Bounds b = intersection.getBoundsInParent();
        return b.isEmpty() ? null : new Coordinates(b.getCenterX() - width/2, - b.getCenterY()  + height / 2);
    }

    private Line drawLine(Color color,int strokeWidth, double xStart, double yStart, double xEnd, double yEnd) {
        Line l = new Line(xStart, yStart, xEnd, yEnd);
        l.setStroke(color);
        l.setStrokeWidth(strokeWidth);
        group.getChildren().add(l);
        return l;
    }

    private Line drawLine(double xStart, double yStart, double xEnd, double yEnd) {
        Line l = new Line(xStart, yStart, xEnd, yEnd);
        group.getChildren().add(l);
        return l;
    }


    //accurate
    private double getDelta(MathFunction f) {
        return BigDecimal.valueOf(f.count(2)).subtract(BigDecimal.valueOf(f.count(1))).doubleValue();
    }

    private double getDelta(MathFunctionBD f) {
        return f.count(BigDecimal.valueOf(2))
                .subtract(f.count(BigDecimal.valueOf(1)))
                        .doubleValue();
    }

    interface MathFunctionBD {
        BigDecimal count(BigDecimal num);
    }
    interface MathFunction{
        double count(double num);
    }

    private class Coordinates {
        private double x;
        private double y;

        Coordinates() {

        }

        Coordinates(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

}




