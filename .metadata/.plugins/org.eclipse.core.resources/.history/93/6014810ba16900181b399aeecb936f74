package ai.A3C;

import java.util.Random;

public class Agent {
	Random random = new Random();
	Brain brain = new Brain();
	
	
	public int frames = 0;
	
	private double eps_Start;
	private double eps_End;
	private double eps_Steps;
	private double r_Agent = 0;
	private double[][] memory;
	private DistributedRandomNumberGenerator generator = new DistributedRandomNumberGenerator();
	
	public Agent(double eps_Start, double eps_End, double eps_Steps) {
		this.eps_Start = eps_Start;
		this.eps_End = eps_End;
		this.eps_Steps = eps_Steps;
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
	
	public double act(double s){
		double eps = this.getEpsilon();
		frames += 1;
		
		if(random.nextDouble() < eps){
			return random.nextInt();
		}else{
			double p = brain.predict_p(s);
			
			double a = ;
			return a ;
		}
	}
	
	public void train(double s, double a, double r, double s_){
		
		this.r_Agent = (this.r_Agent + r*Constants.GAMMA_N) / Constants.GAMMA;
		int n = memory.length;
		
		if(s_ != 0){
			while(memory.length > 0){
				double[] samples = this.getSamples(this.memory, n);
				brain.trainPush(samples);
				this.r_Agent = (this.r_Agent - this.memory[0][2]) / Constants.GAMMA;
				this.memory = this.removeRow(memory,0);
			}
			this.r_Agent = 0;
		}
		
		if(memory.length >= Constants.N_STEP_RETURN){
			double[] samples = this.getSamples(this.memory, n);
			brain.trainPush(samples);
			this.r_Agent = (this.r_Agent - this.memory[0][2]) / Constants.GAMMA;
		}
		
	}
	
	private double[] getSamples(double[][] thisMemory, int n){
		double[] samples = {thisMemory[n][0], thisMemory[n][1], this.r_Agent, thisMemory[n-1][3]};
		return samples;
	}
	
	private double[][] removeRow(double[][]array, int row){
		int r = 0;
		double[][] copyArray = new double[array.length-1][array[0].length];
		for(int i = 0; i < array.length; i++){
			for(int j = 0; j < array[1].length; j++){
				if(i != row){
					copyArray[r][j] = array[i][j];
				}
			}
			if(i != row){r++;}
		}
		return copyArray;
	}
	
}
