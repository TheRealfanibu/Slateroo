package ai;

import java.util.List;

import ai.A3C.Agent;
import ai.A3C.Constants;
import game.FPSCounter;
import gui.Frame;
import io.Direction;
import io.KeyboardSteering;
import items.manage.ItemManager;
import logic.Arena;
import logic.Snake;
import logic.SnakeManager;
import render.GamePanel;
import utilities.Utils;

public class Environment extends Thread{
	public static final int FPS = 140;
	private static final int SLEEP_TIME = (int) Math.round(1000d / FPS);
	
	private static final int SNAKE_AMOUNT = 4;
	private static final int PLAYER_AMOUNT = 1;
	
	private SnakeManager snakeManager;
	private ItemManager itemManager;
	
	private KeyboardSteering keyListener;
	private Frame frame;
	private GamePanel gamePanel;
	private Arena arena;
	
	private Agent agent;
	private EnvironmentInfo envInfo;
	private ObjectDetector objectDetector;
	
	private boolean trainAI;
	
	public Environment(boolean aiTraining) {
		this.trainAI = aiTraining;
		init();
		start();
	}
	
	/**
	 * Creates instances of the classes, which only have to exist once.
	 * It also handles the access between each other
	 */
	private void init() {
		arena = new Arena();
		itemManager = new ItemManager();
		snakeManager = new SnakeManager(SNAKE_AMOUNT, trainAI ? 0 : PLAYER_AMOUNT, itemManager);
		if(trainAI) {
			agent = new Agent();
			objectDetector = new ObjectDetector(snakeManager, itemManager);
			envInfo = new EnvironmentInfo(objectDetector);
		} else {
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
			double[][] states = calcEnvironmentStates();
			while(true) {
				
				
			}
		}
	}
	
	private double[][] evaluateOneGameStep(Direction[] snakeActions) {
		
	}
	
	private double[][] calcEnvironmentStates() {
		double[][] states = new double[snakeManager.getSnakeAmount()][Constants.TRAIN_ENVS];
		List<Snake> snakes = snakeManager.getSnakes();
		for(int i = 0; i < snakeManager.getSnakeAmount(); i++) {
			Snake snake = snakes.get(i);
			if(snake.isSteeredByAI())
				states[i] = envInfo.calcAIInputs(snake);
		}
		return states;
	}
	
	private Direction[] calcSnakeActions(double[][] envStates) {
		Direction[] actions = new Direction[snakeManager.getSnakeAmount()];
		List<Snake> snakes = snakeManager.getSnakes();
		for(int i = 0; i < snakeManager.getSnakeAmount(); i++) {
			if(snakes.get(i).isSteeredByAI())
				actions[i] = Direction.ofIndex(agent.act(envStates[i]));
			else
				actions[i] = keyListener.getMoveDirection();
		}
		return actions;
	}
	
	private void gameStep(Direction[] snakeActions) {
		snakeManager.takeActionForEachSnake(snakeActions);
		snakeManager.removeObsoleteSnakes();
		snakeManager.checkSnakeCrashes();
		snakeManager.checkSnakeItemIntersections();
		itemManager.removeActivatedItems();
	}
	
	/**
	 * This method contains a loop running all the time during the game and controlling the actions which happen in the game
	 */
	private void playUserGame() {
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
