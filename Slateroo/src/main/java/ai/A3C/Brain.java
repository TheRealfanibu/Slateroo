package ai.A3C;

import java.util.ArrayList;

public class Brain {
	
	private NeuralNetwork network;
	private ArrayList<Double[]> trainQueue_S;
	private ArrayList<Integer> trainQueue_A;
	private ArrayList<Double> trainQueue_R;
	private ArrayList<Double[]> trainQueue_S_;
	private ArrayList<Double[]> trainQueue_S_Mask;
	
	public Brain(){
		network = new NeuralNetwork();
	}
	
	public void optimize(){
		
	}
	
	public void trainPush(double[][] samples){
		double[] s = samples[0];
		int a = (int) samples[1][0];
		double r = samples[2][0];
		double[] s_ = samples[3];
		
	}
	
	public double[] predict_probabilities(double[] s) {
		return null;
	}

	public double predict_value(double[] s) {
		return 0;
	}

	
}
