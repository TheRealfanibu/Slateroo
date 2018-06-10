package items;

import items.superClasses.Item;
import logic.Snake;
import logic.SnakeManager;

public class MegaFood extends Item{
	public static final double AVG_SPAWN_TIME = 30;
	public static final double MAX_SPAWN_DIFF = 15;

	private static final int TILE_ADD = 20;
	
	private static final double COLLECT_REWARD = 0.3;
	
	public MegaFood(int x, int y, SnakeManager snakeManager) {
		super(x, y, snakeManager, "burger");
	}

	@Override
	protected void intersectionHandling(Snake snake) {
		snake.addTilesToTail(TILE_ADD);
		snake.addReward(COLLECT_REWARD);
	}
	
}
