package ai.A3C;


public class Optimizer extends Thread{
	boolean stopSignal = false;
	private Brain brain;
	
	public Optimizer(Brain brain){
		this.brain = brain;
	}
	
	public void run(){
		while(!isInterrupted()) {
			brain.optimize();
		}
	}
	
}
