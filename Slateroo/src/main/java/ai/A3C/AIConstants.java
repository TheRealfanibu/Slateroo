package ai.A3C;

public class AIConstants {
	public static final long RANDOM_SEED = 987654321;

	public static final double GAMMA = 0.99;
	public static final int N_STEP_RETURN = 8;
	public static final double GAMMA_N = Math.pow(GAMMA, N_STEP_RETURN);
	
	public static final int NUM_ACTIONS = 3;
	public static final int NUM_STATES = 557;
	public static final double[] NONE_STATE = new double[NUM_STATES];
	
	public static final double EPS_START = 0.6;
	public static final double EPS_STOP = 0.15;
	public static final double EPS_STEPS = 200000;
	
	public static final double MIN_BATCH = 32;
	public static final double LEARNING_RATE = 0.005;
	public static final double RMS_PROP_DECAY = 0.99;

	public static final int TRAIN_ENVS = 4;
	public static final int OPTIMIZERS = 2;
}
