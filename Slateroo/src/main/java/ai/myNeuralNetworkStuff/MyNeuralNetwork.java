package ai.myNeuralNetworkStuff;

import game.Matrix;

public class MyNeuralNetwork {
	private Matrix[] weights;
	private Matrix[] biases;
	private String[] activationFunctions;
	
	private int amountLayers;
	private int amountWeightLayers;
	
	private double learningRate;
	
	public MyNeuralNetwork(int[] amountNeuronsInEachLayer, String[] activationFunctions, double learningRate) {
		this.activationFunctions = activationFunctions;
		this.learningRate = learningRate;
		this.amountLayers = amountNeuronsInEachLayer.length;
		this.amountWeightLayers = amountLayers - 1;
		
		if(activationFunctions.length != amountWeightLayers)
			throw new IllegalArgumentException("Illegal Number of Activation Functions: " + activationFunctions.length + ", must be " + amountWeightLayers);
		
		initWeightsAndBiases(amountNeuronsInEachLayer);
	}
	
	public Matrix predict(double[] inputs) {
		Matrix[] outputs = feedForward(Matrix.fromArray(inputs).transpose());
		return outputs[amountWeightLayers];
	}
	
	public int predictIndex(double[] inputs) {
		Matrix output = predict(inputs);
		return output.getIndexOfLargestValueInColumn(0);
	}
	
	public void train(double[] inputs, double[] targets) {
		Matrix inputVector = Matrix.fromArray(inputs).transpose();
		Matrix targetsVector = Matrix.fromArray(targets).transpose();
		
		Matrix[] outputs = feedForward(inputVector);
		
		Matrix[] errors = calculateErrors(targetsVector, outputs[amountWeightLayers]);
		adjustWeightsAndBiases(outputs, errors);
	}
	
	private Matrix[] feedForward(Matrix inputVector) {
		Matrix[] outputs = new Matrix[amountLayers];
		outputs[0] = inputVector;
		
		Matrix outputFromLastLayer = inputVector;
		for(int i = 0; i < amountWeightLayers; i++) {
			Matrix weightedInputsToNextLayer = Matrix.multiply(weights[i], outputFromLastLayer);
			Matrix weightedInputsPlusBiases = Matrix.add(weightedInputsToNextLayer, biases[i]);;
			Matrix output = activationFunction(activationFunctions[i], weightedInputsPlusBiases, false);
			outputs[i+1] = output;
			outputFromLastLayer = output;
		}
		return outputs;
	}
	
	private Matrix[] calculateErrors(Matrix targets, Matrix finalOutput) {
		Matrix[] errors = new Matrix[amountWeightLayers];
		errors[0] = Matrix.subtract(targets, finalOutput);
		
		for(int i = 1; i < amountWeightLayers; i++) {
			Matrix transposedWeights = weights[amountWeightLayers - i].transpose();
			Matrix error = Matrix.multiply(transposedWeights, errors[i - 1]);
			errors[i] = error;
		}
		return errors;
	}
	
	private void adjustWeightsAndBiases(Matrix[] outputs, Matrix[] errors) {// backpropagation
		for(int i = 0; i < amountWeightLayers; i++) {
			Matrix gradients = activationFunction(activationFunctions[i], outputs[i+1], true);
			Matrix gradientsProportionalToError = Matrix.multiplyElementwise(gradients, errors[amountWeightLayers - 1 - i]);
			Matrix gradientsProportionalToErrorAndLearningRate = gradientsProportionalToError.scale(learningRate);
			Matrix transposedOutputsFromPreviousLayer = outputs[i].transpose();
			Matrix weightAdjustments = Matrix.multiply(gradientsProportionalToErrorAndLearningRate, transposedOutputsFromPreviousLayer);
			weights[i] = Matrix.add(weights[i], weightAdjustments);
			biases[i] = Matrix.add(biases[i], gradientsProportionalToErrorAndLearningRate);
		}
	}
	
	private void initWeightsAndBiases (int[] amountNeurons) {
		weights = new Matrix[amountWeightLayers];
		biases = new Matrix[amountWeightLayers];
		for(int i = 0; i < amountWeightLayers; i++) {
			weights[i] = new Matrix(amountNeurons[i+1], amountNeurons[i]);
			weights[i].randomizeNormal(Math.pow(amountNeurons[i], -0.5));
			
			biases[i] = new Matrix(amountNeurons[i+1], 1);
			biases[i].randomizeNormal(Math.pow(amountNeurons[i], -0.5));
		}
	}
	
	private Matrix activationFunction(String activationFunction, Matrix inputs, boolean derivative) {
		int rows = inputs.getRows();
		double[][] outputs = new double[rows][1];
		for(int i = 0; i < rows; i++) {
			outputs[i][0] = ActivationFunctions.applyActivation(activationFunction, inputs.getValue(i, 0), derivative);
		}
		return Matrix.fromArray(outputs);
	}
	
	private static class ActivationFunctions {
		public static double applyActivation(String activationFunction, double input, boolean derivative) {
			switch(activationFunction) {
			case "sigmoid": return sigmoid(input, derivative);
			case "relu": return relu(input, derivative);
			case "softmax": return softmax(input, derivative);
			}
			throw new IllegalArgumentException("Illegal Activation Function: " + activationFunction);
		}
		
		private static double sigmoid(double value, boolean derivative) {
			if(derivative)
				return value * (1 - value);
			return 1 / (1 + Math.exp(-value));
		}
		
		private static double relu(double value, boolean derivative) {
			if(derivative)
				return value < 0 ? 0 : 1;
			else
				return value < 0 ? 0 : value;
		}
		
		private static double softmax(double input, boolean derivative) {
			return 0;
		}
	}
}
