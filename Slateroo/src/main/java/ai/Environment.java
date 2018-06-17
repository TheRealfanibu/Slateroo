package ai;

import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;

import ai.A3C.Agent;
import ai.A3C.AIConstants;
import ai.A3C.Brain;
import gui.Frame;
import io.Direction;
import io.KeyboardSteering;
import items.manage.ItemManager;
import logic.Arena;
import logic.Snake;
import logic.SnakeManager;
import render.GamePanel;
import utilities.Utils;
import game.FPSCounter;

public class Environment extends Thread{
	public static final int FPS = 140;
	private static final int SLEEP_TIME = (int) Math.round(1000d / FPS);
	
	private static final int SNAKE_AMOUNT = 4;
	private static final int PLAYER_AMOUNT = 4;
	
	private SnakeManager snakeManager;
	private ItemManager itemManager;
	
	private KeyboardSteering keyListener;
	private GamePanel gamePanel;
	private Frame frame;
	
	private Agent agent;
	private EnvironmentInfo envInfo;
	
	private boolean trainAI;

	public Environment(Brain brain, boolean trainAI) {
	    this.trainAI = trainAI;
        agent = new Agent(brain);
	    start();
    }
	
	/**
	 * Creates instances of the classes, which only have to exist once.
	 * It also handles the access between each other
	 */
	private void reset() {
		Arena arena = new Arena();
		snakeManager = new SnakeManager(SNAKE_AMOUNT, trainAI ? 0 : PLAYER_AMOUNT, arena);
		itemManager = new ItemManager(snakeManager);
        ObjectDetector objectDetector = new ObjectDetector(snakeManager, itemManager);
        envInfo = new EnvironmentInfo(objectDetector);
        if(!trainAI) {
            keyListener = new KeyboardSteering();
            gamePanel = new GamePanel(snakeManager, itemManager, arena);
            frame = new Frame(gamePanel);
            frame.addKeyListener(keyListener);
        }
	}
	
	public void run() {
		if(trainAI)
			trainAI();
		else
			playUserGame();
	}
	
	private void trainAI() {
		while(!isInterrupted()) {
			double totalReward = 0;
			reset();
			double[][] states = calcEnvironmentStates();
			while(true) {
			   // gamePanel.repaint();

				Direction[] actions = calcSnakeActions(states);
                int[] actionIndices = Arrays.stream(actions).mapToInt(Direction::ordinal).toArray();
				gameStep(actions);
				double[][] nextStates = calcEnvironmentStates();
				double[] rewards = getRewardsFromSnakes();
				
				for(int i = 0; i < snakeManager.getSnakeAmount(); i++) {
					double[] state = states[i];
					if(state != null) {
                        int actionIndex = actionIndices[i];
                        double reward = rewards[i];
                        totalReward += reward;
                        double[] nextState = nextStates[i];
                        agent.train(state, actionIndex, reward, nextState);
                    }
				}
				
				if(snakeManager.isEverySnakeCollided())
					break;
				
				states = nextStates;
			}
			//frame.setVisible(false);
			//frame.dispose();
			System.out.println("Total Reward: " + totalReward);
		}
	}
	
	private double[] getRewardsFromSnakes() {
		double[] rewards = new double[snakeManager.getSnakeAmount()];
		List<Snake> snakes = snakeManager.getSnakes();
		for(int i = 0; i < snakeManager.getSnakeAmount(); i++) {
			rewards[i] = snakes.get(i).getAndResetReward();
		}
		return rewards;
	}
	
	private double[][] calcEnvironmentStates() {
		double[][] states = new double[snakeManager.getSnakeAmount()][AIConstants.NUM_STATES];
		List<Snake> snakes = snakeManager.getSnakes();
		for(int i = 0; i < snakeManager.getSnakeAmount(); i++) {
			Snake snake = snakes.get(i);
			if(snake.isSteeredByAI() && !snake.isCollided())
			    states[i] = envInfo.calcAIInputs(snake);
			else
			    states[i] = null;
		}
		return states;
	}
	
	private Direction[] calcSnakeActions(double[][] envStates) {
		Direction[] actions = new Direction[snakeManager.getSnakeAmount()];
		List<Snake> snakes = snakeManager.getSnakes();
		for(int i = 0; i < snakeManager.getSnakeAmount(); i++) {
			Snake snake = snakes.get(i);
			if(snake.isCollided())
				actions[i] = Direction.LEFT; // default value such that it can be processed by the stream, but not needed anyways
			else if (snake.isSteeredByAI()) {
				actions[i] = Direction.ofIndex(agent.act(envStates[i], trainAI));
			} else {
				actions[i] = keyListener.getMoveDirection();
			}
		}
		return actions;
	}
	
	private void gameStep(Direction[] snakeActions) {
		snakeManager.takeActionForEachSnake(snakeActions);
		snakeManager.removeObsoleteSnakes();
		snakeManager.checkSnakeBorderCollision();
		snakeManager.checkSnakeCrashes();
		itemManager.checkSnakeItemIntersections();
		itemManager.removeActivatedItems();
	}
	
	/**
	 * This method contains a loop running all the time during the game and controlling the actions which happen in the game
	 */
	private void playUserGame() {
	    reset();
		FPSCounter fps = new FPSCounter("main", 5);
		while(snakeManager.isGameRunning()) {
			fps.count();
			double[][] envStates = calcEnvironmentStates();
			Direction[] actions = calcSnakeActions(envStates);
			gameStep(actions);
			gamePanel.repaint();
			Utils.sleep(SLEEP_TIME);
		}
		System.exit(0);
	}
}
