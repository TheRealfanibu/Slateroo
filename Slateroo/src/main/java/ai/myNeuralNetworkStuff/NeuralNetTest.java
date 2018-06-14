package ai.myNeuralNetworkStuff;

import java.io.IOException;

import ai.A3C.NeuralNetwork;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NeuralNetTest {
	public static void main(String[] args) {
		/*Matrix weights = new Matrix(2,1);
		weights.randomizeNormal(1);
		double bias = new Random().nextGaussian();
		System.out.println(weights + " " + bias);
		double[][] x = {
				{0,0,1,1},
				{0,1,0,1}
		};
		int trainingExamples = 4;
		double[] y = {0,1,1,0};
		double lr = 0.01;
		Matrix X = Matrix.fromArray(x);
		Matrix Y = Matrix.fromArray(y);
		for(int i = 0; i < 1000; i++) {
			Matrix Z = Matrix.multiply(weights.transpose(), X).addNumber(bias);
			UnaryOperator<Double> sigmoid = z -> 1 / (1 + Math.exp(-z));
			Matrix A = Z.applyToEveryElement(sigmoid);
			Matrix dZ = Matrix.subtract(A, Y);
			Matrix dW = Matrix.multiply(X, dZ.transpose()).multiplyWithNumber(1 / trainingExamples);
			double dB = dZ.sum() * (1 / trainingExamples);
			weights = Matrix.subtract(weights, dW.multiplyWithNumber(lr));
			bias = bias - lr * dB;

		}
		Matrix testM = Matrix.fromArray(test).transpose();
		Matrix Z = Matrix.multiply(weights.transpose(), testM).addNumber(bias);
		UnaryOperator<Double> sigmoid = z -> 1 / (1 + Math.exp(-z));
		Matrix A = Z.applyToEveryElement(sigmoid);
		System.out.println("A " + A);
		System.out.println(weights + " " + bias);*/
		Logger log = LoggerFactory.getLogger(NeuralNetTest.class);

		NeuralNetwork network = new NeuralNetwork();
        DataSetIterator mnistTrain = null;
        DataSetIterator mnistTest = null;
        try {
            mnistTrain = new MnistDataSetIterator(10, true, 12345);
            mnistTest = new MnistDataSetIterator(10, false, 12345);
        } catch (IOException e) {
            throw new RuntimeException();
        }

        int epochs = 10;
        for(int i = 0; i < epochs; i++) {
            //network.train(mnistTrain);
        }
        //Evaluation eval = network.evaluate(mnistTest);
        //System.out.println(eval.stats());
        //System.out.println(eval.precision());
    }
}
