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
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
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
    private Shape intersection;

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

    public ChartSolve() {

    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        prepareWindow();
        scaleX = 10;
        scaleY = 10;

        offsetX = -width/2/scaleX;
        offsetY = -height/2/scaleY;
        setOnActions();
        /*this.slae = new double[][]{{-10,1,8},
                {1,-2,9}};*/
        update();
    }


    private int  expandCounter = 0;
    private void solve(double[][] m) {
        BigDecimal[][] matrix = toBigDecMatrix(m);
        l1 = drawFuncGraph(matrix[0][0], matrix[0][1], matrix[0][2]);
        l2 = drawFuncGraph(matrix[1][0], matrix[1][1], matrix[1][2]);
        if (hasSingleSolution(matrix)) {
            intersection = Line.intersect(l1, l2);
            double maxCoordinate = getBiggestWorldCoordinate(l1, l2);

            while (intersection.getBoundsInParent().isEmpty()) {
                expandWorld(maxCoordinate);
                equaliseWorldNStage();
                l1 = drawFuncGraph(matrix[0][0], matrix[0][1], matrix[0][2]);
                l2 = drawFuncGraph(matrix[1][0], matrix[1][1], matrix[1][2]);
                intersection = Line.intersect(l1, l2);
            }
            screenResult = new Coordinates(intersection.getBoundsInParent().getCenterX(), intersection.getBoundsInParent().getCenterY());
            worldResult = screenToWorld(screenResult);
            updateWorld();
            equaliseWorldNStage();
            designateWorldDot(worldResult);
        } else {
            double maxCoordinate = getBiggestWorldCoordinate(l1, l2);
            while (!isVisibleInCurrSystem(l1) || !isVisibleInCurrSystem(l2)) {
                expandWorld(maxCoordinate);
                equaliseWorldNStage();
                l1 = drawFuncGraph(matrix[0][0], matrix[0][1], matrix[0][2]);
                l2 = drawFuncGraph(matrix[1][0], matrix[1][1], matrix[1][2]);
            }
            updateWorld();
        }

        l1.setStroke(Color.BLUE);
        l1.setStrokeWidth(2);
        l2.setStroke(Color.GREEN);
        l2.setStrokeWidth(2);


        updateWith(l1,l2);
    }

    private void equaliseWorldNStage() {
        scaleX = 0.49 * width / worldMaxX;
        scaleY = 0.49 * height / worldMaxY;
        centrate();
    }

    private void centrate() {
        offsetX = -width/2/scaleX;
        offsetY = -height/2/scaleY;
    }

    private boolean isVisibleInCurrSystem(Line l) {
        double visBorderX = worldMaxX * 0.9;
        double visBorderY = worldMaxY * 0.9;

        Coordinates screenRectCoord = worldToScreen(-visBorderX, visBorderY);
        double rectScreenWidth = visBorderX * 2 * scaleX;
        double rectScreenHeight = visBorderY * 2 * scaleY;

        Rectangle visibleBorder = new Rectangle(rectScreenWidth,rectScreenHeight);
        visibleBorder.setX(screenRectCoord.x);
        visibleBorder.setY(screenRectCoord.y);
        visibleBorder.setWidth(rectScreenWidth);
        visibleBorder.setHeight(rectScreenHeight);
        return !Line.intersect(visibleBorder, l).getBoundsInParent().isEmpty();
    }

    private void expandWorld(double addSize){
        worldMaxX += addSize;
        worldMinX -= addSize;
        worldMaxY += addSize;
        worldMinY -= addSize;
    }

    private boolean hasSingleSolution(BigDecimal[][] m) {
        if(m[0][0].doubleValue()==0 & m[0][1].doubleValue() == 0 || m[1][0].doubleValue()==0 & m[1][1] .doubleValue() == 0 )
            return false;
        if(m[0][0].doubleValue() != 0 & m[0][1].doubleValue() != 0) {
            return m[1][0].divide(m[0][0], 2, RoundingMode.HALF_EVEN).doubleValue()
                    != m[1][1].divide(m[0][1],2,RoundingMode.HALF_EVEN).doubleValue();
        }
        if(m[1][0].doubleValue() != 0 & m[1][1].doubleValue() != 0) {
            return m[0][0].divide(m[1][0], 2, RoundingMode.HALF_EVEN).doubleValue()
                    != m[0][1].divide(m[1][1],2,RoundingMode.HALF_EVEN).doubleValue();
        }
        return true;
    }

    private boolean fitsIntoCurrSystem(Line l) {
        return l.getStartX() < worldMaxX &&
                l.getStartX() > worldMinX &&
                l.getEndX() < worldMaxX &&
                l.getEndX() > worldMinX &&
                l.getStartY() < worldMaxY &&
                l.getStartY() > worldMinY &&
                l.getEndY() < worldMaxY &&
                l.getEndY() > worldMinY;
    }


    private double getBiggestWorldCoordinate(Line l) {
        Coordinates s = screenToWorld(l.getStartX(), l.getStartY());
        Coordinates e = screenToWorld(l.getEndX(), l.getEndY());
        double max = s.x;
        double num;
        if((num = s.y) > max)
            max = num;
        if((num = e.x) > max)
            max = num;
        if((num = e.y) > max)
            max = num;
        return max;
    }

    private double getBiggestWorldCoordinate(Line l1, Line l2) {
        double l1c = getBiggestWorldCoordinate(l1);
        double l2c = getBiggestWorldCoordinate(l2);
        return Math.max(l1c, l2c);
    }

    private void designateWorldDot(Coordinates worldDot) {
        System.out.println("CrossDotDesignating");
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

    private double round(double value, int places) {
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
                centrate();
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
            System.out.println("Scale:" + scaleX  + " | " + scaleY + "\n");
            update();

        });
    }

    private void update() {
        updateWorld();
        solve(slae);
    }


    private void updateWorld() {
        worldPane.getChildren().clear();
        drawLines();
    }

    private void updateWith(Node... n) {
        worldPane.getChildren().addAll(n);
    }

    private int drawLinesCounter = 0;
    private void drawLines() {
        int smallStrWidth = 1;
        int bigStrWidth = 3;
        double dashSize = worldMaxX/30;
        int gap = (int)worldMaxX/10;
        Line l = null;
        Text xAxisName = new Text();
        Text yAxisName = new Text();
        xAxisName.setFont(Font.font(18));
        yAxisName.setFont(Font.font(18));
        Coordinates screenS, screenE, axisNameScreenLoc;
        for (int x =(int) worldMinX; x <= worldMaxX; x++) {
            if (x != 0 & x%gap == 0) {
                screenS = worldToScreen(x, -dashSize);
                screenE = worldToScreen(x, dashSize);
                l = new Line(screenS.x, screenS.y, screenE.x, screenE.y);
                l.setStrokeWidth(smallStrWidth);
                worldPane.getChildren().add(l);
            } else if (x == 0){
                screenS = worldToScreen(x, worldMinY);
                screenE = worldToScreen(x, worldMaxY);
                l = new Line(screenS.x, screenS.y, screenE.x, screenE.y);
                l.setStrokeWidth(bigStrWidth);
                xAxisName.setText("X");
                axisNameScreenLoc = worldToScreen(worldMaxX,0);
                xAxisName.setX(axisNameScreenLoc.x - xAxisName.getLayoutBounds().getWidth() - 3);
                xAxisName.setY(axisNameScreenLoc.y + xAxisName.getLayoutBounds().getHeight());
                worldPane.getChildren().add(xAxisName);
                worldPane.getChildren().add(l);
            }
        }
        for (int y = (int)worldMinY; y <= worldMaxY; y++) {
            if (y != 0 & y%gap == 0) {
                screenS = worldToScreen(-dashSize,y);
                screenE = worldToScreen(dashSize, y);
                l = new Line(screenS.x, screenS.y, screenE.x, screenE.y);
                l.setStrokeWidth(smallStrWidth);
                worldPane.getChildren().add(l);
            } else if (y == 0){
                screenS = worldToScreen(worldMinX, y);
                screenE = worldToScreen(worldMaxX, y);
                l = new Line(screenS.x, screenS.y, screenE.x, screenE.y);
                l.setStrokeWidth(bigStrWidth);
                yAxisName.setText("Y");
                axisNameScreenLoc = worldToScreen(0, worldMaxY);
                yAxisName.setX(axisNameScreenLoc.x+4);
                yAxisName.setY(axisNameScreenLoc.y + yAxisName.getLayoutBounds().getHeight()-5);
                worldPane.getChildren().add(l);
                worldPane.getChildren().add(yAxisName);

            }

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
        if(yCoef.doubleValue() == 0 & xCoef.doubleValue() == 0)
            throw new RuntimeException("Cant draw graph");
        Coordinates worldStart = new Coordinates();
        Coordinates worldEnd = new Coordinates();
        Coordinates screenStart, screenEnd;
        BigDecimal wMinYBD = BigDecimal.valueOf(worldMinY);
        BigDecimal wMaxYBD = BigDecimal.valueOf(worldMaxY);
        BigDecimal wMinXBD = BigDecimal.valueOf(worldMinX);
        BigDecimal wMaxXBD = BigDecimal.valueOf(worldMaxX);
        MathFunctionBD f = getStraightFunc(xCoef, yCoef, freeCoef);

        if (f != null && Math.abs(getDelta(f)) >= 1) { //yCoef != 0 & func with xVariable doesnt fit in height size
            if ((f = getStraightFunc(yCoef, xCoef, freeCoef)) != null) {        //xCoef != 0
                worldStart.x = f.count(wMinYBD).doubleValue();
                worldStart.y = worldMinY;
                worldEnd.x = f.count(wMaxYBD).doubleValue();
                worldEnd.y = worldMaxY;
            } else {            //xCoef == 0
                double yVal = freeCoef.divide(yCoef).doubleValue();
                worldStart.x = worldMinX;
                worldStart.y = yVal;
                worldEnd.x = worldMaxX;
                worldEnd.y = yVal;
            }
        } else if(f != null){
            worldStart.x = worldMinX;
            worldStart.y = f.count(wMinXBD).doubleValue();
            worldEnd.x = worldMaxX;
            worldEnd.y = f.count(wMaxXBD).doubleValue();
        } else if(xCoef.doubleValue() != 0){
            /*ERROR HERE LOOOK!!!!!!
                WTF??? YOU USE HERE / yCoef with knowing its eq 0!!!!!*/
            double x = freeCoef.divide(xCoef, 2, RoundingMode.HALF_UP).doubleValue();
            worldStart.x = x;
            worldStart.y = worldMinY;
            worldEnd.x = x;
            worldEnd.y = worldMaxY;
        }
        screenStart = worldToScreen(worldStart);
        screenEnd = worldToScreen(worldEnd);
        return new Line(screenStart.x, screenStart.y, screenEnd.x, screenEnd.y);
    }

    private MathFunctionBD getStraightFunc(BigDecimal varCoef, BigDecimal denominator, BigDecimal freeVal){
        if (denominator.doubleValue() == 0)
            return null;
        return var -> (freeVal.subtract(varCoef.multiply(var))).divide(denominator, 2, RoundingMode.HALF_UP);
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
        private double getBigger() {
            return x >= y ? x : y;
        }
    }

    



}
