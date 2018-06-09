package items;

import items.superClasses.Item;
import logic.Snake;

public class Food extends Item {
	public static final double AVG_SPAWN_TIME = 6;
	public static final double MAX_SPAWN_DIFF = 1.5;
	
	private static final int TILE_ADD = 10;

	public Food(int x, int y) {
		super(x, y, "steak");
	}

	@Override
	protected void intersectionHandling(Snake snake) {
		snake.addTilesToTail(TILE_ADD);
	}
}
