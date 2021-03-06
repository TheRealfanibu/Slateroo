package items;

import items.superClasses.Item;
import logic.Snake;
import logic.SnakeManager;

public class Food extends Item {
	public static final double AVG_SPAWN_TIME = 5;
	public static final double MAX_SPAWN_DIFF = 1.5;
	
	private static final int TILE_ADD = 10;
	
	private static final double COLLECT_REWARD = 0.3;

	public Food(int x, int y, SnakeManager snakeManager) {
		super(x, y, snakeManager, "steak");
	}

	@Override
	protected void intersectionHandling(Snake snake) {
		snake.addTilesToTail(TILE_ADD);
		snake.addReward(COLLECT_REWARD);
	}
}
