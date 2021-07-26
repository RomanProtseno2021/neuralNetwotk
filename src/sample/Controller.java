package sample;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.Arrays;
import java.util.Random;

public class Controller {
    @FXML
    private ImageView imageView;
    @FXML
    private TextField trueAnswerTextField;
    @FXML
    private TextField answerTextField;
    @FXML
    private TextField countIterations;

    private Stage stage;
    private Image image;
    //вхідні дані з зображення
    private double[] inputs = new double[900];
    //штучний інтелект
    private NeuralNetwork neuralNetwork;

    private int randNumb;
    private int numbAutoLearn;
    private int outputNeuronNetwork;
    private double[] result;

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    @FXML
    void initialize() {
        //створення та ініціалізація штучного інтелекту при завантаженні форми
        this.neuralNetwork = new NeuralNetwork();
        neuralNetwork.init(900, 90, 10, 0.3);
    }

    /**
     * Завантаження файлу зображення та відображення на його на формі, формування початкових даних
     */
    @FXML
    void loadImage() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Png files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);
        image = new Image("file:" + fileChooser.showOpenDialog(stage).getAbsolutePath());

        //формування початкових даних для ШІ та початкове опрацювання їх, для внесення до діапазону від 0,01 до 1,001
        drawImage();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * опитування ШІ для отримання результатів, виведення результатів на форму
     */
    @FXML
    void query() {
        result = neuralNetwork.query(inputs);

        int number = 0;
        double max = 0;
        System.out.print("Neuron Network outputs:  ");
        for (int i = 0; i < result.length; i++) {
            if (max <= result[i]) {
                max = result[i];
                number = i;
            }
            System.out.printf("(" + i + ") - " + String.format("%.3f",result[i]) + " , ");
        }
        System.out.println();
        answerTextField.setText(String.valueOf(number));
        outputNeuronNetwork = number;
    }

    /**
     * тренування нейроної мережі, задання кінцевих, правильних даних
     */
    @FXML
    void train() {
        if (!trueAnswerTextField.getText().equals("")) {
            double[] outputs = new double[10];
            int numb = Integer.parseInt(trueAnswerTextField.getText());
            for (int i = 0; i < outputs.length; i++) {
                if (i == numb) {
                    outputs[i] = 0.99;
                } else {
                    outputs[i] = 0.01;
                }
            }
            neuralNetwork.train(inputs, outputs);
        }
    }

    /**
     * функція, що автоматично тренує ШІ
     */
    private void autoLearn() {
        Random random = new Random();
        randNumb = random.nextInt(7);
        numbAutoLearn = random.nextInt(10);

        trueAnswerTextField.setText(String.valueOf(numbAutoLearn));
        image = new Image(new File("images/" + numbAutoLearn + "_" + randNumb + ".png").toURI().toString());

        drawImage();
        query();
        while (numbAutoLearn != outputNeuronNetwork) {
            train();
            query();
        }
    }

    /**
     * функція, що автоматично тренує ШІ, кількість сетів тренування вказує користувач на формі
     */
    @FXML
    void autoLearning() {
        int iterations = Integer.parseInt(countIterations.getText());
        new Thread(() -> {
            try {
                for (int i = 0; i < iterations; i++) {
                    Thread.sleep(500);
                    Platform.runLater(() -> autoLearn());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();
    }

    /**
     * Відображення зображення на формі і формування вхідних даних
     */
    private void drawImage() {
        imageView.setImage(image);
        PixelReader pixelReader = image.getPixelReader();
        double value;
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                value = pixelReader.getArgb(j,i) / -16777216.0 + 0.001;
                inputs[i * 30 + j] = value;
            }
        }
    }
}
