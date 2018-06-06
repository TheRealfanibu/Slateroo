package ai;

import java.util.Random;
import java.util.function.UnaryOperator;

import utilities.Utils;

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
		
	}
}
