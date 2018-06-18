package ai.A3C;

import java.util.*;

public class Agent {
	private Random random;
	private Brain brain;

	private int frames = 0;

	private double nStepReward = 0;
	private List<Sample> memory;
	
	public Agent(Brain brain) {
		memory = new LinkedList<>();
		random = new Random();
		this.brain = brain;
	}


	private double getEpsilon(){
		if(frames >=  AIConstants.EPS_STEPS){
			return AIConstants.EPS_STOP;
		}else{
			return AIConstants.EPS_START + frames * (AIConstants.EPS_STOP - AIConstants.EPS_START) / AIConstants.EPS_STEPS;
		}
	}
	
	public int act(double[] state, boolean train){
		if(train) {
			double eps = this.getEpsilon();
			frames++;
			if(frames % 20000 == 0)
				System.out.println("Frames: " + frames);
			if(frames % 100000 == 0)
				brain.save();

			if (random.nextDouble() < eps)
				return random.nextInt(AIConstants.NUM_ACTIONS);
			else
				return chooseDistributedAction(state);
		}
		else
			return chooseDistributedAction(state);

	}

	private int chooseDistributedAction(double[] state) {
		double[] probabilities = brain.predict_probabilities(state);

		return RandomDistributedGenerator.getDistributedRandomNumber(probabilities);
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

				Sample sample_0 = this.memory.remove(0);
				this.nStepReward = (this.nStepReward - sample_0.getReward()) / AIConstants.GAMMA;
			}
			this.nStepReward = 0;
		}
		
		else if(memory.size() >= AIConstants.N_STEP_RETURN){
			Sample trainSample = this.calcTrainingSample(AIConstants.N_STEP_RETURN);
			brain.trainPush(trainSample);
			Sample sample_0 = this.memory.remove(0);
			this.nStepReward = (this.nStepReward - sample_0.getReward()) / AIConstants.GAMMA;
		}
		
	}
	
	private Sample calcTrainingSample(int nStep){
		Sample sample_0 = memory.get(0);
		Sample sample_N = memory.get(nStep-1);
		return new Sample(sample_0.getState(), sample_0.getAction(), nStepReward, sample_N.getNextState());
	}
	
}
