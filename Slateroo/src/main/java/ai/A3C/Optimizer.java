package ai.A3C;


public class Optimizer extends Thread{
	private static Brain brain;
	
	public Optimizer(){
		start();
	}
	
	public void run(){
		while(!isInterrupted()) {
			brain.optimize();
		}
	}

	public static void setBrain(Brain brain) {
		Optimizer.brain = brain;
	}
	
}
