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

		INDArray ndRewards = Nd4j.create(rewards);
		ndRewards = ndRewards.transpose();

		INDArray ndTerminalMask = Nd4j.create(terminalMask).transpose();

		if(states.length / AIConstants.MIN_BATCH > 5)
		    System.out.println("Optimize Batch " + states.length + " = " + states.length / (double) AIConstants.MIN_BATCH + " times the minibatch");

		INDArray ndValues = predict_values(nextStates);
		INDArray rewardPrediction = ndValues.mul(AIConstants.GAMMA_N).mul(ndTerminalMask);
		INDArray expectedReward = ndRewards.add(rewardPrediction);

		network.fit(states, onehotActions, expectedReward);
	}
	
	public synchronized void trainPush(Sample sample){
		trainQueue.add(sample);
	}
	
	public double[] predict_probabilities(double[] state) {
        INDArray[] output = network.predict(state);
          return output[0].toDoubleVector();
	}

	public INDArray predict_values (double[][] states) {
        INDArray[] output = network.predict(states);
        return output[1];
    }

    public void save() {
	    network.save();
    }

    public void load() {
	    network.load();
    }
}
