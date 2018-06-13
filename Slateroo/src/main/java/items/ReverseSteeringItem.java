package items;

import items.manage.ItemEffectMode;
import items.superClasses.MultiplePlayerEffectItem;
import logic.Snake;
import logic.SnakeManager;

public class ReverseSteeringItem extends MultiplePlayerEffectItem{
	public static final int AVG_SPAWN_TIME = 40;
	public static final int MAX_SPAWN_DIFF = 20;
	
	public ReverseSteeringItem(int x, int y, ItemEffectMode mode, SnakeManager snakeManager) {
		super(x, y, mode, snakeManager, "reverse");
	}
	

	@Override
	protected void effect(Snake snake) {
		snake.reverseSteering();
	}

}
