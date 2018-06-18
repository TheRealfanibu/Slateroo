package ai.A3C;

import ai.EnvironmentInfo;

public class AIConstants {
	private AIConstants(){}

	public static final long RANDOM_SEED = 987654321;

	public static final double GAMMA = 0.99;
	public static final int N_STEP_RETURN = 8;
	public static final double GAMMA_N = Math.pow(GAMMA, N_STEP_RETURN);
	
	public static final int NUM_ACTIONS = 3;
	public static final int NUM_STATES_PER_FRAME = 12;
	public static final int STATE_STACK = 3;
	public static final int NUM_STATES = STATE_STACK * NUM_STATES_PER_FRAME;
	public static final double[] NONE_STATE = new double[NUM_STATES];
	
	public static final double EPS_START = 0.8;
	public static final double EPS_STOP = 0.15;
	public static final double EPS_STEPS = 20000000;
	
	public static final int MIN_BATCH = 32;
	public static final double LEARNING_RATE = 0.0001;
	public static final double RMS_PROP_DECAY = 0.99;

	public static final int TRAIN_ENVS = 8;
	public static final int OPTIMIZERS = 2;
}
