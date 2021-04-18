import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class TestMain extends Application {

    public static void main(String[] args) {
        int x = 3;
        System.out.println(x);
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Chart");
        Group g = new Group();
        Scene scene;
        int height = 600;
        int width = 600;

        stage.setHeight(height);
        stage.setWidth(height);

        g.getChildren().addAll(
                new Line(0, height/2, width, height/2),
                new Line(width/2,0, width/2, height)
        );



        int x;
        int y;
        Line l;
        for (int i = 0; i < width; i += 2) {
            x = i;
            y = (int)Math.pow(x,2);
            l = new Line(x,y,x,y);
            l.setStrokeWidth(2);
            g.getChildren().add(l);
        }


        /*NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("X");
        yAxis.setLabel("Y");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        XYChart.Series<Number, Number> e1 = new XYChart.Series<>();
        XYChart.Series<Number, Number> e2 = new XYChart.Series<>();
        e1.getData().add(new XYChart.Data<>(4, 0));
        e1.getData().add(new XYChart.Data<>(0, 5));
        e1.setName("element");
        chart.getData().addAll(e1,e2);
        g.getChildren().add(chart);*/

        scene = new Scene(g);
        stage.setScene(scene);
        stage.show();

    }

    /*@Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Coordinate system");
        BorderPane layout = new BorderPane();
        Scene scene;

        Line xCoordinate = new Line();
        xCoordinate.setStartX(300);
        xCoordinate.setStartY(0);
        xCoordinate.setEndX(300);
        xCoordinate.setEndY(600);

        Line yCoordinate = new Line();
        yCoordinate.setStartY(300);
        yCoordinate.setStartX(0);
        yCoordinate.setEndY(300);
        yCoordinate.setEndX(600);

        xCoordinate.setStrokeWidth(2);
        yCoordinate.setStrokeWidth(2);

        layout.getChildren().addAll(xCoordinate, yCoordinate);

        scene = new Scene(layout, 600, 600);
       *//*
        stage.setScene(scene);
        stage.show();
    }*/



    static void setScale(Line line) {

    }
}



