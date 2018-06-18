package ai;

import java.awt.*;
import java.util.*;
import java.util.List;

import ai.A3C.Agent;
import ai.A3C.AIConstants;
import ai.A3C.Brain;
import ai.A3C.TrainRenderListener;
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

import javax.swing.*;

public class Environment extends Thread{
	private static TrainRenderListener renderListener;

	static {
		renderListener = new TrainRenderListener();
		JFrame loggerFrame = new JFrame();
		loggerFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		loggerFrame.setSize(new Dimension(500,500));
		loggerFrame.setLocationRelativeTo(null);
		loggerFrame.addKeyListener(renderListener);
		loggerFrame.setVisible(true);
	}

	public static final int FPS = 140;
	private static final int SLEEP_TIME = (int) Math.round(1000d / FPS);
	
	private static final int SNAKE_AMOUNT = 1;
	private static final int PLAYER_AMOUNT = 0;

	private static final int REWARD_PRINT_FREQUENCY = 30;
	private static double totalRewardSum = 0;
	private static int frequencyCounter = 0;

	private Arena arena;
	private SnakeManager snakeManager;
	private ItemManager itemManager;
	
	private KeyboardSteering keyListener;
	private GamePanel gamePanel;
	private Frame frame;
	
	private Map<Snake,Agent> agents;

	private EnvironmentInfo envInfo;
	
	private boolean trainAI;

	public Environment( boolean trainAI) {
	    this.trainAI = trainAI;
		keyListener = new KeyboardSteering();
		agents = new HashMap<>();
		init();
	    start();
    }

    private void initAgents() {
		agents.clear();
		snakeManager.getSnakes().forEach(snake -> agents.put(snake, new Agent()));
	}

    private void init() {
		arena = new Arena();
		snakeManager = new SnakeManager(SNAKE_AMOUNT, trainAI ? 0 : PLAYER_AMOUNT, arena);
		itemManager = new ItemManager(snakeManager);
		ObjectDetector objectDetector = new ObjectDetector(snakeManager, itemManager);
		envInfo = new EnvironmentInfo(objectDetector);
		gamePanel = new GamePanel(snakeManager, itemManager, arena);
		frame = new Frame(gamePanel);
		if(!trainAI) {
			frame.addKeyListener(keyListener);
			frame.setVisible(true);
		} else {
			initAgents();
			frame.addKeyListener(renderListener);
		}
	}
	
	/**
	 * Creates instances of the classes, which only have to exist once.
	 * It also handles the access between each other
	 */
	private void reset() {
		arena.reset();
		snakeManager.reset();
		itemManager.reset();
		initAgents();
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

			double[][] states = createStateStack();
			while(true) {
				render();

				Direction[] actions = calcSnakeActions(states);
                int[] actionIndices = Arrays.stream(actions).mapToInt(Direction::ordinal).toArray();
				gameStep(actions);
				double[][] nextStates = calcEnvironmentStates();
				nextStates = insertStateIntoStack(states, nextStates);
				double[] rewards = getRewardsFromSnakes();

				List<Snake> snakes = snakeManager.getSnakes();
				for(int i = 0; i < snakeManager.getSnakeAmount(); i++) {
					double[] state = states[i];
					int actionIndex = actionIndices[i];
					double reward = rewards[i];
					totalReward += reward;
					double[] nextState = nextStates[i];
					agents.get(snakes.get(i)).train(state, actionIndex, reward, nextState);
				}
				snakeManager.removeCollidedSnakes();

				if(!snakeManager.isGameRunning())
					break;

				states = Arrays.stream(nextStates).filter(Objects::nonNull).toArray(double[][]::new);
			}
			reset();
			System.gc();
			totalRewardSum += totalReward;
			if(++frequencyCounter == REWARD_PRINT_FREQUENCY) {
				System.out.println("Average reward of " + frequencyCounter + ": " + totalRewardSum / frequencyCounter);
				totalRewardSum = 0;
				frequencyCounter = 0;
			}

		}
	}

	private double[][] insertStateIntoStack(double[][] stateStack, double[][] state) {
		int snakeAmount = snakeManager.getSnakeAmount();
		int currentStateBeginIndex = AIConstants.NUM_STATES - AIConstants.NUM_STATES_PER_FRAME;
		double[][] newStateStack = new double[snakeAmount][AIConstants.NUM_STATES];
		for(int i = 0; i < snakeAmount; i++) {
			if(state[i] != null) {
				System.arraycopy(stateStack[i], AIConstants.NUM_STATES_PER_FRAME, newStateStack[i], 0, currentStateBeginIndex);
				System.arraycopy(state[i], 0, newStateStack[i], currentStateBeginIndex, AIConstants.NUM_STATES_PER_FRAME);
			}
			else
				newStateStack[i] = null;
		}
		return newStateStack;
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

	private double[][] createStateStack() {
		double[][] state = calcEnvironmentStates();
		double[][] stateStack = new double[snakeManager.getSnakeAmount()][AIConstants.NUM_STATES];
		for(int i = 0; i < snakeManager.getSnakeAmount(); i++) {
			for(int j = 0; j < AIConstants.NUM_STATES; j++) {
				int stateIndex = j % AIConstants.NUM_STATES_PER_FRAME;
				stateStack[i][j] = state[i][stateIndex];
			}
		}
		return stateStack;
	}


	private void render() {
		if(renderListener.isRendering()) {
			if(!frame.isVisible()) {
				frame.setVisible(true);
		 }
		 gamePanel.repaint();
		 Utils.sleep(1);
	 } else {
			if(frame.isVisible())
				frame.setVisible(false);
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

	
	private Direction[] calcSnakeActions(double[][] envStates) {
		Direction[] actions = new Direction[snakeManager.getSnakeAmount()];
		List<Snake> snakes = snakeManager.getSnakes();
		for(int i = 0; i < snakeManager.getSnakeAmount(); i++) {
			Snake snake = snakes.get(i);
			//if(snake.isCollided())
			//	actions[i] = Direction.LEFT; // default value such that it can be processed by the stream, but not needed anyways
			if (snake.isSteeredByAI()) {
				actions[i] = Direction.ofIndex(Agent.act(envStates[i], trainAI));
			} else {
				actions[i] = keyListener.getMoveDirection();
			}
		}
		return actions;
	}
	
	private void gameStep(Direction[] snakeActions) {
		snakeManager.takeActionForEachSnake(snakeActions);
		if(!trainAI)
			snakeManager.removeNonVisibleSnakes();
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
