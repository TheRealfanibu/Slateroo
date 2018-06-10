package ai.A3C;

public class AIConstants {
	public static final double GAMMA = 0.99;
	public static final int N_STEP_RETURN = 8;
	public static final double GAMMA_N = Math.pow(GAMMA, N_STEP_RETURN);
	
	public static final int NUM_ACTIONS = 3;
	public static final int NUM_STATE = 557;
	public static final double[] NONE_STATE = new double[NUM_STATE];
	
	public static final double EPS_START = 0.4;
	public static final double EPS_STOP = 0.15;
	public static final double EPS_STEPS = 75000;
	
	public static final double MIN_BATCH = 32;
	public static final double LEARNING_RATE = 0.005;
	
	public static final double LOSS_V = 0.5;
	public static final double LOSS_ENTROPY = 0.01;
	
	public static final int[] LAYER_STRUCTURE = {NUM_STATE, 100, 3};
	public static final String[] ACTIVATION_FUNCTIONS = {"relu", "softmax"};
	public static final int TRAIN_ENVS = 4;
}
