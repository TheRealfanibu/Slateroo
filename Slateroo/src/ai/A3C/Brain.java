package ai.A3C;

import ai.NeuralNetwork;

public class Brain {
	
	private NeuralNetwork network;
	
	public Brain(){
		network = new NeuralNetwork();
	}
	
	public void optimize(){
		
	}
	
	public void trainPush(double[][] samples){
		double[] s = samples[0];
		double a = samples[1][0];
		double r = samples[2][0];
		double[] s_ = samples[3];
	}
	
	public double[] predict(double[] s){
		return network.predict(s).getColumn(0);
	}
	
	public double predict_p(double s){
		double[] pv_array;
		double p;
		
		return p ;
	}
	
	public double predict_v(double s){
		double[] pv_array;
		double v;
		
		return v ;
	}
	
}
