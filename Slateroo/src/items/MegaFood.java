package items;

import items.superClasses.Item;
import logic.Snake;

public class MegaFood extends Item{
	public static final double AVG_SPAWN_TIME = 30;
	public static final double MAX_SPAWN_DIFF = 15;

	private static final int TILE_ADD = 20;
	
	public MegaFood(int x, int y) {
		super(x, y, "burger");
	}

	@Override
	protected void intersectionHandling(Snake snake) {
		snake.addTilesToTail(TILE_ADD);
	}
	
}
