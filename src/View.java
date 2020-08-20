import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class View extends Application  {

    Stage st;
    Scene scene;
    BorderPane layout;
    HBox topMenu, manChosePane, genLengthPane, genWidthPane, readInputPane, botMenu, genPane;
    VBox readPane, manPane;
    AnchorPane buttonPane;
    GridPane manFieldsPane;
    StackPane generalpanePane;
    Button randomizeB, solveB, genGenB, manApplyB;
    TextField readFileF, genLengthF, genWidthF, manCoefsF[][], manFreeValsF[],outputFileF;
    Label inputL, methodL, manLengthL, manWidthL, readWarnL, readInL, genLengthL, genWidthL;
    ChoiceBox<String> inputTypeList, methodList;
    ChoiceBox<Integer> manLengthCB, manWidthCB;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        st = stage;
        st.setTitle("SLAE solver");
        st.setWidth(580);
        st.setHeight(500);

        //general menu
        inputL = new Label("Choose input type : ");
        methodL = new Label(" Choose solving method : ");
        inputTypeList = new ChoiceBox<>();
        inputTypeList.getItems().addAll("Generate randomly", "Read from file", "Enter manually");
        inputTypeList.getSelectionModel().selectedItemProperty().addListener((v, oldVal, newVal) ->{
            if (newVal == "Read from file") {
                layout.setCenter(readPane);
                scene.setRoot(layout);
            } else if (newVal == "Generate randomly") {
                layout.setCenter(genPane);
                scene.setRoot(layout);
            } else if (newVal == "Enter manually") {
                layout.setCenter(manPane);
                scene.setRoot(layout);
            }
        });
        methodList = new ChoiceBox<>();
        methodList.getItems().addAll("Gauss", "Jordan-Gauss", "Rotating");
        methodList.setValue("Gauss");
        outputFileF = new TextField();
        outputFileF.setMaxSize(700, 10d);
        outputFileF.setPromptText("Enter output file path");
        topMenu = new HBox(10);
        topMenu.getChildren().addAll(inputL,inputTypeList,methodL, methodList);
        topMenu.setPadding(new Insets(10,10,10,10));
        solveB = new Button("Solve");
        botMenu = new HBox(10);
        botMenu.getChildren().addAll(outputFileF, solveB);
        buttonPane = new AnchorPane();
        buttonPane.getChildren().addAll(botMenu);
        AnchorPane.setRightAnchor(botMenu, 10d);
        AnchorPane.setBottomAnchor(botMenu, 10d);
        layout = new BorderPane();
        layout.setTop(topMenu);
        layout.setBottom(buttonPane);
        scene = new Scene(layout);


        // "generate" pane
        genLengthL = new Label("Set length : ");
        genWidthL = new Label("  Set width : ");
        genLengthF = new TextField("2");
        genLengthF.setMaxSize(50,10);
        genWidthF = new TextField("2");
        genWidthF.setMaxSize(50,10);
        genGenB = new Button("Generate");
        genGenB.setOnAction(ae -> {});
        genPane = new HBox();
        genPane.setPadding(new Insets(10));
        genPane.getChildren().addAll(genLengthL, genLengthF, genWidthL, genWidthF, genGenB);

        // "readPane"
        readWarnL = new Label("WARNING : coefficients in file have to be separated by spaces");
        readWarnL.setPadding(new Insets(10));
        readFileF = new TextField();
        readFileF.setPromptText("Enter input expanded matrix file path : ");
        readFileF.setMaxSize(1000,10d);
        readPane = new VBox();
        readPane.setPadding(new Insets(3,3,3,3));
        readPane.getChildren().addAll(readWarnL, readFileF);


        //"manual" choose pane
        manLengthL = new Label("Choose length : ");
        manWidthL = new Label("  Choose width : ");
        manLengthCB = new ChoiceBox<>();
        manLengthCB.getItems().addAll(2,3,4,5,6,7,8);
        manLengthCB.setValue(2);
        manWidthCB = new ChoiceBox<>();
        manWidthCB.getItems().addAll(2,3,4,5,6,7,8);
        manWidthCB.setValue(2);
        manApplyB = new Button("Apply");
        manApplyB.setOnAction(ae -> {});
        manChosePane = new HBox();
        manChosePane.setPadding(new Insets(10,10,10,10));
        manChosePane.getChildren().addAll(manLengthL, manLengthCB, manWidthL, manWidthCB);
        //"manual" elements pane
        manFieldsPane = new GridPane();
        manFieldsPane.setPadding(new Insets(10));
        manFieldsPane.setHgap(5);
        manFieldsPane.setVgap(10);
        setManGridPane(manLengthCB.getValue(), manWidthCB.getValue());
        manPane = new VBox();
        manPane.setPadding(new Insets(10));
        manPane.getChildren().addAll(manChosePane,manFieldsPane);


        st.setScene(scene);
        st.show();
    }

    public void setManGridPane(int length, int width) {
        manCoefsF = new TextField[length][width];
        manFreeValsF = new TextField[length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                manCoefsF[i][j] = new TextField("0");
                manCoefsF[i][j].setMaxSize(30,30);
                manCoefsF[i][j].setPadding(new Insets(7,15,7,15));
                manFieldsPane.getChildren().add(manCoefsF[i][j]);
                GridPane.setConstraints(manCoefsF[i][j], j,i);
                if (j + 1 == width) {
                    manFreeValsF[i] = new TextField("0");
                    manFreeValsF[i].setMaxSize(30,30);
                    manFreeValsF[i].setPadding(new Insets(7,15,7,15));
                    manFieldsPane.getChildren().add(manFreeValsF[i]);
                    GridPane.setConstraints(manFreeValsF[i], j+1 ,i);
                    GridPane.setMargin(manFreeValsF[i], new Insets(0,7,0,7));
                }

            }
        }
    }


}
/*
readWarnL = new Label("WARNING : coefficients in file have to be separated by spaces");
        readInL = new Label("Enter input expanded matrix file path : ");
        readFileF = new TextField();
        readPane = new VBox();
        readPane.setPadding(new Insets(3,3,3,3));
        readPane.getChildren().addAll(readWarnL, readInL, readFileF);
        readBP.setTop(topMenu);
        readBP.setCenter(readPane);
        readBP.getChildren().add(buttonPane);
        readSc = new Scene(readBP, 500,400);*/

/*
* genLengthL = new Label("Set length : ");
        genWidthL = new Label("Set width : ");
        genLengthF = new TextField("2");
        genWidthF = new TextField("2");
        genPane = new GridPane();
        genPane.setPadding(new Insets(5, 5, 5, 5));
        genBP = new BorderPane();
        genBP.setTop(topMenu);
        genBP.setCenter(genPane);
        genSc = new Scene(genBP, 500, 400);*/

/*
        manPane = new GridPane();
                manPane.setPadding(new Insets(10,10,10,10));
                manPane.setHgap(3);
                manPane.setVgap(3);*/
