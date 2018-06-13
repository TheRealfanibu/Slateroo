package logic;

import java.util.ArrayList;
import java.util.List;

import ai.Environment;
import ai.Matrix;
import ai.MyNeuralNetwork;
import ai.A3C.AIConstants;
import utilities.StopWatch;

/**
 * The class which contains the main method for the program.
 * There will be one instance of this class which introduces all main events happening in the application
 * @author Jonas
 */
public class Main {
	private boolean trainAI = true;
	
	private List<Environment> environments = new ArrayList<>();
	
	/**
	 * The constructor which has to be called first in the program.
	 * It creates the application
	 */
	public Main() {
		if(trainAI)
			createTrainingEnvironments();
		else
			createUserEnvironment();
		
		addOnDestroy();
	}
	
	private void createTrainingEnvironments() {
		for(int i = 0; i < AIConstants.TRAIN_ENVS; i++) {
			environments.add(new Environment(true));
		}
	}
	
	private void createUserEnvironment() {
		environments.add(new Environment(false));
	}
	
	private void addOnDestroy() {
		Runnable shutdownHook = () -> {
			for(Environment env : environments) {
				env.interrupt();
				try {
					env.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook));
	}

	/**
	 * The method which the JVM calls when starting the program
	 * @param args - Console inputs (not needed here)
	 */
	public static void main(String[] args) {
		/*List<String> trainData = null;
		List<String> testData = null;
		try {
			trainData = Files.readAllLines(Paths.get("res/ai/mnist_train.csv"));
			testData = Files.readAllLines(Paths.get("res/ai/mnist_test.csv"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		double[][] trainLabels = prepareOnehotLabels(trainData);
		double[][] trainFeatures = prepareFeatures(trainData);
		double[][] testFeatures = prepareFeatures(testData);
		int[] testLabels = prepareLabels(testData);
		System.out.println("Done Reading Data");
		
		int[] dims = {784,100,10};
		String[] activations = {"sigmoid","sigmoid"};
		double learningRate = 0.01;
		MyNeuralNetwork network = new MyNeuralNetwork(dims, activations, learningRate);
		
		int episodes = 3;
		train(network, trainFeatures, trainLabels, episodes);
		test(network, testFeatures, testLabels);*/
		//new Main();
		Matrix m1 = new Matrix(1000, 1000);
		Matrix m2 = new Matrix(1000,1000);
		
		long start = System.currentTimeMillis();
		Matrix m3 = Matrix.multiply(m1, m2);
		System.out.println((System.currentTimeMillis() - start) / 1000d);
	}
	
	public static void train(MyNeuralNetwork network, double[][] trainFeatures, double[][] trainLabels, int episodes) {
		StopWatch watch = new StopWatch();
		for(int i = 0; i < episodes; i++) {
			watch.start("s");
			for(int j = 0; j < trainLabels.length; j++) {
				network.train(trainFeatures[j], trainLabels[j]);
			}
			watch.stopAndPrint("Training Episode " + i + " took");
		}
	}
	
	public static void test(MyNeuralNetwork network, double[][] testFeatures, int[] testLabels) {
		int correctPredictions = 0;
		for(int i = 0; i < testFeatures.length; i++) {
			int indexPrediction = network.predictIndex(testFeatures[i]);
			if(indexPrediction == testLabels[i])
				correctPredictions++;
		}
		System.out.println("Correct Answers out of " + testFeatures.length + " training data: " + correctPredictions + " = "
				+ ((double) correctPredictions / testFeatures.length * 100) + "%");
	}
	
	public static int[] prepareLabels(List<String> data) {
		return data.stream().mapToInt(line -> Integer.valueOf(line.split(",")[0])).toArray();
	}
	
	public static double[][] prepareOnehotLabels(List<String> data) {
		int[] labels = prepareLabels(data);
		double[][] oneHotLabels = new double[labels.length][10];
		for(int i = 0; i < labels.length; i++) {
			for(int j = 0; j < 10; j++) {
				if(j == labels[i])
					oneHotLabels[i][j] = 0.99;
				else
					oneHotLabels[i][j] = 0.01;
					
			}
		}
		return oneHotLabels;
		
	}
	
	public static double[][] prepareFeatures(List<String> data) {
		double[][] features = new double[data.size()][784];
		for(int i = 0; i < data.size(); i++) {
			String line = data.get(i);
			String[] allValues = line.split(",");
			for(int j = 1; j < allValues.length; j++) {
				features[i][j - 1] = (Double.valueOf(allValues[j]) / 255 * 0.99) + 0.01;
			}
			
		}
		return features;
	}
}
