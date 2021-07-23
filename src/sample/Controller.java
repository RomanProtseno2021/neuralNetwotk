package sample;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
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

    private int numbAutoLearn;
    private int outputNeuronNetwork;

    @FXML
    void initialize() {
        //створення та ініціалізація штучного інтелекту при завантаженні форми
        this.neuralNetwork = new NeuralNetwork();
        neuralNetwork.init(900, 900, 10, 0.3);
    }

    /**
     * Завантаження файлу зображення та відображення на його на формі, формування початкових даних
     */
    @FXML
    void loadImage() {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Png files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(stage);
        this.image = new Image("file:" + file.getAbsolutePath());
        imageView.setImage(this.image);

        //формування початкових даних для ШІ та початкове опрацювання їх, для внесення до діапазону від 0,01 до 1,001
        PixelReader pixelReader = image.getPixelReader();
        double value;
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                value = pixelReader.getArgb(j, i) / -16777216.0 + 0.001;
                if (value < 0.01) {
                    value = 0.01;
                }
                inputs[i * 30 + j] = value;
            }
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * опитування ШІ для отримання результатів, виведення результатів на форму
     */
    @FXML
    void query() {
        double[] result = neuralNetwork.query(inputs);
        int number = 0;
        double max = 0;

        for (int i = 0; i < result.length; i++) {
            if (max <= result[i]) {
                max = result[i];
                number = i;
            }
        }
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
    void autoLearn(){
        int n;
        Random random = new Random();
        n = random.nextInt(5);
        numbAutoLearn = random.nextInt(10);
        trueAnswerTextField.setText(String.valueOf(numbAutoLearn));

        String str = "images/" + numbAutoLearn + "_" + n + ".png";
        this.image = new Image(new File(str).toURI().toString());
        imageView.setImage(this.image);

        PixelReader pixelReader = image.getPixelReader();
        double value;
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                value = pixelReader.getArgb(j, i) / -16777216.0 + 0.001;
                if (value < 0.01) {
                    value = 0.01;
                }
                inputs[i * 30 + j] = value;
            }
        }

        query();
        while(outputNeuronNetwork != numbAutoLearn) {
            train();
            query();
        }
    }

    /**
     * функція, що автоматично тренує ШІ, кількість сетів тренування вказує користувач на формі
     */
    @FXML
    void auroLearning(){
        int iterations = Integer.parseInt(countIterations.getText());

        for (int i = 0; i < iterations; i++) {
            autoLearn();
        }
    }
}
