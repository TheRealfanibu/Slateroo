package ai.A3C;

public class Sample {
	
	private double[] state;
	private int action;
	private double[] onehotAction;
	private double reward;
	private double[] nextState;
	private boolean terminateState;
	
	public Sample(double[] state, int action, double reward, double[] nextState) {
		this.state = state;
		this.action = action;
		this.reward = reward;
		this.nextState = nextState;

		onehotAction = new double[AIConstants.NUM_ACTIONS]; // every entry is 0 except the index of the action is 1
		onehotAction[action] = 1;

		terminateState = nextState == null;

	}

	public double[] getState() {
		return state;
	}

	public int getAction() {
		return action;
	}

	public double[] getOnehotAction() {
		return onehotAction;
	}

	public double getReward() {
		return reward;
	}

	public double[] getNextState() {
		return nextState;
	}

	public boolean isTerminateState() {
		return terminateState;
	}
}
