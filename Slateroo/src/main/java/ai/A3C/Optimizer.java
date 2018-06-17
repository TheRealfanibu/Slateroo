package ai.A3C;


public class Optimizer extends Thread{
	private Brain brain;
	
	public Optimizer(Brain brain){
		this.brain = brain;
		start();
	}
	
	public void run(){
		while(!isInterrupted()) {
			brain.optimize();
		}
	}
	
}
