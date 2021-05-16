import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicReference;

public class ChartSolve extends Application {
    private Stage stage;
    private Scene scene;
    private StackPane root;
    private Pane worldPane;
    private AnchorPane infoPane;
    private String title = "PanAndZoom";
    private double height =720;
    private double width =  720;

    private double[][] slae;
    private Coordinates screenResult, worldResult;

    private Line l1;
    private Line l2;

    private double worldMinX = -10;
    private double worldMaxX = 10;
    private double worldMinY = -10;
    private double worldMaxY = 10;

    private double offsetX = 0;
    private double offsetY = 0;

    private double scaleX = 1.0;
    private double scaleY = 1.0;


    public static void main(String[] args) {
        launch(args);
    }

    public ChartSolve(double[][] matrix) {
        this.slae = matrix;
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        prepareWindow();
        scaleX = 40;
        scaleY = 40;

        offsetX = -width/2/scaleX;
        offsetY = -height/2/scaleY;
        setOnActions();
        drawLines();
        /*this.slae = new double[][]{{-10,1,8},
                {1,-2,9}};*/
        solve(slae);
    }

    private void solve(double[][] m) {
        BigDecimal[][] matrix = toBigDecMatrix(m);
        Line l1 = drawFuncGraph(matrix[0][0], matrix[0][1], matrix[0][2]);
        Line l2 = drawFuncGraph(matrix[1][0], matrix[1][1], matrix[1][2]);
        System.out.println("Intersects:"+l1.intersects(l2.getStartX(), l2.getStartY(), l2.getEndX(), l2.getEndY()));
        Shape intersection = Line.intersect(l1, l2);
//        if (l1.intersects(l2.getStartX(), l2.getStartY(), l2.getEndX(), l2.getEndY())) {
        if(!intersection.getBoundsInParent().isEmpty()){
            screenResult = new Coordinates(intersection.getBoundsInLocal().getCenterX(), intersection.getBoundsInLocal().getCenterY());
            worldResult = screenToWorld(screenResult);
            designateWorldDot(worldResult);
            System.out.println(worldResult.toString());
        }

        l1.setStroke(Color.BLUE);
        l1.setStrokeWidth(2);
        l2.setStroke(Color.GREEN);
        l2.setStrokeWidth(2);

        updateWith(l1,l2);
    }
    private void designateWorldDot(Coordinates worldDot) {
        Coordinates horStart = worldToScreen(new Coordinates(worldDot.x,0));
        Coordinates horEnd = worldToScreen(new Coordinates(worldDot.x, worldDot.y));
        Coordinates vertStart = worldToScreen(new Coordinates(0, worldDot.y));
        Coordinates vertEnd = worldToScreen(new Coordinates(worldDot.x, worldDot.y));
        Line horizontal = new Line(horStart.x, horStart.y, horEnd.x, horEnd.y);
        Line vertical = new Line(vertStart.x, vertStart.y, vertEnd.x, vertEnd.y);

        Coordinates xStringLoc = worldToScreen(new Coordinates(worldDot.x, 0));
        Coordinates yStringLoc = worldToScreen(new Coordinates(0, worldDot.y));
        Text xString = new Text(Double.toString(round(worldDot.x,2)));
        xString.setStroke(Color.RED);
        xString.setX(xStringLoc.x - xString.getLayoutBounds().getWidth()/2);
        if(worldDot.y > 0)
            xString.setY(xStringLoc.y + xString.getLayoutBounds().getHeight());
        else
            xString.setY(xStringLoc.y - 5);

        Text yString = new Text(Double.toString(round(worldDot.y,2)));
        yString.setStroke(Color.RED);
        if (worldDot.x > 0)
            yString.setX(yStringLoc.x - yString.getLayoutBounds().getWidth() - 5);
        else
            yString.setX(yStringLoc.x + 3);
        yString.setY(yStringLoc.y + yString.getLayoutBounds().getHeight()/3);

        horizontal.setStroke(Color.RED);
        vertical.setStroke(Color.RED);
        horizontal.setStrokeWidth(2);
        vertical.setStrokeWidth(2);
        worldPane.getChildren().addAll(horizontal, vertical, xString, yString);
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private BigDecimal[][] toBigDecMatrix(double[][] m) {
        BigDecimal[][] result = new BigDecimal[m.length][m[0].length];
        for (int i = 0; i < m.length; i++)
            for (int j = 0; j < m[0].length; j++)
                result[i][j] = BigDecimal.valueOf(m[i][j]);
        return result;
    }

    private void prepareWindow() {
        root = new StackPane();
        scene = new Scene(root, width, height);
        worldPane = new Pane();
//        infoPane = new AnchorPane();
//        infoPane.setMaxHeight(100);
//        infoPane.setMaxWidth(100);
//        infoPane.set
//        infoPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        root.getChildren().add(worldPane);
//        root.getChildren().add(infoPane);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    private void setOnActions() {
        AtomicReference<Coordinates> startPan = new AtomicReference<>();
        scene.setOnMousePressed(me -> {
            Coordinates screen = new Coordinates(me.getX(), me.getY());
            startPan.set(screen);
            System.out.println("Screen click:" + screen.x + " | " + screen.y);
            Coordinates world = screenToWorld(screen);
            System.out.println("World click:" + world.x + " | " + world.y);
            System.out.println("Offset:" + offsetX + " | " + offsetY);
            System.out.println("Scale:" + scaleX  + " | " + scaleY + "\n");
        });

        scene.setOnMouseDragged(me ->{
            Coordinates prev = startPan.get();
            System.out.println("Screen dragged:" + me.getX() + " | " + me.getY());
            offsetX -= (me.getX() - prev.x) / scaleX;
            offsetY -= (me.getY() - prev.y) / scaleY;
            System.out.println("Offset:" + offsetX + " | " + offsetY + "\n");
            prev.x = me.getX();
            prev.y = me.getY();
            update();

        });


        scene.setOnKeyPressed(ke ->{
            System.out.println("Key pressed:" + ke.getCode().getChar());

            Point2D cursorLoc = new Robot().getMousePosition().subtract(scene.getX() + stage.getX(), scene.getY() + stage.getY());

            Coordinates mouseLoc = new Coordinates(cursorLoc.getX(), cursorLoc.getY());
            Coordinates mouseWorldBeforeZoom = screenToWorld(mouseLoc);
            if (ke.getCode().getChar().equals("R")) {
                offsetX = -width/2/scaleX;
                offsetY = -height/2/scaleY;
                System.out.println("Offset:" + offsetX + " | " + offsetY);
                System.out.println("Screen click:" + cursorLoc.getX() + "|"+cursorLoc.getY());
                System.out.println("World click:" + mouseWorldBeforeZoom.x + " | " + mouseWorldBeforeZoom.y + "\n");
                update();
                return;
            }

            System.out.println("Screen click:" + cursorLoc.getX() + "|"+cursorLoc.getY());
            System.out.println("World click bz:" + mouseWorldBeforeZoom.x + " | " + mouseWorldBeforeZoom.y);


            switch (ke.getCode().getChar()) {
                case "Q":
                    scaleX *= 1.05;
                    scaleY *= 1.05;
                    break;
                case "A":
                    scaleX *= 0.95;
                    scaleY *= 0.95;
                    break;
                case "W":
                    scaleX *= 1.001;
                    scaleY *= 1.001;
                    break;
                case "S":
                    scaleX *= 0.999;
                    scaleY *= 0.999;
                    break;
            }

            Coordinates mouseWorldAfterZoom = screenToWorld(mouseLoc);
            System.out.println("World click az:" + mouseWorldAfterZoom.x + " | " + mouseWorldAfterZoom.y);

            offsetX += (mouseWorldBeforeZoom.x - mouseWorldAfterZoom.x);
            offsetY -= (mouseWorldBeforeZoom.y - mouseWorldAfterZoom.y);

            System.out.println("Offset:" + offsetX + " | " + offsetY);
            update();


            System.out.println("Scale:" + scaleX  + " | " + scaleY + "\n");

        });
    }

    private void update() {
        worldPane.getChildren().clear();
        drawLines();
        solve(slae);
    }

    private void updateWith(Node... n) {
        worldPane.getChildren().addAll(n);
    }

    private void drawLines() {
        int smallStrWidth = 1;
        int bigStrWidth = 3;
        double dashSize = 0.2;
        Line l;
        Coordinates screenS, screenE;
        for (double x = worldMinX; x <= worldMaxX; x++) {
            if (x != 0) {
                screenS = worldToScreen(x, -dashSize);
                screenE = worldToScreen(x, dashSize);
                l = new Line(screenS.x, screenS.y, screenE.x, screenE.y);
                l.setStrokeWidth(smallStrWidth);
            } else {
                screenS = worldToScreen(x, worldMinY);
                screenE = worldToScreen(x, worldMaxY);
                l = new Line(screenS.x, screenS.y, screenE.x, screenE.y);
                l.setStrokeWidth(bigStrWidth);
            }
            worldPane.getChildren().add(l);
        }
        for (double y = worldMinY; y <= worldMaxY; y++) {
            if (y != 0) {
                screenS = worldToScreen(-dashSize,y);
                screenE = worldToScreen(dashSize, y);
                l = new Line(screenS.x, screenS.y, screenE.x, screenE.y);
                l.setStrokeWidth(smallStrWidth);
            } else {
                screenS = worldToScreen(worldMinX, y);
                screenE = worldToScreen(worldMaxX, y);
                l = new Line(screenS.x, screenS.y, screenE.x, screenE.y);
                l.setStrokeWidth(bigStrWidth);
            }
            worldPane.getChildren().add(l);
        }

    }

    private Coordinates worldToScreen(double wx, double wy) {
        return new Coordinates(
                (wx - offsetX) * scaleX,
                (-wy - offsetY) * scaleY
        );
    }

    private Coordinates worldToScreen(Coordinates world) {
        return new Coordinates(
                (world.x - offsetX) * scaleX,
                (-world.y - offsetY) * scaleY
        );
    }

    private Coordinates screenToWorld(double sx, double sy) {
        return new Coordinates(
                sx/scaleX + offsetX,
                -(sy/scaleY + offsetY)
        );
    }

    private Coordinates screenToWorld(Coordinates screen) {
        return new Coordinates(
                screen.x/scaleX + offsetX,
                -(screen.y/scaleY + offsetY)
        );
    }

    private Line drawFuncGraph(BigDecimal xCoef, BigDecimal yCoef, BigDecimal freeCoef){
        if(yCoef.equals(BigDecimal.valueOf(0)) & xCoef.equals(BigDecimal.valueOf(0)))
            throw new RuntimeException("Cant draw graph");
        Coordinates worldStart = new Coordinates();
        Coordinates worldEnd = new Coordinates();
        Coordinates screenStart, screenEnd;
        BigDecimal wMinY = BigDecimal.valueOf(worldMinY);
        BigDecimal wMaxY = BigDecimal.valueOf(worldMaxY);
        BigDecimal wMinX = BigDecimal.valueOf(worldMinX);
        BigDecimal wMaxX = BigDecimal.valueOf(worldMaxX);
        MathFunctionBD f = getStraightFunc(xCoef, yCoef, freeCoef);

        if (f != null && Math.abs(getDelta(f)) >= 1) { //yCoef != 0 & func with xVariable doesnt fit in height size
            if ((f = getStraightFunc(yCoef, xCoef, freeCoef)) != null) {        //xCoef != 0
                worldStart.x = f.count(wMinY)
                        .doubleValue();
                worldStart.y = worldMinY;
                worldEnd.x = f.count(wMaxY)
                        .doubleValue();
                worldEnd.y = worldMaxY;
            } else {            //xCoef == 0
                double yVal = freeCoef.divide(yCoef)
                        .doubleValue();
                worldStart.x = worldMinX;
                worldStart.y = yVal;
                worldEnd.x = worldMaxX;
                worldEnd.y = yVal;
            }
        } else if(f != null){
            worldStart.x = worldMinX;
            worldStart.y = f.count(wMinX).doubleValue();
            worldEnd.x = worldMaxX;
            worldEnd.y = f.count(wMaxX).doubleValue();
        } else if(!xCoef.equals(0)){
            worldStart.x = freeCoef.divide(yCoef).doubleValue();
            worldStart.y = worldMinY;
            worldEnd.x = freeCoef.divide(yCoef).doubleValue();
            worldEnd.y = worldMaxY;
        }
        screenStart = worldToScreen(worldStart);
        screenEnd = worldToScreen(worldEnd);
        return new Line(screenStart.x, screenStart.y, screenEnd.x, screenEnd.y);
    }

    private MathFunctionBD getStraightFunc(BigDecimal varCoef, BigDecimal denominator, BigDecimal freeVal){
        System.out.println(denominator.equals(BigDecimal.valueOf(0)));
        if (denominator.equals(BigDecimal.valueOf(0)))
            return null;
        System.out.println(varCoef + " " + denominator + " " + freeVal);
        try {
            return var -> (freeVal.subtract(varCoef.multiply(var))).divide(denominator, 2, RoundingMode.HALF_UP);
        } catch (ArithmeticException ae) {
            System.out.println("AE thrown");
            return null;
        }
    }

    private double getDelta(MathFunctionBD f) {
        return f.count(BigDecimal.valueOf(2))
                .subtract(f.count(BigDecimal.valueOf(1)))
                .doubleValue();
    }

    interface MathFunctionBD {
        BigDecimal count(BigDecimal num);
    }

    private void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private class Coordinates{
        private double x;
        private double y;

        private Coordinates() { }

        private Coordinates(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public String toString() {
            return "Coordinates:" + x + " | " + y;
        }
    }



}
