import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TestMain extends Application {

    private Stage st;
    private Scene sc;
    private double[][] m;

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
        Group g = new Group();
        Scene scene = new Scene(g);
        this.sc = scene;
        int height = 720;
        int width = 1080;

        stage.setHeight(height);
        stage.setWidth(width);
        getGraphSolve(g, height, width, this.m);
        stage.setScene(scene);
        stage.show();
    }

    private Group drawCoordinates(Group g, int height, int width) {
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
        return g;
    }

    private Group getGraphSolve(Group g, int height, int width, double[][] m) {
        if (m.length != 2 && m[0].length != 3)
            throw new RuntimeException("Given slae have more or less than two variables and two equations");
        drawCoordinates(g, height, width);
        int scaler = 30;
        MathFunction f1 = x -> (m[0][2] - m[0][0] * (x - scaler)) / m[0][1] + scaler;
        MathFunction f2 = x -> (m[1][2] - m[1][0] * (x - scaler)) / m[1][1] + scaler;
        Line graph1 = drawGraph(height, width, f1);
        Line graph2 = drawGraph(height, width, f2);
        /*if(Math.abs(getDeltaY(f1)) <= 1)

        else{
            f1 = y -> m[0][2] -
        }*/

        g.getChildren().addAll(graph1, graph2);



        Shape l = Line.
                intersect(graph1, graph2);
        System.out.println(l.getBoundsInParent().getCenterX() - width / 2 - scaler);
        System.out.println(- l.getBoundsInParent().getCenterY() + height / 2 - scaler);
        return g;
    }

    private double getDeltaY(MathFunction f) {
        return f.count(2) - f.count(1);
    }

    private Line drawGraph(int height, int width, MathFunction f) {
        return new Line(
                0,

                -f.count(-width / 2) + height / 2,
                width,
                -f.count(width / 2) + height / 2
        );
    }

    private Set<Line> drawGraph(Group g, int height, int width, MathFunction f) {
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




