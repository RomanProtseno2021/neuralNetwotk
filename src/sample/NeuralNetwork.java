package sample;

import static java.lang.Math.pow;

public class NeuralNetwork {
    private double learningRate;

    private double[][] w_hiddenOutput;
    private double[][] w_inputHidden;

    private double[] inputs;
    private double[] hiddenInputs;
    private double[] hiddenOutputs;
    private double[] outputInputs;
    private double[] outputOutputs;

    /**
     * початкова ініціалізація нейроної мережі
     * @param inputNodes кількість вхідних вузлів
     * @param hiddenNodes кількість прихованих вузлів
     * @param outputNodes кількість вихідних вузлів
     * @param learningRate швидкість навчання
     */
    public void init(int inputNodes, int hiddenNodes, int outputNodes, double learningRate) {
        this.learningRate = learningRate;
        this.w_hiddenOutput = new double[outputNodes][hiddenNodes];
        this.w_inputHidden = new double[hiddenNodes][inputNodes];
        randomGenerateMatrixWeight(w_hiddenOutput);
        randomGenerateMatrixWeight(w_inputHidden);
    }

    /**
     * тренування(навчання) ШІ
     * @param inputs вхідні дані
     * @param idealOutputs ідеальні вихідні дані
     */
    public void train(double[] inputs, double[] idealOutputs) {
        this.inputs = inputs;
        this.hiddenInputs = multiplyWeightMatrix(w_inputHidden, inputs);
        this.hiddenOutputs = sigmoidInputArr(this.hiddenInputs);
        this.outputInputs = multiplyWeightMatrix(w_hiddenOutput, hiddenOutputs);
        this.outputOutputs = sigmoidInputArr(this.outputInputs);

        double[] errorOutputs = differenceMatrixWeigth(idealOutputs, this.outputOutputs);
        double[] errorHiddenLayer = searchErrorHiddenLayer(w_hiddenOutput, errorOutputs);

        double[][] deltaWeight = searchDeltaWeight(errorOutputs, outputOutputs, hiddenOutputs);
        this.w_hiddenOutput = newWeightMatrix(this.w_hiddenOutput, deltaWeight, learningRate);

        double[][] deltaWeightHidden = searchDeltaWeight(errorHiddenLayer, this.hiddenOutputs, inputs);
        this.w_inputHidden = newWeightMatrix(this.w_inputHidden, deltaWeightHidden, learningRate);
    }

    /**
     * опитування нейроної мережі
     * @param inputs вхідні дані
     * @return результат опрацювання
     */
    public double[] query(double... inputs) {
        this.inputs = inputs;
        this.hiddenInputs = multiplyWeightMatrix(w_inputHidden, inputs);
        this.hiddenOutputs = sigmoidInputArr(this.hiddenInputs);
        this.outputInputs = multiplyWeightMatrix(w_hiddenOutput, hiddenOutputs);
        this.outputOutputs = sigmoidInputArr(this.outputInputs);
        return this.outputOutputs;
    }

    /**
     * заповнення матриці вагових коефіцієнтів випадковими числами
     * @param matrix матриця, яку необхідно заповнити
     */
    private void randomGenerateMatrixWeight(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = Math.random() - 0.5;
            }
        }
    }

    /**
     * формування значень вузлів прихованого(вихідного) слою НМ
     * @param weights матриця вагових коефіцієнтів, що знаходяться між слоями
     * @param inputs вихідні дані попереднього слою
     * @return матриця вхідних даних наступного слою
     */
    private double[] multiplyWeightMatrix(double[][] weights, double[] inputs) {
        double[] output = new double[weights.length];
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[0].length; j++) {
                output[i] += weights[i][j] * inputs[j];
            }
        }
        return output;
    }

    /**
     * активація усіх вузлів слою НМ
     * @param inputs вхідні дані вузлів слою
     * @return вихідні дані вузлів слою
     */
    private double[] sigmoidInputArr(double[] inputs) {
        double[] output = new double[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            output[i] += sigmoidFunction(inputs[i]);
        }
        return output;
    }

    /**
     * функція активації - сигмоїда
     * @param x вхідне значення
     * @return результат активації вхідного значення
     */
    private static double sigmoidFunction(double x) {
        return 1 / (1 + pow(Math.E, -x));
    }

    /**
     * функція віднімання матриць, для обчислення величини похибки
     * @param idealOutput матриця бажаних вихідних значень
     * @param outputResult матриця отриманих вихідних значень
     * @return матрия, що відповідає різниці двох вхідних
     */
    private double[] differenceMatrixWeigth(double[] idealOutput, double[] outputResult){
        double[] result = new double[idealOutput.length];
        for (int i = 0; i < outputResult.length; i++) {
            result[i] = idealOutput[i] - outputResult[i];
        }
        return result;
    }

    /**
     * транспонування матриці
     * @param matrix вхідна матриця
     * @return транспонованна матриця
     */
    private double[][] transMatrix(double[][] matrix){
        double[][] transMatr = new double[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                transMatr[j][i] = matrix[i][j];
            }
        }
        return transMatr;
    }

    /**
     * обчислення помилки прихованих вузлів матриці
     * @param matrixWeight матриця вагових коефіцієнтів між слоями
     * @param errorOutput масив помилок вузлів вихідного слою НМ
     * @return масив помилок вузлів прихованого слою НМ
     */
    private double[] searchErrorHiddenLayer(double[][] matrixWeight, double[] errorOutput) {
        double[][] matrixWeigthT = transMatrix(matrixWeight);
        double[] result = multiplyWeightMatrix(matrixWeigthT, errorOutput);
        return result;
    }

    /**
     * пошук значеннь поправки до значень вагових коефіцієнтів за допомогою градієнтного спуску
     * @param errors масив значень помилок вузлів слою
     * @param outputLayer масив вихідних значень поточного слою
     * @param outputPreLayer масив вихідних значень попереднього слою
     * @return матриця поправок до вагових коефіцієнтів
     */
    private double[][] searchDeltaWeight(double[] errors, double[] outputLayer, double[] outputPreLayer){
        double[][] deltaWeight = new double[errors.length][outputPreLayer.length];
        for (int i = 0; i < errors.length; i++) {
            for (int j = 0; j < outputPreLayer.length; j++) {
                deltaWeight[i][j] = errors[i] * outputLayer[i] * (1.0 - outputLayer[i]) * outputPreLayer[j];
            }
        }
        return deltaWeight;
    }

    /**
     * оновлення вагових коефіцієнтів НМ
     * @param oldWeight матриця актуальних вагових коефіцієнтів
     * @param deltaWeight матриця, що містить значення поправки вагових коефіцієнтів
     * @param learningRate швидкість навчання НМ
     * @return оновлена матриця вагових коефіцієнтів НМ
     */
    double[][] newWeightMatrix(double[][] oldWeight, double[][] deltaWeight, double learningRate){
        double[][] result = new double[oldWeight.length][oldWeight[0].length];
        for (int i = 0; i < oldWeight.length; i++) {
            for (int j = 0; j < oldWeight[0].length; j++) {
                result[i][j] = oldWeight[i][j] + learningRate * deltaWeight[i][j];
            }
        }
        return result;
    }
}
