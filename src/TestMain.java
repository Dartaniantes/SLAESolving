import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TestMain extends Application {

    Stage st;
    Scene sc;
    public static void main(String[] args) {
        launch(args);
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
        double[][] m = {
                {10,1,8},
                {1,2,9}
        };
        getGraphSolve(g, height, width, m);
        stage.setScene(scene);
        stage.show();
    }

    private Group drawCoordinates(Group g, int height, int width) {
        g.getChildren().clear();
        Line xLine = new Line(0, height/2, width, height/2);
        Line yLine = new Line(width/2,0, width/2, height);
        xLine.setStrokeWidth(2);
        yLine.setStrokeWidth(2);
        int xBigDashStart = height/2 + 4,xBigDashEnd = height/2 - 4;
        int xSmallDashStart = height/2 + 2,xSmallDashEnd = height/2 - 2;
        int yBigDashStart = width/2 + 4, yBigDashEnd = width/2 - 4;
        int ySmallDashStart = width/2 + 2, ySmallDashEnd = width/2 - 2;
        int xCenter = width/2;
        int yCenter = height/2;
        g.getChildren().addAll(xLine,yLine);
        for (int i = 0; i < width/2 | i < height/2; i += 10) {
            if (i % 20 == 0) {
                if (i < width / 2) {
                    g.getChildren().add(new Line(xCenter + i, xBigDashStart, xCenter + i, xBigDashEnd));
                    g.getChildren().add(new Line(xCenter - i, xBigDashStart, xCenter - i, xBigDashEnd));
                }
                if (i < height / 2) {
                    g.getChildren().add(new Line(yBigDashStart,yCenter + i,  yBigDashEnd,yCenter + i));
                    g.getChildren().add(new Line(yBigDashStart,yCenter - i, yBigDashEnd,  yCenter - i));
                }
            } else {
                if (i < width / 2) {
                    g.getChildren().add(new Line(xCenter + i, xSmallDashStart, xCenter + i, xSmallDashEnd));
                    g.getChildren().add(new Line(xCenter - i, xSmallDashStart, xCenter - i, xSmallDashEnd));
                }
                if (i < height / 2) {
                    g.getChildren().add(new Line(ySmallDashStart,yCenter + i,  ySmallDashEnd,yCenter + i));
                    g.getChildren().add(new Line(ySmallDashStart,yCenter - i, ySmallDashEnd,  yCenter - i));
                }
            }
        }
        return g;
    }
    private Group getGraphSolve(Group g, int height, int width, double[][] slae) {
        if(slae.length != 2 && slae[0].length != 3)
            throw new RuntimeException("Given slae have more or less than two variables and two equations");
        drawCoordinates(g, height, width);
        int scaler = 30;
        Set<Line> graph1 = drawGraph(g,height, width, x -> (slae[0][2] - slae[0][0]*(x - scaler))/slae[0][1] + scaler);
        Set<Line> graph2 = drawGraph(g,height, width, x -> (slae[1][2] - slae[1][0]*(x - scaler))/slae[1][1] + scaler);

        showGraphByDots(graph1);
        showGraphByDots(graph2);

 ит ит иитит                                                                                                т ит т ит и  итиитттит           иииииииииииииииииииииииит и ииииииииииииииииииииииииииииииииииииииииииииииииит иииииииит  ит     и ит ииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииииттттттттттттттттттттттттттттттттттттттттттттттттттттт    сччсям ит ит  ииит  ит ит ит ит       т иииииииииии   ит ит т            итттттттттттттттттттттттттттттттттттттттттттттттттттттттттттттттттттттттттттттттттттттттттттттит
        double[][] crossDots = getCrossDots(graph1, graph2);
        /*if (crossDots == null) {
            throw new RuntimeException("SLAE has no solutions");
        }                                                                                                                                   итрор         итит    иииииит итт иииииииииииииииииииииииииииииииииииииииитттттттттттттттттттттттттттттттттттттттттттттттттттттттттттттттттттттттт итттт итттттттттттттттттттттттттт  и ит  и  ит ит ит итит   и  и  и  ииииииииииииииииииииииииииииииииииит    и          т       и   и      и  и    р  ти    ит  ии и ит иииит и  ииииииииииииииииииииииииииииииииииииит  итттттттттттттт ит   ит
        System.out.println("Pixels:");
        System.out.println("X = "+crossDots[0][0]);
        System.out.println("Y = "+crossDots[0][1]);
        System.out.println("Decaрптиит   и           ит  ит        иrt Coordinates:");
        System.out.println("Y = "+(crossDots[0][1] + scaler - width/2));
        System.out.println("Y = "+(crossDots[0][1] - scaler - height/2));*/
        return g;
    }

    private void showGraphByDots(Set<Line> graph) {
        System.out.println("New graph:");
        for (Line l : graph) {
            System.out.print("X=" + l.getStartX() + ",Y=" + l.getStartY() + " ");
        }
        System.out.println();
    }

    private Set<Line> drawGraph(Group g, int height, int width, Operation o) {
        Set<Line> graph = new HashSet();
        int scaler = 1;
        for (int i = -width /2; i < width/2 - 1; i += 10) {
            graph.add(new Line(
                    i + width /2,
                    -o.process(i) + height /2,
                    i+scaler+ width /2,
                    -o.process(i+scaler) + height /2
            ));
        }
        g.getChildren().addAll(graph);
        return graph;
    }

    private double[][] getCrossDots(Set<Line> graph1, Set<Line> graph2) {
        graph1.retainAll(graph2);
        if (graph1.isEmpty())
            return null;
        double[][] crossDots = new double[graph1.size()][2];
        Iterator<Line> iter = graph1.iterator();
        Line l;
        for (int i = 0; i < graph1.size(); i++) {
            l = iter.next();
            crossDots[i][0] = l.getStartX();
            crossDots[i][1] = l.getStartY();
        }
        return crossDots;
    }

    interface Operation{
        double process(double num);
    }
}



