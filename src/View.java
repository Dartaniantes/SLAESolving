import Model.Model;
import Model.exception.InconsistentMatrixException;
import Model.exception.InvalidInputException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

public class View extends Application {
    Model model;
    Stage st, alertStage, resultStage, chartStage;
    Scene scene, alertScene, resultScene, chartScene;
    BorderPane layout, alertLayout;
    HBox topMenu, manChosePane, botMenu, genChoosePane, readReadPane;
    VBox readPane, manPane, genPane, alertPane, resultPane, chartPane;
    AnchorPane buttonPane;
    GridPane manFieldsPane;
    Button solveB, genGenDoubleB, manApplyBб, okB, genGenIntB, okRB, readB;
    TextField inputFileF, manCoefsF[][], manFreeValsF[], outputFileF;
    Label inputL, methodL, manVarNumL, readWarnL, genVarNumL, outFileL, enterPathL, generateMatrixL, generatedMatrixL,
            enterValidOutputPathL, incorrectInputL,resultL, resultWrittenL, inputFieldEmptyL, fileDoesntExistL, readMatrixL, inconsistentMatrix, inputMatrixL, inputTitleL, resultTitleL;
    String outputPath = "E://CourseWorkOutput/output.txt";
    ChoiceBox<String> inputTypeList, methodList;
    ChoiceBox<Integer> manVarNumCB, genVarNumCB;
    LineChart<Number,Number> chart;
    NumberAxis xAxis, yAxis;
    XYChart.Series<Number, Number> e1;
    XYChart.Series<Number, Number> e2;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        model = new Model();
        st = stage;

        st.setTitle("SLAE solver");
        st.setWidth(580);
        st.setHeight(500);

        //general menu
        inputL = new Label("Choose input type : ");
        methodL = new Label(" Choose solving method : ");
        inputTypeList = new ChoiceBox<>();
        inputTypeList.getItems().addAll("Generate randomly", "Read from file", "Enter manually");
        inputTypeList.setValue("Generate randomly");
        inputTypeList.getSelectionModel().selectedItemProperty().addListener((v, oldVal, newVal) -> {
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
        methodList.getItems().addAll("Gauss", "Jordan-Gauss", "Rotation");
        methodList.setValue("Gauss");
        outFileL = new Label("Enter output file path :");
        outputFileF = new TextField();
        outputFileF.setMinSize(300, 10d);
        outputFileF.setPromptText("default : " + outputPath);
        topMenu = new HBox(10);
        topMenu.getChildren().addAll(inputL, inputTypeList, methodL, methodList);
        topMenu.setPadding(new Insets(10, 10, 10, 10));

        solveB = new Button("Solve");
        solveB.setOnAction(actionEvent -> {
            try {
                if (!outputFileF.getText().trim().isEmpty())
                    if (new File(outputFileF.getText()).isFile())
                        outputPath = outputFileF.getText();
                    else {
                        alertPane.getChildren().remove(0);
                        alertPane.getChildren().add(0, enterValidOutputPathL);
                        alertStage.show();
                    }

                if (inputTypeList.getValue() == null) {
                    alertStage.show();
                } else {
                    if (inputTypeList.getValue() == "Read from file") {
                        if (!inputFileF.getText().trim().isEmpty() & new File(inputFileF.getText()).exists()) {
                            if (model.getVarNum() == 2)
                                getChartSolving(model.getOriginSlae());
                            model.solveMatrixByMethod(methodList.getValue());
                        } else {
                            alertPane.getChildren().remove(0);
                            alertPane.getChildren().add(0, enterPathL);
                            alertStage.show();
                        }
                    } else if (inputTypeList.getValue() == "Generate randomly") {
                        if (model.matrixExists()) {
                            if (model.getVarNum() == 2)
                                getChartSolving(model.getOriginSlae());
                            model.solveMatrixByMethod(methodList.getValue());
                        } else {
                            alertPane.getChildren().remove(0);
                            alertPane.getChildren().add(0, generateMatrixL);
                            alertStage.show();
                        }
                    } else if (inputTypeList.getValue() == "Enter manually") {
                        makeMatrixManually();
                        if (model.getVarNum() == 2)
                            getChartSolving(model.getOriginSlae());
                        model.solveMatrixByMethod(methodList.getValue());
                    }
                    if (model.resultExists()) {
                        model.writeResultToFile(outputPath);
                        resultWrittenL.setText("Result was written to file : " + outputPath);
                        inputMatrixL = getLabeledMatrix(model.getOriginSlae());
                        resultPane.getChildren().remove(2);
                        resultPane.getChildren().add(2, inputMatrixL);
                        resultL.setText(resultToString(model.getResult()));
                        resultStage.show();
                    }
                }
            } catch (InconsistentMatrixException e) {
                alertPane.getChildren().remove(0);
                alertPane.getChildren().add(0, inconsistentMatrix);
                alertStage.show();
            }
        });
        botMenu = new HBox(10);
        botMenu.getChildren().addAll(outFileL, outputFileF, solveB);
        buttonPane = new AnchorPane();
        buttonPane.getChildren().addAll(botMenu);
        AnchorPane.setRightAnchor(botMenu, 10d);
        AnchorPane.setBottomAnchor(botMenu, 10d);
        layout = new BorderPane();
        layout.setTop(topMenu);
        layout.setBottom(buttonPane);
        scene = new Scene(layout);

        //result box;
        resultStage = new Stage();
        resultStage.setTitle("Alert box");
        resultStage.setHeight(500);
        resultStage.setWidth(500);
        resultWrittenL = new Label("Result written to file : " + outputPath);
        inputTitleL = new Label("Input matrix : ");
        resultTitleL = new Label("Result : ");
        inputMatrixL = new Label();
        resultL = new Label();
        okRB = new Button("OK");
        okRB.setOnAction(ae -> {
            resultStage.close();
        });
        resultPane = new VBox();
        resultPane.getChildren().addAll(resultWrittenL,inputTitleL, inputMatrixL, resultTitleL, resultL, okRB);
        VBox.setMargin(okRB, new Insets(10, 50, 10, 50));
        resultScene = new Scene(resultPane);
        resultStage.setScene(resultScene);

        // alertBox
        alertStage = new Stage();
        alertStage.initModality(Modality.APPLICATION_MODAL);
        alertStage.setTitle("Alert box");
        alertStage.setHeight(130);
        alertStage.setWidth(250);
        enterPathL = new Label("Enter valid input file path!");
        enterPathL.setPadding(new Insets(10, 10, 10, 10));
        enterValidOutputPathL = new Label("Enter valid output file path!");
        incorrectInputL = new Label("Input file contains incorrect symbols");
        inputFieldEmptyL = new Label("Input file field is empty");
        fileDoesntExistL = new Label("Specified file doesn't exist");
        generateMatrixL = new Label("Generate matrix firstly!");
        inconsistentMatrix = new Label("Matrix is inconsistent");
        generateMatrixL.setPadding(new Insets(10, 10, 10, 10));
        okB = new Button("OK");
        okB.setOnAction(ae -> {
            alertStage.close();
        });
        alertPane = new VBox();
        alertPane.getChildren().add(0, enterPathL);
        alertPane.getChildren().add(1, okB);
        VBox.setMargin(okB, new Insets(10, 50, 10, 10));
        alertLayout = new BorderPane();
        alertLayout.setCenter(alertPane);
        alertScene = new Scene(alertLayout);
        alertStage.setScene(alertScene);


        // "generate" pane
        genVarNumL = new Label("Set variables amount : ");
        genVarNumCB = new ChoiceBox<>();
        genVarNumCB.getItems().addAll(2, 3, 4, 5, 6, 7, 8);
        genVarNumCB.setValue(2);
        genGenDoubleB = new Button("Generate float num");
        genGenDoubleB.setOnAction(ae -> {
            model.generateFloatMatrix(genVarNumCB.getValue());
            genPane.getChildren().remove(generatedMatrixL);
            generatedMatrixL = getLabeledMatrix(model.getOriginSlae());
            genPane.getChildren().add(generatedMatrixL);
            layout.setCenter(genPane);
        });
        genGenIntB = new Button("Generate integer num");
        genGenIntB.setOnAction(ae -> {
            model.generateIntMatrix(genVarNumCB.getValue());
            genPane.getChildren().remove(generatedMatrixL);
            generatedMatrixL = getLabeledMatrix(model.getOriginSlae());
            genPane.getChildren().add(generatedMatrixL);
            layout.setCenter(genPane);
        });
        generatedMatrixL = new Label();
        genChoosePane = new HBox();
        genChoosePane.setPadding(new Insets(10));
        genChoosePane.getChildren().addAll(genVarNumL, genVarNumCB, genGenDoubleB, genGenIntB);
        genPane = new VBox();
        genPane.getChildren().addAll(genChoosePane, generatedMatrixL);
        layout.setCenter(genPane);


        // "readPane"
        readWarnL = new Label("WARNINGS :\n   - coefficients in file have to be separated by spaces\n" +
                "   - each coefficient including '0' have to be specified");

        readB = new Button("Read");
        readB.setOnAction(ae -> {
            if (!inputFileF.getText().trim().isEmpty()) {
                if (new File(inputFileF.getText()).exists()){
                    if(model.checkFile(inputFileF.getText())){
                        readPane.getChildren().removeAll(readMatrixL);
                        readMatrixL = getLabeledMatrix(model.getOriginSlae());
                        readPane.getChildren().add(readMatrixL);
                    }
                    else {
                        alertPane.getChildren().remove(0);
                        alertPane.getChildren().add(0, incorrectInputL);
                        alertStage.show();
                    }
                } else {
                    alertPane.getChildren().remove(0);
                    alertPane.getChildren().add(0, fileDoesntExistL);
                    alertStage.show();
                }
            } else {
                alertPane.getChildren().remove(0);
                alertPane.getChildren().add(0, inputFieldEmptyL);
                alertStage.show();
            }
        });
        readWarnL.setPadding(new Insets(10));
        inputFileF = new TextField();
        inputFileF.setPromptText("Enter input SLAE matrix file path : ");
        readMatrixL = new Label("");
        inputFileF.setMinSize(300, 10d);
        inputFileF.setPadding(new Insets(3,3,3,3));
        readReadPane = new HBox();
        readReadPane.setPadding(new Insets(3, 3, 3, 3));

        readReadPane.getChildren().addAll(inputFileF, readB);
        HBox.setMargin(inputFileF, new Insets(5,5,5,5));
        HBox.setMargin(readB, new Insets(5,5,5,5));
        readPane = new VBox();
        readPane.setPadding(new Insets(3, 3, 3, 3));
        readPane.getChildren().addAll(readWarnL, readReadPane,readMatrixL);
        VBox.setMargin(readMatrixL, new Insets(10,10,10,10));


        //"manual" choose pane
        manVarNumL = new Label("Choose variables amount : ");
        manVarNumCB = new ChoiceBox<>();
        manVarNumCB.getItems().addAll(2, 3, 4, 5, 6, 7, 8);
        manVarNumCB.setValue(2);
        manVarNumCB.getSelectionModel().selectedItemProperty().addListener((v, oldval, newVal) -> {
            manPane.getChildren().remove(manFieldsPane);
            manFieldsPane = new GridPane();
            manFieldsPane.setPadding(new Insets(10));
            manFieldsPane.setHgap(5);
            manFieldsPane.setVgap(10);
            setManualGridPane(newVal);
            manPane.getChildren().add(manFieldsPane);
            layout.setCenter(manPane);
        });
        manChosePane = new HBox(10);
        manChosePane.setPadding(new Insets(10, 10, 10, 10));
        manChosePane.getChildren().addAll(manVarNumL, manVarNumCB);
        //"manual" elements pane
        manFieldsPane = new GridPane();
        manFieldsPane.setPadding(new Insets(10));
        manFieldsPane.setHgap(5);
        manFieldsPane.setVgap(10);
        setManualGridPane(manVarNumCB.getValue());
        manPane = new VBox();
        manPane.setPadding(new Insets(10));
        manPane.getChildren().addAll(manChosePane, manFieldsPane);

        st.setScene(scene);
        st.show();
    }

    public void getChartSolving() {

    }

    public void getChartSolving(double[][] matrix) {
        if (matrix.length != 2 & matrix[0].length != 3)
            throw new RuntimeException("Matrix has less or more than two variables to solve it by chart");
        else {
            double e1x = matrix[0][2]/matrix[0][0];    //e1y = 0
            double e1y = matrix[0][2] / matrix[0][1];  //e1x = 0
            double e2x = matrix[1][2]/ matrix[1][0];   // e2y = 0
            double e2y = matrix[1][2]/ matrix[1][1];   // e2x = 0
            xAxis = new NumberAxis();
            xAxis.setLabel("X1");
            yAxis = new NumberAxis();
            yAxis.setLabel("X2");
            chart = new LineChart<>(xAxis, yAxis);
            e1 = new XYChart.Series<>();
            e1.setName(matrix[0][0] + " x1  +  " + matrix[0][1]+" x2  =  " + matrix[0][2]);
            e2 = new XYChart.Series<>();
            e2.setName(matrix[1][0] + " x1  +  " + matrix[1][1]+" x2  =  " + matrix[1][2]);
            e1.getData().add(new XYChart.Data<>(e1x, 0));
            e1.getData().add(new XYChart.Data<>(0, e1y));
            e2.getData().add(new XYChart.Data<>(e2x, 0));
            e2.getData().add(new XYChart.Data<>(0, e2y));
            chart.setTitle("Chart solving : ");
            chart.getData().addAll(e1,e2);
            chartPane = new VBox();
            chartPane.getChildren().add(chart);
            chartScene = new Scene(chartPane);
            chartStage = new Stage();
            chartStage.setTitle("Chart solving");
            chartStage.setWidth(500);
            chartStage.setHeight(500);
            chartStage.setScene(chartScene);
            chartStage.show();
        }
    }


    public void makeMatrixManually() {
        int length = manCoefsF.length;
        int width = manCoefsF[0].length+1;
        double[][] matrix = new double[length][width];

        String var;
        for (int i = 0; i < length; i++)
            for (int j = 0; j < width; j++) {
                if (j < width-1 && gridInputIsValid(var = manCoefsF[i][j].getText()))
                    matrix[i][j] = Double.parseDouble(var);
                else if(j == width-1 && gridInputIsValid(var = manFreeValsF[i].getText()))
                    matrix[i][j] = Double.parseDouble(var);
                else
                    throw new InvalidInputException("Matrix grid must contain only decimal or float value followed by '-' if needed");
            }
        model.setMatrix(matrix);
    }

    private boolean gridInputIsValid(String s) {
        return s.matches("-?\\d+(.\\d+)?");
    }

    public String resultToString(double[] result) {
        String res = "";
        int index;
        for (int i = 0; i < result.length; i++) {
            index = i + 1;
            res += String.format("x" + index + "  =  " + " %.2f \n", result[i]);
        }
        return res;
    }



    public void setManualGridPane(int length) {
        int width = length + 1;
        manCoefsF = new TextField[length][length];
        manFreeValsF = new TextField[length];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width - 1; j++) {
                manCoefsF[i][j] = new TextField("0");
                manCoefsF[i][j].setMaxSize(30, 30);
                int finalI = i;
                int finalJ = j;
                /*manCoefsF[i][j].textProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue.matches("\\d*")) {
                        manCoefsF[finalI][finalJ].setText(newValue.replaceAll("[^\\d]", ""));
                    }
                });*/
                manFieldsPane.getChildren().add(manCoefsF[i][j]);
                GridPane.setConstraints(manCoefsF[i][j], j, i);
                if (j + 2 == width) {
                    manFreeValsF[i] = new TextField("0");
                    manFreeValsF[i].setMaxSize(30, 30);
                    manFieldsPane.getChildren().add(manFreeValsF[i]);
                    GridPane.setConstraints(manFreeValsF[i], j + 1, i);
                    GridPane.setMargin(manFreeValsF[i], new Insets(0, 10, 0, 10));
                }
            }
        }
    }

    public static Label getLabeledMatrix(double[][] matrix) {
        Label label = new Label("");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++)
                if (j < matrix[0].length - 1) {
                    int index = j + 1;
                    if (j < matrix[0].length - 2)
                        label.setText(label.getText() + String.format(" %.2f x" + index + "  +  ", matrix[i][j]));
                    else
                        label.setText(label.getText() + String.format(" %.2f x" + index, matrix[i][j]));
                } else
                    label.setText(label.getText() + String.format("   =   %.2f " + "%n", matrix[i][j]));
        }
        return label;
    }


}