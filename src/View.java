import Model.exception.InconsistentMatrixException;
import Model.exception.InfiniteSolutionsAmountException;
import Model.exception.InvalidInputException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

public class View extends Application {
    //поле моделі, в якій проводятсья вся робота над СЛАР
    Model model;
    //поля вікон програм, контейнерів їх елементів, самих елементів графічного інтерфейсу
    Stage st, alertStage, resultStage;
    Scene scene, alertScene, resultScene;
    BorderPane layout, alertLayout;
    HBox topMenu, manChosePane, botMenu, genGenPane, genSlaeParamPane, readReadPane;
    VBox readPane, manPane, genPane, alertPane, resultPane, genChoosePane;
    AnchorPane buttonPane;
    GridPane manFieldsPane;
    Button solveB, genGenDoubleB, okB, genGenIntB, okRB, chooseInputFileB,chooseOutputDirB;
    TextField inputFileF, manCoefsF[][], manFreeValsF[];
    Label inputL, methodL, manVarNumL, readWarnL, genVarNumL, enterPathL, generateMatrixL, generatedMatrixL,
            chooseFileFirstlyL, incorrectInputL, resultWrittenL, inputFieldEmptyL, fileDoesntExistL,
            readMatrixL, inconsistentMatrixL, infiniteSolutionsL, invalidInputL, inputMatrixL,
            genEqtNumL,manEqtNumL, inputFileL, outputFileL, inputFilePathL, outputFilePathL,
            totalResultL;
    ChoiceBox<String> inputTypeList, methodList;
    ChoiceBox<Integer> manVarNumCB, genVarNumCB, genEqtNumCB, manEqtNumCB;
    FileChooser fileChoose;
    DirectoryChooser dirChoose;


    //Метод, що запускає програму
    public static void main(String[] args) {
        launch(args);
    }

    //метод, що починає роботу поргами, на вхід подається об'єкт вікна програми
    //Повністю оформлюємо сторінки інтерфейсу
    @Override
    public void start(Stage stage) throws Exception {
        model = new Model();
        st = stage;

        st.setTitle("SLAE solver");
        st.setWidth(580);
        st.setHeight(500);


        //оформлення частини інтерфейсу, що буде супроводжувати всі сторінки
        //general menu
        inputL = new Label("Choose input type : ");
        methodL = new Label(" Choose solving method : ");
        inputTypeList = new ChoiceBox<>();
        inputTypeList.getItems().addAll("Generate randomly", "Read from file", "Enter manually");
        inputTypeList.setValue("Enter manually");
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
        outputFileL = new Label("Output file :");
        outputFilePathL = new Label("unselected");
        topMenu = new HBox(10);
        topMenu.getChildren().addAll(inputL, inputTypeList, methodL, methodList);
        topMenu.setPadding(new Insets(10, 10, 10, 10));
        totalResultL = new Label();

        chooseOutputDirB = new Button("Choose directory");
        chooseOutputDirB.setOnAction(ae ->{
            dirChoose = new DirectoryChooser();
            File outputDir = dirChoose.showDialog(null);

            if(outputDir != null)
                outputFilePathL.setText(outputDir.getAbsolutePath());
        });


        solveB = new Button("Solve");
        solveB.setOnAction(actionEvent -> {
            try {
                model.nullifyResultingStrings();
                if (inputTypeList.getValue() == "Generate randomly" && !model.matrixIsReady()) {
                    alertPane.getChildren().remove(0);
                    alertPane.getChildren().add(0, generateMatrixL);
                    alertStage.show();
                } else if (inputTypeList.getValue() == "Enter manually") 
                    makeMatrixManually();


                model.solveMatrixByMethod(methodList.getValue());
                if (model.getVarNum() == 2 && model.getEquationsNum() == 2)
                    getChartSolving(model.getOriginSlae());
                if (model.resultExists()) {
                    totalResultL.setText(model.makeTotalResultString());
                    setResultStage();
                    if (!outputFilePathL.getText().equals("unselected"))
                        model.writeResultToFile(outputFilePathL.getText());

                    resultWrittenL.setText("Result was written to file : " + outputFilePathL.getText());
                    resultPane.getChildren().remove(1);
                    resultPane.getChildren().add(1, totalResultL);
                    resultStage.show();
                }

            } catch (InconsistentMatrixException e) {
                inconsistentMatrixL.setText(e.getMessage());
                alertPane.getChildren().remove(0);
                alertPane.getChildren().add(0, inconsistentMatrixL);
                alertStage.setMinWidth(inconsistentMatrixL.getLayoutBounds().getWidth());
                alertStage.show();
            } catch (InfiniteSolutionsAmountException e) {
                infiniteSolutionsL.setText(e.getMessage());
                alertPane.getChildren().remove(0);
                alertPane.getChildren().add(0, infiniteSolutionsL);
                alertStage.setMinWidth(infiniteSolutionsL.getLayoutBounds().getWidth());
                alertStage.show();
            } catch (InvalidInputException e) {
                invalidInputL.setText(e.getMessage());
                alertPane.getChildren().remove(0);
                alertPane.getChildren().add(0, invalidInputL);
                alertStage.setMinWidth(invalidInputL.getLayoutBounds().getWidth());
                alertStage.show();
            }
        });
        botMenu = new HBox(10);
        botMenu.getChildren().addAll(outputFileL, outputFilePathL, chooseOutputDirB, solveB);
        buttonPane = new AnchorPane();
        buttonPane.getChildren().addAll(botMenu);
        AnchorPane.setRightAnchor(botMenu, 10d);
        AnchorPane.setBottomAnchor(botMenu, 10d);
        layout = new BorderPane();
        layout.setTop(topMenu);
        layout.setBottom(buttonPane);
        scene = new Scene(layout);

        //Оформлення вікна помилок
        // alertBox
        alertStage = new Stage();
        alertStage.initModality(Modality.APPLICATION_MODAL);
        alertStage.setTitle("Alert box");
        alertStage.setHeight( 160);
        alertStage.setWidth(240 );
        enterPathL = new Label("Enter valid input file path!");
        enterPathL.setPadding(new Insets(10, 10, 10, 10));
        chooseFileFirstlyL = new Label("Enter valid output file path!");
        incorrectInputL = new Label("Input matrix file must contain only " +
                                    "\ndecimal or float(using '.') values " +
                                    "\nfollowed by '-' if needed");
        inputFieldEmptyL = new Label("Input file field is empty");
        fileDoesntExistL = new Label("Specified file doesn't exist");
        generateMatrixL = new Label("Generate matrix firstly!");
        inconsistentMatrixL = new Label("Matrix is inconsistent");
        infiniteSolutionsL = new Label("Matrix has infinite solutions amount");
        invalidInputL = new Label("Input matrix characters must contain only " +
                                    "\ndecimal or floating point(using '.') numbers " +
                                    "\nfollowed by '-' if need");
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


        // оформлення сторінки генерації матриці
        // "generate" pane
        genVarNumL = new Label("Set variables amount : ");
        genVarNumCB = new ChoiceBox<>();
        genVarNumCB.getItems().addAll(2, 3, 4, 5, 6, 7, 8);
        genVarNumCB.setValue(2);
        genEqtNumL = new Label("Set equations amount : ");
        genEqtNumCB = new ChoiceBox<>();
        genEqtNumCB.getItems().addAll(2, 3, 4, 5, 6, 7, 8);
        genEqtNumCB.setValue(2);
        genGenDoubleB = new Button("Generate float num");
        genGenDoubleB.setOnAction(ae -> {
            model.generateFloatMatrix(genVarNumCB.getValue(), genEqtNumCB.getValue());
            genPane.getChildren().remove(generatedMatrixL);
            generatedMatrixL = getLabeledMatrix(model.getOriginSlae());
            genPane.getChildren().add(generatedMatrixL);
            layout.setCenter(genPane);
        });
        genGenIntB = new Button("Generate integer num");
        genGenIntB.setOnAction(ae -> {
            model.generateIntMatrix(genVarNumCB.getValue(), genEqtNumCB.getValue());
            genPane.getChildren().remove(generatedMatrixL);
            generatedMatrixL = getLabeledMatrix(model.getOriginSlae());
            genPane.getChildren().add(generatedMatrixL);
            layout.setCenter(genPane);
        });
        generatedMatrixL = new Label();
        VBox.setMargin(generatedMatrixL, new Insets(10,10,10,10));
        genGenPane = new HBox();
        genSlaeParamPane = new HBox();
        genChoosePane = new VBox();
        genChoosePane.setPadding(new Insets(10));
        genSlaeParamPane.getChildren().addAll(genVarNumL, genVarNumCB, genEqtNumL, genEqtNumCB);
        genGenPane.getChildren().addAll(genGenDoubleB, genGenIntB);

        genChoosePane.getChildren().addAll(genSlaeParamPane, genGenPane);
        genPane = new VBox();
        genPane.getChildren().addAll(genChoosePane, generatedMatrixL);



        //оформлення сторінки зчитування матриці з файлу
        // "readPane"
        readWarnL = new Label("WARNINGS :\n   - coefficients in file have to be separated by spaces\n" +
                "   - each coefficient including '0' have to be specified");

        fileChoose = new FileChooser();
        chooseInputFileB = new Button("Choose file");
        chooseInputFileB.setOnAction(ae -> {
            File inputF = fileChoose.showOpenDialog(null);

            if(inputF != null)
                if(model.fileIsValid(inputF)){
                    readPane.getChildren().removeAll(readMatrixL);
                    readMatrixL = getLabeledMatrix(model.getOriginSlae());
                    readPane.getChildren().add(readMatrixL);
                    readReadPane.getChildren().remove(1);
                    inputFilePathL.setText(inputF.getAbsolutePath());
                    readReadPane.getChildren().add(1, inputFilePathL);
                    solveB.setDisable(false);

                } else {
                    if(!solveB.isDisabled())
                        solveB.setDisable(true);
                    alertPane.getChildren().remove(0);
                    alertPane.getChildren().add(0, incorrectInputL);
                    alertStage.show();
                }


        });
        readWarnL.setPadding(new Insets(10));
        inputFileF = new TextField();
        inputFileF.setPromptText("Enter input SLAE matrix file path : ");
        inputFileL = new Label("Input file : ");
        inputFilePathL = new Label("unselected");
        readMatrixL = new Label("");

        readReadPane = new HBox();
        readReadPane.setPadding(new Insets(3, 3, 3, 3));

        readReadPane.getChildren().addAll(inputFileL,inputFilePathL, chooseInputFileB);
        HBox.setMargin(inputFileL, new Insets(5,5,5,5));
        HBox.setMargin(inputFilePathL, new Insets(5,5,5,5));
        HBox.setMargin(chooseInputFileB, new Insets(5,5,5,5));
        readPane = new VBox();
        readPane.setPadding(new Insets(3, 3, 3, 3));
        readPane.getChildren().addAll(readWarnL, readReadPane,readMatrixL);



        //оформлення сторінки введення коефіцієнтів матриці вручну
        //"manual" choose pane
        manVarNumL = new Label("Choose variables amount : ");
        manVarNumCB = new ChoiceBox<>();
        manVarNumCB.getItems().addAll(2, 3, 4, 5, 6, 7, 8);
        manVarNumCB.setValue(2);
        manEqtNumL = new Label("Choose equations amount : ");
        manEqtNumCB = new ChoiceBox<>();
        manEqtNumCB.getItems().addAll(2, 3, 4, 5, 6, 7, 8);
        manEqtNumCB.setValue(2);
        manVarNumCB.getSelectionModel().selectedItemProperty().addListener((v, oldval, newVal) -> {
            manPane.getChildren().remove(manFieldsPane);
            manFieldsPane = new GridPane();
            manFieldsPane.setPadding(new Insets(10));
            manFieldsPane.setHgap(5);
            manFieldsPane.setVgap(10);
            setManualGridPane(newVal, manEqtNumCB.getValue());
            manPane.getChildren().add(manFieldsPane);
            layout.setCenter(manPane);
        });
        manEqtNumCB.getSelectionModel().selectedItemProperty().addListener((v, oldval, newVal) -> {
            manPane.getChildren().remove(manFieldsPane);
            manFieldsPane = new GridPane();
            manFieldsPane.setPadding(new Insets(10));
            manFieldsPane.setHgap(5);
            manFieldsPane.setVgap(10);
            setManualGridPane(manVarNumCB.getValue(),newVal);
            manPane.getChildren().add(manFieldsPane);
            layout.setCenter(manPane);
        });
        manChosePane = new HBox(10);
        manChosePane.setPadding(new Insets(10, 10, 10, 10));
        manChosePane.getChildren().addAll(manVarNumL, manVarNumCB, manEqtNumL, manEqtNumCB);
        //"manual" elements pane
        manFieldsPane = new GridPane();
        manFieldsPane.setPadding(new Insets(10));
        manFieldsPane.setHgap(5);
        manFieldsPane.setVgap(10);
        setManualGridPane(manVarNumCB.getValue(), genEqtNumCB.getValue());
        manPane = new VBox();
        manPane.setPadding(new Insets(10));
        manPane.getChildren().addAll(manChosePane, manFieldsPane);
        layout.setCenter(manPane);

        st.setScene(scene);
        st.show();
    }

    //Метод виводить на укран вікно з графічним рішенням вхідної СЛАР розмірності 2
    public void getChartSolving(double[][] matrix) {
        if (matrix.length != 2 & matrix[0].length != 3)
            throw new RuntimeException("Matrix has less or more than two variables to solve it by chart");
        else {
            ChartSolve cs = new ChartSolve(matrix);
            cs.start(new Stage());
        }
    }


    //перевіряє коефіцієнти, введені користувачем вручну та записує іх до матриці моделі
    public void makeMatrixManually() {
        int length = manCoefsF.length;
        int width = manCoefsF[0].length+1;
        double[][] matrix = new double[length][width];

        String var;
        for (int i = 0; i < length; i++)
            for (int j = 0; j < width; j++) {
                if (j < width-1 && gridInputIsValid(var = manCoefsF[i][j].getText().trim()))
                    matrix[i][j] = Double.parseDouble(var);
                else if(j == width-1 && gridInputIsValid(var = manFreeValsF[i].getText().trim()))
                    matrix[i][j] = Double.parseDouble(var);
                else
                    throw new InvalidInputException("Matrix grid must contain only decimal or float value followed by '-' if needed");
            }
        model.setMatrix(matrix);
    }

    //Повертає ТАК, якщо вхідний рядок містить корректні для обробки програмою символи
    private boolean gridInputIsValid(String s) {
        return s.matches("-?\\d+([.,]\\d+)?");
    }

    // Оформлює сітку елементів, в які можна вводити вхідні коефіцієнти СЛАР заданої розмірності
    public void setManualGridPane(int varNum, int eqtNum) {
        manCoefsF = new TextField[eqtNum][varNum];
        manFreeValsF = new TextField[eqtNum];
        for (int i = 0; i < eqtNum; i++) {
            for (int j = 0; j < varNum; j++) {
                manCoefsF[i][j] = new TextField("0");
                manCoefsF[i][j].setMaxSize(30, 30);
                manFieldsPane.getChildren().add(manCoefsF[i][j]);
                GridPane.setConstraints(manCoefsF[i][j], j, i);
                if (j + 2 == varNum+1) {
                    manFreeValsF[i] = new TextField("0");
                    manFreeValsF[i].setMaxSize(30, 30);
                    manFieldsPane.getChildren().add(manFreeValsF[i]);
                    GridPane.setConstraints(manFreeValsF[i], j + 1, i);
                    GridPane.setMargin(manFreeValsF[i], new Insets(0, 10, 0, 10));
                }
            }
        }
    }

    //Представляє вхідну матрицю у вигляді лейблу
    //Зручно для відображення на інтерфейсі
    public static Label getLabeledMatrix(double[][] matrix) {
        Label label = new Label("");
        if (matrix[0].length == 3) {
            for (int i = 0; i < matrix.length; i++)
                label.setText(label.getText() + matrix[i][0] + " x" + (i+1) + "  " + (matrix[i][1] >= 0 ? "+" + matrix[i][1] : matrix[i][1])
                        + " y" + (i+1) + "  = " + matrix[i][2] + "\n");

        }else
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

    //Готує вікно результату до заповнення
    private void setResultStage() {
        resultStage = new Stage();
        resultStage.setTitle("Result");
        resultStage.setHeight(500);
        resultStage.setWidth(500);
        resultWrittenL = new Label();
        okRB = new Button("OK");
        okRB.setOnAction(ae -> {
            resultStage.close();
        });
        resultPane = new VBox();
        resultPane.getChildren().addAll(resultWrittenL, totalResultL, okRB);
        VBox.setMargin(okRB, new Insets(10, 50, 10, 50));
        resultScene = new Scene(resultPane);
        resultStage.setScene(resultScene);
    }
}