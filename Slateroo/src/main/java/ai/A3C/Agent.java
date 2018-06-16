package ai.A3C;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Agent {
	private Random random = new Random();
	private Brain brain = new Brain();

	private int frames = 0;

	private double nStepReward = 0;
	private List<Sample> memory;
	private RandomDistributedGenerator generator = new RandomDistributedGenerator();
	
	public Agent() {
		memory = new ArrayList<>();
	}


	private double getEpsilon(){
		if(frames >=  AIConstants.EPS_STEPS){
			return AIConstants.EPS_STOP;
		}else{
			return AIConstants.EPS_START + frames * (AIConstants.EPS_STOP - AIConstants.EPS_START) / AIConstants.EPS_STEPS;
		}
	}
	
	public int act(double[] state){
		double eps = this.getEpsilon();
		frames += 1;
		
		if(random.nextDouble() < eps){
			return random.nextInt(AIConstants.NUM_ACTIONS-1);
		}else{
			double[] probabilities = brain.predict_probabilities(state);

			return RandomDistributedGenerator.getDistributedRandomNumber(probabilities);
		}
	}
	
	public void train(double[] state, int action, double reward, double[] nextState) {
		Sample sample = new Sample(state, action, reward, nextState);
		memory.add(sample);
		
		this.nStepReward = (this.nStepReward + reward *AIConstants.GAMMA_N) / AIConstants.GAMMA;
		
		if(sample.isTerminateState()){
			while(!memory.isEmpty()){
				int nStep = memory.size();
				Sample trainSample = this.calcTrainingSample(nStep);
				brain.trainPush(trainSample);

				Sample sample_0 = this.memory.get(0);
				this.nStepReward = (this.nStepReward - sample_0.getReward()) / AIConstants.GAMMA;
				this.memory.remove(0);
			}
			this.nStepReward = 0;
		}
		
		else if(memory.size() >= AIConstants.N_STEP_RETURN){
			Sample trainSample = this.calcTrainingSample(AIConstants.N_STEP_RETURN);
			brain.trainPush(trainSample);
			Sample sample_0 = this.memory.get(0);
			this.nStepReward = (this.nStepReward - sample_0.getReward()) / AIConstants.GAMMA;
		}
		
	}
	
	private Sample calcTrainingSample(int nStep){
		Sample sample_0 = memory.get(0);
		Sample sample_N = memory.get(nStep-1);
		return new Sample(sample_0.getState(), sample_0.getAction(), nStepReward, sample_N.getNextState());
	}
	
}
