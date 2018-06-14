package ai.A3C;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Agent {
	Random random = new Random();
	Brain brain = new Brain();
	
	
	public int frames = 0;
	
	private double eps_Start;
	private double eps_End;
	private double eps_Steps;
	private double r_Agent = 0;
	private List<Sample> memory;
	private RandomDistributedGenerator generator = new RandomDistributedGenerator();
	
	public Agent(double eps_Start, double eps_End, double eps_Steps) {
		this.eps_Start = eps_Start;
		this.eps_End = eps_End;
		this.eps_Steps = eps_Steps;
		memory = new ArrayList<>();
	}

	public Agent(){
		
	}

	public double getEpsilon(){
		if(frames >=  this.eps_Steps){
			return this.eps_End;
		}else{
			return eps_Start + frames * (eps_End - eps_Start) / eps_Steps;
		}
	}
	
	public int act(double[] state){
		double eps = this.getEpsilon();
		frames += 1;
		
		if(random.nextDouble() < eps){
			return random.nextInt(AIConstants.NUM_ACTIONS-1);
		}else{
			double[] probabilities = brain.predict_probabilities(state);

			int a = generator.getDistributedRandomNumber(probabilities);
			return a;
		}
	}
	
	public void train(double[] s, int a, double r, double[] s_) {
		double[] a_cats = new double[memory.size()];
		a_cats[a] = 1;
		Sample sample = new Sample(s, a_cats, this.r_Agent, s_);
		memory.add(sample);
		
		this.r_Agent = (this.r_Agent + r*AIConstants.GAMMA_N) / AIConstants.GAMMA;
		
		if(s_ == null){
			while(memory.size() > 0){
				int n = memory.size();
				double[][] samples = this.getSample(n);
				brain.trainPush(samples);
				Sample sample_0 = this.memory.get(0);
				this.r_Agent = (this.r_Agent - sample_0.getR()) / AIConstants.GAMMA;
				this.memory.remove(0);
			}
			this.r_Agent = 0;
		}
		
		if(memory.size() >= AIConstants.N_STEP_RETURN){
			double[][] samples = this.getSample(AIConstants.N_STEP_RETURN);
			brain.trainPush(samples);
			Sample sample_0 = this.memory.get(0);
			this.r_Agent = (this.r_Agent - sample_0.getR()) / AIConstants.GAMMA;
		}
		
	}
	
	private double[][] getSample(int n){
		Sample sample_0 = memory.get(0);
		Sample sample_N = memory.get(n-1);
		double[][] sample = {sample_0.getS(), sample_0.getOnehot_A(), {this.r_Agent}, sample_N.getS_()}; 
		return sample;
	}
	
}
