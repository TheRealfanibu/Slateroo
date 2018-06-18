package ai.A3C;

import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import scala.Int;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Agent {
	private static final File frameCounterFile = new File(System.getProperty("user.dir") + "/src/main/resources/ai/frames.txt");

	private static Random random = new Random();
	private static Brain brain;

	private static int frames = 0;

	private double nStepReward = 0;
	private List<Sample> memory;
	
	public Agent() {
		memory = new LinkedList<>();
	}


	private static double getEpsilon(){
		if(frames >=  AIConstants.EPS_STEPS){
			return AIConstants.EPS_STOP;
		}else{
			return AIConstants.EPS_START + frames * (AIConstants.EPS_STOP - AIConstants.EPS_START) / AIConstants.EPS_STEPS;
		}
	}
	
	public static int act(double[] state, boolean train){
		if(train) {
			double eps = getEpsilon();
			frames++;
			if(frames % 20000 == 0)
				System.out.println("Frames: " + frames);
			if(frames % 100000 == 0) {
				brain.save();
				save();
			}


			if (random.nextDouble() < eps)
				return random.nextInt(AIConstants.NUM_ACTIONS);
			else
				return chooseDistributedAction(state);
		}
		else
			return chooseDistributedAction(state);

	}

	private static int chooseDistributedAction(double[] state) {
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
			this.nStepReward = this.nStepReward - sample_0.getReward();
		}
		
	}

	public static void save() {
		try {
			FileUtils.writeStringToFile(frameCounterFile, String.valueOf(frames));
		} catch (IOException e) {
			System.err.println("Saving the frame counter failed");
		}

	}

	public static void load() {
		try {
			frames = Integer.parseInt(FileUtils.readFileToString(frameCounterFile));
		} catch (IOException e) {
			System.err.println("Loading the frame counter failed");
		}
	}
	
	private Sample calcTrainingSample(int nStep){
		Sample sample_0 = memory.get(0);
		Sample sample_N = memory.get(nStep-1);
		return new Sample(sample_0.getState(), sample_0.getAction(), nStepReward, sample_N.getNextState());
	}

	public static void setBrain(Brain brain) {
		Agent.brain = brain;
	}

}
