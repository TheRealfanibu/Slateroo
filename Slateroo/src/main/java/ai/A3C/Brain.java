package ai.A3C;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.MultiDataSet;
import org.nd4j.linalg.factory.Nd4j;

import java.util.LinkedList;
import java.util.Queue;

public class Brain {
	
	private NeuralNetwork network;

	private Queue<Sample> trainQueue;
	
	public Brain(){
		network = new NeuralNetwork();
		trainQueue = new LinkedList<>();
	}
	
	public void optimize(){
	    INDArray ndStates, ndOnehotActions, ndRewards, ndNextStates, ndTerminalMask;
	    double[][] states, nextStates, onehotActions;
	    double[] rewards, terminalMask;
		synchronized (this) {
		    int sampleSize = trainQueue.size();
		    if(sampleSize < AIConstants.MIN_BATCH)
		        return;

            states = new double[sampleSize][AIConstants.NUM_STATES];
            nextStates = new double[sampleSize][AIConstants.NUM_STATES];
            onehotActions = new double[sampleSize][AIConstants.NUM_ACTIONS];
            rewards = new double[sampleSize];
            terminalMask = new double[sampleSize];

            for(int i = 0; i < sampleSize; i++) {
                Sample trainSample = trainQueue.remove();
                states[i] = trainSample.getState();
                nextStates[i] = trainSample.getNextState();
                onehotActions[i] = trainSample.getOnehotAction();
                rewards[i] = trainSample.getReward();
                terminalMask[i] = trainSample.isTerminateState() ? 0 : 1;
            }
        }

        ndStates = Nd4j.create(states).transpose();
		ndNextStates = Nd4j.create(nextStates).transpose();
		ndOnehotActions = Nd4j.create(onehotActions).transpose();
		ndRewards = Nd4j.create(rewards).transpose();
		ndTerminalMask = Nd4j.create(terminalMask).transpose();


	}
	
	public void trainPush(Sample sample){
		
	}
	
	public double[] predict_probabilities(double[] state) {
        INDArray[] output = network.predict(state);
        return output[1].toDoubleVector();
	}

	public INDArray predict_values (INDArray states) {
        INDArray[] output = network.predict(state);
        MultiDataSet set = new MultiDataSet();
        return output[0].toDoubleVector()[0];
    }
}
