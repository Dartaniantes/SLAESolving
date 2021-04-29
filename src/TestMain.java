import Model.exception.InconsistentMatrixException;
import Model.exception.ZerosLinearEquation;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TestMain extends Application {

    private Stage st;
    private Scene sc;
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
        Group graph = new Group();

        Scene scene = new Scene(graph);
        this.sc = scene;

        stage.setHeight(height);
        stage.setWidth(width);
        drawCoordinates(graph, height, width);

        MathFunction f = x -> x*x;
//        drawFunc(graph, height, width, f);
        getGraphSolve(graph, height, width, this.m);

        stage.setScene(scene);
        stage.show();



        /*double xs = width/2 + 30;
        double ys = height/2 + 50;
        double xe = width/2 - 50;
        double ye = height/20 + 90;
        Line l = new Line(xs, ys, xe, ye);
        Text ts = new Text("Start");
        Text te = new Text("End");
        ts.setX(xs + 10);
        ts.setY(ys);
        te.setX(xe + 10);
        te.setY(ye + 10);
        l.setStroke(Color.RED);
        System.out.println(l.getFill());
        graph.getChildren().addAll(l, ts, te);*/
    }

    private void drawFunc(Group g, int height, int width, MathFunction f) {
        for (int i = -width/2; i < width/2; i++) {
            g.getChildren().add(
                    new Line(
                            i + width/2,
                            -f.count(i) + height/2,
                            i+1 + width/2,
                            -f.count(i+1) + height/2
                    )
            );
        }
    }

    private Group getGraphSolve(Group g, int height, int width, double[][] m) {
        if (m.length != 2 && m[0].length != 3)
            throw new RuntimeException("Given slae have more or less than two variables and two equations");
        drawCoordinates(g, height, width);
        int scaler = 30;
        double x1Coef = m[0][0], x2Coef = m[1][0], y1Coef = m[0][1], y2Coef = m[1][1];
        double freeCoef1 = m[0][2], freeCoef2 = m[1][2];
        MathFunction f1 = x -> (freeCoef1 - x1Coef * (x - scaler)) / y1Coef + scaler;
        MathFunction f2 = x -> (freeCoef2 - x2Coef * (x - scaler)) / y2Coef + scaler;


//        System.out.println("Delta out vertical = " + getDelta(f1));


//        Line graph1 = drawFuncGraph(height, width, m[0][0], m[0][1], m[0][2]);
//        Line graph2 = drawFuncGraph(height, width, m[1][0], m[1][1], m[1][2]);
//
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


//        Line graph1 = drawFuncGraph(height, width, f1);
//        Line graph2 = drawFuncGraph(height, width, f2);
        showCross(graph1,graph2);
        g.getChildren().addAll(graph1, graph2);

        return g;
    }

    private void showCross(Line l1, Line l2) {
        Shape cross = Line.intersect(l1, l2);
        System.out.println("Cross x="+(cross.getBoundsInParent().getCenterX() - width/2 - 30));
        System.out.println("Cross y="+(- cross.getBoundsInParent().getCenterY() + height / 2 - 30));
    }

    private void drawCoordinates(Group g, int height, int width) {
        g.getChildren().clear();
        Line xLine = new Line(0, height / 2, width, height / 2);
        Line yLine = new Line(width / 2, 0, width / 2, height);
        xLine.setStrokeWidth(2);
        yLine.setStrokeWidth(2);
        int xBigDashStart = height / 2 + 4, xBigDashEnd = height / 2 - 4;
        int xSmallDashStart = height / 2 + 2, xSmallDashEnd = height / 2 - 2;
        int yBigDashStart = width / 2 + 4, yBigDashEnd = width / 2 - 4;
        int ySmallDashStart = width / 2 + 2, ySmallDashEnd = width / 2 - 2;
        int xCenter = width / 2;
        int yCenter = height / 2;
        g.getChildren().addAll(xLine, yLine);
        for (int i = 0; i < width / 2 | i < height / 2; i += 10) {
            if (i % 20 == 0) {
                if (i < width / 2) {
                    g.getChildren().add(new Line(xCenter + i, xBigDashStart, xCenter + i, xBigDashEnd));
                    g.getChildren().add(new Line(xCenter - i, xBigDashStart, xCenter - i, xBigDashEnd));
                }
                if (i < height / 2) {
                    g.getChildren().add(new Line(yBigDashStart, yCenter + i, yBigDashEnd, yCenter + i));
                    g.getChildren().add(new Line(yBigDashStart, yCenter - i, yBigDashEnd, yCenter - i));
                }
            } else {
                if (i < width / 2) {
                    g.getChildren().add(new Line(xCenter + i, xSmallDashStart, xCenter + i, xSmallDashEnd));
                    g.getChildren().add(new Line(xCenter - i, xSmallDashStart, xCenter - i, xSmallDashEnd));
                }
                if (i < height / 2) {
                    g.getChildren().add(new Line(ySmallDashStart, yCenter + i, ySmallDashEnd, yCenter + i));
                    g.getChildren().add(new Line(ySmallDashStart, yCenter - i, ySmallDashEnd, yCenter - i));
                }
            }
        }
    }


    //accurate
    private double getDelta(MathFunction f) {
        return BigDecimal.valueOf(f.count(2)).subtract(BigDecimal.valueOf(f.count(1))).doubleValue();
    }


    //all double values became BigDecimal
    private Line drawFuncGraph(int height, int width, BigDecimal xCoef, BigDecimal yCoef, BigDecimal freeCoef){
        BigDecimal bigScaler = BigDecimal.valueOf(30);
        BigDecimal bigHeight = BigDecimal.valueOf(height);
        BigDecimal bigWidth = BigDecimal.valueOf(width);
        MathFunction f = null;
        if (!yCoef.equals(0)) {
            f = x -> freeCoef.subtract(
                    xCoef.multiply(
                            BigDecimal.valueOf(x).subtract(bigScaler)
                    )
            )
                    .divide(yCoef)
                    .add(bigScaler)
                    .doubleValue();
        }

        if (f != null && Math.abs(getDelta(f)) >= 1) {
            if(!xCoef.equals(0)){
                f = y -> freeCoef.subtract(
                        yCoef.multiply(
                                BigDecimal.valueOf(y).add(bigScaler)
                        )
                )
                        .divide(xCoef)
                        .subtract(bigScaler)
                        .doubleValue();

                System.out.println("Delta in vertical = " + getDelta(f));
                return new Line(
                        -f.count(-height/2) + width/2,
                        0,
                        -f.count(height/2) + width/2,
                        height
                );
            } else return new Line(
                    0,
                    freeCoef.multiply(BigDecimal.valueOf(-1)).divide(yCoef).add(bigScaler).add(bigHeight.divide(BigDecimal.valueOf(2))).doubleValue(),
                    width,
                    freeCoef.multiply(BigDecimal.valueOf(-1)).divide(yCoef).add(bigScaler).add(bigHeight.divide(BigDecimal.valueOf(2))).doubleValue()
            );
        }
        else if(f != null){
            return new Line(
                    0,
                    BigDecimal.valueOf(-f.count(-width/2)).add(bigHeight.divide(BigDecimal.valueOf(2))).doubleValue(),
                    width,
                    BigDecimal.valueOf(-f.count(width/2)).add(bigHeight.divide(BigDecimal.valueOf(2))).doubleValue()
            );
        } else if(!xCoef.equals(0)){

            return new Line(
                    freeCoef.divide(yCoef).add(bigWidth.divide(BigDecimal.valueOf(2))).add(bigScaler).doubleValue(),
                    0,
                    freeCoef.divide(yCoef).add(bigWidth.divide(BigDecimal.valueOf(2))).add(bigScaler).doubleValue(),
                    height
            );
        }
        throw new RuntimeException("Cant draw graph");
    }


    //Not all double became BigDecimal
    private Line drawFuncGraph(int height, int width, double xCoef, double yCoef, double freeCoef){
        double scaler = 30.0;
        MathFunction f = null;
        if (yCoef != 0)
            f = x -> (freeCoef - xCoef*(x))/yCoef;

        if (f != null && Math.abs(getDelta(f)) >= 1)
            if(xCoef != 0){
                f = y -> (freeCoef - yCoef * (y)/xCoef);
                System.out.println("Delta in vertical = " + getDelta(f));
                return new Line(
                        -f.count(-height/2) + width/2 + scaler,
                        0 - scaler,
                        -f.count(height/2) + width/2 + scaler,
                        height - scaler
                );
            } else return new Line(
                    0 + scaler,
                    -freeCoef / yCoef + height/2 - scaler,
                    width + scaler,
                    -freeCoef / yCoef + height/2 - scaler
            );
        else if(f != null)
            return new Line(
                    0 + scaler,
                    -f.count(-width/2) + height/2 - scaler,
                    width + scaler,
                    -f.count(width/2) + height/2 - scaler
            );
        else if(xCoef != 0)
            return new Line(
                    freeCoef/xCoef + width/2 + scaler,
                    0 - scaler,
                    freeCoef/xCoef + width/2 + scaler,
                    height - scaler
            );

        throw new RuntimeException("Cant draw graph");
    }

    private Line drawFuncGraph(int height, int width, MathFunction f) {
        System.out.println("Delta Y = " + getDelta(f));
        return new Line(
                0,
                -f.count(-width / 2) + height / 2,
                width,
                -f.count(width / 2) + height / 2
        );
    }

    private Set<Line> drawFuncGraph(Group g, int height, int width, MathFunction f) {
        Set<Line> graph = new HashSet();
        double yVal;

        for (int i = -width /2; i < width/2 - 1 & (yVal = -f.count(i+1) + height/2) < height; i++) {
            graph.add(new Line(
                    i + width /2,
                    -f.count(i) + height /2,
                    i+1 + width /2,
                    yVal
            ));
        }
        g.getChildren().addAll(graph);
        return graph;
    }

    interface MathFunction {
        double count(double num);
    }
}




