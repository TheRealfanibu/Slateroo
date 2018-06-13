package ai.A3C;

import java.util.ArrayList;

import ai.Matrix;
import ai.NeuralNetwork;

public class Brain {
	
	private NeuralNetwork network;
	private ArrayList<Double[]> trainQueue_S;
	private ArrayList<Integer> trainQueue_A;
	private ArrayList<Double> trainQueue_R;
	private ArrayList<Double[]> trainQueue_S_;
	private ArrayList<Double[]> trainQueue_S_Mask;
	
	public Brain(){
		network = new NeuralNetwork(AIConstants.LAYER_STRUCTURE, AIConstants.ACTIVATION_FUNCTIONS, AIConstants.LEARNING_RATE);
	}
	
	public void optimize(){
		
	}
	
	public void trainPush(double[][] samples){
		double[] s = samples[0];
		int a = (int) samples[1][0];
		double r = samples[2][0];
		double[] s_ = samples[3];
		
	}
	
	/*public double[] predict(double[] s){
		return network.predict(s);
	}*/
	
	public double predict_p(double[] s){
	}
	
	/*public double predict_v(double[] s){
		double[] pv_array = network.predict(s).getColumn(0);
		double v = pv_array[1];
		
		return v ;
	}*/ 
	
}
