package logic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import ai.EnvironmentInfo;
import ai.NeuralNetwork;
import ai.ObjectDetector;
import game.FPSCounter;
import gui.Frame;
import io.KeyboardSteering;
import items.manage.ItemManager;
import render.GamePanel;
import utilities.StopWatch;
import utilities.Utils;

public class Main {
	public static final int FPS = 140;
	private static final int SLEEP_TIME = (int) Math.round(1000d / FPS);
	
	private SnakeManager snakeManager;
	private ItemManager itemManager;
	
	private KeyboardSteering keyListener;
	private Frame frame;
	private GamePanel gamePanel;
	private Arena arena;
	
	private EnvironmentInfo envInfo;
	private ObjectDetector objectDetector;
	
	public Main() {
		arena = new Arena();
		keyListener = new KeyboardSteering();
		itemManager = new ItemManager();
		snakeManager = new SnakeManager(keyListener, itemManager);
		objectDetector = new ObjectDetector(snakeManager, itemManager);
		envInfo = new EnvironmentInfo(objectDetector);
		gamePanel = new GamePanel(snakeManager, itemManager, objectDetector, arena);
		frame = new Frame(gamePanel);
		frame.addKeyListener(keyListener);
		
		gameLoop();
	}
	
	private void gameLoop() {
		FPSCounter fps = new FPSCounter("main", 5);
		while(snakeManager.isGameRunning()) {
			fps.count();
			snakeManager.takeActionForEachSnake();
			snakeManager.removeObsoleteSnakes();
			snakeManager.checkSnakeCrashes();
			snakeManager.checkItemIntersections();
			itemManager.removeActivatedItems();
			if(!snakeManager.getSnakes().isEmpty())
				envInfo.calcAIInputs(snakeManager.getSnakes().get(0));
			gamePanel.repaint();
			Utils.sleep(SLEEP_TIME);
		}
		System.exit(0);
	}
	
	public static void main(String[] args) {
		List<String> trainData = null;
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
		NeuralNetwork network = new NeuralNetwork(dims, activations, learningRate);
		
		int episodes = 3;
		train(network, trainFeatures, trainLabels, episodes);
		test(network, testFeatures, testLabels);
		//new Main();
	}
	
	public static void train(NeuralNetwork network, double[][] trainFeatures, double[][] trainLabels, int episodes) {
		StopWatch watch = new StopWatch();
		for(int i = 0; i < episodes; i++) {
			watch.start("s");
			for(int j = 0; j < trainLabels.length; j++) {
				network.train(trainFeatures[j], trainLabels[j]);
			}
			watch.stopAndPrint("Training Episode " + i + " took");
		}
	}
	
	public static void test(NeuralNetwork network, double[][] testFeatures, int[] testLabels) {
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
