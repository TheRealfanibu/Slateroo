package items;

import items.manage.ItemEffectMode;
import items.markerInterfaces.IStackableEffectItem;
import items.superClasses.MultiplePlayerTimeEffectItem;
import logic.Snake;
import logic.SnakeManager;

public class SpeedItem extends MultiplePlayerTimeEffectItem implements IStackableEffectItem{
	public static final double AVG_SPAWN_TIME = 30;
	public static final double MAX_SPAWN_DIFF = 15;
	
	private static final double EFFECT_TIME = 5;
	private static final double SPEED_CHANGE = 150;
	
	public SpeedItem(int x, int y, ItemEffectMode mode, SnakeManager snakeManager) {
		super(x, y, mode, snakeManager, "nitro", EFFECT_TIME);
	}

	@Override
	protected void effect(Snake snake) {
		snake.changeVelocity(SPEED_CHANGE);
	}
	
	@Override
	protected void resetEffect(Snake snake) {
		snake.changeVelocity(-SPEED_CHANGE);
	}
}
