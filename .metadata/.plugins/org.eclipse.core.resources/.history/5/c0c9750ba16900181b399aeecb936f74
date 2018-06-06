package ai;

import game.FPSCounter;
import gui.Frame;
import io.KeyboardSteering;
import items.manage.ItemManager;
import logic.Arena;
import logic.SnakeManager;
import render.GamePanel;
import utilities.Utils;

public class Environment extends Thread{
	public static final int FPS = 140;
	private static final int SLEEP_TIME = (int) Math.round(1000d / FPS);
	
	private SnakeManager snakeManager;
	private ItemManager itemManager;
	
	private KeyboardSteering keyListener;
	private Frame frame;
	private GamePanel gamePanel;
	private Arena arena;
	
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
		keyListener = new KeyboardSteering();
		itemManager = new ItemManager();
		snakeManager = new SnakeManager(keyListener, itemManager);
		objectDetector = new ObjectDetector(snakeManager, itemManager);
		envInfo = new EnvironmentInfo(objectDetector);
		gamePanel = new GamePanel(snakeManager, itemManager, arena);
		frame = new Frame(gamePanel);
		frame.addKeyListener(keyListener);
	}
	
	public void run() {
		if(trainAI)
			trainAI();
		else
			playUserGame();
	}
	
	private void trainAI() {
		while(!isInterrupted()) {
			
		}
	}
	
	/**
	 * This method contains a loop running all the time during the game and controlling the actions which happen in the game
	 */
	private void playUserGame() {
		FPSCounter fps = new FPSCounter("main", 5);
		while(snakeManager.isGameRunning()) {
			fps.count();
			snakeManager.takeActionForEachSnake();
			snakeManager.removeObsoleteSnakes();
			snakeManager.checkSnakeCrashes();
			snakeManager.checkSnakeItemIntersections();
			itemManager.removeActivatedItems();
			gamePanel.repaint();
			Utils.sleep(SLEEP_TIME);
		}
		System.exit(0);
	}
}
