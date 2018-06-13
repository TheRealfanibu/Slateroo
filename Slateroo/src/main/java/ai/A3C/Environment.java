package ai.A3C;

import java.util.List;

import ai.EnvironmentInfo;
import logic.Snake;
import logic.SnakeManager;

public class Environment extends Thread{
	private Agent agent = new Agent();
	private EnvironmentInfo envInfo;
	private SnakeManager snakeManager;
	
	public Environment(EnvironmentInfo envInfo, SnakeManager snakeManager, 
			double epsStart, double epsEnd, double epsSteps){
		
		this.agent = new Agent(epsStart, epsEnd, epsSteps);
		this.envInfo = envInfo;
		this.snakeManager = snakeManager;
	}
	
	public void runEpisode(){
		
		double r_Env = 0;
		int snakeAmount = snakeManager.getSnakeAmount();
		while(!isInterrupted()){
			double[][] allActions = new double[snakeAmount][];
			List<Snake> snakes = snakeManager.getSnakes();
			for(int i = 0; i < snakeAmount; i++) {
				allActions[i] = envInfo.calcAIInputs(snakes.get(i));
			}
			double a = agent.act();
			
			
			if(done == true){s_ = None;}
			
			agent.train(s, a, r, s_);
			
			s = s_;
			r_Env += r;
			
		}
		
		System.out.println("Total R: "+r_Env);
		
	}
	
	public void run(){
		while(!isInterrupted()){
			this.runEpisode();
		}
	}
	
}
