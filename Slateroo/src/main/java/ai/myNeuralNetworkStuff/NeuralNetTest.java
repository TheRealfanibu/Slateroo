package ai.myNeuralNetworkStuff;

import java.io.IOException;

import ai.A3C.AIConstants;
import ai.A3C.NeuralNetwork;
import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
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
        ComputationGraphConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(AIConstants.RANDOM_SEED)
                .graphBuilder()
                .addInputs("input")
                .addLayer("out1", new OutputLayer.Builder()
                            .activation(Activation.SOFTMAX)
                            .nIn(3)
                            .nOut(3)
                            .build(), "input")
                .addLayer("out2", new OutputLayer.Builder()
                            .activation(Activation.IDENTITY)
                            .nIn(3)
                            .nOut(1)
                            .build(), "input")
                .setOutputs("out1", "out2")
                .build();

        ComputationGraph network = new ComputationGraph(conf);
        network.init();

        double[][] trainData = {{1d,2d,3d}, {4d,5d,6d}};
        INDArray[] output = network.output(Nd4j.create(trainData));
        System.out.println(output[0]);
    }
}
